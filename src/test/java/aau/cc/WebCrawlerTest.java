package aau.cc;

import aau.cc.external.HTMLParserAdapter;
import aau.cc.external.HTMLParserAdapterTest;
import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;
import aau.cc.model.Language;
import aau.cc.model.WebsiteToCrawl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class WebCrawlerTest {

    private static final List<String> DOMAINS = List.of("example.org");
    private static final String URL = "https://test.at";
    private static final String EXPECTED_URL1 = "https://example.com.1";
    private static final String EXPECTED_URL2 = "https://example.org.1";
    private static final List<Heading> HEADINGS = List.of(new Heading("Test"), new Heading("Test2"));
    private static final List<String> URLS = List.of(URL, EXPECTED_URL1, EXPECTED_URL2);
    private HTMLParserAdapter htmlParser;
    private WebCrawler webCrawler;
    private WebsiteToCrawl website;
    private List<WebsiteToCrawl> websites;
    private final Set<String> alreadyVisited = Collections.synchronizedSet(new HashSet<>());
    private HTMLParserAdapter mockedHtmlParser;
    private Map<Future<CrawledWebsite>, String> futures;

    @BeforeEach
    public void setUp() {
        htmlParser = new HTMLParserAdapter();
        htmlParser.setDocumentFromString(HTMLParserAdapterTest.HTML);
        webCrawler = new WebCrawler(DOMAINS, true);
        website = new WebsiteToCrawl(URL, 2, Language.GERMAN, Language.ENGLISH);
        futures = new HashMap<>();
    }

    @AfterEach
    public void tearDown() {
        htmlParser = null;
        website = null;
        webCrawler = null;
        mockedHtmlParser = null;
        futures = null;
    }

    private void setUpMultipleWebsites() {
        websites = new ArrayList<>();
        websites.add(website);
        websites.add(website);
    }

    private void setUpMockedHtmlParser() {
        mockedHtmlParser = Mockito.mock(HTMLParserAdapter.class);
        when(mockedHtmlParser.fetchHTMLFromURL(anyString())).thenReturn(true);
        when(mockedHtmlParser.hasDocument()).thenReturn(true);
        when(mockedHtmlParser.getHeadingsFromHTML()).thenReturn(HEADINGS);
        when(mockedHtmlParser.getTranslatedHeadingsFromHTML(any())).thenReturn(HEADINGS);
        when(mockedHtmlParser.getLinksFromHTML()).thenReturn(URLS);
    }

    private void setUpFutures() throws ExecutionException, InterruptedException, TimeoutException {
        Future<CrawledWebsite> future = Mockito.mock(Future.class);
        when(future.get(anyLong(), any())).thenReturn(new CrawledWebsite(website));
        futures.put(future, URL);
    }

    private void setUpFuturesError() throws ExecutionException, InterruptedException, TimeoutException {
        Future<CrawledWebsite> future_error = Mockito.mock(Future.class);
        when(future_error.get(anyLong(), any())).thenThrow(new InterruptedException());
        futures.put(future_error, URL);
    }

    @Test
    public void testSizeOfWebsitesToCrawl() {
        List<String> list = htmlParser.getLinksFromHTML();
        List<String> result = webCrawler.filterLinksByDomains(list);
        assertEquals(1, result.size());
    }

    @Test
    public void testStringsOfWebsitesToCrawl() {
        List<String> list = htmlParser.getLinksFromHTML();
        List<String> result = webCrawler.filterLinksByDomains(list);
        assertEquals(EXPECTED_URL2, result.get(0));
    }

    @Test
    public void testSizeOfWebsitesToCrawlEmptyDomains() {
        List<String> list = htmlParser.getLinksFromHTML();
        webCrawler = new WebCrawler(true);
        List<String> result = webCrawler.filterLinksByDomains(list);
        assertEquals(2, result.size());
    }

    @Test
    public void testStringsOfWebsitesToCrawlEmptyDomains() {
        List<String> list = htmlParser.getLinksFromHTML();
        webCrawler = new WebCrawler(true);
        List<String> result = webCrawler.filterLinksByDomains(list);
        assertLinks(result);
    }

    @Test
    public void testFetchAndCrawlWebsiteBasic() {
        setUpMockedHtmlParser();
        website = webCrawler.fetchAndCrawlWebsite(website, alreadyVisited, mockedHtmlParser);
        assertNotNull(website);
        assertEquals(website.getUrl(), URL);
    }

    @Test
    public void testFetchAndCrawlWebsiteEmptyDomains() {
        setUpMockedHtmlParser();
        webCrawler = new WebCrawler(true);
        website = webCrawler.fetchAndCrawlWebsite(website, alreadyVisited, mockedHtmlParser);
        assertNotNull(website);
    }

    @Test
    public void testFetchAndCrawlWebsiteError() {
        website.setUrl("Error.net");
        CrawledWebsite crawledWebsite = webCrawler.fetchAndCrawlWebsite(website, alreadyVisited, new HTMLParserAdapter());
        assertTrue(crawledWebsite.hasBrokenUrl());
    }

    @Test
    public void testFetchAndCrawlWebsiteErrorUrl() {
        website.setUrl("Error.net");
        CrawledWebsite crawledWebsite = webCrawler.fetchAndCrawlWebsite(website, alreadyVisited, new HTMLParserAdapter());
        assertEquals("Error.net (Error while fetching Website)", crawledWebsite.getUrl());
    }

    @Test
    public void testCrawlWebsitesSize() {
        setUpMultipleWebsites();
        List<CrawledWebsite> websites1 = webCrawler.crawlWebsites(websites);
        assertEquals(2, websites1.size());
    }

    @Test
    public void testCollectCrawledWebsites() throws Exception {
        setUpFutures();
        webCrawler.setFutures(futures);
        webCrawler.collectCrawledWebsitesFromFutures();
        assertEquals(1, webCrawler.getCrawledWebsites().size());
    }

    @Test
    public void testCollectCrawledWebsitesException() throws Exception {
        setUpFuturesError();
        webCrawler.setFutures(futures);
        webCrawler.collectCrawledWebsitesFromFutures();
        assertEquals(1, webCrawler.getCrawledWebsites().size());
        assertTrue(webCrawler.getCrawledWebsites().get(0).hasBrokenUrl());
    }

    @Test
    public void testCollectAndLinkWebsites() throws Exception {
        setUpFutures();
        CrawledWebsite crawledWebsite = new CrawledWebsite(website);
        webCrawler.collectAndLinkWebsitesFromFutures(futures, crawledWebsite);
        assertEquals(1, crawledWebsite.getLinkedWebsites().size());
    }

    @Test
    public void testCollectAndLinkWebsitesException() throws Exception {
        setUpFuturesError();
        CrawledWebsite crawledWebsite = new CrawledWebsite(website);
        webCrawler.collectAndLinkWebsitesFromFutures(futures, crawledWebsite);
        assertTrue(crawledWebsite.getLinkedWebsites().isEmpty());
        assertEquals(1,crawledWebsite.getBrokenLinks().size());
    }

    @Test
    public void testAltConstructor() {
        webCrawler = new WebCrawler();
        assertTrue(webCrawler.getCrawledWebsites().isEmpty());
        assertFalse(webCrawler.isSkipTranslations());
    }

    @Test
    public void testUpdateDomains() {
        webCrawler = new WebCrawler(DOMAINS);
        assertEquals(DOMAINS.size(), webCrawler.getDomains().size());
        webCrawler.setDomains(new ArrayList<>());
        assertEquals(0, webCrawler.getDomains().size());
    }

    @Test
    public void testGetIntersectionOfLinks() {
        Set<String> result = webCrawler.getIntersectionOfLinks(Arrays.asList(EXPECTED_URL1, EXPECTED_URL2));
        assertTrue(result.contains(EXPECTED_URL2));
        assertFalse(result.contains(EXPECTED_URL1));
    }

    @Test
    public void testGetIntersectionOfLinksEmpty() {
        Set<String> result = webCrawler.getIntersectionOfLinks(List.of(EXPECTED_URL1));
        assertTrue(result.isEmpty());
    }

    @Test
    public void testResetWebCrawler() {
        setUpMultipleWebsites();
        webCrawler.crawlWebsites(websites);
        assertEquals(2, webCrawler.getCrawledWebsites().size());
        webCrawler.reset();
        assertTrue(webCrawler.getCrawledWebsites().isEmpty());
        assertTrue(webCrawler.getFutures().isEmpty());
    }

    private void assertLinks(List<String> result) {
        assertEquals(EXPECTED_URL1, result.get(0));
        assertEquals(EXPECTED_URL2, result.get(1));
    }
}
