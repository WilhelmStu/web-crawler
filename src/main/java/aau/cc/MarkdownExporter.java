package aau.cc;

import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarkdownExporter {

    private boolean skipTranslation;

    public MarkdownExporter(boolean skipTranslation) {
        this.skipTranslation = skipTranslation;
    }

    public void exportToMdFile(String filePath, CrawledWebsite website) {
        if (!filePath.endsWith(".md")) {
            filePath += ".md";
        }
        BufferedWriter writer;

        try {
            writer = new BufferedWriter(new FileWriter(filePath));

            writer.write("# Crawled Website: <a>" + website.getUrl() + "</a>\n");
            writer.write("### Depth: " + website.getDepth() + "\n");
            writer.write("### Source language: " + website.getSource().getDisplayName() + "\n");
            writer.write("### Target language: " + website.getTarget().getDisplayName() + "\n\n");
            if (skipTranslation) {
                writer.write("### Translation has been skipped!\n");
            }
            writer.write("## Overview:\n");

            writeHeadings(writer, website);

            for (CrawledWebsite subWebsite : website.getLinkedWebsites()) {
                writer.write("\n___\n");
                writer.write("## Link to: " + subWebsite.getUrl() + "\n");
                writeHeadings(writer, subWebsite);
                if (website.getDepth() > 1) {
                    writeSubWebsites(writer, subWebsite, website.getDepth() - 1);
                }
            }
            writer.close();

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }


    }

    public void writeHeadings(BufferedWriter writer, CrawledWebsite website) throws IOException {
        Translator translator = new Translator(website.getTarget(), website.getSource());
        List<Heading> headings = website.getHeadings();
        List<String> toTranslate = new ArrayList<>();
        int[] depths = new int[website.getHeadings().size()];
        for (int i = 0; i < headings.size(); i++) {
            toTranslate.add(headings.get(i).getText());
            depths[i] = headings.get(i).getDepth();
        }
        List<String> translatedHeadings = skipTranslation ? new ArrayList<>() : translator.getMultipleTranslations(toTranslate);
        StringBuilder strBuilder = new StringBuilder();

        for (int i = 0; i < depths.length; i++) {
            strBuilder.append("#".repeat(Math.max(0, depths[i])));
            strBuilder.append(" ");
            strBuilder.append(skipTranslation ? toTranslate.get(i) : translatedHeadings.get(i));
            strBuilder.append("\n");
        }
        writer.write(strBuilder.toString());
        writer.write("\n___\n");
    }

    public void writeSubWebsites(BufferedWriter writer, CrawledWebsite website, int depth) throws IOException {
        writer.write("\n___\n");
        writer.write("\nChildren of: " + website.getUrl() + "\n");
        for (CrawledWebsite subWebsite : website.getLinkedWebsites()) {
            writer.write("Link to: " + subWebsite.getUrl() + "\n");
            writeHeadings(writer, subWebsite);
            if (depth > 1) {
                writeSubWebsites(writer, subWebsite, website.getDepth() - 1);
            }
        }
    }

    public boolean isSkipTranslation() {
        return skipTranslation;
    }

    public void setSkipTranslation(boolean skipTranslation) {
        this.skipTranslation = skipTranslation;
    }
}
