package aau.cc;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
        String result = Translator.getSingleTranslation("Hello World!", "Fr");
        System.out.println(result);
    }
}
