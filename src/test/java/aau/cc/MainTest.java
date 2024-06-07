package aau.cc;

import aau.cc.model.CrawledWebsite;
import aau.cc.model.Language;
import aau.cc.model.UserInput;
import aau.cc.model.WebsiteToCrawl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


public class MainTest {
    private static final String WEBSITE_URL = "https://www.test.com";
    private static final String FILE_NAME = "test .md";
    private static final int DEPTH = 2;
    private List<String> urls;
    private UserInput userInput;

    @BeforeEach
    public void setUp() {
        urls = new ArrayList<>();
        urls.add(WEBSITE_URL);
        urls.add(WEBSITE_URL);
        setUpUserInputMock();
    }

    private void setUpUserInputMock() {
        userInput = Mockito.mock(UserInput.class);
        Main.setUserInput(userInput);
        when(userInput.getUrls())
                .thenReturn(new ArrayList<>(urls));
        when(userInput.getDepth())
                .thenReturn(DEPTH);
        when(userInput.isSkipTranslation())
                .thenReturn(false);
        when(userInput.getTargetLanguage())
                .thenReturn(Language.ENGLISH);
        when(userInput.getDomains())
                .thenReturn(new ArrayList<>());
    }

    @AfterEach
    public void tearDown() {
        userInput = null;
        urls = null;
        File file = new File(FILE_NAME);
        if (file.exists()) {
            assertTrue(file.delete());
        }
    }

    @Test
    public void testPrepareWebsitesToCrawl() {
        List<WebsiteToCrawl> result = Main.prepareWebsitesToCrawl();
        assertWebsitesToCrawl(result, false);
    }

    @Test
    public void testPrepareWebsitesToCrawlSkipTranslation() {
        when(userInput.isSkipTranslation())
                .thenReturn(true);
        when(userInput.getTargetLanguage())
                .thenReturn(null);
        List<WebsiteToCrawl> result = Main.prepareWebsitesToCrawl();
        assertWebsitesToCrawl(result, true);
    }

    @Test
    public void testCrawlWebsiteTimed() {
        List<WebsiteToCrawl> input = Main.prepareWebsitesToCrawl();
        List<CrawledWebsite> result = Main.crawlWebsitesTimed(input);
        assertCrawledWebsites(result);
    }

    @Test
    public void testWriteResultsToFile() {
        List<WebsiteToCrawl> input = Main.prepareWebsitesToCrawl();
        List<CrawledWebsite> result = Main.crawlWebsitesTimed(input);
        Main.writeResultsToFile(result, FILE_NAME);
        assertTrue(Files.exists(Path.of(FILE_NAME)));
    }

    private void assertCrawledWebsites(List<CrawledWebsite> crawledWebsites) {
        for (CrawledWebsite crawledWebsite : crawledWebsites) {
            assertTrue(crawledWebsite.hasBrokenUrl());
            assertEquals(WEBSITE_URL, crawledWebsite.getUrl());
        }
    }

    private void assertWebsitesToCrawl(List<WebsiteToCrawl> websitesToCrawl, boolean skipTranslation){
        for(WebsiteToCrawl websiteToCrawl: websitesToCrawl){
            assertEquals(DEPTH, websiteToCrawl.getDepth());
            assertEquals(WEBSITE_URL, websiteToCrawl.getUrl());
            assertEquals(Language.GERMAN, websiteToCrawl.getSource());
            assertEquals(Language.ENGLISH, websiteToCrawl.getTarget());
        }
    }
}
