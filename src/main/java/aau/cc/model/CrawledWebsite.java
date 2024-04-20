package aau.cc.model;

import java.util.ArrayList;
import java.util.List;

public class CrawledWebsite {
    private String url;
    private int depth;
    private List<Heading> headings;
    private Language source;
    private Language target;
    private List<CrawledWebsite> linkedWebsites;
    private List<String> brokenLinks;

    public CrawledWebsite(String url, int depth) {
        this.url = url;
        this.depth = depth;
        this.headings = new ArrayList<>();
        this.linkedWebsites = new ArrayList<>();
        this.brokenLinks = new ArrayList<>();
    }

    public CrawledWebsite(String url) {
        this(url, 1);
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<Heading> getHeadings() {
        return headings;
    }

    public void setHeadings(List<Heading> headings) {
        this.headings = headings;
    }

    public Language getSource() {
        return source;
    }

    public void setSource(Language source) {
        this.source = source;
    }

    public Language getTarget() {
        return target;
    }

    public void setTarget(Language target) {
        this.target = target;
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
