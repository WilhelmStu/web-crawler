package aau.cc.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CrawledWebsiteTest {

    private static final String WEBSITE_URL = "https://www.test.com";
    private static final Heading HEADING_1 = new Heading("Test Überschrift Nummer 1");
    private static final String BROKEN_LINK = "https://www.broken.com";

    private CrawledWebsite website;

    @BeforeEach
    public void setUp() {
        website = new CrawledWebsite(WEBSITE_URL);
    }

    @AfterEach
    public void tearDown() {
        website = null;
    }

    @Test
    void testSecondConstructor() {
        website = new CrawledWebsite(WEBSITE_URL, 3);
        assertEquals(3, website.getDepth());
        assertEquals(WEBSITE_URL, website.getUrl());
    }

    @Test
    public void testAddSingleHeading() {
        website.addHeading(HEADING_1);
        assertHeadingsListAndSize(website.getHeadings(), 1);
    }

    @Test
    public void testAddMultipleHeadings() {
        for (int i = 0; i < 5; i++) {
            website.addHeading(HEADING_1);
        }
        assertHeadingsListAndSize(website.getHeadings(), 5);
    }

    @Test
    public void testGetHeadingsTextsAsList() {
        website.addHeading(HEADING_1);
        List<String> result =  website.getHeadingsTextsAsList();
        assertEquals(1, result.size());
        assertEquals(HEADING_1.getText(), result.get(0));
    }

    @Test
    public void testGetHeadingsDepths() {
        website.addHeading(HEADING_1);
        int[] result = website.getHeadingsDepths();
        assertEquals(1, result[0]);
    }

    @Test
    void testAddBrokenLink() {
        website.addBrokenLink(BROKEN_LINK);
        assertEquals(1, website.getBrokenLinks().size());
        assertEquals(BROKEN_LINK, website.getBrokenLinks().get(0));
    }

    @Test
    void testNotBrokenWebsite() {
        assertFalse(website.hasBrokenUrl());
    }

    @Test
    void testBrokenWebsite() {
        website = new CrawledWebsite(WEBSITE_URL, true);
        assertTrue(website.hasBrokenUrl());
    }

    @Test
    void testBrokenWebsiteCollectionsEmpty() {
        website = new CrawledWebsite(WEBSITE_URL, true);
        assertTrue(website.getHeadingsTextsAsList().isEmpty());
        assertEquals(0, website.getHeadingsDepths().length);
    }


    private void assertHeadingsListAndSize(List<Heading> headings, int expectedSize){
        assertEquals(expectedSize, headings.size());
        for (Heading heading : headings) {
            assertEquals(HEADING_1, heading);
        }
    }
}
