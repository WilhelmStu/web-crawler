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
import java.util.concurrent.*;

class CrawlTask implements Callable<CrawledWebsite> {
    private final WebsiteToCrawl website;
    private final List<String> domains;
    private final Set<String> alreadyVisited;

    public CrawlTask(WebsiteToCrawl website, List<String> domains, Set<String> alreadyVisited) {
        this.website = website;
        this.domains = domains;
        this.alreadyVisited = alreadyVisited;
    }

    @Override
    public CrawledWebsite call() throws Exception {
        System.out.println("Called!");
        return WebCrawler.crawlWebsite(website, domains, alreadyVisited);
    }
}

public class WebCrawler {
    private static final int FETCH_TIMEOUT = 3000;
    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public static List<CrawledWebsite> crawlWebsites(List<WebsiteToCrawl> websites, List<String> domains) {
        List<CrawledWebsite> crawledWebsites = new ArrayList<>();
        List<Future<CrawledWebsite>> futures = new ArrayList<>();

        for (WebsiteToCrawl website : websites) {
            Set<String> alreadyVisited = Collections.synchronizedSet(new HashSet<>());
            futures.add(executorService.submit(new CrawlTask(website, domains, alreadyVisited)));
        }

        for (Future<CrawledWebsite> future : futures) {
            try {
                CrawledWebsite crawledWebsite = future.get();
                if (crawledWebsite != null) {
                    crawledWebsites.add(crawledWebsite);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        executorService.shutdown();

        return crawledWebsites;
    }

    public static CrawledWebsite crawlWebsite(WebsiteToCrawl website, List<String> domains, Set<String> alreadyVisited) {
        alreadyVisited.add(website.getUrl());
        String url = website.getUrl();
        int depth = website.getDepth();

        // todo move inside CrawledWebsite as factory
        CrawledWebsite crawledWebsite;
        if (website instanceof CrawledWebsite) {
            crawledWebsite = (CrawledWebsite) website;
        } else {
            crawledWebsite = new CrawledWebsite(website);
        }

        try {
            Document document = Jsoup.parse(new URL(url), FETCH_TIMEOUT);
            crawledWebsite.setHeadings(getHeadingsOfWebsite(document));

            List<String> links = getLinksOfWebsite(document);
            List<String> linksToCrawl = getLinksToCrawl(domains, links);

            Map<Future<CrawledWebsite>, String> futureToLinkMap = new ConcurrentHashMap<>();
            for (String link : links) {
                CrawledWebsite linkedWebsite = new CrawledWebsite(link, depth - 1, website.getSource(), website.getTarget());
                if (!alreadyVisited.contains(link) && linksToCrawl.contains(link) && depth > 1) {
                    Future<CrawledWebsite> future = executorService.submit(() -> crawlWebsite(linkedWebsite, domains, alreadyVisited));
                    futureToLinkMap.put(future, link);
                }else {
                    crawledWebsite.addLinkedWebsite(linkedWebsite);
                }
            }
            
            for (Map.Entry<Future<CrawledWebsite>, String> entry : futureToLinkMap.entrySet()) {
                try {
                    Future<CrawledWebsite> future = entry.getKey();
                    String link = entry.getValue();
                    CrawledWebsite linkedWebsite = future.get();
                    if (linkedWebsite != null) {
                        crawledWebsite.addLinkedWebsite(linkedWebsite);
                    } else {
                        crawledWebsite.addBrokenLink(link);
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt(); // Set interrupt flag
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

}
