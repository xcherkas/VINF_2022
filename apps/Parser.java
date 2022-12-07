package vinf;

import java.util.ArrayList;
import java.lang.StringBuilder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
  public final static Pattern regexPage = Pattern.compile("<page>.*", Pattern.DOTALL);
  public final static Pattern regexTitle = Pattern.compile("<(\\w+)>\\s*(?<title>.+?)\\s*((?:\\(.+\\))?)</\\1>.*<(\\w+).*>(\\[.*\\])?(\\{.*\\})?\\s*'{0,3}(\\k<title>)+.*ozlišovacia stránka\\}\\}[^<>]*</\\4>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

  public final static Pattern regexAnchor = Pattern.compile("\\[\\[(?<url>[^\\[\\]:\\|]*)(?:\\|.*)?\\]\\]");
  public final static Pattern regexKeyValue = Pattern.compile("<(?<key>\\w*).*>(?<value>.+)</\\1>");

  private final static String NEW_LINE_DELIMETER = "-=NL=-";
  private static ArrayList<String> buffer = new ArrayList<String>();

  Parser() {}

  public Page parseByLines(String data) {
    Matcher m = regexPage.matcher(data);

    if (!m.find())
      return null;

    String[] pageLines = m.group(0).split("\n");
    this.flush();
    for (String line : pageLines)
      this.push(line);

    return this.parse();
  }

  public Page parse() {
    String joined = String.join("\n", buffer);
    if (!joined.toLowerCase().contains("{{rozlišovacia stránka}}"))
      return this.flush();

    // Parse title
    final Matcher matcherTitle = regexTitle.matcher(joined);
    if (!matcherTitle.find())
      return this.flush();

    String title = matcherTitle.group("title");

    // Parse annotation
    final Matcher matcherText = regexKeyValue.matcher(String.join(Parser.NEW_LINE_DELIMETER, buffer));
    if (!matcherText.find())
      return this.flush();

    String text = "N/A";
    while(matcherText.find()) {
      String matched = matcherText.group(2);
      if (matched.length() > text.length() && matched.contains(Parser.NEW_LINE_DELIMETER))
        text = matched;
    }

    // Parse anchors
    final Matcher matcherAnchor = regexAnchor.matcher(text);
    ArrayList<String> anchors = new ArrayList<String>();
    while (matcherAnchor.find())
      anchors.add(matcherAnchor.group(1));

    text = text.replaceAll(Parser.NEW_LINE_DELIMETER, "\n");

    // Clean comments
		text = text.replaceAll("<!--[^>]+>", "");

		// Clean comments v2
		text = text.replaceAll("\\{\\{.+?\\}{2}", "");

		// Clean metas
		text = text.replaceAll("\\*? ?\\{\\{[^}]+\\}\\}","");

		// Remove attachments
		text = text.replaceAll("\\[\\[.+:.*\\]\\]", "");

		//Remove headers
		text = text.replaceAll("={2,}.+={2,}", "");

		// Remove styling
		text = text.replaceAll("\\'{2,3}", "");

    this.clear();

    return new Page(title, anchors, text);
  }

  public Page flush() {
    this.clear();
    return null;
  }

  public void push(String line) {
    buffer.add(line);
  }

  public void clear() {
    buffer.clear();
  }
}