import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import vinf.Page;

public class App {

  private final static Index index = new Index('.');

	public static void main(String[] args) throws IOException {
    preparse();
    commandLine();
	}


  public static void preparse() throws IOException {

    final File folder = new File("/opt/spark-data/results/.");
    final List<File> fileList = Arrays.asList(folder.listFiles(new FileFilter() {
      public boolean accept(File pathname) {
          return pathname.isFile();
      }
    }));

    for (File file : fileList) {
      if (file.getName().equals("_SUCCESS"))
        continue;

      if (file.getName().endsWith(".crc"))
        continue;

      BufferedReader reader = new BufferedReader(new FileReader(file));
      String line = reader.readLine();
      while (line != null) {
        Page page = Page.deserialize(line);
        if (page == null)
          continue;

        index.add(page.getTitle().toLowerCase(), page);
        line = reader.readLine();
      }
      reader.close();
    }
  }

  public static void commandLine() throws IOException {
    BufferedReader stdInReader = new BufferedReader(new InputStreamReader(System.in));

    while (true) {

      System.out.print("Enter a title to search:\n>> ");
      String line = stdInReader.readLine();

      if (line == null)
        break;

      if (line.length() == 0) {
        System.out.println("Empty input. Try again.\n>> ");
        continue;
      }

      ArrayList<Page> pages = index.get(line.toLowerCase());

      if (pages == null) {
        System.out.println("No results found.\n>> ");
        continue;
      }

      if (pages.size() == 1) {
        System.out.println(pages.get(0).print());
        continue;
      }

      int i = 0;
      System.out.println("Found " + pages.size() + " pages with the simmilar title:");
      for (Page page : pages)
        System.out.println("> " + (++i) + ". " + page.getTitle());
      System.out.println("> 0. Exit");

      System.out.print("Enter a number of a page to see its content:\n>> ");
      line = stdInReader.readLine();
      if (line == null)
        break;

      if (line.length() == 0)
        continue;

      int index = Integer.parseInt(line);
      if (index == 0)
        continue;

      System.out.println(pages.get(index - 1).print());
    }
    stdInReader.close();
  }
}