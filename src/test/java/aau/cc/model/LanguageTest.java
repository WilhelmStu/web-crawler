package aau.cc.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LanguageTest {

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {

    }

    @Test
    void testFindLanguageNotNUll(){
        Language language = Language.findLanguage("en");
        assertNotNull(language);
    }

    @Test
    void testFindLanguageNull(){
        Language language = Language.findLanguage("error");
        assertNull(language);
    }

    @Test
    void testFindLanguageCheckCode(){
        Language language = Language.findLanguage(Language.ENGLISH.getCode());
        assertEquals(Language.ENGLISH, language);
    }
}
