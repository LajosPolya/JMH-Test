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

import org.openjdk.jmh.annotations.Benchmark;

import com.github.lajospolya.meterRegistry.SimpleCounter;
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
     * The state contains an instance of {@link SimpleCounter} which is used to create and/or increment instances of
     * {@link io.micrometer.core.instrument.Counter}
     */
    @State(Scope.Benchmark)
    public static class SimpleCounterBenchmarkState {
        private final SimpleCounter simpleCounter = new SimpleCounter();
    }

    /**
     * This state is shared across the entire benchmark.
     * The state contains an instance of {@link SimpleCounter} which is used to create and/or increment instances of
     * {@link io.micrometer.core.instrument.Counter}
     * <p>
     * The state also contains an array of {@link com.github.lajospolya.meterRegistry.SimpleCounter.EnumState}. This
     * array is pseudo randomly initialized to a size dictated by {@link BenchmarkEnumState#size}.
     */
    @State(Scope.Benchmark)
    public static class BenchmarkEnumState {
        private final SimpleCounter simpleCounter = new SimpleCounter();

        private final int size = 1_000_000;
        private final SimpleCounter.EnumState[] states = initState();


        private SimpleCounter.EnumState[] initState() {
            final int numStates = SimpleCounter.EnumState.values().length;
            final SimpleCounter.EnumState[] tempStates = new SimpleCounter.EnumState[size];
            for (int i = 0; i < size; i++) {
                tempStates[i] = SimpleCounter.EnumState.values()[(int) (i * 3L) % numStates];
            }
            return tempStates;
        }
    }

    @Benchmark
    public void measureShared(SimpleCounterBenchmarkState state) {
        state.simpleCounter.increment();
    }

    @Benchmark
    public void measureSharedCreate(SimpleCounterBenchmarkState state) {
        state.simpleCounter.createAndIncrement();
    }

    @Benchmark
    public void measureSharedCreateEnum(BenchmarkEnumState state) {
        for (SimpleCounter.EnumState enumState : state.states) {
            state.simpleCounter.createAndIncrement(enumState);
        }
    }

    @Benchmark
    public void measureSharedEnum(BenchmarkEnumState state) {
        for (SimpleCounter.EnumState enumState : state.states) {
            state.simpleCounter.incrementEnum(enumState);
        }
    }

    @Benchmark
    public void measureSharedHash(BenchmarkEnumState state) {
        for (SimpleCounter.EnumState enumState : state.states) {
            state.simpleCounter.incrementHash(enumState);
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
