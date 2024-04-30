package aau.cc;

import aau.cc.model.Language;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    void testFindLanguageNotNUll(){
        Language language = Main.findLanguage("en");
        assertNotNull(language);
    }

    @Test
    void testFindLanguageNull(){
        Language language = Main.findLanguage("error");
        assertNull(language);
    }

    @Test
    void testFindLanguageCheckCode(){
        Language language = Main.findLanguage(Language.ENGLISH.getCode());
        assertEquals(Language.ENGLISH, language);
    }
}
