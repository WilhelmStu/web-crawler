package aau.cc;

public class Main {
    public static void main(String[] args)  {
        Translator translator = new Translator();
        System.out.println(translator.getAvailableLanguages());
        System.out.println(translator.getSingleTranslation("Hello World!", "fr"));
        System.out.println(translator.getSingleTranslation("Hello World", "de"));

    }
}
