package vinf;

import java.util.List;
import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.api.java.JavaRDD;

public class InitiatorSpark {

  public final static String DATA_PATH = "/opt/spark-data/skwiki-latest-pages-meta-current.xml";
  public final static String OUT_PATH = "/opt/spark-data/results";
  public final static String TEST = "/opt/spark-data/demo.xml";

  public final static Parser parser = new Parser();

	public static void main(String[] args) {

    final String query = args.length > 0 && !("null".equals(args[0])) ? args[0] : null;
    final String dataPath = args.length > 1 ? args[1] : DATA_PATH;

    System.out.println("Found query -> " + query);
    System.out.println("Found dataPath -> " + dataPath);
    System.out.println("Starting Spark session...");
    SparkSession spark = SparkSession
      .builder()
      .appName("VINF Cherkas")
      .getOrCreate();

    JavaRDD<Page> promRes = spark
      .read()
      .option("lineSep", "</page>\n")
      .textFile(dataPath)
      .javaRDD()
      .map(s -> parser.parseByLines(s))
      .filter(p -> p != null);

    if (query != null)
      promRes = promRes
        .filter(p -> p.getTitle().toLowerCase().equals(query.toLowerCase()));


    JavaRDD<String> results = query != null ? promRes.map(p -> p.print() + "\n\n") : promRes.map(p -> p.serialize());

    results
      .saveAsTextFile(OUT_PATH);

    spark.stop();
  }
}
