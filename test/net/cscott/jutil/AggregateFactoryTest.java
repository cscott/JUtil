// AggregateFactoryTest.java
// Copyright (C) 2026 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * {@link AggregateFactoryTest} checks that {@link AggregateSetFactory}
 * and {@link AggregateMapFactory} drop the entries of the sets and maps
 * that are garbage from their backing store.
 */
class AggregateFactoryTest {
    /** Returns the size of the factory's backing store. */
    private static int backingSize(Object factory) throws Exception {
	Field f = factory.getClass().getDeclaredField("m");
	f.setAccessible(true);
	return ((Map<?,?>) f.get(factory)).size();
    }
    /** Collects garbage until <code>ref</code> is cleared. */
    private static void collect(WeakReference<?> ref) throws Exception {
	for (int i = 0; i < 100 && ref.get() != null; i++) {
	    System.gc();
	    Thread.sleep(10);
	}
	assertTrue(ref.get() == null, "not collected");
    }

    @Test void setFactory() throws Exception {
	AggregateSetFactory<String> f = new AggregateSetFactory<>();
	Set<String> kept = f.makeSet(Arrays.asList("a", "b"));
	WeakReference<Set<String>> ref =
	    new WeakReference<>(f.makeSet(Arrays.asList("a", "c", "d")));
	assertEquals(5, backingSize(f));
	collect(ref);
	kept.add("e"); // expunges
	assertEquals(3, backingSize(f));
	assertEquals(Set.of("a", "b", "e"), kept);
    }

    @Test void mapFactory() throws Exception {
	AggregateMapFactory<String,Integer> f = new AggregateMapFactory<>();
	Map<String,Integer> kept = f.makeMap(Map.of("a", 1, "b", 2));
	WeakReference<Map<String,Integer>> ref =
	    new WeakReference<>(f.makeMap(Map.of("a", 1, "c", 3, "d", 4)));
	assertEquals(5, backingSize(f));
	collect(ref);
	kept.put("e", 5); // expunges
	assertEquals(3, backingSize(f));
	assertEquals(Map.of("a", 1, "b", 2, "e", 5), kept);
    }
}
