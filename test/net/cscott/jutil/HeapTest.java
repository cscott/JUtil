// HeapTest.java, created from the self-tests in BinaryHeap, BinomialHeap
// and FibonacciHeap.
// Copyright (C) 1999-2002 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * {@link HeapTest} runs the same tests on each {@link Heap}
 * implementation.
 */
class HeapTest {
    /** Makes the heaps to test. */
    record Factory(String name,
		   Supplier<Heap<String,String>> empty,
		   BiFunction<Collection<Map.Entry<Integer,Integer>>,
			      Comparator<Integer>,
			      Heap<Integer,Integer>> fromEntries) {
	@Override public String toString() { return name; }
    }
    static Stream<Factory> factories() {
	return Stream.of(
	    new Factory("BinaryHeap", BinaryHeap::new, BinaryHeap::new),
	    new Factory("BinomialHeap", BinomialHeap::new, BinomialHeap::new),
	    new Factory("FibonacciHeap", FibonacciHeap::new, FibonacciHeap::new)
	);
    }

    /** Returns the entries {@code (i, i)} for each {@code i}. */
    static List<Map.Entry<Integer,Integer>> entries(int... el) {
	List<Map.Entry<Integer,Integer>> l = new ArrayList<>();
	for (int i : el)
	    l.add(new PairMapEntry<>(i, i));
	return l;
    }

    /** Builds the heap from CLR, page 146/151, and extracts the keys
     *  in sorted order.  A {@code null} comparator means the keys'
     *  natural order. */
    @ParameterizedTest(name="{0}") @MethodSource("factories")
    void buildAndExtract(Factory f) {
	Heap<Integer,Integer> h = f.fromEntries().apply
	    (entries(-4, -1, -3, -2, -16, -9, -10, -14, -8, -7), null);
	assertEquals(10, h.size());
	assertTrue(!h.isEmpty());
	assertEquals(-16, h.minimum().getKey());
	h.insert(-15, -15);
	assertEquals(11, h.size());
	assertEquals(-16, h.minimum().getKey());
	for (int k : new int[] { -16, -15, -14, -10, -9, -8, -7, -4, -3, -2, -1 })
	    assertEquals(k, h.extractMinimum().getKey());
	assertTrue(h.isEmpty());
	assertEquals(0, h.size());
	h.insert(1, 1);
	h.clear();
	assertTrue(h.isEmpty());
	assertEquals(0, h.size());
    }

    /** Tests {@code delete}, {@code decreaseKey} and
     *  {@code updateKey}. */
    @ParameterizedTest(name="{0}") @MethodSource("factories")
    void deleteAndUpdate(Factory f) {
	Heap<String,String> h = f.empty().get();
	assertTrue(h.isEmpty());
	assertEquals(0, h.size());

	List<Map.Entry<String,String>> mel = new ArrayList<>();
	mel.add(h.insert("C", "c1"));
	mel.add(h.insert("S", "s1"));
	mel.add(h.insert("A", "a"));
	mel.add(h.insert("S", "s2"));
	mel.add(h.insert("C", "c2"));
	mel.add(h.insert("O", "o"));
	mel.add(h.insert("T", "t1"));
	mel.add(h.insert("T", "t2"));
	mel.add(h.insert("Z", "z"));
	mel.add(h.insert("M", "m"));

	assertEquals("a", h.extractMinimum().getValue());
	h.decreaseKey(mel.get(3), "B"); // s2
	assertEquals("s2", h.extractMinimum().getValue());
	h.delete(mel.get(4)); // c2
	assertEquals("c1", h.extractMinimum().getValue());
	h.updateKey(mel.get(9), "P"); // m
	assertEquals("o", h.extractMinimum().getValue());
	assertEquals("m", h.extractMinimum().getValue());
	assertEquals(4, h.size());
    }
}
