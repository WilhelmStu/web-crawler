package aau.cc;

import aau.cc.model.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TranslatorTest {
    private static final String[] WORDS_TO_TRANSLATE = {"Hello World", "book", "tree"};
    private static final String[] EXPECTED_RESULTS1 = {"Hallo Welt", "Buch", "Baum"};
    private static final String[] EXPECTED_RESULTS2 = {"Hallo Welt", "livre", "albero"};
    private static final Language[] LANGUAGES = {Language.GERMAN, Language.FRENCH, Language.ITALIAN};
    private Translator translator;
    private List<String> results;

    @BeforeEach
    public void setUp() {
        translator = new Translator();
        results = new ArrayList<>();
    }

    @AfterEach
    public void tearDown() {
        translator = null;
        results = null;
    }

    @Test
    public void testSingleTranslation() {
        for (String s : WORDS_TO_TRANSLATE) {
            results.add(translator.getSingleTranslation(s));
        }
        assertResults1(results);
    }

    @Test
    public void testSingleTranslationWithTargetLanguage() {
        for (int i = 0; i < WORDS_TO_TRANSLATE.length; i++) {
            results.add(translator.getSingleTranslation(WORDS_TO_TRANSLATE[i], LANGUAGES[i]));
        }
        assertResults2(results);
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
    public void testMultipleTranslations(){
        results = translator.getMultipleTranslations(Arrays.stream(WORDS_TO_TRANSLATE).toList());
        assertResults1(results);
    }

    @Test
    public void testMultipleTranslationsWithTargetLanguage(){
        List<String> result = translator.getMultipleTranslations(Arrays.stream(WORDS_TO_TRANSLATE).toList(), Language.GERMAN);
        assertResults1(result);
    }

    @Test
    public void testGetAvailableLanguages(){
        String result = translator.getAvailableLanguages();
        assertTrue(result.contains("\"de\":{\"name\":\"German\",\"nativeName\":\"Deutsch\",\"dir\":\"ltr\"}"));
    }

    private void assertResults1(List<String> results){
        for (int i = 0; i < EXPECTED_RESULTS1.length; i++) {
            assertEquals(EXPECTED_RESULTS1[i], results.get(i));
        }
    }

    private void assertResults2(List<String> results){
        for (int i = 0; i < EXPECTED_RESULTS2.length; i++) {
            assertEquals(EXPECTED_RESULTS2[i], results.get(i));
        }
    }

}
