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

    private boolean translationSkipped;

    public MarkdownExporter(boolean skipTranslations) {
        this.translationSkipped = skipTranslations;
    }

    public void generateMarkdownFile(String filePath, List<CrawledWebsite> websites) {
        File mdFile = getMarkdownFile(filePath);
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

    private void generateContentAndExportToFile(File file, CrawledWebsite website) throws IOException {
        BufferedWriter writer;
        writer = new BufferedWriter(new FileWriter(file, true));

        writeToFile(writer, getFormattedHeaderContent(website));

        writer.write("\n## Overview of: " + website.getUrl() + "\n\n");
        System.out.println("Writing website: " + website.getUrl() + " to: " + file.getAbsolutePath());
        if (!website.hasBrokenUrl()) {
            writeToFile(writer, getFormattedMainContent(website, 0));
            writer.write("\n___\n");
            writeToFile(writer, getFormattedSubWebsiteContent(website, website.getDepth()));
        } else {
            writer.write("\n## Error while reaching/parsing Website!\n");
        }
        writer.write("\n___\n### End of: " + website.getUrl() + "\n___\n\n\n");
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

    public List<String> getFormattedMainContent(CrawledWebsite website, int depthOffset) {
        List<String> headings = Heading.getHeadingsTextsAsList(website.getHeadings());
        int[] depths = website.getHeadingsDepths();
        return getFormattedContent(headings, depths, depthOffset);
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
        formattedContent.add("<br>\n___");
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

    public boolean isTranslationSkipped() {
        return translationSkipped;
    }

    public void setTranslationSkipped(boolean translationSkipped) {
        this.translationSkipped = translationSkipped;
    }
}
