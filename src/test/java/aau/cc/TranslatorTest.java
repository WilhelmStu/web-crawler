package aau.cc;

import aau.cc.external.HTTPClientAdapter;
import aau.cc.model.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class TranslatorTest {
    private static final String RESPONSE = "[{\"detectedLanguage\":{\"language\":\"en\",\"score\":0.98},\"translations\":[{\"text\":\"Salve, mondo\",\"to\":\"it\"}]}]";
    private static final String RESPONSE2 = "[{\"detectedLanguage\":{\"language\":\"en\",\"score\":1.0},\"translations\":[{\"text\":\"(Hallo, Welt!)\",\"to\":\"de\"}]}]";
    private static final String RESPONSE3 = "[{\"detectedLanguage\":{\"language\":\"en\",\"score\":1.0},\"translations\":[{\"text\":\"\",\"to\":\"de\"}]}]";
    private static final String RESPONSE4 = """
            [{"detectedLanguage":{"language":"en","score":0.98},"translations":[{"text":"Hallo Welt","to":"de"}]},
            {"detectedLanguage":{"language":"en","score":1.0},"translations":[{"text":"Buch","to":"de"}]},
            {"detectedLanguage":{"language":"en","score":1.0},"translations":[{"text":"Baum","to":"de"}]}]
            """;
    private static final String[] WORDS_TO_TRANSLATE = {"Hello World", "book", "tree"};
    private static final String[] EXPECTED_RESULTS1 = {"Hallo Welt", "Buch", "Baum"};
    private static final String[] EXPECTED_RESULTS2 = {"Salve, mondo", "Salve, mondo", "Salve, mondo"};
    private static final String EXPECTED_ERROR = "Error during translation request";
    private static final Language[] LANGUAGES = {Language.GERMAN, Language.FRENCH, Language.ITALIAN};
    private Translator translator;
    private HTTPClientAdapter mockClientAdapter;
    private List<String> results;

    @BeforeEach
    public void setUp() {
        translator = new Translator();
        results = new ArrayList<>();
        mockClientAdapter = Mockito.mock(HTTPClientAdapter.class);
        translator.setHttpClientAdapter(mockClientAdapter);
        when(mockClientAdapter.doAPICall()).thenReturn(RESPONSE);
    }

    @AfterEach
    public void tearDown() {
        translator = null;
        results = null;
        mockClientAdapter = null;
    }

    @Test
    public void testSingleTranslation() {
        for (String s : WORDS_TO_TRANSLATE) {
            results.add(translator.translateSingleLine(s));
        }
        assertResults2(results);
    }

    @Test
    public void testSingleTranslationWithTargetLanguage() {
        for (int i = 0; i < WORDS_TO_TRANSLATE.length; i++) {
            results.add(translator.translateSingleLine(WORDS_TO_TRANSLATE[i], LANGUAGES[i]));
        }
        assertResults2(results);
    }

    @Test
    public void testSingleTranslationWithSpecialCharacters() {
        when(mockClientAdapter.doAPICall()).thenReturn(RESPONSE2);
        String result = translator.translateSingleLine("(Hello, World!)");
        assertEquals("(Hallo, Welt!)", result);
    }

    @Test
    public void testSingleTranslationUpdatedTargetLanguage() {
        translator.setDefaultTargetLanguage(Language.ITALIAN);
        String result = translator.translateSingleLine("Hello World");
        assertEquals("Salve, mondo", result);
    }

    @Test
    public void testSingleTranslationSecondConstructor() {
        translator = new Translator(Language.ITALIAN);
        translator.setHttpClientAdapter(mockClientAdapter);
        String result = translator.translateSingleLine("Hello World");
        assertEquals("Salve, mondo", result);
    }

    @Test
    public void testMultipleTranslations() {
        when(mockClientAdapter.doAPICall()).thenReturn(RESPONSE4);
        results = translator.translateMultipleLines(Arrays.stream(WORDS_TO_TRANSLATE).toList());
        assertResults1(results);
    }

    @Test
    public void testMultipleTranslationsWithTargetLanguage() {
        when(mockClientAdapter.doAPICall()).thenReturn(RESPONSE4);
        List<String> result = translator.translateMultipleLines(Arrays.stream(WORDS_TO_TRANSLATE).toList(), Language.GERMAN);
        assertResults1(result);
    }

    @Test
    public void testGetAvailableLanguages() {
        String result = translator.getAvailableLanguages();
        assertTrue(result.contains(RESPONSE));
    }

    @Test
    public void testTranslateSingleLineEmpty() {
        when(mockClientAdapter.doAPICall()).thenReturn(RESPONSE3);
        String result = translator.translateSingleLine(WORDS_TO_TRANSLATE[0]);
        assertEquals(EXPECTED_ERROR, result);
    }

    @Test
    public void testTranslateSingleEmptyResponse() {
        when(mockClientAdapter.doAPICall()).thenReturn("");
        String result = translator.translateSingleLine(WORDS_TO_TRANSLATE[0]);
        assertEquals(EXPECTED_ERROR, result);
    }


    private void assertResults1(List<String> results) {
        for (int i = 0; i < EXPECTED_RESULTS1.length; i++) {
            assertEquals(EXPECTED_RESULTS1[i], results.get(i));
        }
    }

    private void assertResults2(List<String> results) {
        for (int i = 0; i < EXPECTED_RESULTS2.length; i++) {
            assertEquals(EXPECTED_RESULTS2[i], results.get(i));
        }
    }

}
