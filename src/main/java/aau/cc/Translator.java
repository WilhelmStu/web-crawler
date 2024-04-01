package aau.cc;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Translator {

    private static final String API_URL_GET_LANGUAGES = "https://microsoft-translator-text.p.rapidapi.com/languages?api-version=3.0&scope=translation";
    private static final String API_URL_TRANSLATE = "https://microsoft-translator-text.p.rapidapi.com/translate?api-version=3.0&textType=plain&profanityAction=NoAction";
    private static final String API_HOST = "microsoft-translator-text.p.rapidapi.com";
    private static final String OBFUSCATED_API_KEY = "6829db37d45msha2b2d16c6ae926fw9p1326d0jsn0d545l9c433b15";
    private static String API_KEY;

    private String defaultTargetLanguage;
    private String defaultSourceLanguage;
    private final OkHttpClient httpClient;

    public Translator(String defaultTargetLanguage, String defaultSourceLanguage) {
        this.defaultTargetLanguage = defaultTargetLanguage;
        this.defaultSourceLanguage = defaultSourceLanguage;
        this.httpClient = new OkHttpClient();
        decryptAPIKey();
    }

    public Translator() {
        this("de", "en");
    }

    public Translator(String defaultTargetLanguage) {
        this(defaultTargetLanguage, "en");
    }

    public String getSingleTranslation(String toTranslate, String targetLanguage) {

        RequestBody body = buildRequestBody(toTranslate);

        Request request = buildBaseRequest(API_URL_TRANSLATE + "&to=" + targetLanguage)
                .post(body)
                .addHeader("content-type", "application/json")
                .build();

        String result = doAPICall(request);
        return getTranslationsFromResponseBody(result).get(0);
    }

    public String getSingleTranslation(String toTranslate) {
        return getSingleTranslation(toTranslate, defaultTargetLanguage);
    }

    public String getAvailableLanguages() {
        Request request = buildBaseRequest(API_URL_GET_LANGUAGES)
                .get()
                .build();

        return doAPICall(request);
    }

    private RequestBody buildRequestBody(String toTranslate) {
        String body = "[{\"Text\": \"" + toTranslate + "\"}]";
        MediaType type = MediaType.parse("application/json");
        return RequestBody.create(body, type);
    }

    private Request.Builder buildBaseRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Accept-Language", "en")
                .addHeader("Accept-Encoding", "UTF-8")
                .addHeader("X-RapidAPI-Key", API_KEY)
                .addHeader("X-RapidAPI-Host", API_HOST);
    }

    private String doAPICall(Request request) {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            return response.body().source().readString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error during request:" + e.getMessage());
            return "Error during translation";
        }
    }

    private List<String> getTranslationsFromResponseBody(String body) {
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
        List<String> results = new ArrayList<>();

        JsonArray translations = JsonParser.parseString(body).getAsJsonArray();
        for (JsonElement element : translations) {
            results.add(element
                    .getAsJsonObject()
                    .get("translations")
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonObject()
                    .get("text")
                    .getAsString());
        }
        return results;
    }

    private static void decryptAPIKey() {
        StringBuilder result = new StringBuilder(OBFUSCATED_API_KEY);
        result.deleteCharAt(3);
        result.deleteCharAt(17);
        result.deleteCharAt(27);
        result.deleteCharAt(38);
        result.deleteCharAt(42);
        API_KEY = result.toString();
    }

    public String getDefaultTargetLanguage() {
        return defaultTargetLanguage;
    }

    public void setDefaultTargetLanguage(String defaultTargetLanguage) {
        this.defaultTargetLanguage = defaultTargetLanguage;
    }

    public String getDefaultSourceLanguage() {
        return defaultSourceLanguage;
    }

    public void setDefaultSourceLanguage(String defaultSourceLanguage) {
        this.defaultSourceLanguage = defaultSourceLanguage;
    }
}
