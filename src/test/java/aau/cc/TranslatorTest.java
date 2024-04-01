package aau.cc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class TranslatorTest {
    private static final String[] WORDS_TO_TRANSLATE = {"Hello World", "book", "tree"};
    private static final String[] LANGUAGES = {"de", "fr", "it"};
    private Translator translator;
    private String[] results;

    @BeforeEach
    public void setUp() {
        translator = new Translator();
        results = new String[WORDS_TO_TRANSLATE.length];
    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    public void testSingleTranslationsWithDefaultParams() {
        for (int i = 0; i < WORDS_TO_TRANSLATE.length; i++) {
            results[i] = translator.getSingleTranslation(WORDS_TO_TRANSLATE[i]);
        }
        assertEquals("Hallo Welt", results[0]);
        assertEquals("Buch", results[1]);
        assertEquals("Baum", results[2]);
    }

    @Test
    public void testSingleTranslationsWithTargetLanguage() {
        for (int i = 0; i < WORDS_TO_TRANSLATE.length; i++) {
            results[i] = translator.getSingleTranslation(WORDS_TO_TRANSLATE[i], LANGUAGES[i]);
        }
        assertEquals("Hallo Welt", results[0]);
        assertEquals("livre", results[1]);
        assertEquals("albero", results[2]);
    }

    @Test
    public void testSingleTranslationWithSpecialCharacters() {
        String result = translator.getSingleTranslation("(Hello, World!)");
        assertEquals("(Hallo, Welt!)", result);
    }

    @Test
    public void testSingleTranslationUpdatedTargetLanguage(){
        translator.setDefaultTargetLanguage("it");
        String result = translator.getSingleTranslation("Hello World");
        assertEquals("Salve, mondo", result);
    }

    @Test
    public void testSingleTranslationSecondConstructor(){
        translator = new Translator("it");
        String result = translator.getSingleTranslation("Hello World");
        assertEquals("Salve, mondo", result);
    }

    @Test
    public void testGetAvailableLanguages(){
        String result = translator.getAvailableLanguages();
        assertTrue(result.contains("\"de\":{\"name\":\"German\",\"nativeName\":\"Deutsch\",\"dir\":\"ltr\"}"));
    }

}
