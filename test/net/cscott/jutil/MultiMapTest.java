// MultiMapTest.java, created Sat Oct 31 2026 by cananian
// Copyright (C) 2026 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/** The {@link Map} methods which a {@link MultiMap} inherits, on each
 *  {@link MultiMap} implementation. */
class MultiMapTest {
    record Factory(String name, Supplier<MultiMap<String,Integer>> make) {
	@Override public String toString() { return name; }
    }
    static Stream<Factory> factories() {
	return Stream.of(
	    new Factory("GenericMultiMap", GenericMultiMap::new),
	    new Factory("PersistentMultiMapFactory",
			new PersistentMultiMapFactory<String,Integer>
			(Comparator.naturalOrder(), Comparator.naturalOrder())
			::makeMultiMap),
	    new Factory("GenericInvertibleMultiMap",
			GenericInvertibleMultiMap::new));
    }
    /** Returns {a=[1, 2], b=[3]}. */
    static MultiMap<String,Integer> make(Factory f) {
	MultiMap<String,Integer> mm = f.make().get();
	mm.add("a", 1);
	mm.add("a", 2);
	mm.add("b", 3);
	return mm;
    }
    /** The mappings of a multimap, as a map to sets of values. */
    static Map<String,Set<Integer>> contents(MultiMap<String,Integer> mm) {
	Map<String,Set<Integer>> m = new TreeMap<>();
	for (Map.Entry<String,Integer> e : mm.entrySet())
	    m.computeIfAbsent(e.getKey(), k -> new TreeSet<>()).add(e.getValue());
	return m;
    }

    @ParameterizedTest(name="{0}") @MethodSource("factories")
    void replaceOneValue(Factory f) {
	for (int old : new int[] { 1, 2 }) {
	    MultiMap<String,Integer> mm = make(f);
	    assertTrue(mm.replace("a", old, 9));
	    assertEquals(Map.of("a", Set.of(3 - old, 9), "b", Set.of(3)),
			 contents(mm));
	}
	MultiMap<String,Integer> mm = make(f);
	assertFalse(mm.replace("a", 3, 9));
	assertFalse(mm.replace("z", 1, 9));
	assertEquals(Map.of("a", Set.of(1, 2), "b", Set.of(3)), contents(mm));
	assertTrue(mm.replace("b", 3, 4));
	assertEquals(Map.of("a", Set.of(1, 2), "b", Set.of(4)), contents(mm));
    }

    @ParameterizedTest(name="{0}") @MethodSource("factories")
    void replaceAll(Factory f) {
	MultiMap<String,Integer> mm = make(f);
	mm.replaceAll((k, v) -> v + 10);
	assertEquals(Map.of("a", Set.of(11, 12), "b", Set.of(13)),
		     contents(mm));
	assertEquals(3, mm.size());
	assertThrows(ArithmeticException.class,
		     () -> mm.replaceAll((k, v) -> v / (v - 13)));
	assertEquals(Map.of("a", Set.of(11, 12), "b", Set.of(13)),
		     contents(mm));
    }

    /** Removing the last value of a key removes the key. */
    @ParameterizedTest(name="{0}") @MethodSource("factories")
    void removeLastValue(Factory f) {
	MultiMap<String,Integer> mm = make(f);
	assertTrue(mm.remove("b", 3));
	assertFalse(mm.containsKey("b"));
	assertEquals(Map.of("a", Set.of(1, 2)), contents(mm));

	mm = make(f);
	assertTrue(mm.getValues("b").remove(3));
	assertFalse(mm.containsKey("b"));
	assertEquals(2, mm.size());

	mm = make(f);
	assertTrue(mm.keySet().removeIf(k -> k.equals("a")));
	assertEquals(Map.of("b", Set.of(3)), contents(mm));
	assertTrue(mm.entrySet().removeIf(e -> e.getValue() == 3));
	assertTrue(mm.isEmpty());
    }

    /** The other default methods of {@link Map} use get(), put() and
     *  remove(), as documented. */
    @ParameterizedTest(name="{0}") @MethodSource("factories")
    void mapDefaults(Factory f) {
	MultiMap<String,Integer> mm = make(f);
	assertEquals(0, mm.getOrDefault("z", 0));
	assertTrue(Set.of(1, 2).contains(mm.putIfAbsent("a", 9)));
	assertEquals(null, mm.putIfAbsent("z", 9));
	assertEquals(9, mm.computeIfAbsent("y", k -> 9));
	assertEquals(Map.of("a", Set.of(1, 2), "b", Set.of(3),
			    "y", Set.of(9), "z", Set.of(9)), contents(mm));
	mm.computeIfPresent("a", (k, v) -> 7);
	assertEquals(Set.of(7), contents(mm).get("a"));
	mm.add("a", 8);
	mm.compute("a", (k, v) -> null);
	assertFalse(mm.containsKey("a"));
	assertEquals(8, mm.merge("b", 5, Integer::sum));
	assertEquals(Set.of(8), contents(mm).get("b"));
	StringBuilder sb = new StringBuilder();
	new TreeMap<>(contents(mm)).forEach((k, v) -> sb.append(k).append(v));
	assertEquals("b[8]y[9]z[9]", sb.toString());
    }
}
