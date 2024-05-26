package aau.cc.model;

import java.util.ArrayList;
import java.util.List;

public class CrawledWebsite extends WebsiteToCrawl {
    private List<Heading> headings;
    private List<CrawledWebsite> linkedWebsites;
    private List<String> brokenLinks;

    public CrawledWebsite(WebsiteToCrawl websiteToCrawl) {
        super(websiteToCrawl);
        initLists();
    }

    public CrawledWebsite(String url, int depth, Language source, Language target) {
        super(url, depth, source, target);
        initLists();
    }

    public CrawledWebsite(String url, int depth) {
        super(url, depth, Language.GERMAN, Language.ENGLISH);
        initLists();
    }

    public CrawledWebsite(String url) {
        this(url, 1);
    }

    private void initLists(){
        this.headings = new ArrayList<>();
        this.linkedWebsites = new ArrayList<>();
        this.brokenLinks = new ArrayList<>();
    }

    public void addHeading(Heading heading){
        this.headings.add(heading);
    }

    public void addLinkedWebsite(CrawledWebsite linkedWebsite){
        this.linkedWebsites.add(linkedWebsite);
    }

    public void addBrokenLink(String link){
        this.brokenLinks.add(link);
    }

    public List<String> getHeadingsTextsAsList(){
        List<String> headingsTexts = new ArrayList<>();
        for(Heading heading : headings){
            headingsTexts.add(heading.getText());
        }
        return headingsTexts;
    }

    public int[] getHeadingsDepths(){
        int[] headingsDepths = new int[headings.size()];
        for(int i = 0; i < headings.size(); i++){
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
}
