package aau.cc;

import aau.cc.external.HTMLParserAdapter;
import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;
import aau.cc.model.Language;
import aau.cc.model.WebsiteToCrawl;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.*;

public class WebCrawler {
    private static final int FETCH_TIMEOUT = 3000;
    private static final int EXECUTOR_TIMEOUT = 10000;
    private ExecutorService executorService;
    private List<String> domains;
    private List<CrawledWebsite> crawledWebsites;
    private boolean skipTranslations;

    public WebCrawler(List<String> domains, boolean skipTranslations) {
        this.domains = domains;
        this.skipTranslations = skipTranslations;
        executorService = Executors.newCachedThreadPool(); // cachedThreadPool has the best performance
        crawledWebsites = new ArrayList<>();
    }

    public WebCrawler(List<String> domains) {
        this(domains, false);
    }

    public WebCrawler(boolean skipTranslations) {
        this(Collections.emptyList(),skipTranslations);
    }

    public WebCrawler() {
        this(Collections.emptyList(),false);
    }


    public List<CrawledWebsite> crawlWebsites(List<WebsiteToCrawl> websites) {
        resetExecutorServiceIfDown();

        List<Future<CrawledWebsite>> futures = createFutureForEachWebsite(websites);
        collectCrawledWebsitesFromFutures(futures);

        shutdownExecutorService();
        return crawledWebsites;
    }

    private List<Future<CrawledWebsite>> createFutureForEachWebsite(List<WebsiteToCrawl> websites) {
        List<Future<CrawledWebsite>> futures = new ArrayList<>();
        for (WebsiteToCrawl website : websites) {
            Set<String> alreadyVisited = Collections.synchronizedSet(new HashSet<>());
            futures.add(executorService.submit(() -> crawlWebsite(website, alreadyVisited)));
        }
        return futures;
    }

