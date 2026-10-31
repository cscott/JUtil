// EnvironmentTest.java, created from the self-test in HashEnvironment.
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class EnvironmentTest {
    @Test void undoToMark() {
	Environment<String,String> e = new HashEnvironment<>();
	assertEquals(0, e.size());
	assertFalse(e.containsKey("a") || e.containsKey("b"));
	e.put("a","a"); e.put("a","b");
	assertEquals(1, e.size());
	assertTrue(e.containsKey("a") && e.containsValue("b"));
	assertFalse(e.containsValue("a") || e.containsValue("c"));
	Environment.Mark m = e.getMark();
	e.remove("a"); e.remove("a");
	assertEquals(0, e.size());
	assertFalse(e.containsKey("a") || e.containsKey("b"));
	assertFalse(e.containsValue("a") || e.containsValue("b"));
	e.put("b","b"); e.put("b","c");
	assertEquals(1, e.size());
	assertTrue(e.containsKey("b") && e.containsValue("c"));
	assertFalse(e.containsKey("a") || e.containsValue("b"));
	assertEquals(Map.of("b", "c"), e);
	e.undoToMark(m);
	assertEquals(1, e.size());
	assertTrue(e.containsKey("a") && !e.containsKey("b"));
	assertEquals(Map.of("a", "b"), e);
	e.put("c", "d"); e.put("c", "d");
	assertEquals(2, e.size());
	assertTrue(e.containsKey("c") && e.containsValue("d"));
	m = e.getMark();
	e.clear();
	assertEquals(0, e.size());
	assertFalse(e.containsKey("a") || e.containsKey("b"));
	e.undoToMark(m);
	assertEquals(2, e.size());
	assertEquals(Map.of("a", "b", "c", "d"), e);
    }

    /** Changes through the entry set can be undone too. */
    @Test void entrySetChanges() {
	Environment<String,String> e = new HashEnvironment<>();
	e.put("a", "1"); e.put("b", "2"); e.put("c", "3");
	Environment.Mark m = e.getMark();
	for (Iterator<Map.Entry<String,String>> it = e.entrySet().iterator();
	     it.hasNext(); ) {
	    Map.Entry<String,String> me = it.next();
	    if (me.getKey().equals("a")) it.remove();
	    else if (me.getKey().equals("b")) assertEquals("2", me.setValue("x"));
	}
	assertEquals(Map.of("b", "x", "c", "3"), e);
	e.undoToMark(m);
	assertEquals(Map.of("a", "1", "b", "2", "c", "3"), e);
    }

    static Stream<Supplier<Environment<String,Integer>>> environments() {
	return Stream.of(HashEnvironment::new, PersistentEnvironment::new);
    }

    /** Closing a mark undoes the changes in its scope. */
    @SuppressWarnings("try") // the marks are only closed
    @ParameterizedTest @MethodSource("environments")
    void scopes(Supplier<Environment<String,Integer>> make) {
	Environment<String,Integer> e = make.get();
	e.put("x", 1);
	try (Environment.Mark outer = e.getMark()) {
	    e.put("x", 2); e.put("y", 3);
	    try (Environment.Mark inner = e.getMark()) {
		e.remove("x"); e.put("z", 4);
		assertEquals(Map.of("y", 3, "z", 4), e);
	    }
	    assertEquals(Map.of("x", 2, "y", 3), e);
	}
	assertEquals(Map.of("x", 1), e);
    }

    /** A scope is undone when it ends with an exception, too. */
    @SuppressWarnings("try") // the mark is only closed
    @ParameterizedTest @MethodSource("environments")
    void scopeWithException(Supplier<Environment<String,Integer>> make) {
	Environment<String,Integer> e = make.get();
	e.put("x", 1);
	assertThrows(IllegalStateException.class, () -> {
	    try (Environment.Mark scope = e.getMark()) {
		e.put("x", 2);
		throw new IllegalStateException();
	    }
	});
	assertEquals(Map.of("x", 1), e);
    }

    @ParameterizedTest @MethodSource("environments")
    void markOfAnotherEnvironment(Supplier<Environment<String,Integer>> make) {
	Environment<String,Integer> e = make.get(), f = make.get();
	Environment.Mark m = f.getMark();
	assertThrows(IllegalArgumentException.class, () -> e.undoToMark(m));
	assertThrows(IllegalArgumentException.class,
		     () -> e.undoToMark(new Environment.Mark() { }));
    }

    /** A mark which does not know its environment cannot be closed. */
    @Test void defaultClose() {
	Environment.Mark m = new Environment.Mark() { };
	assertThrows(UnsupportedOperationException.class, m::close);
    }
}
