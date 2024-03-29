package aau.cc;

import okhttp3.*;

import java.io.IOException;
import java.util.function.Consumer;

public class Translator {

    public static String getSingleTranslation(String toTranslate, String language) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = new FormBody.Builder()
                .add("q", toTranslate)
                .add("target", language)
                .add("source", "en")
                .build();

        Request request = new Request.Builder()
                .url("https://google-translate1.p.rapidapi.com/language/translate/v2")
                .post(body)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("Accept-Encoding", "application/gzip")
                .addHeader("X-RapidAPI-Key", "682db37d45msha2b216c6ae926f9p1326d0jsnd5459c433b15")
                .addHeader("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            assert response.body() != null;
            return response.body().string();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private static Consumer<? super Response> processResults() {

        return null;
    }

    public static String getAvailableLanguages() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://google-translate1.p.rapidapi.com/language/translate/v2/languages?target=en")
                .get()
                .addHeader("Accept-Encoding", "application/gzip")
                .addHeader("X-RapidAPI-Key", "682db37d45msha2b216c6ae926f9p1326d0jsnd5459c433b15")
                .addHeader("X-RapidAPI-Host", "google-translate1.p.rapidapi.com")
                .build();


        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            assert response.body() != null;
            System.out.println(response.body().string());
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return "";
    }
}
