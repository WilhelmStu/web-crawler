package aau.cc.external;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JSONParserAdapter {

    public static List<String> parseTranslationFromString(String body) {
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
        if (Objects.equals(body, "")) return results;

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
}
