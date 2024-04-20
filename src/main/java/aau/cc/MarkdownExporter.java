package aau.cc;

import aau.cc.model.CrawledWebsite;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarkdownExporter {

    private boolean skipTranslation;

    public MarkdownExporter(boolean skipTranslations) {
        this.skipTranslation = skipTranslations;
    }

    public void generateContentAndExportToFile(String filePath, CrawledWebsite website) {
        if (!filePath.endsWith(".md")) {
            filePath += ".md";
        }
        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new FileWriter(filePath));
            writeToFile(writer, getFormattedHeaderContent(website, skipTranslation));
            writer.write("\n## Overview:\n");
            writeToFile(writer, getFormattedMainContent(website,0));
            writer.write("\n___\n");
            writeToFile(writer, getFormattedSubWebsiteContent(website, website.getDepth()));
            writer.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public List<String> getFormattedHeaderContent(CrawledWebsite website, boolean skipTranslation) {
        List<String> header = new ArrayList<>();
        header.add("# Crawled Website: <a>" + website.getUrl() + "</a>");
        header.add("### Depth: " + website.getDepth());
        header.add("### Source language: " + website.getSource().getDisplayName());
        header.add("### Target language: " + website.getTarget().getDisplayName());
        if (skipTranslation) {
            header.add("### Translation has been skipped!");
        }
        return header;
    }

    public List<String> getFormattedMainContent(CrawledWebsite website, int depthOffset) {
        List<String> headings = website.getHeadingsTextsAsList();
        int[] depths = website.getHeadingsDepths();
        if (skipTranslation){
            return getFormattedContent(headings, depths, depthOffset);
        }else{
            Translator translator = new Translator(website.getTarget(), website.getSource());
            return getFormattedContent(translator.getMultipleTranslations(headings), depths, depthOffset);
        }
    }

    public List<String> getFormattedContent(List<String> headings, int[] depths, int depthOffset) {
        List<String> formattedContent = new ArrayList<>();
        for (int i = 0; i < depths.length; i++) {
            formattedContent.add("#".repeat(Math.max(0, Math.min(depths[i] + depthOffset, 4))) + " " + headings.get(i));
        }
        return formattedContent;
    }

    public List<String> getFormattedSubWebsiteContent(CrawledWebsite parentWebsite, int depth) {
        List<String> formattedContent = new ArrayList<>();
        formattedContent.add("<br>\n\n___");
        formattedContent.add("\n### Children of: " + parentWebsite.getUrl());
        formattedContent.add("___");
        getSubWebsiteContentRecursively(parentWebsite, formattedContent, depth);
        return formattedContent;
    }

    private void getSubWebsiteContentRecursively(CrawledWebsite parentWebsite, List<String> formattedContent, int depth) {
        if (parentWebsite.getLinkedWebsites().isEmpty()){
            formattedContent.add("### No links found");
            return;
        }

        for (CrawledWebsite subWebsite : parentWebsite.getLinkedWebsites()) {
            formattedContent.add("### Link to: " + subWebsite.getUrl());
            formattedContent.addAll(getFormattedMainContent(subWebsite, 2));
            formattedContent.add("<br>\n");
            if (depth > 1) {
                formattedContent.addAll(getFormattedSubWebsiteContent(subWebsite, parentWebsite.getDepth() - 1));
            }
        }
    }

    private void writeToFile(BufferedWriter writer, List<String> lines) throws IOException {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }

    public boolean isSkipTranslation() {
        return skipTranslation;
    }

    public void setSkipTranslation(boolean skipTranslation) {
        this.skipTranslation = skipTranslation;
    }
}