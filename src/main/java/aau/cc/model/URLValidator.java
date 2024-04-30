package aau.cc.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLValidator {

    //help of AI
    private static final String URL_REGEX =
            "^(http|https)://[a-zA-Z0-9]+([\\-\\.]{1}[a-zA-Z0-9]+)\\.[a-zA-Z]{2,5}(:[0-9]{1,5})?(\\/\\S)?$";

    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);

    public static boolean checkIfValidUrl(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        return matcher.matches();
    }
}
