package aau.cc.external;

import aau.cc.model.Heading;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class HTMLParserAdapterTest {

    public static final String HTML = "<html>" +
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
    private static final String URL = "https://google.at";
    private static final String MALFORMED_URL = "not an url!";
    private static final int FETCH_TIMEOUT = 3000;
    private HTMLParserAdapter htmlParser; ;



    @BeforeEach
    public void setUp() {
        htmlParser = new HTMLParserAdapter();
        htmlParser.setDocumentFromString(HTML);
    }

    @AfterEach
    public void tearDown() {
        htmlParser = null;
    }


    @Test
    public void testSizeOfHeadingsOfWebsite() {
        List<Heading> result = htmlParser.getHeadingsFromHTML();
        assertEquals(3, result.size());
    }

    @Test
    public void testTextOfHeadingsOfWebsite() {
        List<Heading> result = htmlParser.getHeadingsFromHTML();
        assertHeadingText(result);
    }

    @Test
    public void testDepthOfHeadingsOfWebsite() {
        List<Heading> result = htmlParser.getHeadingsFromHTML();
        assertHeadingDepth(result);
    }

    @Test
    public void testGetHeadingsFromHTMLNoParse() {
        htmlParser = new HTMLParserAdapter();
        List<Heading> result = htmlParser.getHeadingsFromHTML();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testSizeOfLinksOfWebsite() {
        List<String> result = htmlParser.getLinksFromHTML();
        assertEquals(2, result.size());
    }

    @Test
    public void testStringsOfLinksOfWebsite() {
        List<String> result = htmlParser.getLinksFromHTML();
        assertLinks(result);
    }

    @Test
    public void testGetLinksFromHTMLNoParse() {
        htmlParser = new HTMLParserAdapter();
        List<String> result = htmlParser.getLinksFromHTML();
        assertTrue(result.isEmpty());
    }

    @Test
    public void testParserHasDocument() {
        assertTrue(htmlParser.hasDocument());
    }

    @Test
    public void testParseWebsiteFromURL() {
        assertTrue(htmlParser.fetchHTMLFromURL(URL, FETCH_TIMEOUT));
        assertTrue(htmlParser.hasDocument());
    }

    @Test
    public void testParseWebsiteFromMalformedURL() {
        assertFalse(htmlParser.fetchHTMLFromURL(MALFORMED_URL, FETCH_TIMEOUT));
        assertFalse(htmlParser.hasDocument());
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
