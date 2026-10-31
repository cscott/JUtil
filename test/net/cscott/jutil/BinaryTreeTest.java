// BinaryTreeTest.java, created from the self-test in BinaryTree.
// Copyright (C) 2001 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * {@link BinaryTreeTest} tests {@link BinaryTree} and
 * {@link RedBlackTree}.
 */
class BinaryTreeTest {
    /** Makes the trees to test. */
    record Factory(String name, Supplier<BinaryTree> empty) {
	@Override public String toString() { return name; }
    }
    static Stream<Factory> factories() {
	return Stream.of(new Factory("BinaryTree", BinaryTree::new),
			 new Factory("RedBlackTree", RedBlackTree::new));
    }

    /** Lists the keys of <code>t</code> with an inorder tree walk. */
    static List<Object> inorder(BinaryTree t) {
	List<Object> l = new ArrayList<>();
	inorder(t, t.root(), l);
	return l;
    }
    private static void inorder(BinaryTree t, BinaryTree.Node x,
				List<Object> l) {
	if (x == t.NIL) return;
	inorder(t, x.left(), l);
	l.add(x.key);
	inorder(t, x.right(), l);
    }
    /** Checks the tree's invariants. */
    static void check(BinaryTree t) {
	List<Object> keys = inorder(t);
	List<Object> sorted = new ArrayList<>(keys);
	sorted.sort(t.comp);
	assertEquals(sorted, keys, "keys out of order");
	if (t instanceof RedBlackTree rbt)
	    rbt.checkTree(rbt.root());
    }

    private static final String[] STRINGS = {
	"foo", "bar", "wistfully_3", "willing_4", "whilst_5",
	"whitling_6", "away_7", "at_8", "the_9", "bar" };

    @ParameterizedTest(name="{0}") @MethodSource("factories")
    void addAndRemove(Factory f) {
	BinaryTree t = f.empty().get();
	List<Object> expected = new ArrayList<>();
	for (String s : STRINGS) {
	    t.add(s);
	    expected.add(s);
	    check(t);
	    assertTrue(t.contains(s));
	}
	expected.sort(null);
	assertEquals(expected, inorder(t)); // duplicates are allowed
	assertEquals("at_8", t.minimum());
	assertEquals("wistfully_3", t.maximum());
	for (String s : STRINGS) {
	    t.remove(s);
	    expected.remove(s);
	    check(t);
	    assertEquals(expected, inorder(t));
	}
	assertTrue(inorder(t).isEmpty());
	assertFalse(t.contains("foo"));
	t.remove("foo"); // removing an absent key does nothing
    }

    /** Compares many random adds and removes with a sorted list. */
    @ParameterizedTest(name="{0}") @MethodSource("factories")
    void random(Factory f) {
	BinaryTree t = f.empty().get();
	List<Object> expected = new ArrayList<>();
	Random r = new Random(42);
	for (int i = 0; i < 2000; i++) {
	    Integer k = r.nextInt(100);
	    if (r.nextInt(3) == 0) {
		t.remove(k);
		expected.remove(k);
	    } else {
		t.add(k);
		expected.add(k);
	    }
	    assertEquals(expected.contains(k), t.contains(k));
	}
	check(t);
	expected.sort(null);
	assertEquals(expected, inorder(t));
    }

    /** Walks the tree with {@code successor}. */
    @ParameterizedTest(name="{0}") @MethodSource("factories")
    void successor(Factory f) {
	BinaryTree t = f.empty().get();
	for (int i : new int[] { 5, 3, 8, 1, 4, 7, 9, 2, 6 })
	    t.add(i);
	List<Object> walk = new ArrayList<>();
	for (BinaryTree.Node n = t.minimum(t.root()); n != t.NIL;
	     n = t.successor(n))
	    walk.add(n.key);
	assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9), walk);
    }

    /** The intervals from CLR, page 291. */
    static IntervalTree clr291() {
	IntervalTree t = new IntervalTree();
	int[][] ivals = { {0, 3}, {5, 8}, {15, 23}, {6, 10}, {16, 21},
			  {8, 9}, {17, 19}, {25, 30}, {19, 20}, {26, 26} };
	for (int[] iv : ivals)
	    t.addInterval(iv[0]+"-"+iv[1], iv[0], iv[1]);
	return t;
    }

    @Test
    void intervalTree() {
	IntervalTree t = clr291();
	check(t);
	assertEquals("15-23",
		   t.searchOverlapping(new IntervalTree.Interval(null, 22, 25))
		   .datum);
	assertNull(t.searchOverlapping(new IntervalTree.Interval(null, 11, 14)));
    }

    /** Compares {@code searchOverlapping} and {@code allOverlapping}
     *  with a search of every interval. */
    @Test
    void intervalTreeRandom() {
	Random r = new Random(42);
	IntervalTree t = new IntervalTree();
	List<IntervalTree.Interval> all = new ArrayList<>();
	// remove() finds an interval by its low end, so keep them distinct.
	List<Integer> lows = new ArrayList<>();
	for (int i = 0; i < 1000; i++) lows.add(i);
	Collections.shuffle(lows, r);
	for (int i = 0; i < 1000; i++) {
	    int low = lows.get(i), high = low + r.nextInt(50);
	    all.add(t.addInterval(null, low, high));
	    if (r.nextInt(3) == 0)
		t.remove(all.remove(r.nextInt(all.size())));
	    check(t);
	}
	for (int i = 0; i < 1000; i++) {
	    int low = r.nextInt(1000), high = low + r.nextInt(20);
	    IntervalTree.Interval q = new IntervalTree.Interval(null, low, high);
	    List<IntervalTree.Interval> expected = new ArrayList<>();
	    for (IntervalTree.Interval iv : all)
		if (iv.overlaps(q)) expected.add(iv);
	    IntervalTree.Interval found = t.searchOverlapping(q);
	    if (expected.isEmpty())
		assertNull(found, "found "+found+" overlapping "+low+"-"+high);
	    else
		assertTrue(found != null && found.overlaps(q),
			   "missed overlap with "+low+"-"+high);
	    List<Object> got = new ArrayList<>();
	    t.allOverlapping(q).forEachRemaining(got::add);
	    assertEquals(expected.size(), got.size());
	    assertTrue(got.containsAll(expected));
	}
    }
}
