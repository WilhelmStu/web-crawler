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
import java.util.Set;

public class WebCrawler {
    private static final Set<String> alreadyVisited = new HashSet<>();

    public static CrawledWebsite crawlWebsite(CrawledWebsite website, String[] domains) {
        alreadyVisited.add(website.getUrl());
        String url = website.getUrl();
        int depth = website.getDepth();


        try {
            Document doc = Jsoup.parse(new URL(url), 3000);
            Elements headings = doc.select("h1, h2, h3, h4, h5, h6");

            // get headings
            Set<Heading> headingsSet = new HashSet<>();
            for (Element heading : headings) {
                int headingDepth = Integer.parseInt(heading.tagName().substring(1));
                headingsSet.add(new Heading(heading.text(), headingDepth));
            }
            website.setHeadings(new ArrayList<>(headingsSet));

            // get links
            Elements links = doc.select("a[href]");

            Set<String> linkSet = new HashSet<>();
            Set<String> linksToCrawl = new HashSet<>();
            for (Element link : links) {
                String linkUrl = link.absUrl("href");
                linkSet.add(linkUrl);
                if (domains != null) {
                    for (String domain : domains) {
                        if (linkUrl.contains(domain)) {
                            // Add the URL to the set of valid URLs
                            linksToCrawl.add(linkUrl);
                            break;
                        }
                    }
                } else {
                    linksToCrawl.add(linkUrl);
                }
            }
            if (!linkSet.isEmpty()) { //  && depth > 1
                for (String link : linkSet) {
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
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
            return null;
        }

        return website;
    }
}
