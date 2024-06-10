package aau.cc;

import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;

import java.util.ArrayList;
import java.util.List;

public class MarkdownFormatter {

    private final boolean translationSkipped;
    private final int crawlingDepth;

    public MarkdownFormatter(boolean translationSkipped, int crawlingDepth) {
        this.translationSkipped = translationSkipped;
        this.crawlingDepth = crawlingDepth;
    }

    public List<String> getFormattedHeaderContent(CrawledWebsite website) {
        List<String> header = new ArrayList<>();
        header.add("# Crawled Website: <a>" + website.getUrl() + "</a>");
        header.add("### Depth: " + website.getDepth());
        header.add("### Source language: " + website.getSource().getDisplayName());
        header.add("### Target language: " + website.getTarget().getDisplayName());
        if (translationSkipped) {
            header.add("### Translation has been skipped!");
        }
        return header;
    }

    public List<String> getFormattedMainContent(CrawledWebsite website) {
        List<String> mainContent = new ArrayList<>();
        mainContent.add("\n## Overview of: " + website.getUrl() + "\n\n");
        if (!website.hasBrokenUrl()) {
            mainContent.addAll(getFormattedHeadings(website.getHeadings(), 0));
            mainContent.add("\n___\n");
            mainContent.addAll(getFormattedSubWebsiteContentRecursively(website, 1));
        } else {
            mainContent.add("\n## Error while reaching/parsing Website!\n");
        }
        return mainContent;
    }

    public List<String> getFormattedBrokenLinks(List<String> brokenLinks, int printDepth) {
        List<String> formattedBrokenLinks = new ArrayList<>();
        for (String link : brokenLinks) {
            formattedBrokenLinks.add("### <span style=\"color:gray\">" + getArrowAtDepth(printDepth) + "Broken Link to: </span>" + link);
        }
        return formattedBrokenLinks;
    }

    private List<String> getFormattedHeadings(List<Heading> headings, int printDepth) {
        List<String> headingTexts = Heading.getHeadingsTextsAsList(headings);
        for (int i = 0; i < headings.size(); i++) {
            String indentation = "#".repeat(headings.get(i).getDepth()) + " ";
            if (printDepth < 1) {
                headingTexts.set(i, indentation + headingTexts.get(i));
            } else {
                headingTexts.set(i, indentation + getArrowAtDepth(printDepth) + headingTexts.get(i));
            }
        }
        return headingTexts;
    }

    public List<String> getFormattedSubWebsiteContentRecursively(CrawledWebsite parentWebsite, int printDepth) {
        List<String> formattedContent = new ArrayList<>();
        formattedContent.add("<br>\n### Children of: " + parentWebsite.getUrl());
        addContentOfSubWebsites(parentWebsite, formattedContent, printDepth);
        for (CrawledWebsite subWebsite : parentWebsite.getLinkedWebsites()) {
            if (printDepth <= crawlingDepth) {
                formattedContent.addAll(getFormattedSubWebsiteContentRecursively(subWebsite, printDepth + 1));
            }
        }
        return formattedContent;
    }

    private void addContentOfSubWebsites(CrawledWebsite parentWebsite, List<String> formattedContent, int printDepth){
        if (handleNoLinkedWebsite(parentWebsite, formattedContent, printDepth)) return;

        for (CrawledWebsite subWebsite : parentWebsite.getLinkedWebsites()) {
            formattedContent.add("___");
            formattedContent.add("### " + getArrowAtDepth(printDepth) + "Link to: " + subWebsite.getUrl());
            formattedContent.addAll(getFormattedHeadings(subWebsite.getHeadings(), printDepth));
            formattedContent.addAll(getFormattedBrokenLinks(parentWebsite.getBrokenLinks(), printDepth));
        }
    }

    private boolean handleNoLinkedWebsite(CrawledWebsite website, List<String> formattedContent, int printDepth) {
        if (website.getLinkedWebsites().isEmpty()) {
            if (website.getBrokenLinks().isEmpty()) {
                formattedContent.add("### No links found");
            } else {
                formattedContent.addAll(getFormattedBrokenLinks(website.getBrokenLinks(), printDepth));
            }
            return true;
        } else {
            return false;
        }
    }

    private static String getArrowAtDepth(int depth){
        return "-".repeat(depth) + "-> ";
    }
}
