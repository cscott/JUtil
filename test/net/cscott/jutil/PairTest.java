// PairTest.java, created Sat Oct 31 2026 by cananian
// Copyright (C) 2026 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

class PairTest {
    @Test void equality() {
	Set<Pair<String,Integer>> s = new HashSet<>();
	s.add(new Pair<>("a", 1));
	s.add(new Pair<>("a", 1));
	s.add(new Pair<>(null, null));
	s.add(new Pair<>(null, null));
	assertEquals(Set.of(new Pair<>("a", 1), new Pair<String,Integer>(null, null)), s);
	assertNotEquals(new Pair<>("a", 1), new Pair<>(1, "a"));
	assertEquals("a", new Pair<>("a", 1).left());
	assertEquals(1, new Pair<>("a", 1).right());
    }

    @Test void serializable() throws IOException, ClassNotFoundException {
	Pair<String,List<Integer>> p = new Pair<>("a", List.of(1, 2));
	ByteArrayOutputStream bytes = new ByteArrayOutputStream();
	try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
	    out.writeObject(p);
	}
	try (ObjectInputStream in = new ObjectInputStream
	     (new ByteArrayInputStream(bytes.toByteArray()))) {
	    assertEquals(p, in.readObject());
	}
    }

    @SuppressWarnings("deprecation") // PairList is still tested
    @Test void pairList() {
	Default.PairList<String,Integer> p = Default.pair("a", 1);
	assertEquals(List.of("a", 1), p);
	assertEquals(List.of("a", 1).hashCode(), p.hashCode());
	p.set(1, 2);
	assertEquals(2, p.right());
	assertEquals("a", p.left());
    }
}
