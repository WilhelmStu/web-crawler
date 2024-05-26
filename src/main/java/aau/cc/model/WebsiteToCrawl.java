package aau.cc.model;

public class WebsiteToCrawl {
    private String url;
    private int depth;
    private Language source;
    private Language target;

    public WebsiteToCrawl(String url, int depth, Language source, Language target) {
        this.url = url;
        this.depth = depth;
        this.source = source;
        this.target = target;
    }

    public WebsiteToCrawl(String url) {
        this(url, 1, Language.GERMAN, Language.ENGLISH);
    }

    public WebsiteToCrawl(String url,  int depth) {
        this(url, depth, Language.GERMAN, Language.ENGLISH);
    }

    public WebsiteToCrawl(WebsiteToCrawl website) {
        this(website.getUrl(), website.getDepth(), website.getSource(), website.getTarget());
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
}
