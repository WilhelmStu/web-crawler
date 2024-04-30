package aau.cc.model;

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
