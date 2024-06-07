package aau.cc.model;

import java.util.ArrayList;
import java.util.List;

public class UserInput {
    private List<String> urls;
    private int depth;
    private List<String> domains;
    private Language targetLanguage;

    public UserInput() {
        urls = new ArrayList<>();
        depth = 1;
        domains = new ArrayList<>();
        targetLanguage = Language.ENGLISH;
    }

    public boolean isSkipTranslation(){
        return targetLanguage == null;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public Language getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(Language targetLanguage) {
        this.targetLanguage = targetLanguage;
    }
}
