package aau.cc;

import aau.cc.model.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TranslatorTest {
    private static final String[] WORDS_TO_TRANSLATE = {"Hello World", "book", "tree"};
    private static final Language[] LANGUAGES = {Language.GERMAN, Language.FRENCH, Language.ITALIAN};
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
    public void testSingleTranslation() {
        for (int i = 0; i < WORDS_TO_TRANSLATE.length; i++) {
            results[i] = translator.getSingleTranslation(WORDS_TO_TRANSLATE[i]);
        }
        assertEquals("Hallo Welt", results[0]);
        assertEquals("Buch", results[1]);
        assertEquals("Baum", results[2]);
    }

    @Test
    public void testSingleTranslationWithTargetLanguage() {
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
        translator.setDefaultTargetLanguage(Language.ITALIAN);
        String result = translator.getSingleTranslation("Hello World");
        assertEquals("Salve, mondo", result);
    }

    @Test
    public void testSingleTranslationSecondConstructor(){
        translator = new Translator(Language.ITALIAN);
        String result = translator.getSingleTranslation("Hello World");
        assertEquals("Salve, mondo", result);
    }

    @Test
    public void testMultipleTranslation(){
        List<String> result = translator.getMultipleTranslations(Arrays.stream(WORDS_TO_TRANSLATE).toList());
        assertEquals("Hallo Welt", result.get(0));
        assertEquals("Buch", result.get(1));
        assertEquals("Baum", result.get(2));
    }

    @Test
    public void testMultipleTranslationWithTargetLanguage(){
        List<String> result = translator.getMultipleTranslations(Arrays.stream(WORDS_TO_TRANSLATE).toList(), Language.GERMAN);
        assertEquals("Hallo Welt", result.get(0));
        assertEquals("Buch", result.get(1));
        assertEquals("Baum", result.get(2));
    }

    @Test
    public void testGetAvailableLanguages(){
        String result = translator.getAvailableLanguages();
        assertTrue(result.contains("\"de\":{\"name\":\"German\",\"nativeName\":\"Deutsch\",\"dir\":\"ltr\"}"));
    }

}
