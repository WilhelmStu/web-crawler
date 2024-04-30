package aau.cc;

import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WebCrawler {
    private static final Set<String> alreadyVisited = new HashSet<>();
    private static final int FETCH_TIMEOUT = 3000;

    public static CrawledWebsite crawlWebsite(CrawledWebsite website, String[] domains) {
        alreadyVisited.add(website.getUrl());
        String url = website.getUrl();
        int depth = website.getDepth();

        try {
            Document document = Jsoup.parse(new URL(url), FETCH_TIMEOUT);
            website.setHeadings(getHeadingsOfWebsite(document));

            List<String> links = getLinksOfWebsite(document);
            List<String> linksToCrawl = getLinksToCrawl(domains, links);

            for (String link : links) {
                CrawledWebsite linkedWebsite = new CrawledWebsite(link, depth - 1);
                if (!alreadyVisited.contains(link) && linksToCrawl.contains(link) && depth > 1) {
                    linkedWebsite = crawlWebsite(linkedWebsite, domains);
                }
                if (linkedWebsite != null) {
                    website.addLinkedWebsite(linkedWebsite);
                } else {
                    website.addBrokenLink(link);
                }
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }

        return website;
    }

    public static List<Heading> getHeadingsOfWebsite(Document document) {
        Elements headings = document.select("h1, h2, h3, h4, h5, h6");
        Set<Heading> headingsSet = new HashSet<>(); // use set to prevent duplicates
        for (Element heading : headings) {
            int headingDepth = Integer.parseInt(heading.tagName().substring(1));
            headingsSet.add(new Heading(heading.text(), headingDepth));
        }
        return new ArrayList<>(headingsSet);
    }

    public static List<String> getLinksOfWebsite(Document document) {
        Elements links = document.select("a[href]");
        Set<String> linkSet = new HashSet<>(); // use set to prevent duplicates
        for (Element link : links) {
            String linkUrl = link.absUrl("href");
            linkSet.add(linkUrl);
        }
        return new ArrayList<>(linkSet);
    }

    public static List<String> getLinksToCrawl(String[] domains, List<String> links) {
        Set<String> linksToCrawl = new HashSet<>();
        if (domains != null) {
            for (String link : links) {
                for (String domain : domains) {
                    if (link.contains(domain)) {
                        linksToCrawl.add(link);
                        break;
                    }
                }
            }
        } else {
            linksToCrawl.addAll(links);
        }
        return new ArrayList<>(linksToCrawl);
    }

}
