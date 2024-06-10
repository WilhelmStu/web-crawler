package aau.cc;

import aau.cc.external.HTTPClientAdapter;
import aau.cc.external.JSONParserAdapter;
import aau.cc.external.TranslationAPI;
import aau.cc.model.Heading;
import aau.cc.model.Language;

import java.util.ArrayList;
import java.util.List;

public class Translator {

    private Language defaultTargetLanguage;
    private HTTPClientAdapter httpClientAdapter;

    public Translator(Language defaultTargetLanguage) {
        this.defaultTargetLanguage = defaultTargetLanguage;
        this.httpClientAdapter = new HTTPClientAdapter();
    }

    public Translator() {
        this(Language.ENGLISH);
    }

    public String translateSingleLine(String toTranslate, Language targetLanguage) {
        String body = TranslationAPI.getJsonBodyForTranslation(toTranslate);
        httpClientAdapter.prepareTranslationRequest(body, targetLanguage);
        String result = httpClientAdapter.doAPICall();
        List<String> translations = JSONParserAdapter.parseTranslationFromString(result);
        if (!translations.isEmpty() && !translations.get(0).isEmpty()) return translations.get(0);
        else return "Error during translation request";
    }

    public String translateSingleLine(String toTranslate) {
        return translateSingleLine(toTranslate, defaultTargetLanguage);
    }

    public List<String> translateMultipleLines(List<String> toTranslate, Language targetLanguage) {
        List<String> translations = new ArrayList<>();
        // API for translations is limited to a maximum batch size
        for (int i = 0; i < toTranslate.size(); i += TranslationAPI.API_MAX_BATCH_SIZE) {
            translations.addAll(doBatchRequest(toTranslate, i, targetLanguage));
        }
        return translations;
    }

    public List<String> translateMultipleLines(List<String> toTranslate) {
        return translateMultipleLines(toTranslate, defaultTargetLanguage);
    }

    private List<String> doBatchRequest(List<String> toTranslate, int index, Language targetLanguage) {
        List<String> batch = toTranslate.subList(index, Math.min(index + TranslationAPI.API_MAX_BATCH_SIZE, toTranslate.size()));
        String body = TranslationAPI.getJsonBodyForTranslation(batch);
        httpClientAdapter.prepareTranslationRequest(body, targetLanguage);
        String result = httpClientAdapter.doAPICall();

        return JSONParserAdapter.parseTranslationFromString(result);
    }

    public static void translateHeadingsInPlace(List<Heading> headings, Translator translator){
        List<String> headingsToTranslate = Heading.getHeadingsTextsAsList(headings);
        List<String> translatedHeadings = translator.translateMultipleLines(headingsToTranslate);
        for (int i = 0; i < headings.size(); i++) {
            Heading heading = headings.get(i);
            if(translatedHeadings.isEmpty()){ // Error in translation
                heading.setText(heading.getText() + " (Not translated due to API error)");
            }else {
                heading.setText(translatedHeadings.get(i));
            }
        }
    }

    public String getAvailableLanguages() {
        httpClientAdapter.prepareGetAvailableLanguagesRequest();
        return httpClientAdapter.doAPICall();
    }

    public void setDefaultTargetLanguage(Language defaultTargetLanguage) {
        this.defaultTargetLanguage = defaultTargetLanguage;
    }

    protected void setHttpClientAdapter(HTTPClientAdapter httpClientAdapter) {
        this.httpClientAdapter = httpClientAdapter;
    }
}
