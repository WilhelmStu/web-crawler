package aau.cc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Heading {
    private String text;
    private int depth;

    public Heading(String text, int depth) {
        this.text = text;
        this.depth = depth;
    }

    public Heading(String text) {
        this.text = text;
        this.depth = 1;
    }

    public static List<String> getHeadingsTextsAsList(List<Heading> headings) {
        if (headings == null) {
            return Collections.emptyList();
        }
        List<String> headingsTexts = new ArrayList<>();
        for (Heading heading : headings) {
            headingsTexts.add(heading.getText());
        }
        return headingsTexts;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Heading heading = (Heading) o;
        return depth == heading.depth && Objects.equals(text, heading.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, depth);
    }
}
