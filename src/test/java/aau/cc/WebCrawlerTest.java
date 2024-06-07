package aau.cc;

import aau.cc.external.HTMLParserAdapter;
import aau.cc.model.CrawledWebsite;
import aau.cc.model.Language;
import aau.cc.model.WebsiteToCrawl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static aau.cc.external.HTMLParserAdapterTest.HTML;
import static org.junit.jupiter.api.Assertions.*;

public class WebCrawlerTest {

    private static final List<String> DOMAINS = List.of("example.org");
    private static final String URL = "https://google.at";
    private static final String EXPECTED_URL1 = "https://example.com";
    private static final String EXPECTED_URL2 = "https://example.org";
    private HTMLParserAdapter htmlParser;
    private WebCrawler webCrawler;
    private WebsiteToCrawl website;
    private List<WebsiteToCrawl> websites;
    private final Set<String> alreadyVisited = Collections.synchronizedSet(new HashSet<>());

    @BeforeEach
    public void setUp() {
        htmlParser = new HTMLParserAdapter();
        htmlParser.setDocumentFromString(HTML);
        webCrawler = new WebCrawler(DOMAINS);
        website = new WebsiteToCrawl(URL, 1,Language.GERMAN, Language.ENGLISH);
        website.setSource(Language.GERMAN);
        website.setTarget(Language.ENGLISH);
    }

    @AfterEach
    public void tearDown() {
        htmlParser = null;
    }

    private void setUpMultipleWebsites(){
        websites = new ArrayList<>();
        websites.add(website);
        websites.add(website);
    }

    @Test
    public void testSizeOfWebsitesToCrawl() {
        List<String> list = htmlParser.getLinksFromHTML();
        List<String> result = webCrawler.getLinksToCrawlFromDomains(list);
        assertEquals(1, result.size());
    }

    @Test
    public void testStringsOfWebsitesToCrawl() {
        List<String> list = htmlParser.getLinksFromHTML();
        List<String> result = webCrawler.getLinksToCrawlFromDomains(list);
        assertEquals(EXPECTED_URL2, result.get(0));
    }

    @Test
    public void testSizeOfWebsitesToCrawlEmptyDomains() {
        List<String> list = htmlParser.getLinksFromHTML();
        webCrawler = new WebCrawler();
        List<String> result = webCrawler.getLinksToCrawlFromDomains(list);
        assertEquals(2, result.size());
    }

    @Test
    public void testStringsOfWebsitesToCrawlEmptyDomains() {
        List<String> list = htmlParser.getLinksFromHTML();
        webCrawler = new WebCrawler();
        List<String> result = webCrawler.getLinksToCrawlFromDomains(list);
        assertLinks(result);
    }

    @Test
    public void testCrawlWebsiteBasic() {
        website = webCrawler.crawlWebsite(website, alreadyVisited);
        assertNotNull(website);
    }


    @Test
    public void testCrawlWebsiteEmptyDomains() {
        webCrawler = new WebCrawler();
        website = webCrawler.crawlWebsite(website, alreadyVisited);
        assertNotNull(website);
    }

    @Test
    public void testCrawlWebsiteError() {
        website.setUrl("Error.net");
        CrawledWebsite crawledWebsite = webCrawler.crawlWebsite(website, alreadyVisited);
        assertTrue(crawledWebsite.hasBrokenUrl());
    }

    @Test
    public void testCrawlWebsiteErrorUrl() {
        website.setUrl("Error.net");
        CrawledWebsite crawledWebsite = webCrawler.crawlWebsite(website, alreadyVisited);
        assertEquals("Error.net", crawledWebsite.getUrl());
    }

    @Test
    public void testCrawlWebsitesSize() {
        setUpMultipleWebsites();
        List<CrawledWebsite> websites1 = webCrawler.crawlWebsites(websites);
        assertEquals(2, websites1.size());
    }

    @Test
    public void testCrawlWebsitesContent() {
        setUpMultipleWebsites();
        List<CrawledWebsite> websites1 = webCrawler.crawlWebsites(websites);
        assertEquals(URL, websites1.get(0).getUrl());
        assertEquals(URL, websites1.get(1).getUrl());
    }

    @Test
    public void testResetWebCrawler() {
        setUpMultipleWebsites();
        webCrawler.crawlWebsites(websites);
        assertEquals(2, webCrawler.getCrawledWebsites().size());
        webCrawler.reset();
        assertTrue(webCrawler.getCrawledWebsites().isEmpty() );
    }

    private void assertLinks(List<String> result) {
        assertEquals(EXPECTED_URL1, result.get(0));
        assertEquals(EXPECTED_URL2, result.get(1));
    }
}
