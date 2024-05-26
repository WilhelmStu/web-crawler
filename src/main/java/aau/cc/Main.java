package aau.cc;


import aau.cc.model.CrawledWebsite;
import aau.cc.model.Language;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final List<String> urls = new ArrayList<>();
    private static int depth;
    private static final List<String> domains = new ArrayList<>();
    private static Language targetLanguage;
    private static final Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        getUserInput();
        CrawledWebsite website = new CrawledWebsite(urls.get(0), depth);
        website.setSource(Language.GERMAN);
        if(targetLanguage != null) {
            website.setTarget(targetLanguage);
        }

        WebCrawler.crawlWebsite(website, domains);

        MarkdownExporter exporter = new MarkdownExporter(targetLanguage == null);
        exporter.generateMarkdownFile("out.md", website);
    }

    // todo: move to extra class for user input (SRP) (same for others below)
    protected static void getUserInput() {
        getURLsFromUser();
        getCrawlingDepthFromUser();
        getTargetLanguageFromUser();
        getDomainsToBeCrawledFromUser();
    }

    protected static void getURLsFromUser(){
        System.out.println("Enter multiple URLs to be crawled (empty line to proceed):");
        String url;
        while(!(url = scanner.nextLine()).isEmpty()){
            urls.add(url);
        }
        if (urls.isEmpty()) {
            System.out.println("Error. At least 1 URL is required!");
            getURLsFromUser();
        }
    }

    protected static void getCrawlingDepthFromUser(){
        System.out.print("Enter the depth of websites to crawl: ");
        depth = scanner.nextInt();
        scanner.nextLine();
    }

    protected static void getTargetLanguageFromUser(){
        do {
            System.out.print("Enter the target language code (e.g., en, de, fr, it, es, or none): ");
            String inputLanguage = scanner.nextLine().trim().toLowerCase();
            if (inputLanguage.equals("none")) {
                targetLanguage = null;
                break;
            }
            targetLanguage = findLanguage(inputLanguage);
        } while (targetLanguage == null);
    }

    protected static void getDomainsToBeCrawledFromUser(){
        System.out.print("""
                Enter the domain(s) to be crawled (leave empty if all)
                e.g. website.at, sub.website.at, website.de:\s""");
        String domain;
        while(!(domain = scanner.nextLine()).isEmpty()){
            domains.add(domain);
        }
    }

    // todo: move to language class?
    protected static Language findLanguage(String languageCode) {
        for (Language language : Language.values()) {
            if (language.getCode().equals(languageCode)) {
                return language;
            }
        }
        return null;
    }
}
