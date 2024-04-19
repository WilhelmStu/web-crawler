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
        exporter.exportToMdFile(fileName, website);
        List<String> result = readResultFile();

        assertEquals("# Crawled Website: <a>https://www.test.com</a>", result.get(0));
        assertEquals("### Depth: 2", result.get(1));
        assertEquals("### Source language: German", result.get(2));
        assertEquals("### Target language: English", result.get(3));
        assertEquals("### Translation has been skipped!", result.get(4));
    }

    @Test
    public void testExportTranslation() {
        exporter = new MarkdownExporter(false);
        exporter.exportToMdFile(fileName, website);
        List<String> result = readResultFile();
        assertNotEquals("### Translation has been skipped!\n", result.get(4));
        List<String> subResults = result.subList(6, 8);
        for (int i = 0; i < subResults.size(); i++) {
            assertEquals(subResults.get(i), expectedResultTranslated[i]);
        }
    }

    @Test
    public void testExportNoTranslation() {
        exporter = new MarkdownExporter(true);
        exporter.exportToMdFile(fileName, website);
        List<String> result = readResultFile();
        assertEquals("### Translation has been skipped!", result.get(4));
        List<String> subResults = result.subList(7, 9);
        for (int i = 0; i < subResults.size(); i++) {
            assertEquals(subResults.get(i), expectedResultNotTranslated[i]);
        }
    }

    @Test
    public void testExportTranslationChildren() {
        exporter = new MarkdownExporter(false);
        exporter.exportToMdFile(fileName, website);
        List<String> result = readResultFile();
        assertNotEquals("### Translation has been skipped!", result.get(4));
        assertEquals("## Link to: https://www.child.com", result.get(13));
        List<String> subResults = result.subList(14, 16);
        for (int i = 0; i < subResults.size(); i++) {
            assertEquals(subResults.get(i), expectedResultTranslated[i]);
        }
    }

    @Test
    public void testExportNoTranslationChildren() {
        exporter = new MarkdownExporter(true);
        exporter.exportToMdFile(fileName, website);
        List<String> result = readResultFile();
        assertEquals("### Translation has been skipped!", result.get(4));
        assertEquals("## Link to: https://www.child.com", result.get(14));
        List<String> subResults = result.subList(15, 17);
        for (int i = 0; i < subResults.size(); i++) {
            assertEquals(subResults.get(i), expectedResultNotTranslated[i]);
        }
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
