package aau.cc;


import aau.cc.model.CrawledWebsite;
import aau.cc.model.Language;
import aau.cc.model.WebsiteToCrawl;

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
        askUserForInput();
        List<CrawledWebsite> crawledWebsites = new ArrayList<>();
        for (String url : urls) {
            WebsiteToCrawl website = new WebsiteToCrawl(url, depth);
            if(targetLanguage != null) {
                website.setTarget(targetLanguage);
            }
            CrawledWebsite crawledWebsite = WebCrawler.crawlWebsite(website, domains);
            crawledWebsites.add(crawledWebsite);
        }

        MarkdownExporter exporter = new MarkdownExporter(targetLanguage == null);
        exporter.generateMarkdownFile("out.md", crawledWebsites.get(0));
    }

    // todo: move to extra class for user input (SRP) (same for others below)
    protected static void askUserForInput() {
        askUserForURLs();
        askUserForCrawlingDepth();
        askUserForTargetLanguage();
        askUserForDomainsToBeCrawled();
    }

    protected static void askUserForURLs(){
        System.out.println("Enter multiple URLs to be crawled (empty line to proceed):");
        String url;
        while(!(url = scanner.nextLine()).isEmpty()){
            urls.add(url);
        }
        if (urls.isEmpty()) {
            System.out.println("Error. At least 1 URL is required!");
            askUserForURLs();
        }
    }

    protected static void askUserForCrawlingDepth(){
        System.out.print("Enter the depth of websites to crawl: ");
        depth = scanner.nextInt();
        scanner.nextLine();
    }

    protected static void askUserForTargetLanguage(){
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

    protected static void askUserForDomainsToBeCrawled(){
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
