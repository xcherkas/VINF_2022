package vinf;

import java.util.ArrayList;

public class Page {
  public static final String ROW_SEPARATOR = "-=NR=-";

  Page (String title, ArrayList<String> anchors, String text) {
    this.title = title;
    this.anchors = anchors;
    this.text = text;
  }

  private String title;
  public String getTitle() {
    return this.title;
  }
  public void setTitle(String title) {
    this.title = title;
  }

  private ArrayList<String> anchors;
  public ArrayList<String> getAnchors() {
    return this.anchors;
  }
  public void setAnchors(ArrayList<String> anchors) {
    this.anchors = anchors;
  }

  private String text;
  public String getText() {
    return this.text;
  }
  public void setText(String text) {
    this.text = text;
  }

  public static Page deserialize(String line) {
    String[] parts = line.replaceAll(ROW_SEPARATOR, "\n").split("\t");
    String title = parts[0];
    ArrayList<String> anchors = new ArrayList<String>();
    for (String anchor : parts[1].split(","))
      anchors.add(anchor);
    String text = parts[2];
    return new Page(title, anchors, text);
  }

  public String serialize() {
    return (this.title + "\t" + String.join(",", this.anchors) + "\t" + this.text).replaceAll("\n", ROW_SEPARATOR);
  }

  public String print() {
    return String.format("\nTitle: %s\nAnchors: %s\nText:\n%s\n", this.title, String.join(", ", this.anchors), this.text);
  }
}