package aau.cc;

import aau.cc.model.Language;
import aau.cc.model.LanguageChecker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LanguageCheckerTest {
    @Test
    void testCheckLanguageEnglish() {
        assertEquals(Language.ENGLISH, LanguageChecker.checkLanguage("en"));
    }

    @Test
    void testCheckLanguageGerman() {
        assertEquals(Language.GERMAN, LanguageChecker.checkLanguage("de"));
    }

    @Test
    void testCheckLanguageFrench() {
        assertEquals(Language.FRENCH, LanguageChecker.checkLanguage("fr"));
    }

    @Test
    void testCheckLanguageSpanish() {
        assertEquals(Language.SPANISH, LanguageChecker.checkLanguage("es"));
    }

    @Test
    void testCheckLanguageItalian() {
        assertEquals(Language.ITALIAN, LanguageChecker.checkLanguage("it"));
    }
}
