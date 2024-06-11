package aau.cc;


import aau.cc.model.CrawledWebsite;
import aau.cc.model.UserInput;
import aau.cc.model.WebsiteToCrawl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final String FILE_NAME = "out.md";
    private static UserInput userInput;


    public static void main(String[] args) {
        userInput = UserInputHandler.askUserForInput();

        List<WebsiteToCrawl> websitesToCrawl = prepareWebsitesToCrawl();
        List<CrawledWebsite> crawledWebsites = crawlWebsitesTimed(websitesToCrawl);

        writeResultsToFile(crawledWebsites, FILE_NAME);
    }

    protected static List<WebsiteToCrawl> prepareWebsitesToCrawl() {
        List<WebsiteToCrawl> websitesToCrawls = new ArrayList<>();
        for (String url : userInput.getUrls()) {
            WebsiteToCrawl website = new WebsiteToCrawl(url, userInput.getDepth());
            if (!userInput.isSkipTranslation()) {
                website.setTarget(userInput.getTargetLanguage());
            }
            websitesToCrawls.add(website);
        }
        return websitesToCrawls;
    }

    protected static List<CrawledWebsite> crawlWebsitesTimed(List<WebsiteToCrawl> websitesToCrawl) {
        long startTime = System.nanoTime();
        List<CrawledWebsite> crawledWebsite = crawlWebsites(websitesToCrawl);
        long elapsedTime = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
        System.out.println("it took " + elapsedTime + "ms.");
        return crawledWebsite;
    }

    protected static List<CrawledWebsite> crawlWebsites(List<WebsiteToCrawl> websitesToCrawl) {
        System.out.println("Proceeding to crawl..");
        WebCrawler webCrawler = new WebCrawler(userInput.getDomains(), userInput.isSkipTranslation());
        List<CrawledWebsite> crawledWebsites = webCrawler.crawlWebsites(websitesToCrawl);
        System.out.print("Crawling done, ");
        return crawledWebsites;
    }

    protected static void writeResultsToFile(List<CrawledWebsite> crawledWebsites, String filename) {
        System.out.println("\nWriting results to " + filename);
        MarkdownFormatter formatter = new MarkdownFormatter(userInput.isSkipTranslation(), userInput.getDepth());
        MarkdownExporter exporter = new MarkdownExporter(formatter);
        exporter.deleteMarkdownFileIfExists(filename);
        exporter.writeContentToMarkdownFile(filename, crawledWebsites);
    }

    protected static void setUserInput(UserInput userInput) {
        Main.userInput = userInput;
    }
}
