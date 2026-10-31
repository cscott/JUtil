// HashEnvironmentTest.java, created from the self-test in HashEnvironment.
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Iterator;
import java.util.Map;

import org.junit.jupiter.api.Test;

class HashEnvironmentTest {
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
}
