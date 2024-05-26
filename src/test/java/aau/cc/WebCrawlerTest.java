package aau.cc;

import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;
import aau.cc.model.Language;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class WebCrawlerTest {

    private static final String HTML = "<html>" +
            "<head><title>Test Document</title></head>" +
            "<body>" +
            "<h1>Heading 1</h1>" +
            "<p>Paragraph</p>" +
            "<h2>Heading 2</h2>" +
            "<a href=\"https://example.com\">Link 1</a>" +
            "<a href=\"https://example.org\">Link 2</a>" +
            "<h4>Heading 3</h4>" +
            "</body>" +
            "</html>";
    private static final List<String> DOMAINS = List.of("example.org");
    private Document document;
    private CrawledWebsite website;


    @BeforeEach
    public void setUp() {
        document = Jsoup.parse(HTML);
        website = new CrawledWebsite("https://google.at", 2);
        website.setSource(Language.GERMAN);
        website.setTarget(Language.ENGLISH);
    }

    @AfterEach
    public void tearDown() {
        document = null;

    }

    @Test
    public void testSizeOfHeadingsOfWebsite() {
        List<Heading> result = WebCrawler.getHeadingsOfWebsite(document);
        assertEquals(3, result.size());
    }

    @Test
    public void testTextOfHeadingsOfWebsite() {
        List<Heading> result = WebCrawler.getHeadingsOfWebsite(document);
        assertHeadingText(result);
    }

    @Test
    public void testDepthOfHeadingsOfWebsite() {
        List<Heading> result = WebCrawler.getHeadingsOfWebsite(document);
        assertHeadingDepth(result);
    }

    @Test
    public void testSizeOfLinksOfWebsite() {
        List<String> result = WebCrawler.getLinksOfWebsite(document);
        assertEquals(2, result.size());
    }

    @Test
    public void testStringsOfLinksOfWebsite() {
        List<String> result = WebCrawler.getLinksOfWebsite(document);
        assertLinks(result);
    }

    @Test
    public void testSizeOfWebsitesToCrawl() {
        List<String> list = WebCrawler.getLinksOfWebsite(document);
        List<String> result = WebCrawler.getLinksToCrawl(DOMAINS, list);
        assertEquals(1, result.size());
    }

    @Test
    public void testStringsOfWebsitesToCrawl() {
        List<String> list = WebCrawler.getLinksOfWebsite(document);
        List<String> result = WebCrawler.getLinksToCrawl(DOMAINS, list);
        assertEquals("https://example.org", result.get(0));
    }

    @Test
    public void testSizeOfWebsitesToCrawlNullDomains() {
        List<String> list = WebCrawler.getLinksOfWebsite(document);
        List<String> result = WebCrawler.getLinksToCrawl(null, list);
        assertEquals(2, result.size());
    }

    @Test
    public void testStringsOfWebsitesToCrawlNullDomains() {
        List<String> list = WebCrawler.getLinksOfWebsite(document);
        List<String> result = WebCrawler.getLinksToCrawl(null, list);
        assertLinks(result);
    }

    @Test
    public void testCrawlWebsiteBasic() {
        website = WebCrawler.crawlWebsite(website, DOMAINS);
        assertNotNull(website);
    }

    @Test
    public void testCrawlWebsiteNullDomains() {
        website = WebCrawler.crawlWebsite(website, null);
        assertNotNull(website);
    }

    @Test
    public void testCrawlWebsiteError() {
        website.setUrl("Error.net");
        website = WebCrawler.crawlWebsite(website, DOMAINS);
        assertNull(website);
    }

    @Test
    public void testGetAlreadyVisited() {
        website = WebCrawler.crawlWebsite(website, DOMAINS);
        Set<String> set = WebCrawler.getAlreadyVisited();
        assertFalse(set.isEmpty());
    }

    @Test
    public void testResetWebCrawler() {
        website = WebCrawler.crawlWebsite(website, DOMAINS);
        WebCrawler.reset();
        assertTrue(WebCrawler.getAlreadyVisited().isEmpty());
    }

    private void assertHeadingText(List<Heading> result) {
        assertEquals("Heading 1", result.get(0).getText());
        assertEquals("Heading 2", result.get(1).getText());
        assertEquals("Heading 3", result.get(2).getText());
    }

    private void assertHeadingDepth(List<Heading> result) {
        assertEquals(1, result.get(0).getDepth());
        assertEquals(2, result.get(1).getDepth());
        assertEquals(4, result.get(2).getDepth());
    }

    private void assertLinks(List<String> result) {
        assertEquals("https://example.com", result.get(0));
        assertEquals("https://example.org", result.get(1));
    }
}
