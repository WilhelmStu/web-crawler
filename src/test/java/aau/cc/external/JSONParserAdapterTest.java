package aau.cc.external;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JSONParserAdapterTest {
    /* structure of body to parse:
     * [ {
     *   "detectedLanguage": { "language": "de", "score": 0.99 },
     *   "translations":
     *    [
     *      { "text": "translated text", "to": "en" }
     *    ]
     *   },
     *   {..}
     * ]
     */
    private static final String BODY_TO_PARSE_SINGLE =
            """ 
                [{"detectedLanguage":{"language":"en","score":0.98},
                "translations":[
                    {"text":"Hallo Welt","to":"de"}
                ]}]
                """;

    private static final String BODY_TO_PARSE_EMPTY_JSON =
            """ 
                []
                """;

    private static final String BODY_TO_PARSE_MULTIPLE =
            """
                [{"detectedLanguage":{"language":"en","score":0.98},
                    "translations":[
                    {"text":"Hallo Welt","to":"de"}]},
                 {"detectedLanguage":{"language":"en","score":1.0},
                    "translations":[
                    {"text":"Hallo Welt","to":"de"}]},
                 {"detectedLanguage":{"language":"en","score":1.0},
                    "translations":[
                    {"text":"Hallo Welt","to":"de"}]}
                   ]
                """;
    private static final String EXPECTED_ENTRY = "Hallo Welt";

    @Test
    public void testParseSingleTranslation() {
        List<String> results = JSONParserAdapter.parseTranslationFromString(BODY_TO_PARSE_SINGLE);
        assertResults(results, 1);
    }

    @Test
    public void testParseMultipleTranslations() {
        List<String> results = JSONParserAdapter.parseTranslationFromString(BODY_TO_PARSE_MULTIPLE);
        assertResults(results, 3);

    }

    @Test
    public void testParseInvalidJson() {
        List<String> results = JSONParserAdapter.parseTranslationFromString("Error");
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void testParseEmptyBody() {
        List<String> results = JSONParserAdapter.parseTranslationFromString("");
        assertEquals(Collections.emptyList(), results);
    }

    @Test
    public void testParseEmptyJsonArray() {
        List<String> results = JSONParserAdapter.parseTranslationFromString(BODY_TO_PARSE_EMPTY_JSON);
        assertEquals(Collections.emptyList(), results);
    }

    private void assertResults(List<String> results, int expectedSize){
        assertEquals(expectedSize, results.size());
        for (String result : results) {
            assertEquals(EXPECTED_ENTRY, result);
        }
    }
}
