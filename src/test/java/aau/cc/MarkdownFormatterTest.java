package aau.cc;

import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;
import aau.cc.model.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarkdownFormatterTest {

    private static final String FILE_NAME = "Test.md";
    private static final String WEBSITE_URL = "https://www.test.com";
    private static final String CHILD_WEBSITE_URL = "https://www.subtest.com";
    private static final String BROKEN_LINK = "https://www.broken.com";
    private static final Heading HEADING_1 = new Heading("Test Überschrift Nummer 1", 1);
    private static final Heading HEADING_2 = new Heading("Test Überschrift Nummer 2", 1);
    private static final Heading HEADING_3 = new Heading("Test Überschrift Nummer 3", 2);
    private static final String EXPECTED_FORMATED_BROKEN_LINK = "### <span style=\"color:gray\">--> Broken Link to: </span>";
    private static final String[] EXPECTED_RESULT_NOT_TRANSLATED = {
            "# Test Überschrift Nummer 1",
            "# Test Überschrift Nummer 2",
            "## Test Überschrift Nummer 3"};

    private List<CrawledWebsite> websites;
    private CrawledWebsite website;
    private MarkdownFormatter formatter;
    private List<String> brokenLinks;

    @BeforeEach
    public void setUp() {
        formatter = new MarkdownFormatter(true, 2);
        setUpBrokenLinks();
        setupWebsite();
    }

    private void setUpBrokenLinks() {
        brokenLinks = new ArrayList<>();
        brokenLinks.add(BROKEN_LINK);
        brokenLinks.add("https://www.moreBroken.com");
        brokenLinks.add("https://www.veryBroken.net");
    }

    private void setupWebsite(){
        website = getWebsite(WEBSITE_URL, 2);
        website.addBrokenLink(BROKEN_LINK);
        CrawledWebsite childWebSite = getWebsite(CHILD_WEBSITE_URL, 1);
        childWebSite.setBrokenLinks(brokenLinks);
        website.addLinkedWebsite(childWebSite);
        websites = new ArrayList<>();
        websites.add(website);
    }

    private CrawledWebsite getWebsite(String URL, int depth) {
        CrawledWebsite website = new CrawledWebsite(URL, depth);
        List<Heading> headings = getHeadingList();
        website.setHeadings(headings);
        website.setSource(Language.GERMAN);
        website.setTarget(Language.ENGLISH);
        return website;
    }

    private List<Heading> getHeadingList() {
        List<Heading> headings = new ArrayList<>();
        headings.add(HEADING_1);
        headings.add(HEADING_2);
        headings.add(HEADING_3);
        return headings;
    }

    @AfterEach
    public void tearDown() {
        website = null;
        formatter = null;
        brokenLinks = null;
        File file = new File(FILE_NAME);
        if (file.exists()) {
            assertTrue(file.delete());
        }
    }

    @Test
    public void testExportHeaderContent() {
        List<String> result = formatter.getFormattedHeaderContent(website);
        assertFormattedHeader(result, true);
    }

    @Test
    public void testExportHeaderContentTranslated() {
        formatter = new MarkdownFormatter(false,2);
        List<String> result = formatter.getFormattedHeaderContent(website);
        assertFormattedHeader(result, false);
    }

    @Test
    public void testExportMainContent() {
        List<String> results = formatter.getFormattedMainContent(website);
        assertFormattedContent(results.subList(1,4));
    }

    @Test
    public void testExportChildrenContent() {
        List<String> results = formatter.getFormattedSubWebsiteContentRecursively(website, website.getDepth());
        assertFormattedChildrenContent(results);
    }

    @Test
    public void testExportBrokenLinkFromWebsite() {
        formatter = new MarkdownFormatter(false,2);
        List<String> results = formatter.getFormattedBrokenLinks(website.getBrokenLinks(),1);
        assertFormattedBrokenLinks(results);
    }

    @Test
    public void testExportBrokenLinks() {
        formatter = new MarkdownFormatter(false,2);
        List<String> results = formatter.getFormattedBrokenLinks(brokenLinks,1);
        assertFormattedBrokenLinks(results);
    }

    private void assertFormattedHeader(List<String> result, boolean translationSkipped) {
        assertEquals("# Crawled Website: <a>https://www.test.com</a>", result.get(0));
        assertEquals("### Depth: 2", result.get(1));
        assertEquals("### Source language: German", result.get(2));
        assertEquals("### Target language: English", result.get(3));
        if (translationSkipped) {
            assertEquals("### Translation has been skipped!", result.get(4));
        } else {
            assertFalse(result.contains("### Translation has been skipped!"));
        }
    }

    private void assertFormattedContent(List<String> results) {
        for (int i = 0; i < results.size(); i++) {
            assertEquals(EXPECTED_RESULT_NOT_TRANSLATED[i], results.get(i));
        }
    }

    private void assertFormattedChildrenContent(List<String> results) {
        assertEquals("<br>\n### Children of: https://www.test.com", results.get(0));
        assertEquals("### ---> Link to: https://www.subtest.com", results.get(2));
        List<String> subResults = results.subList(3, 5);
        for (int i = 0; i < subResults.size(); i++) {
            assertEquals(EXPECTED_RESULT_NOT_TRANSLATED[i], subResults.get(i).replace("---> ", ""));
        }
    }

    private void assertFormattedBrokenLinks(List<String> results) {
        for (int i = 0; i < results.size(); i++) {
            String expected = EXPECTED_FORMATED_BROKEN_LINK + brokenLinks.get(i);
            assertEquals(expected, results.get(i));
        }
    }
}
