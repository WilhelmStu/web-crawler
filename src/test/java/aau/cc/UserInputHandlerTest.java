package aau.cc;

import aau.cc.model.Language;
import aau.cc.model.UserInput;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class UserInputHandlerTest {
    private static final String WEBSITE_URL = "https://www.test.com";
    private static final String DOMAIN = "test.com";
    private Scanner scanner;

    @BeforeEach
    public void setUp() {
        scanner = Mockito.mock(Scanner.class);
        System.setIn(new ByteArrayInputStream("".getBytes()));
        UserInputHandler.setScanner(scanner);
    }

    @AfterEach
    public void tearDown() {
        scanner = null;
    }

    @Test
    public void testAskUserForUrlsSingle() {
        when(scanner.nextLine())
                .thenReturn(WEBSITE_URL)
                .thenReturn("");
        List<String> result = UserInputHandler.askUserForURLs();
        assertUrlList(result, 1);
    }

    @Test
    public void testAskUserForUrlsMultiple() {
        when(scanner.nextLine())
                .thenReturn(WEBSITE_URL)
                .thenReturn(WEBSITE_URL)
                .thenReturn("");
        List<String> result = UserInputHandler.askUserForURLs();
        assertUrlList(result, 2);
    }

    @Test
    public void testAskUserForUrlsEmpty() {
        when(scanner.nextLine())
                .thenReturn("")
                .thenReturn(WEBSITE_URL)
                .thenReturn("");
        List<String> result = UserInputHandler.askUserForURLs();
        assertUrlList(result, 1);
    }

    @Test
    public void testAskUserForCrawlingDepth() {
        when(scanner.nextInt())
                .thenReturn(2);
        assertEquals(2, UserInputHandler.askUserForCrawlingDepth());
    }

    @Test
    public void testAskUserForCrawlingDepthInvalid() {
        when(scanner.nextInt())
                .thenReturn(-1)
                .thenReturn(2);
        assertEquals(2, UserInputHandler.askUserForCrawlingDepth());
    }

    @Test
    public void testAskUserForCrawlingDepthError() {
        when(scanner.nextInt())
                .thenThrow(InputMismatchException.class)
                .thenReturn(2);
        assertEquals(2, UserInputHandler.askUserForCrawlingDepth());
    }

    @Test
    public void testAskUserForTargetLanguage() {
        when(scanner.nextLine())
                .thenReturn("de");
        assertEquals(Language.GERMAN, UserInputHandler.askUserForTargetLanguage());
    }

    @Test
    public void testAskUserForTargetLanguageInvalid() {
        when(scanner.nextLine())
                .thenReturn("xx")
                .thenReturn("de");
        assertEquals(Language.GERMAN, UserInputHandler.askUserForTargetLanguage());
    }

    @Test
    public void testAskUserForTargetLanguageNone() {
        when(scanner.nextLine())
                .thenReturn("none");
        assertNull(UserInputHandler.askUserForTargetLanguage());
    }

    @Test
    public void testAskUserForTargetLanguageEmpty() {
        when(scanner.nextLine())
                .thenReturn("");
        assertNull(UserInputHandler.askUserForTargetLanguage());
    }

    @Test
    public void testAskUserForDomainsToCrawl() {
        when(scanner.nextLine())
                .thenReturn(DOMAIN)
                .thenReturn("");
        List<String> domains = UserInputHandler.askUserForDomainsToBeCrawled();
        assertDomainList(domains, 1);
    }

    @Test
    public void testAskUserForDomainsToCrawlMultiple() {
        when(scanner.nextLine())
                .thenReturn(DOMAIN)
                .thenReturn(DOMAIN)
                .thenReturn("");
        List<String> domains = UserInputHandler.askUserForDomainsToBeCrawled();
        assertDomainList(domains, 2);
    }

    @Test
    public void testAskUserForDomainsToCrawlNone() {
        when(scanner.nextLine())
                .thenReturn("");
        List<String> domains = UserInputHandler.askUserForDomainsToBeCrawled();
        assertTrue(domains.isEmpty());
    }

    @Test
    public void testAskUserForInput() {
        prepareScannerForUserInput();
        UserInput userInput = UserInputHandler.askUserForInput();
        assertUserInput(userInput);
    }

    private void assertUrlList(List<String> urls, int size) {
        for (int i = 0; i < size; i++) {
            assertEquals(WEBSITE_URL, urls.get(i));
        }
    }

    private void assertDomainList(List<String> domains, int size) {
        for (int i = 0; i < size; i++) {
            assertEquals(DOMAIN, domains.get(i));
        }
    }

    private void prepareScannerForUserInput() {
        when(scanner.nextLine())
                .thenReturn(WEBSITE_URL)
                .thenReturn("")
                .thenReturn("")
                .thenReturn("en")
                .thenReturn(DOMAIN)
                .thenReturn("");
        when(scanner.nextInt())
                .thenReturn(2);
    }

    private void assertUserInput(UserInput userInput) {
        assertNotNull(userInput);
        assertUrlList(userInput.getUrls(), 1);
        assertEquals(Language.ENGLISH, userInput.getTargetLanguage());
        assertEquals(2, userInput.getDepth());
        assertDomainList(userInput.getDomains(), 1);
    }
}