    private void collectCrawledWebsitesFromFutures(List<Future<CrawledWebsite>> futures) {
        for (Future<CrawledWebsite> future : futures) {
            try {
                CrawledWebsite crawledWebsite = future.get();
                if (crawledWebsite != null) {
                    crawledWebsites.add(crawledWebsite);
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Future collection of CrawledWebsites failed: " + e.getMessage());
            }
        }
    }

    public CrawledWebsite crawlWebsite(WebsiteToCrawl website, Set<String> alreadyVisited) {
        resetExecutorServiceIfDown();
        alreadyVisited.add(website.getUrl());

        HTMLParserAdapter htmlParser = fetchAndGetParser(website);
        if (!htmlParser.hasDocument()) {
            return new CrawledWebsite(website.getUrl(), true);
        }

        CrawledWebsite crawledWebsite = CrawledWebsite.from(website);
        crawledWebsite.setHeadings(getTranslatedHeadings(htmlParser, crawledWebsite.getTarget(), crawledWebsite.getSource()));
        List<String> links = htmlParser.getLinksFromHTML();

        Map<Future<CrawledWebsite>, String> futureToLinkMap = createWebsiteFutures(crawledWebsite, alreadyVisited, links);
        collectLinkedWebsitesFromFutures(futureToLinkMap, crawledWebsite);
        return crawledWebsite;
    }

    private List<Heading> getTranslatedHeadings(HTMLParserAdapter htmlParser, Language target, Language source){
        List<Heading> headings = htmlParser.getHeadingsFromHTML();
        if (!skipTranslations) {
            translateHeadings(headings, target, source);
        }
        return headings;
    }

    private void translateHeadings(List<Heading> headings, Language target, Language source){
        List<String> headingsToTranslate = Heading.getHeadingsTextsAsList(headings);
        Translator translator = new Translator(target);
        List<String> translatedHeadings = translator.translateMultipleLines(headingsToTranslate);
        for (int i = 0; i < headings.size(); i++) {
            Heading heading = headings.get(i);
            if(translatedHeadings.isEmpty()){ // Error in translation
                heading.setText(heading.getText() + " (Not translated due to API error)");
            }else {
                heading.setText(translatedHeadings.get(i));
            }
        }
    }

    private HTMLParserAdapter fetchAndGetParser(WebsiteToCrawl website) {
        HTMLParserAdapter htmlParser = new HTMLParserAdapter();
        htmlParser.fetchHTMLFromURL(website.getUrl(), FETCH_TIMEOUT);
        return htmlParser;
    }

    private @NotNull Map<Future<CrawledWebsite>, String> createWebsiteFutures(CrawledWebsite crawledWebsite, Set<String> alreadyVisited, List<String> links) {
        Map<Future<CrawledWebsite>, String> futureToLinkMap = new ConcurrentHashMap<>();
        List<String> linksToCrawl = getLinksToCrawlFromDomains(links);
        for (String link : links) {
            CrawledWebsite linkedWebsite = new CrawledWebsite(link, crawledWebsite.getDepth() - 1, crawledWebsite.getSource(), crawledWebsite.getTarget());
            if (!alreadyVisited.contains(link) && linksToCrawl.contains(link) && crawledWebsite.getDepth() > 1) {
                Future<CrawledWebsite> future = executorService.submit(() -> crawlWebsite(linkedWebsite, alreadyVisited));
                futureToLinkMap.put(future, link);
            } else {
                crawledWebsite.addLinkedWebsite(linkedWebsite);
            }
        }
        return futureToLinkMap;
    }

    private static void collectLinkedWebsitesFromFutures(Map<Future<CrawledWebsite>, String> futureToLinkMap, CrawledWebsite crawledWebsite) {
        for (Map.Entry<Future<CrawledWebsite>, String> entry : futureToLinkMap.entrySet()) {
            try {
                processFutureMapEntry(crawledWebsite, entry);
            } catch (Exception e) {
                System.err.println("Future collection of CrawledWebsites failed: " + e.getMessage());
            }
        }
    }

    private static void processFutureMapEntry(CrawledWebsite crawledWebsite, Map.Entry<Future<CrawledWebsite>, String> entry) throws Exception {
        Future<CrawledWebsite> future = entry.getKey();
        String link = entry.getValue();
        CrawledWebsite linkedWebsite = future.get();
        if (linkedWebsite != null && !linkedWebsite.hasBrokenUrl()) {
            crawledWebsite.addLinkedWebsite(linkedWebsite);
        } else {
            crawledWebsite.addBrokenLink(link);
        }
    }

    public List<String> getLinksToCrawlFromDomains(List<String> links) {
        Set<String> linksToCrawl;
        if (domains == null || domains.isEmpty()) {
            linksToCrawl = new LinkedHashSet<>(links);
        } else {
            linksToCrawl = getIntersectionOfLinks(links);
        }
        return new ArrayList<>(linksToCrawl);
    }

    private Set<String> getIntersectionOfLinks(List<String> links) {
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
        shutdownExecutorService();
        resetExecutorServiceIfDown();
        crawledWebsites = new ArrayList<>();
    }

    private void shutdownExecutorService() {
        try {
            ensureShutdown();
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            System.err.println("ExecutorService did not terminate: " + e.getMessage());
        }
    }

    private void ensureShutdown() throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(EXECUTOR_TIMEOUT, TimeUnit.MILLISECONDS)) {
            executorService.shutdownNow();
            if (!executorService.awaitTermination(EXECUTOR_TIMEOUT, TimeUnit.MILLISECONDS)) {
                System.err.println("ExecutorService did not terminate");
            }
        }
    }

    private void resetExecutorServiceIfDown() {
        if (executorService.isShutdown() || executorService.isTerminated()) {
            executorService = Executors.newCachedThreadPool();
        }
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public List<CrawledWebsite> getCrawledWebsites() {
        return crawledWebsites;
    }

    public boolean isSkipTranslations() {
        return skipTranslations;
    }

    public void setSkipTranslations(boolean skipTranslations) {
        this.skipTranslations = skipTranslations;
    }
}
