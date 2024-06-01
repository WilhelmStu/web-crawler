package aau.cc;

import aau.cc.model.CrawledWebsite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarkdownExporter {

    private boolean skipTranslation;

    public MarkdownExporter(boolean skipTranslations) {
        this.skipTranslation = skipTranslations;
    }

    public void generateMarkdownFile(String filePath, List<CrawledWebsite> websites) {
        File mdFile = getMarkdownFile(filePath);
        System.out.println(mdFile.getAbsolutePath());
        try {
            for (CrawledWebsite website : websites) {
                generateContentAndExportToFile(mdFile, website);
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void deleteMarkdownFileIfExists(String filePath) {
        File mdFile = getMarkdownFile(filePath);
        if (mdFile.exists()){
            if(mdFile.delete()){
                System.out.println("Deleted old file: " + mdFile.getAbsolutePath());
            }else{
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

    private void generateContentAndExportToFile(File file, CrawledWebsite website) throws IOException {
        BufferedWriter writer;
        writer = new BufferedWriter(new FileWriter(file, true));

        writeToFile(writer, getFormattedHeaderContent(website, skipTranslation));

        writer.write("\n## Overview of: " + website.getUrl() + "\n\n");
        System.out.println("Writing website: "+ website.getUrl() +" to: " + file.getAbsolutePath());
        writeToFile(writer, getFormattedMainContent(website, 0));
        writer.write("\n___\n");

        writeToFile(writer, getFormattedSubWebsiteContent(website, website.getDepth()));
        writer.write("\n___\n### End of: " + website.getUrl() + "\n___\n\n\n");
        writer.close();
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
        if (skipTranslation) {
            return getFormattedContent(headings, depths, depthOffset);
        } else {
            Translator translator = new Translator(website.getTarget(), website.getSource());
            return getFormattedContent(translator.getMultipleTranslations(headings), depths, depthOffset);
        }
    }

    public List<String> getFormattedBrokenLinks(List<String> brokenLinks) {
        List<String> formattedBrokenLinks = new ArrayList<>();
        for (String link : brokenLinks) {
            formattedBrokenLinks.add("### <span style=\"color:gray\"> Broken Link to: </span>" + link);
        }
        return formattedBrokenLinks;
    }

    private List<String> getFormattedContent(List<String> headings, int[] depths, int depthOffset) {
        List<String> formattedContent = new ArrayList<>();
        for (int i = 0; i < headings.size(); i++) {
            int repeatFor = Math.max(0, Math.min(depths[i] + depthOffset, 4));
            formattedContent.add("#".repeat(repeatFor) + " " + headings.get(i));
        }
        return formattedContent;
    }

    public List<String> getFormattedSubWebsiteContent(CrawledWebsite parentWebsite, int depth) {
        List<String> formattedContent = new ArrayList<>();
        formattedContent.add("<br>\n\n___");
        formattedContent.add("\n### Children of: " + parentWebsite.getUrl());
        getSubWebsiteContentRecursively(parentWebsite, formattedContent, depth);
        return formattedContent;
    }

    private void getSubWebsiteContentRecursively(CrawledWebsite parentWebsite, List<String> formattedContent, int depth) {
        if (parentWebsite.getLinkedWebsites().isEmpty()) {
            if (parentWebsite.getBrokenLinks().isEmpty()) {
                formattedContent.add("### No links found");
            } else {
                formattedContent.addAll(getFormattedBrokenLinks(parentWebsite.getBrokenLinks()));
            }
            return;
        }

        for (CrawledWebsite subWebsite : parentWebsite.getLinkedWebsites()) {
            formattedContent.add("___");
            formattedContent.add("### Link to: " + subWebsite.getUrl());
            formattedContent.addAll(getFormattedMainContent(subWebsite, 2));
            formattedContent.add("\n");
            formattedContent.addAll(getFormattedBrokenLinks(parentWebsite.getBrokenLinks()));
        }
        for (CrawledWebsite subWebsite : parentWebsite.getLinkedWebsites()) {
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
