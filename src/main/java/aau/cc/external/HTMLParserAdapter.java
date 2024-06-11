package aau.cc.external;

import aau.cc.Translator;
import aau.cc.model.Heading;
import aau.cc.model.Language;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class HTMLParserAdapter {
    private static final int FETCH_TIMEOUT = 5000;
    private Document document;
    private boolean hasDocument;

    public HTMLParserAdapter() {
        hasDocument = false;
    }

    public boolean fetchHTMLFromURL(String url) {
        try {
            this.document = Jsoup.parse(new URL(url), FETCH_TIMEOUT);
            this.hasDocument = true;
        } catch (IOException e) {
            this.hasDocument = false;
        }
        return this.hasDocument;
    }

    public List<Heading> getHeadingsFromHTML() {
        if (!hasDocument) {
            return Collections.emptyList();
        }
        Elements headings = document.select("h1, h2, h3, h4, h5, h6");
        Set<Heading> headingsSet = new LinkedHashSet<>(); // set to prevent duplicates
        for (Element heading : headings) {
            int headingDepth = Integer.parseInt(heading.tagName().substring(1));
            headingsSet.add(new Heading(heading.text(), headingDepth));
        }
        return new ArrayList<>(headingsSet);
    }

    public List<Heading> getTranslatedHeadingsFromHTML(Language target) {
        List<Heading> headings = getHeadingsFromHTML();
        Translator.translateHeadingsInPlace(headings, new Translator(target));
        return headings;
    }

    public List<String> getLinksFromHTML() {
        if (!hasDocument) {
            return Collections.emptyList();
        }
        Elements links = document.select("a[href]");
        Set<String> linkSet = new LinkedHashSet<>(); // set to prevent duplicates
        for (Element link : links) {
            String linkUrl = link.absUrl("href");
            linkSet.add(linkUrl);
        }
        return new ArrayList<>(linkSet);
    }

    public void setDocumentFromString(String html) {
        this.document = Jsoup.parse(html);
        this.hasDocument = true;
    }

    public boolean hasDocument() {
        return hasDocument;
    }
}
