package aau.cc;

import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;
import aau.cc.model.Language;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MarkdownExporterTest {
    private static final String FILE_NAME = "Test.md";
    private static final String WEBSITE_URL = "https://www.test.com";
    private static final String CHILD_WEBSITE_URL = "https://www.subtest.com";
    private static final String BROKEN_LINK = "https://www.broken.com";
    private static final Heading HEADING_1 = new Heading("Test Überschrift Nummer 1", 1);
    private static final Heading HEADING_2 = new Heading("Test Überschrift Nummer 2", 1);
    private static final Heading HEADING_3 = new Heading("Test Überschrift Nummer 3", 2);
    private static final String EXPECTED_FORMATED_BROKEN_LINK = "### <span style=\"color:gray\"> Broken Link to: </span>";
    private static final String[] EXPECTED_RESULT_TRANSLATED = {
            "# Test Heading Number 1",
            "# Test Heading Number 2",
            "## Test Heading Number 3"};
    private static final String[] EXPECTED_RESULT_NOT_TRANSLATED = {
            "# Test Überschrift Nummer 1",
            "# Test Überschrift Nummer 2",
            "## Test Überschrift Nummer 3"};

    private CrawledWebsite website;
    private MarkdownExporter exporter;
    private List<String> brokenLinks;

    @BeforeEach
    public void setUp() {
        website = new CrawledWebsite(WEBSITE_URL, 2);
        setUpBrokenLinks();
        List<Heading> headings = new ArrayList<>();
        headings.add(HEADING_1);
        headings.add(HEADING_2);
        headings.add(HEADING_3);
        website.setHeadings(headings);
        website.setSource(Language.GERMAN);
        website.setTarget(Language.ENGLISH);
        website.addBrokenLink(BROKEN_LINK);

        CrawledWebsite childSite = new CrawledWebsite(CHILD_WEBSITE_URL, 1);
        childSite.setHeadings(headings);
        childSite.setSource(Language.GERMAN);
        childSite.setTarget(Language.ENGLISH);
        childSite.setBrokenLinks(brokenLinks);
        website.addLinkedWebsite(childSite);

    }

    private void setUpBrokenLinks() {
        brokenLinks = new ArrayList<>();
        brokenLinks.add(BROKEN_LINK);
        brokenLinks.add("https://www.moreBroken.com");
        brokenLinks.add("https://www.veryBroken.net");
    }

    @AfterEach
    public void tearDown() {
        website = null;
        exporter = null;
        brokenLinks = null;
        File file = new File(FILE_NAME);
        if (file.exists()) {
            assertTrue(file.delete());
        }
    }

    @Test
    public void testExportHeaderContent() {
        exporter = new MarkdownExporter(true);
        List<String> result = exporter.getFormattedHeaderContent(website, true);
        assertResultHeader(result, true);
    }

    @Test
    public void testExportHeaderContentTranslated() {
        exporter = new MarkdownExporter(false);
        List<String> result = exporter.getFormattedHeaderContent(website, false);
        assertResultHeader(result, false);
    }

    @Test
    public void testExportMainContent() {
        exporter = new MarkdownExporter(true);
        List<String> results = exporter.getFormattedMainContent(website, 0);
        assertExportedContent(results, true);
    }

    @Test
    public void testExportMainContentTranslated() {
        exporter = new MarkdownExporter(false);
        List<String> results = exporter.getFormattedMainContent(website, 0);
        assertExportedContent(results, false);
    }


    @Test
    public void testExportChildrenContent() {
        exporter = new MarkdownExporter(true);
        List<String> results = exporter.getFormattedSubWebsiteContent(website, website.getDepth());
        assertExportedChildrenContent(results, true);
    }

    @Test
    public void testExportChildrenContentTranslated() {
        exporter = new MarkdownExporter(false);
        List<String> results = exporter.getFormattedSubWebsiteContent(website, website.getDepth());
        assertExportedChildrenContent(results, false);
    }

    @Test
    public void testExportBrokenLinkFromWebsite() {
        exporter = new MarkdownExporter(false);
        List<String> results = exporter.getFormattedBrokenLinks(website.getBrokenLinks());
        assertFormattedBrokenLinks(results);
    }

    @Test
    public void testExportBrokenLinks() {
        exporter = new MarkdownExporter(false);
        List<String> results = exporter.getFormattedBrokenLinks(brokenLinks);
        assertFormattedBrokenLinks(results);
    }

    @Test
    public void testExportToFile() {
        exporter = new MarkdownExporter(true);
        exporter.generateMarkdownFile(FILE_NAME, website);
        assertExportedFile();
    }

    @Test
    public void testExportToFileNoFileExtension() {
        exporter = new MarkdownExporter(true);
        exporter.generateMarkdownFile("Test", website);
        assertExportedFile();
    }

    private void assertResultHeader(List<String> result, boolean translationSkipped) {
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

    private void assertExportedContent(List<String> results, boolean translationSkipped) {
        for (int i = 0; i < results.size(); i++) {
            if (translationSkipped) {
                assertEquals(results.get(i), EXPECTED_RESULT_NOT_TRANSLATED[i]);
            } else {
                assertEquals(results.get(i), EXPECTED_RESULT_TRANSLATED[i]);
            }
        }
    }

    private void assertExportedChildrenContent(List<String> results, boolean translationSkipped) {
        assertEquals("<br>\n\n___", results.get(0));
        assertEquals("\n### Children of: https://www.test.com", results.get(1));
        assertEquals("### Link to: https://www.subtest.com", results.get(3));
        List<String> subResults = results.subList(4, 6);
        for (int i = 0; i < subResults.size(); i++) {
            if (translationSkipped) {
                assertEquals(subResults.get(i), "##" + EXPECTED_RESULT_NOT_TRANSLATED[i]);
            } else {
                assertEquals(subResults.get(i), "##" + EXPECTED_RESULT_TRANSLATED[i]);
            }
        }
    }

    private void assertFormattedBrokenLinks(List<String> results) {
        for (int i = 0; i < results.size(); i++) {
            String expected  = EXPECTED_FORMATED_BROKEN_LINK + brokenLinks.get(i);
            assertEquals(expected, results.get(i));
        }
    }

    private void assertExportedFile() {
        assertTrue(Files.exists(Path.of(FILE_NAME)));
        List<String> result = readResultFile();
        assertTrue(result.size() > 20);
    }

    @NotNull
    private List<String> readResultFile() {
        List<String> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            fail();
        }

        return result;
    }
}
