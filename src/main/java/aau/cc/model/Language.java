package aau.cc.model;

public enum Language {
    ENGLISH("English", "en"),

    GERMAN("German", "de"),

    FRENCH("French", "fr"),
    ITALIAN("Italian", "it"),
    SPANISH("Spanish", "es");

    private final String displayName;
    private final String code;

    Language(String displayName, String code) {
        this.displayName = displayName;
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getCode() {
        return code;
    }
}
