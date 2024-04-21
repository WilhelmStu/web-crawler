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
    static final String fileName = "Test.md";
    static final String websiteUrl = "https://www.test.com";
    static final String childWebsiteUrl = "https://www.child.com";
    static final Heading heading1 = new Heading("Test Überschrift Nummer 1", 1);
    static final Heading heading2 = new Heading("Test Überschrift Nummer 2", 1);
    static final Heading heading3 = new Heading("Test Überschrift Nummer 3", 2);
    static final String[] expectedResultTranslated = {
            "# Test Heading Number 1",
            "# Test Heading Number 2",
            "## Test Heading Number 3"};
    static final String[] expectedResultNotTranslated = {
            "# Test Überschrift Nummer 1",
            "# Test Überschrift Nummer 2",
            "## Test Überschrift Nummer 3"};

    CrawledWebsite website;
    List<Heading> headings;
    MarkdownExporter exporter;


    @BeforeEach
    public void setUp() {
        website = new CrawledWebsite(websiteUrl, 2);
        headings = new ArrayList<>();
        headings.add(heading1);
        headings.add(heading2);
        headings.add(heading3);
        website.setHeadings(headings);
        website.setSource(Language.GERMAN);
        website.setTarget(Language.ENGLISH);

        CrawledWebsite childSite = new CrawledWebsite(childWebsiteUrl, 1);
        childSite.setHeadings(headings);
        childSite.setSource(Language.GERMAN);
        childSite.setTarget(Language.ENGLISH);
        website.addLinkedWebsite(childSite);
    }

    @AfterEach
    public void tearDown() {
        website = null;
        exporter = null;
        File file = new File(fileName);
        if (file.exists()) {
            assertTrue(file.delete());
        }
    }

    @Test
    public void testExportHeaderContent() {
        exporter = new MarkdownExporter(true);
        List<String> result = exporter.getFormattedHeaderContent(website, true);
        assertEquals("# Crawled Website: <a>https://www.test.com</a>", result.get(0));
        assertEquals("### Depth: 2", result.get(1));
        assertEquals("### Source language: German", result.get(2));
        assertEquals("### Target language: English", result.get(3));
        assertEquals("### Translation has been skipped!", result.get(4));
    }

    @Test
    public void testExportHeaderContentTranslation() {
        exporter = new MarkdownExporter(false);
        List<String> result = exporter.getFormattedHeaderContent(website, false);
        assertFalse(result.contains("### Translation has been skipped!"));
    }

    @Test
    public void testExportMainContent() {
        exporter = new MarkdownExporter(true);
        List<String> results = exporter.getFormattedMainContent(website, 0);
        for (int i = 0; i < results.size(); i++) {
            assertEquals(results.get(i), expectedResultNotTranslated[i]);
            System.out.println(results.get(i));
        }
    }

    @Test
    public void testExportMainContentTranslate() {
        exporter = new MarkdownExporter(false);
        List<String> results = exporter.getFormattedMainContent(website, 0);
        for (int i = 0; i < results.size(); i++) {
            assertEquals(results.get(i), expectedResultTranslated[i]);
            System.out.println(results.get(i));
        }
    }

    @Test
    public void testExportChildren() {
        exporter = new MarkdownExporter(true);
        List<String> results = exporter.getFormattedSubWebsiteContent(website, website.getDepth());
        assertEquals("<br>\n\n___", results.get(0));
        assertEquals("\n### Children of: https://www.test.com", results.get(1));
        assertEquals("### Link to: https://www.child.com", results.get(3));
        List<String> subResults = results.subList(4, 6);
        for (int i = 0; i < subResults.size(); i++) {
            assertEquals(subResults.get(i), "##" + expectedResultNotTranslated[i]);
        }
    }

    @Test
    public void testExportChildrenTranslated() {
        exporter = new MarkdownExporter(false);
        List<String> results = exporter.getFormattedSubWebsiteContent(website, website.getDepth());
        assertEquals("<br>\n\n___", results.get(0));
        assertEquals("\n### Children of: https://www.test.com", results.get(1));
        assertEquals("### Link to: https://www.child.com", results.get(3));
        List<String> subResults = results.subList(4, 6);
        for (int i = 0; i < subResults.size(); i++) {
            assertEquals(subResults.get(i), "##" + expectedResultTranslated[i]);
        }
    }

    @Test
    public void testExportToFile() {
        exporter = new MarkdownExporter(true);
        exporter.generateContentAndExportToFile(fileName, website);
        assertTrue(Files.exists(Path.of(fileName)));
        List<String> result = readResultFile();
        assertTrue(result.size() > 20);
    }

    @Test
    public void testExportToFileNoFileExtension() {
        exporter = new MarkdownExporter(true);
        exporter.generateContentAndExportToFile("Test", website);
        assertTrue(Files.exists(Path.of(fileName)));
    }

    @NotNull
    private List<String> readResultFile() {
        List<String> result = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            fail();
        }
        assertFalse(result.isEmpty());
        return result;
    }
}
