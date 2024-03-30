package aau.cc;

import okhttp3.*;

import java.io.IOException;

public class Translator {

    private static final String API_URL_GET_LANGUAGES = "https://google-translate1.p.rapidapi.com/language/translate/v2/languages?target=en";
    private static final String API_URL_TRANSLATE = "https://google-translate1.p.rapidapi.com/language/translate/v2";
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

        RequestBody body = new FormBody.Builder()
                .add("q", toTranslate)
                .add("target", targetLanguage)
                .add("source", defaultSourceLanguage)
                .build();

        Request request = buildBaseRequest(API_URL_TRANSLATE)
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .build();

        return doAPICall(request);
    }

    public String getAvailableLanguages() {
        Request request = buildBaseRequest(API_URL_GET_LANGUAGES)
                .get()
                .build();

        return doAPICall(request);
    }

    private Request.Builder buildBaseRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Accept-Encoding", "application/gzip")
                .addHeader("X-RapidAPI-Key", API_KEY)
                .addHeader("X-RapidAPI-Host", "google-translate1.p.rapidapi.com");
    }

    private String doAPICall(Request request) {
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            return response.body().string();
        } catch (IOException e) {
            System.err.println("Error during request:" + e.getMessage());
            e.printStackTrace();
            return "Error during translation";
        }
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
