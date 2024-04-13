package aau.cc;

import aau.cc.model.CrawledWebsite;
import aau.cc.model.Heading;
import aau.cc.model.Language;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args)  {
        //Translator translator = new Translator();
        //System.out.println(translator.getAvailableLanguages());
        //System.out.println(translator.getSingleTranslation("Hello World!", Language.FRENCH));
        //System.out.println(translator.getSingleTranslation("Hello World", Language.GERMAN));

        CrawledWebsite testSite = new CrawledWebsite("https://www.test.com", 1);
        List<Heading> headings = new ArrayList<>();
        headings.add(new Heading("Test Überschrift Nummer 1", 1));
        headings.add(new Heading("Test Überschrift Nummer 2", 1));
        headings.add(new Heading("Test Überschrift Nummer 3", 2));
        testSite.setHeadings(headings);
        testSite.setSource(Language.GERMAN);
        testSite.setTarget(Language.ENGLISH);
        CrawledWebsite childSite = new CrawledWebsite("https://www.test1.com", 1);
        childSite.setHeadings(headings);
        childSite.setSource(Language.GERMAN);
        childSite.setTarget(Language.ENGLISH);

        testSite.addLinkedWebsite(childSite);

        MarkdownExporter mdExporter = new MarkdownExporter(true);

        mdExporter.exportToMdFile("Test.md", testSite);

    }
}
