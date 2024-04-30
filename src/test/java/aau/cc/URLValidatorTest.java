package aau.cc;

import aau.cc.model.Language;
import aau.cc.model.LanguageChecker;
import aau.cc.model.URLValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class URLValidatorTest {
    @Test
    void testValidatorCheckFalse() {
        assertFalse(URLValidator.checkIfValidUrl("www.google.at"));
    }
    @Test
    void testValidatorCheckTrue() {
        assertTrue(URLValidator.checkIfValidUrl("http://www.google.at"));
    }
    @Test
    void testValidatorCheckFalseTypo() {
        assertFalse(URLValidator.checkIfValidUrl("http:/google.at"));
    }
    @Test
    void testValidatorCheckFalseWithoutHTTPandWWW() {
        assertFalse(URLValidator.checkIfValidUrl("google.at"));
    }
}
