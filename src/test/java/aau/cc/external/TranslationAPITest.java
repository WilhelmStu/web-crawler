package aau.cc.external;

import aau.cc.model.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TranslationAPITest {
    private final static String EXPECTED_SINGLE_LINE = "[{\"Text\": \"Test line\"}]";
    private final static String EXPECTED_MULTIPLE_LINES = "[{\"Text\": \"Test line\"},{\"Text\": \"Test line\"},{\"Text\": \"Test line\"}]";
    private final static String TEST_LINE = "Test line";
    private final static String EXPECTED_URL = TranslationAPI.API_URL_TRANSLATE + "&to=en";
    private List<String> testList;

    @BeforeEach
    void setUp() {
        testList = new ArrayList<>(List.of(new String[]{TEST_LINE, TEST_LINE, TEST_LINE}));
    }

    @AfterEach
    void tearDown() {
        testList = null;
    }

    @Test
    void testGetApiUrlForTranslation() {
        String url = TranslationAPI.getAPIUrlForTranslation(Language.ENGLISH);
        assertEquals(EXPECTED_URL, url);
    }

    @Test
    void testGetJsonBodyForTranslation() {
        String body = TranslationAPI.getJsonBodyForTranslation(TEST_LINE);
        assertEquals(EXPECTED_SINGLE_LINE, body);
    }

    @Test
    void testGetJsonBodyForTranslationMultipleLines() {
        String body = TranslationAPI.getJsonBodyForTranslation(testList);
        assertEquals(EXPECTED_MULTIPLE_LINES, body);
    }

    @Test
    void testGetJsonBodyForTranslationEmpty() {
        String body = TranslationAPI.getJsonBodyForTranslation(Collections.emptyList());
        assertEquals("[]", body);
    }

    @Test
    void testGetApiKey() {
        TranslationAPI.setApiKey(null);
        String key = TranslationAPI.generateAndGetApiKey();
        assertEquals(50, key.length());
    }

    @Test
    void testGetApiKeySet() {
        TranslationAPI.setApiKey("Test");
        String key = TranslationAPI.generateAndGetApiKey();
        assertEquals(4, key.length());
    }
}
