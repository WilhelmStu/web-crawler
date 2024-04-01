package aau.cc.model;

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
}
