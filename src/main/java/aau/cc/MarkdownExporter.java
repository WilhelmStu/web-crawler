package aau.cc;

import aau.cc.model.CrawledWebsite;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MarkdownExporter {

    private MarkdownFormatter formatter;

    public MarkdownExporter(MarkdownFormatter formatter) {
        this.formatter = formatter;
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
        writeToFile(writer, formatter.getFormattedHeaderContent(website));
        writeToFile(writer, formatter.getFormattedMainContent(website));
        writer.write("\n___\n### End of: " + website.getUrl() + "\n___\n<br>");
        writer.close();
    }

    private void writeToFile(BufferedWriter writer, List<String> lines) throws IOException {
        for (String line : lines) {
            writer.write(line);
            writer.newLine();
        }
    }

    public void setFormatter(MarkdownFormatter formatter) {
        this.formatter = formatter;
    }
}
