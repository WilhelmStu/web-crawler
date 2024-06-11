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
    private static final Path path = Path.of(FILE_NAME);
    private static final String WEBSITE_URL = "https://www.test.com";
    private static final Heading HEADING_1 = new Heading("Test Überschrift Nummer 1", 1);

    private List<CrawledWebsite> websites;
    private CrawledWebsite website;
    private MarkdownExporter exporter;

    @BeforeEach
    public void setUp() {
        MarkdownFormatter formatter = new MarkdownFormatter(true, 1);
        exporter = new MarkdownExporter(formatter);
        setupWebsite();
    }

    private void setupWebsite(){
        website = getWebsite();
        websites = new ArrayList<>();
        websites.add(website);
    }

    private CrawledWebsite getWebsite() {
        CrawledWebsite website = new CrawledWebsite(MarkdownExporterTest.WEBSITE_URL, 1);
        List<Heading> headings = getHeadingList();
        website.setHeadings(headings);
        website.setSource(Language.GERMAN);
        website.setTarget(Language.ENGLISH);
        return website;
    }

    private List<Heading> getHeadingList() {
        List<Heading> headings = new ArrayList<>();
        headings.add(HEADING_1);
        return headings;
    }

    @AfterEach
    public void tearDown() {
        website = null;
        exporter = null;
        File file = new File(FILE_NAME);
        if (file.exists()) {
            assertTrue(file.delete());
        }
    }

    @Test
    public void testExportToFile() {
        exporter.writeContentToMarkdownFile(FILE_NAME, websites);
        assertExportedFile();
    }

    @Test
    public void testExportToFileNoFileExtension() {
        exporter.writeContentToMarkdownFile("Test", websites);
        assertExportedFile();
    }

    @Test
    public void testDeleteMarkdownFileIfExists() {
        exporter.writeContentToMarkdownFile(FILE_NAME, websites);
        assertTrue(Files.exists(path));
        exporter.deleteMarkdownFileIfExists(FILE_NAME);
        assertFalse(Files.exists(path));
    }

    @Test
    public void testDeleteMarkdownFileNotExists() {
        exporter.deleteMarkdownFileIfExists(FILE_NAME);
        assertFalse(Files.exists(Path.of(FILE_NAME)));
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
