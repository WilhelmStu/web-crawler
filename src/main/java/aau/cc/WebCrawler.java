package aau.cc;

import aau.cc.external.HTMLParserAdapter;
import aau.cc.model.CrawledWebsite;
import aau.cc.model.WebsiteToCrawl;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

class CrawlTask implements Callable<CrawledWebsite> {
    private final WebCrawler webCrawler;
    private final WebsiteToCrawl website;
    private final List<String> domains;
    private final Set<String> alreadyVisited;

    public CrawlTask(WebCrawler webCrawler, WebsiteToCrawl website, List<String> domains, Set<String> alreadyVisited) {
        this.webCrawler = webCrawler;
        this.website = website;
        this.domains = domains;
        this.alreadyVisited = alreadyVisited;
    }

    @Override
    public CrawledWebsite call() {
        return webCrawler.crawlWebsite(website, domains, alreadyVisited);
    }
}

public class WebCrawler {
    private static final int FETCH_TIMEOUT = 3000;
    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public List<CrawledWebsite> crawlWebsites(List<WebsiteToCrawl> websites, List<String> domains) {
        List<CrawledWebsite> crawledWebsites = new ArrayList<>();
        List<Future<CrawledWebsite>> futures = new ArrayList<>();

        for (WebsiteToCrawl website : websites) {
            Set<String> alreadyVisited = Collections.synchronizedSet(new HashSet<>());
            futures.add(executorService.submit(new CrawlTask(this, website, domains, alreadyVisited)));
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

    // todo handle case where start link is already invalid
    public CrawledWebsite crawlWebsite(WebsiteToCrawl website, List<String> domains, Set<String> alreadyVisited) {
        alreadyVisited.add(website.getUrl());
        String url = website.getUrl();
        int depth = website.getDepth();
        CrawledWebsite crawledWebsite = CrawledWebsite.from(website);


        HTMLParserAdapter htmlParser = new HTMLParserAdapter();
        if (!htmlParser.fetchHTMLFromURL(url, FETCH_TIMEOUT) || !htmlParser.hasDocument()) {
            return null; // todo don't return null
        }
        crawledWebsite.setHeadings(htmlParser.getHeadingsFromHTML());
        List<String> links = htmlParser.getLinksFromHTML();
        List<String> linksToCrawl = getLinksToCrawl(domains, links);

        Map<Future<CrawledWebsite>, String> futureToLinkMap = new ConcurrentHashMap<>();
        for (String link : links) {
            CrawledWebsite linkedWebsite = new CrawledWebsite(link, depth - 1, website.getSource(), website.getTarget());
            if (!alreadyVisited.contains(link) && linksToCrawl.contains(link) && depth > 1) {
                Future<CrawledWebsite> future = executorService.submit(() -> crawlWebsite(linkedWebsite, domains, alreadyVisited));
                futureToLinkMap.put(future, link);
            } else {
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


        return crawledWebsite;
    }

    public List<String> getLinksToCrawl(List<String> domains, List<String> links) {
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
