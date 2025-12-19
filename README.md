# JMH Test

This repository is set up to run JMH performance tests. Currently, it only contains one project which has all the
instructions necessary to run the tests.

### Importing JARs
This project depends on the following dependency:

```xml
<dependency>
    <groupId>com.github.lajospolya</groupId>
    <artifactId>meterRegistryPerformance</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

That dependency is not published to the Maven Central repository. Therefore, in order to run this test, you must pull
the [meterRegistryPerformance](https://github.com/LajosPolya/Micrometer-Performance) GitHub repo, build it, and publish
it to your local maven repository. Instructions to do this are found in the projet's README.

### Running the Benchmarks
It is recommended that the benchmarks are run on the CLI to get most reliable results.

Run the following to build the JARs.
```shell
mvn clean install
```

Then run the following to run the benchmarks.
```shell
java -jar target/benchmarks.jar MicrometerCounterBenchmark -t 4 -f 1
```

For more complete instructions, read the `.java` file you want to run, for example, [MicrometerCounterBenchmark.java](src/main/java/com/github/lajospolya/MicrometerCounterBenchmark.java).
