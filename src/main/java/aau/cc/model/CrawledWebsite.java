package aau.cc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CrawledWebsite extends WebsiteToCrawl {
    private List<Heading> headings;
    private List<CrawledWebsite> linkedWebsites;
    private List<String> brokenLinks;
    private boolean hasBrokenUrl;

    public CrawledWebsite(WebsiteToCrawl websiteToCrawl) {
        super(websiteToCrawl);
        init();
    }

    public CrawledWebsite(String url, int depth, Language source, Language target) {
        super(url, depth, source, target);
        init();
    }

    public CrawledWebsite(String url, int depth) {
        super(url, depth, Language.GERMAN, Language.ENGLISH);
        init();
    }

    public CrawledWebsite(String url) {
        this(url, 1);
    }

    public CrawledWebsite(String url, boolean hasBrokenUrl) {
        super(url);
        this.hasBrokenUrl = hasBrokenUrl;
    }

    private void init() {
        this.headings = new ArrayList<>();
        this.linkedWebsites = new ArrayList<>();
        this.brokenLinks = new ArrayList<>();
        this.hasBrokenUrl = false;
    }

    public static CrawledWebsite from(WebsiteToCrawl website) {
        if (website instanceof CrawledWebsite) {
            return (CrawledWebsite) website;
        } else {
            return new CrawledWebsite(website);
        }
    }

    public void addHeading(Heading heading) {
        this.headings.add(heading);
    }

    public void addLinkedWebsite(CrawledWebsite linkedWebsite) {
        this.linkedWebsites.add(linkedWebsite);
    }

    public void addBrokenLink(String link) {
        this.brokenLinks.add(link);
    }

    public int[] getHeadingsDepths() {
        if (headings == null) {
            return new int[0];
        }
        int[] headingsDepths = new int[headings.size()];
        for (int i = 0; i < headings.size(); i++) {
            headingsDepths[i] = headings.get(i).getDepth();
        }
        return headingsDepths;
    }

    public List<Heading> getHeadings() {
        return headings;
    }

    public void setHeadings(List<Heading> headings) {
        this.headings = headings;
    }

    public List<CrawledWebsite> getLinkedWebsites() {
        return linkedWebsites;
    }

    public void setLinkedWebsites(List<CrawledWebsite> linkedWebsites) {
        this.linkedWebsites = linkedWebsites;
    }

    public List<String> getBrokenLinks() {
        return brokenLinks;
    }

    public void setBrokenLinks(List<String> brokenLinks) {
        this.brokenLinks = brokenLinks;
    }

    public boolean hasBrokenUrl() {
        return hasBrokenUrl;
    }

    public void setHasBrokenUrl(boolean hasBrokenUrl) {
        this.hasBrokenUrl = hasBrokenUrl;
    }
}
