package aau.cc;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;
import aau.cc.model.Language;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
    private int depth = 0; // Maximum depth to crawl

    private String url;
    Language targetLanguage;

    Language sourceLanguage;


    private HashSet<String> visitedUrls; // Set of visited URLs

    private List<CrawledWebsite> linksFromWebpage;

    private HashSet<String> brokenLinks;

    //private TranslateAPIRequestHandler translateAPIRequestHandler;

    public Crawler(String url, Language sourceLanguage, Language targetLanguage, int depth) {
        this.visitedUrls = new HashSet<>();
        this.url = url;
        this.targetLanguage = targetLanguage;
        this.depth = depth;
        this.sourceLanguage = sourceLanguage;
        //this.translateAPIRequestHandler = new TranslateAPIRequestHandler();
    }
    public void mainCrawler(String url, int depth)
    {
        CrawledWebsite webpage = new CrawledWebsite(url,depth);
        crawl(url,depth, webpage);
        translateAndWriteToFile(webpage);
    }

    private void crawl(String url, int depth, CrawledWebsite webpage) {
        linksFromWebpage = new ArrayList<>();
        brokenLinks = new HashSet<>();
        if (depth > this.depth || visitedUrls.contains(url)) {
            return;
        }
        webpage.setSource(Language.GERMAN);
        webpage.setTarget(Language.ENGLISH);
        visitedUrls.add(url);
        try {
            Document document = Jsoup.connect(url).get();
            String heading = document.select("head title").text();
            Elements hTags = document.select("h1, h2, h3, h4, h5, h6");
            Heading header = new Heading(heading,0);
            webpage.addHeading(header);
            addHeadingsToCrawledWebsite(hTags, webpage);
            Elements links = document.select("a[href]");
            checkWebpageLinks(links,webpage);
            //Recursive call of the crawl Method
            for (Element link : links) {
                crawl(link.absUrl("href"), depth + 1, webpage);
            }
        } catch (Exception e2) {
            System.err.println("Could not crawl " + url + ": " + e2.getMessage());
        }
    }
    private void addHeadingsToCrawledWebsite(Elements hTags, CrawledWebsite webpage)
    {
        if(!hTags.select("h1").text().isEmpty()) {
            webpage.addHeading(new Heading(hTags.select("h1").text(),1));
        }
        if(!hTags.select("h2").text().isEmpty()) {
            webpage.addHeading(new Heading(hTags.select("h2").text(),2));
        }
        if(!hTags.select("h3").text().isEmpty()) {
            webpage.addHeading(new Heading(hTags.select("h3").text(),3));
        }
        if(!hTags.select("h4").text().isEmpty()) {
            webpage.addHeading(new Heading(hTags.select("h4").text(),4));
        }
        if(!hTags.select("h5").text().isEmpty()) {
            webpage.addHeading(new Heading(hTags.select("h5").text(),5));
        }
        if(!hTags.select("h6").text().isEmpty()) {
            webpage.addHeading(new Heading(hTags.select("h6").text(),6));
        }
    }
    private void checkWebpageLinks(Elements links, CrawledWebsite webpage)
    {
        try {
            for (Element link : links) {
                String href = link.absUrl("href");
                URL linkResponseChecker = new URL(href);
                HttpURLConnection httpLinkResponseChecker = (HttpURLConnection) linkResponseChecker.openConnection();
                httpLinkResponseChecker.setRequestProperty("User-Agent", "curl/7.64.1");
                httpLinkResponseChecker.setConnectTimeout(15000);
                httpLinkResponseChecker.setReadTimeout(15000);
                httpLinkResponseChecker.setRequestMethod("GET"); //this is important, some websites don't allow head request
                if (httpLinkResponseChecker.getResponseCode() == 200) {
                    CrawledWebsite linkAsCrawledWebsite = new CrawledWebsite(href, depth);
                    linksFromWebpage.add(linkAsCrawledWebsite);
                }
                if (httpLinkResponseChecker.getResponseCode() != 200) {
                    brokenLinks.add(href);
                    webpage.addBrokenLink(href);
                }
                httpLinkResponseChecker.disconnect();
            }
            webpage.setLinkedWebsites(linksFromWebpage);

        }
        catch (Exception ex)
        {
            System.err.println("Error in Crawling: " + url + ": " + ex.getMessage());
        }
    }
    private void translateAndWriteToFile(CrawledWebsite webpage)
    {
        MarkdownExporter exportToFile = new MarkdownExporter(false);
        exportToFile.generateMarkdownFile("generated.md", webpage);
    }

}
