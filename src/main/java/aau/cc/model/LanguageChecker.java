package aau.cc.model;

public class LanguageChecker {
    public static Language checkLanguage(String language) {
        return switch (language) {
            case "en" -> Language.ENGLISH;
            case "de" -> Language.GERMAN;
            case "fr" -> Language.FRENCH;
            case "es" -> Language.SPANISH;
            case "it" -> Language.ITALIAN;
            default -> null;
        };
    }
}
