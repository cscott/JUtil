// FactoriesTest.java, created Sat Oct 31 2026 by cananian
// Copyright (C) 2026 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

/** The factories are functional interfaces. */
class FactoriesTest {
    @Test void methodReferences() {
	SetFactory<String> sf = HashSet::new;
	ListFactory<String> lf = ArrayList::new;
	MapFactory<String,Integer> mf = HashMap::new;
	CollectionFactory<String> cf = TreeSet::new;

	List<String> abc = List.of("c", "a", "b", "a");
	assertInstanceOf(HashSet.class, sf.makeSet(abc));
	assertEquals(Set.of("a", "b", "c"), sf.makeSet(abc));
	assertTrue(sf.makeSet().isEmpty());
	assertTrue(sf.makeSet(10).isEmpty());
	assertEquals(Set.of("a"), sf.makeCollection(List.of("a")));

	assertEquals(abc, lf.makeList(abc));
	assertNotSame(abc, lf.makeList(abc));
	assertTrue(lf.makeList(10).isEmpty());
	assertInstanceOf(ArrayList.class, lf.makeCollection());

	assertEquals(Map.of("a", 1), mf.makeMap(Map.of("a", 1)));
	assertTrue(mf.makeMap().isEmpty());

	assertEquals(List.of("a", "b", "c"),
		     new ArrayList<>(cf.makeCollection(abc)));
    }

    @Test void multiMapFactory() {
	MultiMapFactory<String,Integer> mmf = GenericMultiMap::new;
	MultiMap<String,Integer> mm = mmf.makeMultiMap();
	mm.add("a", 1);
	mm.add("a", 2);
	MultiMap<String,Integer> copy = mmf.makeMultiMap(mm);
	assertEquals(Set.of(1, 2), new HashSet<>(copy.getValues("a")));
	assertEquals(Map.of("b", 3), mmf.makeMap(Map.of("b", 3)));

	MapFactory<String,Collection<Integer>> backing = HashMap::new;
	MultiMapFactory<String,Integer> g =
	    new GenericMultiMapFactory<>(backing, (SetFactory<Integer>) TreeSet::new);
	MultiMap<String,Integer> gm = g.makeMultiMap(mm);
	assertEquals(List.of(1, 2), new ArrayList<>(gm.getValues("a")));
    }

    @SuppressWarnings("unchecked") // reads back what it wrote
    static <T> T roundTrip(T t) throws IOException, ClassNotFoundException {
	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
	    out.writeObject(t);
	}
	try (ObjectInputStream in = new ObjectInputStream
	     (new ByteArrayInputStream(bytes.toByteArray()))) {
	    return (T) in.readObject();
	}
    }

    @Test void serializable() throws IOException, ClassNotFoundException {
	SetFactory<String> hsf = roundTrip(Factories.hashSetFactory());
	assertEquals(Set.of("a"), hsf.makeSet(List.of("a")));
	SetFactory<String> lambda =
	    roundTrip((SetFactory<String> & Serializable) HashSet::new);
	assertEquals(Set.of("a"), lambda.makeSet(List.of("a")));
    }
}
