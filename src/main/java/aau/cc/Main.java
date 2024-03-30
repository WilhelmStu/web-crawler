package aau.cc;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        Translator translator = new Translator();
        System.out.println(translator.getAvailableLanguages());
        System.out.println(translator.getSingleTranslation("Hello World!", "fr"));
    }
}
