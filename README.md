# Spark-Most-Frequent-Word-Counter

This is built using following technologies:
  - Spark 2.3.1
  - JDK 1.8.0_181

To run it:
1) Download Spark https://spark.apache.org/downloads.html

2) Run the program using the following command:
```
$ ./bin/spark-submit --class MostFreqWord --master local[4] most-freq-word-counter-1.0-SNAPSHOT.jar {path to a file}
```

#### How is Spark playing the game:

1) The DAG gets created without actually executing anything. 
The below snippet computes/executes nothing but just creates a dependency graph (DAG) laying out the sequence in which
**transformations** will be done returning us **RDD** of that transformation. 
```
JavaRDD<String> textFile = sc.textFile(args[0]);
JavaPairRDD<String, Integer> counts = textFile
    .flatMap(s -> Arrays.asList(s.split("[ ,]")).iterator())
    .mapToPair(word -> new Tuple2<>(word, 1))
    .reduceByKey((a, b) -> a + b);
JavaPairRDD<Integer, String> swapped = counts
    .mapToPair((PairFunction<Tuple2<String, Integer>, Integer, String>) Tuple2::swap)
    .sortByKey(false);
```

2) It is only when we call ``List<Tuple2<Integer, String>> output = swapped.collect();``, spark actually starts executing in the sequence laid out by the DAG created in step-1.

#### Which tasks get Distributed over clusters?

1) ``.flatMap(s -> Arrays.asList(s.split("[ ,]")).iterator())``

2)  ``.mapToPair(word -> new Tuple2<>(word, 1))``

3) ``.reduceByKey((a, b) -> a + b);``

4) ``.mapToPair((PairFunction<Tuple2<String, Integer>, Integer, String>) Tuple2::swap)``

5) ``.sortByKey(false);``

> The functions (lambda expressions) in the all of the above statements will get distributed over the clusters and execute in parallel.
However, when running locally over a laptop, they would run in the same JVM, but we can set the number of threads we want to run and
the functions will run in different threads.

> While running the Spark program - we can choose to use Yarn as the cluster manager which will in turn distribute tasks over cluster depending
upon available resources. 

```$ ./bin/spark-submit --class MostFreqWord --master yarn most-freq-word-counter-1.0-SNAPSHOT.jar {path to a file}```
This snippet will let yarn to distribute tasks over clusters

```$ ./bin/spark-submit --class MostFreqWord --master local[k] most-freq-word-counter-1.0-SNAPSHOT.jar {path to a file}```
This snippet runs Spark locally with k worker threads to distribute the tasks to.  