package aau.cc.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserInputTest {

    UserInput userInput;
    @BeforeEach
    public void setUp() {
        userInput = new UserInput();
    }

    @AfterEach
    public void tearDown() {
        userInput = null;
    }

    @Test
    public void testIsSkipTranslation() {
        userInput.setTargetLanguage(null);
        assertTrue(userInput.isSkipTranslation());
    }

    @Test
    public void testIsNotSkipTranslation() {
        assertFalse(userInput.isSkipTranslation());
    }
}
