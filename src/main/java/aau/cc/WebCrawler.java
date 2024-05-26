package aau.cc;

import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;
import aau.cc.model.WebsiteToCrawl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.*;

public class WebCrawler {
    private static final Set<String> alreadyVisited = new HashSet<>();
    private static final int FETCH_TIMEOUT = 3000;

    public static CrawledWebsite crawlWebsite(WebsiteToCrawl website, List<String> domains) {
        alreadyVisited.add(website.getUrl());
        String url = website.getUrl();
        int depth = website.getDepth();
        CrawledWebsite crawledWebsite = new CrawledWebsite(website);

        try {
            Document document = Jsoup.parse(new URL(url), FETCH_TIMEOUT);
            crawledWebsite.setHeadings(getHeadingsOfWebsite(document));

            List<String> links = getLinksOfWebsite(document);
            List<String> linksToCrawl = getLinksToCrawl(domains, links);

            for (String link : links) {
                CrawledWebsite linkedWebsite = new CrawledWebsite(link, depth - 1);
                linkedWebsite.setTarget(website.getTarget());
                if (!alreadyVisited.contains(link) && linksToCrawl.contains(link) && depth > 1) {
                    linkedWebsite = crawlWebsite(linkedWebsite, domains);
                }
                if (linkedWebsite != null) {
                    crawledWebsite.addLinkedWebsite(linkedWebsite);
                } else {
                    crawledWebsite.addBrokenLink(link);
                }
            }

        } catch (Exception e) {
            // error fetching site -> it will be handled as broken link
            return null;
        }

        return crawledWebsite;
    }

    public static List<Heading> getHeadingsOfWebsite(Document document) {
        Elements headings = document.select("h1, h2, h3, h4, h5, h6");
        Set<Heading> headingsSet = new LinkedHashSet<>(); // set to prevent duplicates
        for (Element heading : headings) {
            int headingDepth = Integer.parseInt(heading.tagName().substring(1));
            headingsSet.add(new Heading(heading.text(), headingDepth));
        }
        return new ArrayList<>(headingsSet);
    }

    public static List<String> getLinksOfWebsite(Document document) {
        Elements links = document.select("a[href]");
        Set<String> linkSet = new LinkedHashSet<>(); // set to prevent duplicates
        for (Element link : links) {
            String linkUrl = link.absUrl("href");
            linkSet.add(linkUrl);
        }
        return new ArrayList<>(linkSet);
    }

    public static List<String> getLinksToCrawl(List<String> domains, List<String> links) {
        Set<String> linksToCrawl = new LinkedHashSet<>();
        if (domains != null && !domains.isEmpty()) {
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

    public static void reset() {
        alreadyVisited.clear();
    }

    public static Set<String> getAlreadyVisited() {
        return alreadyVisited;
    }

}
