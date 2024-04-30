package aau.cc;


import aau.cc.model.CrawledWebsite;
import aau.cc.model.Language;

import java.util.Scanner;

public class Main {
    private static String url;
    private static int depth;
    private static String[] domains;
    private static Language targetLanguage;


    public static void main(String[] args)  {
        getUserInput();
        CrawledWebsite website = new CrawledWebsite(url, depth);
        website.setSource(Language.GERMAN);
        website.setTarget(targetLanguage==null?Language.ENGLISH:targetLanguage);
        WebCrawler.crawlWebsite(website ,domains);
        MarkdownExporter exporter = new MarkdownExporter(targetLanguage == null);
        exporter.generateMarkdownFile("test123.md", website);
    }

    private static void getUserInput(){
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter URL to be crawled: ");
        url = sc.nextLine();

        System.out.print("Enter the depth of websites to crawl: ");
        depth = sc.nextInt();
        sc.nextLine();

        do {
            System.out.print("Enter the target language code (e.g., en, de, fr, it, es, or none): ");
            String inputLanguage = sc.nextLine().trim().toLowerCase();
            if(inputLanguage.equals("none")){
                targetLanguage = null;
                break;
            }
            targetLanguage = findLanguage(inputLanguage);
        } while (targetLanguage == null);

        System.out.print("""
                Enter the domain(s) to be crawled (comma-separated if multiple, leave empty if all)
                e.g. website.at, sub.website.at, website.de:\s""");
        String domainsInput = sc.nextLine();
        domains = domainsInput.split(",");

        sc.close();
    }

    private static Language findLanguage(String languageCode) {
        for (Language language : Language.values()) {
            if (language.getCode().equals(languageCode)) {
                return language;
            }
        }
        return null;
    }
}
