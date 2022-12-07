import java.util.Hashtable;
import java.util.ArrayList;

import vinf.Page;

public class Index {
  public final Hashtable<Character, Index> ht = new Hashtable<Character, Index>();
  public final ArrayList<Page> collection = new ArrayList<Page>();
  private final char nodeChar;

  Index(char x) {
    this.nodeChar = x;
  }

  public void add(String key, Page value) {
    boolean isLast = key.length() == 0;

    if (isLast) {
      this.collection.add(value);
      return;
    }

    char popped = key.charAt(0);
    String rest = key.substring(1);

    if (!this.ht.containsKey(popped))
      this.ht.put(popped, new Index(popped));

    this.ht.get(popped).add(rest, value);
  }

  public ArrayList<Page> get(String key) {
    boolean isLast = key.length() == 0;

    if (isLast) {
      ArrayList<Page> result = new ArrayList<Page>();
      result.addAll(this.collection);

      if (result.size() == 0)
        for (char k : this.ht.keySet())
          result.addAll(this.ht.get(k).get(""));

      return result;
    }

    char popped = key.charAt(0);
    String rest = key.substring(1);

    if (this.ht.containsKey(popped))
      return this.ht.get(popped).get(rest);

    return null;
  }
}