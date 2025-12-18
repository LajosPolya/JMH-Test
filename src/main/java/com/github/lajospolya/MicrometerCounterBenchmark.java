/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

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
     * The state also contains an array of {@link ArbitraryState}. This array is pseudo randomly initialized to a size
     * dictated by {@link ReinstantiatedTaggedBenchmarkState#size}.
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
     * The state also contains an array of {@link ArbitraryState}. This array is pseudo randomly initialized to a size
     * dictated by {@link CachedEnumMapBenchmarkState#size}.
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
