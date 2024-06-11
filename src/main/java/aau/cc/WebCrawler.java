package aau.cc;

import aau.cc.external.HTMLParserAdapter;
import aau.cc.model.CrawledWebsite;
import aau.cc.model.WebsiteToCrawl;

import java.util.*;
import java.util.concurrent.*;

public class WebCrawler {
    private static final int FUTURE_GET_TIMEOUT = 30;
    private List<String> domains;
    private boolean skipTranslations;
    private Map<Future<CrawledWebsite>, String> futures;
    private List<CrawledWebsite> crawledWebsites;
    private final ConcurrencyManager concurrencyManager;


    public WebCrawler(List<String> domains, boolean skipTranslations) {
        this.domains = domains;
        this.skipTranslations = skipTranslations;
        futures = new HashMap<>();
        crawledWebsites = new ArrayList<>();
        this.concurrencyManager = new ConcurrencyManager();
    }

    public WebCrawler(List<String> domains) {
        this(domains, false);
    }

    public WebCrawler(boolean skipTranslations) {
        this(Collections.emptyList(), skipTranslations);
    }

    public WebCrawler() {
        this(Collections.emptyList(), false);
    }

    public List<CrawledWebsite> crawlWebsites(List<WebsiteToCrawl> websites) {
        concurrencyManager.resetIfDown();
        submitMainCrawlTasks(websites);
        collectCrawledWebsitesFromFutures();

        // shutdown is needed due to cached (60s) Thread pool
        concurrencyManager.shutdown();
        return crawledWebsites;
    }

    private void submitMainCrawlTasks(List<WebsiteToCrawl> websites) {
        for (WebsiteToCrawl website : websites) {
            Set<String> alreadyVisited = Collections.synchronizedSet(new HashSet<>());
            futures.put(concurrencyManager.submitTask(() -> fetchAndCrawlWebsite(website, alreadyVisited, new HTMLParserAdapter())), website.getUrl());
        }
    }

    protected void collectCrawledWebsitesFromFutures() {
        for (Map.Entry<Future<CrawledWebsite>, String> entry : futures.entrySet()) {
            Future<CrawledWebsite> future = entry.getKey();
            String link = entry.getValue();
            try {
                CrawledWebsite crawledWebsite = future.get(FUTURE_GET_TIMEOUT, TimeUnit.SECONDS);
                if (crawledWebsite != null) {
                    crawledWebsites.add(crawledWebsite);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                String urlAndError = link + " (Error during future collection: " + e.getMessage() + ")";
                crawledWebsites.add(new CrawledWebsite(urlAndError, true));
            }
        }
    }

    public CrawledWebsite fetchAndCrawlWebsite(WebsiteToCrawl website, Set<String> alreadyVisited, HTMLParserAdapter htmlParser) {
        alreadyVisited.add(website.getUrl());
        htmlParser.fetchHTMLFromURL(website.getUrl());
        if (!htmlParser.hasDocument()) {
            String urlAndError = website.getUrl() + " (Error while fetching Website)";
            return new CrawledWebsite(urlAndError, true);
        }
        return crawlWebsite(website, alreadyVisited, htmlParser);
    }

    private CrawledWebsite crawlWebsite(WebsiteToCrawl website, Set<String> alreadyVisited, HTMLParserAdapter htmlParser) {
        CrawledWebsite crawledWebsite = CrawledWebsite.from(website);
        crawledWebsite.setHeadings(skipTranslations ?
                htmlParser.getHeadingsFromHTML() :
                htmlParser.getTranslatedHeadingsFromHTML(website.getTarget())
        );
        List<String> links = htmlParser.getLinksFromHTML();
        Map<Future<CrawledWebsite>, String> futureToLinkMap = submitCrawlTasksForLinkedWebsites(crawledWebsite, alreadyVisited, links);
        collectAndLinkWebsitesFromFutures(futureToLinkMap, crawledWebsite);
        return crawledWebsite;
    }

    private Map<Future<CrawledWebsite>, String> submitCrawlTasksForLinkedWebsites(CrawledWebsite crawledWebsite, Set<String> alreadyVisited, List<String> links) {
        concurrencyManager.resetIfDown();
        Map<Future<CrawledWebsite>, String> futureToLinkMap = new HashMap<>();
        List<String> linksToCrawl = filterLinksByDomains(links);
        for (String link : links) {
            CrawledWebsite linkedWebsite = new CrawledWebsite(link, crawledWebsite.getDepth() - 1, crawledWebsite.getSource(), crawledWebsite.getTarget());
            if (!alreadyVisited.contains(link) && linksToCrawl.contains(link) && crawledWebsite.getDepth() >= 1) {
                Future<CrawledWebsite> future = concurrencyManager.submitTask(() -> fetchAndCrawlWebsite(linkedWebsite, alreadyVisited, new HTMLParserAdapter()));
                futureToLinkMap.put(future, link);
            } else {
                crawledWebsite.addLinkedWebsite(linkedWebsite);
            }
        }
        return futureToLinkMap;
    }

    protected void collectAndLinkWebsitesFromFutures(Map<Future<CrawledWebsite>, String> futureToLinkMap, CrawledWebsite crawledWebsite) {
        for (Map.Entry<Future<CrawledWebsite>, String> entry : futureToLinkMap.entrySet()) {
            Future<CrawledWebsite> future = entry.getKey();
            String link = entry.getValue();
            try {
                CrawledWebsite linkedWebsite = future.get(FUTURE_GET_TIMEOUT, TimeUnit.SECONDS);
                if (linkedWebsite != null && !linkedWebsite.hasBrokenUrl()) {
                    crawledWebsite.addLinkedWebsite(linkedWebsite);
                } else {
                    crawledWebsite.addBrokenLink(link + " (Error while fetching Website)");
                }
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                String urlAndError = link + " (Error during future collection: " + e.getMessage() + ")";
                crawledWebsite.addBrokenLink(urlAndError);
            }
        }
    }

    public List<String> filterLinksByDomains(List<String> links) {
        Set<String> linksToCrawl;
        if (domains == null || domains.isEmpty()) {
            linksToCrawl = new LinkedHashSet<>(links);
        } else {
            linksToCrawl = getIntersectionOfLinks(links);
        }
        return new ArrayList<>(linksToCrawl);
    }

    protected Set<String> getIntersectionOfLinks(List<String> links) {
        Set<String> intersection = new LinkedHashSet<>();
        for (String link : links) {
            for (String domain : domains) {
                if (link.contains(domain)) {
                    intersection.add(link);
                    break;
                }
            }
        }
        return intersection;
    }

    public void reset() {
        concurrencyManager.shutdown();
        concurrencyManager.resetIfDown();
        crawledWebsites = new ArrayList<>();
        futures = new HashMap<>();
    }

    public List<CrawledWebsite> getCrawledWebsites() {
        return crawledWebsites;
    }

    protected Map<Future<CrawledWebsite>, String> getFutures() {
        return futures;
    }

    protected void setFutures(Map<Future<CrawledWebsite>, String> futures) {
        this.futures = futures;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setSkipTranslations(boolean skipTranslations) {
        this.skipTranslations = skipTranslations;
    }

    public boolean isSkipTranslations() {
        return skipTranslations;
    }
}
