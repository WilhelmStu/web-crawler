package aau.cc;

import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarkdownExporter {

    private final boolean translationSkipped;
    private final int crawlingDepth;

    public MarkdownExporter(boolean skipTranslations, int crawlingDepth) {
        this.translationSkipped = skipTranslations;
        this.crawlingDepth = crawlingDepth;
    }

    public void writeContentToMarkdownFile(String filePath, List<CrawledWebsite> websites) {
        File mdFile = getMarkdownFile(filePath);
        try {
            for (CrawledWebsite website : websites) {
                System.out.println("Writing result of: " + website.getUrl() + " to: " + mdFile.getAbsolutePath());
                getContentAndWriteToFile(mdFile, website);
            }
        } catch (IOException e) {
            System.err.println("Error during file export:" + e.getMessage());
        }
    }

    public void deleteMarkdownFileIfExists(String filePath) {
        File mdFile = getMarkdownFile(filePath);
        if (mdFile.exists()) {
            if (mdFile.delete()) {
                System.out.println("Deleted old file: " + mdFile.getAbsolutePath());
            } else {
                System.err.println("Failed to delete file: " + mdFile.getAbsolutePath());
            }
        }
    }

    private File getMarkdownFile(String filePath) {
        if (!filePath.endsWith(".md")) {
            filePath += ".md";
        }
        return new File(filePath);
    }

    private void getContentAndWriteToFile(File file, CrawledWebsite website) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
        writeToFile(writer, getFormattedHeaderContent(website));
        writeToFile(writer, getFormattedMainContent(website));
        writer.write("\n___\n### End of: " + website.getUrl() + "\n___\n<br>");
        writer.close();
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
        formattedContent.add("<br>");
        formattedContent.add("\n### Children of: " + parentWebsite.getUrl());
        collectSubWebsiteContent(parentWebsite, formattedContent, printDepth);
        return formattedContent;
    }

    private void collectSubWebsiteContent(CrawledWebsite parentWebsite, List<String> formattedContent, int printDepth) {
        addContentOfSubWebsites(parentWebsite, formattedContent, printDepth);
        for (CrawledWebsite subWebsite : parentWebsite.getLinkedWebsites()) {
            if (printDepth <= crawlingDepth) {
                formattedContent.addAll(getFormattedSubWebsiteContentRecursively(subWebsite, printDepth + 1));
            }
        }
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

    private void writeToFile(BufferedWriter writer, List<String> lines) throws IOException {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }

    private String getArrowAtDepth(int depth){
        return "-".repeat(depth) + "-> ";
    }
}
