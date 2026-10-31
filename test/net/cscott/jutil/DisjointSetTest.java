// DisjointSetTest.java, created from the self-test in DisjointSet.
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class DisjointSetTest {
    @Test void unionFind() {
	DisjointSet<String> ds = new DisjointSet<>();
	String a="a", b="b", c="c", d="d", e="e", f="f", g="g", h="h";
	assertFalse(ds.contains(a) || ds.contains(b) || ds.contains(c));
	assertSame(a, ds.find(a));
	assertSame(b, ds.find(b));
	assertSame(c, ds.find(c));
	// find() does not add the element
	assertFalse(ds.contains(a) || ds.contains(b) || ds.contains(c));
	ds.union(e, c); ds.union(b, h); ds.union(h, c);
	assertSame(ds.find(e), ds.find(c));
	assertSame(ds.find(h), ds.find(e));
	assertSame(ds.find(b), ds.find(c));
	assertNotSame(ds.find(b), ds.find(a));
	ds.union(d, f); ds.union(g, d);
	assertSame(ds.find(d), ds.find(f));
	assertSame(ds.find(f), ds.find(g));
	assertNotSame(ds.find(d), ds.find(c));
	assertNotSame(ds.find(d), ds.find(a));
	ds.union(c, f);
	assertSame(ds.find(e), ds.find(f));
	assertSame(a, ds.find(a));
    }
}
