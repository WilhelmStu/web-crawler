package aau.cc;

import aau.cc.model.Language;

public class Main {
    public static void main(String[] args)  {
        Translator translator = new Translator();
        System.out.println(translator.getAvailableLanguages());
        System.out.println(translator.getSingleTranslation("Hello World!", Language.FRENCH));
        System.out.println(translator.getSingleTranslation("Hello World", Language.GERMAN));

    }
}
