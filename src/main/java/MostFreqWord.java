import org.apache.commons.cli.MissingArgumentException;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;

import java.util.Arrays;
import java.util.List;

public class MostFreqWord {

    public static void main(String[] args) throws MissingArgumentException {
        if (args.length < 1) {
            System.exit(1);
            throw new MissingArgumentException("Please provide file path as argument!");
        }

        //Create a SparkContext to initialize
        SparkConf conf = new SparkConf().setMaster("local").setAppName("Most Frequent Words Counter");

        // Create a Java version of the Spark Context
        JavaSparkContext sc = new JavaSparkContext(conf);

        // Load the text into a Spark RDD, which is a distributed representation of each line of text
        JavaRDD<String> textFile = sc.textFile(args[0]);
        JavaPairRDD<String, Integer> counts = textFile
                .flatMap(s -> Arrays.asList(s.split("[ ,]")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);
        JavaPairRDD<Integer, String> swapped = counts
                .mapToPair((PairFunction<Tuple2<String, Integer>, Integer, String>) Tuple2::swap)
                .sortByKey(false);
        List<Tuple2<Integer, String>> output = swapped.collect();
        for (Tuple2<Integer, String> tuple : output) {
            System.out.println("(" + tuple._1() + ": " + tuple._2() + ")");
        }

    }
}
