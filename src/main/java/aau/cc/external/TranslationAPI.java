package aau.cc.external;

import aau.cc.model.Language;

import java.util.List;

public class TranslationAPI {
    public static final String API_URL_GET_LANGUAGES = "https://microsoft-translator-text.p.rapidapi.com/languages?api-version=3.0&scope=translation";
    private static final String API_URL_TRANSLATE = "https://microsoft-translator-text.p.rapidapi.com/translate?api-version=3.0&textType=plain&profanityAction=NoAction";
    public static final String API_HOST = "microsoft-translator-text.p.rapidapi.com";
    private static final String OBFUSCATED_API_KEY = "6829db37d45msha2b2d16c6ae926fw9p1326d0jsn0d545l9c433b15";
    public static final int API_MAX_BATCH_SIZE = 25;
    private static String API_KEY;

    public static String getAPIUrlForTranslation(Language targetLanguage){
        return API_URL_TRANSLATE + "&to=" + targetLanguage.getCode();
    }

    public static String getJsonBodyForTranslation(String toTranslate){
        // Expected json format: [{ "Text": "Line to be translated" }]
        return "[{\"Text\": \"" + toTranslate + "\"}]";
    }

    public static String getJsonBodyForTranslation(List<String> toTranslate) {
        /* Expected json format:
         * [{ "Text": "Line to be translated" },
         * { "Text": "Second Line" },
         * {...}]
         */

        StringBuilder body = new StringBuilder("[");
        for (String text : toTranslate) {
            body.append("{\"Text\": \"")
                    .append(text)
                    .append("\"},");
        }
        body.setLength(body.length() - 1); // remove redundant ',' at the end
        body.append("]");

        return body.toString();
    }

    public static String generateAndGetApiKey() {
        if (API_KEY == null || API_KEY.isEmpty()) {
            API_KEY = decryptAPIKey();
        }
        return API_KEY;
    }

    private static String decryptAPIKey() {
        // The clear API key is not directly stored in code to make misuse slightly harder
        StringBuilder result = new StringBuilder(OBFUSCATED_API_KEY);
        result.deleteCharAt(3);
        result.deleteCharAt(17);
        result.deleteCharAt(27);
        result.deleteCharAt(38);
        result.deleteCharAt(42);
        return result.toString();
    }
}
