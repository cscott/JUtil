// BinomialHeapTest.java, created from the self-test in BinomialHeap.
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * {@link BinomialHeapTest} tests the parts of {@link BinomialHeap}
 * that {@link HeapTest} does not: its efficient {@code union}, and
 * copying.
 */
class BinomialHeapTest {
    private static final Comparator<Integer> IC = Integer::compare;

    private static BinomialHeap<Integer,Integer> heap(int... keys) {
	BinomialHeap<Integer,Integer> h = new BinomialHeap<>(IC);
	for (int k : keys)
	    h.insert(k, null);
	return h;
    }
    /** Empties the heap with {@code extractMinimum}. */
    private static List<Integer> drain(Heap<Integer,Integer> h) {
	List<Integer> l = new ArrayList<>();
	while (!h.isEmpty())
	    l.add(h.extractMinimum().getKey());
	return l;
    }
    private static List<Integer> sorted(int... keys) {
	List<Integer> l = new ArrayList<>();
	for (int k : keys) l.add(k);
	l.sort(IC);
	return l;
    }

    private static final int[] HEAP1 = { 15, 33, 28, 41, 7, 25, 12 };
    private static final int[] HEAP2 = { 6, 44, 10, 17, 29, 31, 48, 50, 8, 22,
					 23, 24, 30, 32, 45, 55, 3, 37, 18 };

    /** The heap on page 404 of CLR. */
    @Test void clr404() {
	int[] ia = { 29, 6, 14, 38, 17, 8, 11, 27, 1, 25, 12, 18, 10 };
	BinomialHeap<Integer,Integer> h = heap(ia);
	assertEquals(13, h.size());
	assertEquals(1, h.minimum().getKey());
	assertEquals(sorted(ia), drain(h));
    }

    /** The union example on pages 410-411 of CLR. */
    @Test void union() {
	BinomialHeap<Integer,Integer> h1 = heap(HEAP1), h2 = heap(HEAP2);
	h1.union(h2);
	assertEquals(26, h1.size());
	assertTrue(h2.isEmpty());
	assertEquals(3, h1.minimum().getKey());

	h1.insert(3, null); // duplicate key.
	assertEquals(27, h1.size());
	int[] all = new int[27];
	System.arraycopy(HEAP1, 0, all, 0, HEAP1.length);
	System.arraycopy(HEAP2, 0, all, HEAP1.length, HEAP2.length);
	all[26] = 3;
	assertEquals(sorted(all), drain(h1));
	assertEquals(0, h1.size());
    }

    /** Union with a heap of another kind. */
    @Test void unionNonBinomial() {
	BinomialHeap<Integer,Integer> h1 = heap(HEAP1);
	Heap<Integer,Integer> h2 = new BinaryHeap<>(IC);
	for (int k : HEAP2) h2.insert(k, null);
	h1.union(h2);
	assertEquals(26, h1.size());
	assertTrue(h2.isEmpty());
	int[] all = Arrays.copyOf(HEAP1, 26);
	System.arraycopy(HEAP2, 0, all, HEAP1.length, HEAP2.length);
	assertEquals(sorted(all), drain(h1));
    }

    /** The copy constructor and {@code clone()} make independent
     *  heaps. */
    @Test void copy() {
	BinomialHeap<Integer,Integer> h = heap(HEAP2);
	BinomialHeap<Integer,Integer> copy = new BinomialHeap<>(h);
	BinomialHeap<Integer,Integer> clone = h.clone();
	assertEquals(sorted(HEAP2), drain(h));
	assertEquals(sorted(HEAP2), drain(copy));

	// delete through the entries, then drain with minimum() and delete()
	Iterator<Map.Entry<Integer,Integer>> it = clone.entries().iterator();
	it.next(); it.next(); it.next();
	Map.Entry<Integer,Integer> victim = it.next();
	int deleted = victim.getKey();
	clone.delete(victim);
	assertEquals(HEAP2.length - 1, clone.size());
	List<Integer> expected = sorted(HEAP2);
	expected.remove(Integer.valueOf(deleted));
	List<Integer> got = new ArrayList<>();
	while (!clone.isEmpty()) {
	    Map.Entry<Integer,Integer> me = clone.minimum();
	    clone.delete(me);
	    got.add(me.getKey());
	}
	assertEquals(expected, got);
    }
}
