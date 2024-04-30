package aau.cc;


import aau.cc.model.Language;
import aau.cc.model.LanguageChecker;
import aau.cc.model.URLValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    public static void main(String[] args)  {
        //Translator translator = new Translator();
        //System.out.println(translator.getAvailableLanguages());
        System.out.println("Enter your URL like: http://www.example.com");
        int depth = 0;
        String url = "";
        Language sourceLanguage;
        Language targetLanguage;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            url = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (URLValidator.checkIfValidUrl(url)) {

            System.out.println("Enter your targetLanguage");
            try {
                targetLanguage = LanguageChecker.checkLanguage(reader.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Enter your sourceLanguage");

            try {
                sourceLanguage = LanguageChecker.checkLanguage(reader.readLine());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Enter your depth");
            try {
                depth = Integer.parseInt(reader.readLine());
            } catch (Exception e) {
                System.out.println("Enter a valid number");
            }
            System.out.println(url + " " + " " + targetLanguage + " " + depth);
            Crawler crawler = new Crawler(url, sourceLanguage, targetLanguage, depth);
            crawler.mainCrawler(url, 0);
        }
        else
        {
            System.out.println("Wrong URL type");
        }
        //MarkdownExporter exporter = new MarkdownExporter(true);
        //List<String> result = exporter.getFormattedHeaderContent(website, true);

    }

}
