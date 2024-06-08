package aau.cc.external;

import aau.cc.model.Language;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


public class HTTPClientAdapter {
    private final OkHttpClient httpClient;
    private Request nextRequest;

    public HTTPClientAdapter() {
        this.httpClient = new OkHttpClient();
    }

    public void prepareTranslationRequest(String body, Language targetLanguage) {
        RequestBody requestBody = buildRequestBody(body);
        nextRequest = buildTranslationRequest(requestBody, targetLanguage);
    }

    public void prepareGetAvailableLanguagesRequest() {
        nextRequest = buildBaseRequest(TranslationAPI.API_URL_GET_LANGUAGES).get().build();
    }

    public String doAPICall() {
        if (nextRequest == null) return "";
        try (Response response = httpClient.newCall(nextRequest).execute()) {
            return checkResponseAndGetContent(response);
        } catch (IOException e) {
            System.err.println("Error during request:" + e.getMessage());
            return ""; // translation API is not 100% reliable
        }
    }

    protected RequestBody buildRequestBody(String body) {
        MediaType type = MediaType.parse("application/json");
        return RequestBody.create(body, type);
    }

    protected Request buildTranslationRequest(RequestBody body, Language targetLanguage) {
        return buildBaseRequest(TranslationAPI.getAPIUrlForTranslation(targetLanguage))
                .post(body)
                .addHeader("content-type", "application/json")
                .build();
    }

    protected Request.Builder buildBaseRequest(String url) {
        return new Request.Builder()
                .url(url)
                .addHeader("Accept-Language", "en")
                .addHeader("Accept-Encoding", "UTF-8")
                .addHeader("X-RapidAPI-Key", TranslationAPI.generateAndGetApiKey())
                .addHeader("X-RapidAPI-Host", TranslationAPI.API_HOST);
    }

    protected String checkResponseAndGetContent(Response response) throws IOException {
        if (!response.isSuccessful()){
            assert response.body() != null;
            System.out.println("Result is" + response.body().string());
            throw new IOException("Unexpected response code " + response);
        }
        assert response.body() != null;
        return response.body().source().readString(StandardCharsets.UTF_8);
    }
}
