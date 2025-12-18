package com.github.lajospolya;

import com.github.lajospolya.meterRegistry.ArbitraryState;
import com.github.lajospolya.meterRegistry.CachedEnumMapTaggedCounter;
import com.github.lajospolya.meterRegistry.CachedHashMapTaggedCounter;
import com.github.lajospolya.meterRegistry.CachedTaglessCounter;
import com.github.lajospolya.meterRegistry.ReinstantiatedTaggedCounter;
import com.github.lajospolya.meterRegistry.ReinstantiatedTaglessCounter;
import com.github.lajospolya.meterRegistry.TaggedCounter;
import com.github.lajospolya.meterRegistry.TaglessCounter;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class MicrometerCounterBenchmark {


    /**
     * This state is shared across the entire benchmark.
     * The state contains an instance of {@link ReinstantiatedTaglessCounter} which is used to create and increment instances of
     * {@link io.micrometer.core.instrument.Counter}
     */
    @State(Scope.Benchmark)
    public static class ReinstantiatedTaglessCounterBenchmarkState {
        private final TaglessCounter counter = new ReinstantiatedTaglessCounter();
    }

    /**
     * This state is shared across the entire benchmark.
     * The state contains an instance of {@link CachedTaglessCounter} which is used to increment instances of
     * {@link io.micrometer.core.instrument.Counter}
     */
    @State(Scope.Benchmark)
    public static class CachedTaglessCounterBenchmarkState {
        private final TaglessCounter counter = new CachedTaglessCounter();
    }

    /**
     * This state is shared across the entire benchmark.
     * The state contains an instance of {@link ReinstantiatedTaggedCounter} which is used to create and increment
     * instances of {@link io.micrometer.core.instrument.Counter}
     * <p>
     * The state also contains an array of {@link ArbitraryState}. The contents of the array is pseudo randomly
     * initialized with a size dictated by {@link ReinstantiatedTaggedBenchmarkState#size}.
     */
    @State(Scope.Benchmark)
    public static class ReinstantiatedTaggedBenchmarkState {
        private final TaggedCounter counter = new ReinstantiatedTaggedCounter();

        private final int size = 1_000_000;
        private final ArbitraryState[] states = initState();


        private ArbitraryState[] initState() {
            final int numStates = ArbitraryState.values().length;
            final ArbitraryState[] tempStates = new ArbitraryState[size];
            for (int i = 0; i < size; i++) {
                // Make Pseudo random by multiplying and adding numbers to `i` before the modulus operator is used
                tempStates[i] = ArbitraryState.values()[(int) (i * 3L + 3) % numStates];
            }
            return tempStates;
        }
    }

    /**
     * This state is shared across the entire benchmark.
     * The state contains an instance of {@link CachedEnumMapTaggedCounter} which is used to increment instances of
     * {@link io.micrometer.core.instrument.Counter}
     * <p>
     * The state also contains an array of {@link ArbitraryState}. The contents of the array is pseudo randomly
     * initialized with a size dictated by {@link CachedEnumMapBenchmarkState#size}.
     */
    @State(Scope.Benchmark)
    public static class CachedEnumMapBenchmarkState {
        private final TaggedCounter counter = new CachedEnumMapTaggedCounter();

        private final int size = 1_000_000;
        private final ArbitraryState[] states = initState();


        private ArbitraryState[] initState() {
            final int numStates = ArbitraryState.values().length;
            final ArbitraryState[] tempStates = new ArbitraryState[size];
            for (int i = 0; i < size; i++) {
                tempStates[i] = ArbitraryState.values()[(int) (i * 3L) % numStates];
            }
            return tempStates;
        }
    }

    /**
     * This state is shared across the entire benchmark.
     * The state contains an instance of {@link CachedHashMapTaggedCounter} which is used to increment instances of
     * {@link io.micrometer.core.instrument.Counter}
     * <p>
     * The state also contains an array of {@link ArbitraryState}. This array is pseudo randomly initialized to a size
     * dictated by {@link CachedHashMapBenchmarkState#size}.
     */
    @State(Scope.Benchmark)
    public static class CachedHashMapBenchmarkState {
        private final TaggedCounter counter = new CachedHashMapTaggedCounter();

        private final int size = 1_000_000;
        private final ArbitraryState[] states = initState();


        private ArbitraryState[] initState() {
            final int numStates = ArbitraryState.values().length;
            final ArbitraryState[] tempStates = new ArbitraryState[size];
            for (int i = 0; i < size; i++) {
                tempStates[i] = ArbitraryState.values()[(int) (i * 3L) % numStates];
            }
            return tempStates;
        }
    }

    @Benchmark
    public void notCachedTaglessCounter(ReinstantiatedTaglessCounterBenchmarkState state) {
        state.counter.increment();
    }

    @Benchmark
    public void cachedTaglessCounter(CachedTaglessCounterBenchmarkState state) {
        state.counter.increment();
    }

    @Benchmark
    public void notCachedTaggedCounters(ReinstantiatedTaggedBenchmarkState state) {
        for (ArbitraryState enumState : state.states) {
            state.counter.increment(enumState);
        }
    }

    @Benchmark
    public void enumMapCachedTaggedCounters(CachedEnumMapBenchmarkState state) {
        for (ArbitraryState enumState : state.states) {
            state.counter.increment(enumState);
        }
    }

    @Benchmark
    public void hashMapCachedTaggedCounters(CachedHashMapBenchmarkState state) {
        for (ArbitraryState enumState : state.states) {
            state.counter.increment(enumState);
        }
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     * You can run this test:
     *
     * a) Via the command line:
     *    $ mvn clean install
     *    $ java -jar target/benchmarks.jar MicrometerCounterBenchmark -t 4 -f 1
     *    (we requested 4 threads, single fork; there are also other options, see -h)
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MicrometerCounterBenchmark.class.getSimpleName())
                .threads(4)
                .forks(1)
                .build();

        new Runner(opt).run();
    }
}
