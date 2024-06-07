package aau.cc;


import aau.cc.model.CrawledWebsite;
import aau.cc.model.Language;
import aau.cc.model.WebsiteToCrawl;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final List<String> urls = new ArrayList<>();
    private static int depth;
    private static final List<String> domains = new ArrayList<>();
    private static Language targetLanguage;
    private static final Scanner scanner = new Scanner(System.in);
    private static final String FILE_NAME = "out.md";


    public static void main(String[] args) {
        askUserForInput();
        List<WebsiteToCrawl> websitesToCrawls = new ArrayList<>();
        for (String url : urls) { // todo clean up
            WebsiteToCrawl website = new WebsiteToCrawl(url, depth);
            if (targetLanguage != null) {
                website.setTarget(targetLanguage);
            }
            websitesToCrawls.add(website);
        }

        long startTime = System.nanoTime();
        WebCrawler webCrawler = new WebCrawler(domains, targetLanguage == null);
        List<CrawledWebsite> crawledWebsites = webCrawler.crawlWebsites(websitesToCrawls);
        long endTime = System.nanoTime();
        long elapsedTime = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);

        System.out.println("Crawling done, it took " + elapsedTime + "ms. Writing to out.md");
        MarkdownExporter exporter = new MarkdownExporter(webCrawler.isSkipTranslations());

        exporter.deleteMarkdownFileIfExists(FILE_NAME);
        exporter.generateMarkdownFile(FILE_NAME, crawledWebsites);
    }

    // todo: move to extra class for user input (SRP) (same for others below)
    protected static void askUserForInput() {
        askUserForURLs();
        askUserForCrawlingDepth();
        askUserForTargetLanguage();
        askUserForDomainsToBeCrawled();
        scanner.close();
        System.out.println("Got user input. Proceeding to crawl..");
    }

    protected static void askUserForURLs() { //todo handle InputMismatchException
        System.out.println("Enter multiple URLs to be crawled (empty line to proceed):");
        String url;
        while (!(url = scanner.nextLine()).isEmpty()) {
            urls.add(url);
        }
        if (urls.isEmpty()) {
            System.out.println("Error. At least 1 URL is required!");
            askUserForURLs();
        }
    }

    protected static void askUserForCrawlingDepth() {
        System.out.print("Enter the depth of websites to crawl: ");
        depth = scanner.nextInt(); // todo catch exception
        scanner.nextLine();
    }

    protected static void askUserForTargetLanguage() {
        do {
            System.out.print("Enter the target language code (e.g., en, de, fr, it, es, or none): ");
            String inputLanguage = scanner.nextLine().trim().toLowerCase();
            if (inputLanguage.equals("none") || inputLanguage.isEmpty()) {
                targetLanguage = null;
                break;
            }
            targetLanguage = findLanguage(inputLanguage);
        } while (targetLanguage == null);
    }

    protected static void askUserForDomainsToBeCrawled() {
        System.out.println("Enter the domain(s) to be crawled (leave empty if all)\ne.g. website.at, sub.website.at, website.de:");
        String domain;
        while (!(domain = scanner.nextLine()).isEmpty()) {
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
