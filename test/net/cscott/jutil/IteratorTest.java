// IteratorTest.java, created Sat Oct 31 2026 by cananian
// Copyright (C) 2026 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

class IteratorTest {
    static <E> List<E> toList(Iterator<E> it) {
	List<E> l = new ArrayList<>();
	it.forEachRemaining(l::add);
	return l;
    }

    @Test void filterIterator() {
	List<Integer> l = List.of(1, 2, 3, 4, 5, 6);
	assertEquals(List.of(2, 4, 6),
		     toList(FilterIterator.filter(l.iterator(), i -> i % 2 == 0)));
	assertEquals(List.of("1", "2", "3", "4", "5", "6"),
		     toList(FilterIterator.map(l.iterator(), String::valueOf)));
	Iterator<Number> odd = new FilterIterator<Integer,Number>
	    (l.iterator(), i -> i % 2 == 1, i -> i * 10L);
	assertEquals(List.of(10L, 30L, 50L), toList(odd));
	assertFalse(odd.hasNext());
	assertThrows(NoSuchElementException.class, odd::next);
	assertFalse(FilterIterator.filter(l.iterator(), i -> false).hasNext());
	Iterator<Integer> it = FilterIterator.filter(l.iterator(), i -> true);
	it.next();
	assertThrows(UnsupportedOperationException.class, it::remove);
    }

    @SuppressWarnings("deprecation") // Filter is still tested
    @Test void filter() {
	List<Integer> l = List.of(1, 2, 3, 4);
	assertEquals(l, toList(new FilterIterator<Integer,Integer>
			       (l.iterator(), new FilterIterator.Filter<>())));
	assertEquals(List.of("3", "4"), toList(new FilterIterator<Integer,String>
	    (l.iterator(), new FilterIterator.Filter<Integer,String>() {
		@Override public boolean isElement(Integer i) { return i > 2; }
		@Override public String map(Integer i) { return "" + i; }
	    })));
    }

    @Test void combineIterator() {
	List<Integer> a = new ArrayList<>(List.of(1, 2)), b = List.of(),
	    c = new ArrayList<>(List.of(3));
	Iterator<Number> it = new CombineIterator<Number>
	    (a.iterator(), b.iterator(), c.iterator());
	assertThrows(IllegalStateException.class, it::remove);
	assertEquals(1, it.next());
	it.remove();
	assertEquals(List.of(2), a);
	assertEquals(List.of(2, 3), toList(it));
	it.remove();
	assertEquals(List.of(), c);
	assertThrows(NoSuchElementException.class, it::next);

	assertEquals(List.of(1, 2, 3), toList(new CombineIterator<Integer>
	    (List.of(List.of(1).iterator(), List.of(2, 3).iterator()))));
	assertEquals(List.of(1, 2), toList(new CombineIterator<Integer>
	    (List.of(1).iterator(), List.of(2).iterator())));
	assertEquals(List.of(1, 2), toList(new CombineIterator<Integer>
	    (FilterIterator.map(List.of(1, 2).iterator(),
				i -> List.of(i).iterator()))));
    }

    /** The iterators which have no <code>remove()</code>. */
    @Test void unmodifiable() {
	List<Integer> l = new ArrayList<>(List.of(1, 2, 3));
	Iterator<Integer> it = Default.unmodifiableIterator(l.iterator());
	assertEquals(1, it.next());
	assertThrows(UnsupportedOperationException.class, it::remove);
	List<Number> rest = new ArrayList<>();
	Default.<Number>unmodifiableIterator(it).forEachRemaining(rest::add);
	assertEquals(List.of(2, 3), rest);

	Iterator<Integer> snap = new SnapshotIterator<>(l.iterator());
	l.clear();
	assertEquals(1, snap.next());
	assertThrows(UnsupportedOperationException.class, snap::remove);
	assertEquals(2, snap.next());
	assertEquals(3, snap.next());
	assertFalse(snap.hasNext());
	assertThrows(NoSuchElementException.class, snap::next);
    }

    @SuppressWarnings("deprecation") // the deprecated class still works
    @Test void unmodifiableIterator() {
	Iterator<Integer> it = UnmodifiableIterator.proxy(List.of(1).iterator());
	assertEquals(1, it.next());
	assertThrows(UnsupportedOperationException.class, it::remove);
    }

    @Test void iteratorEnumerator() {
	Enumeration<Number> e = new IteratorEnumerator<>(List.of(1, 2).iterator());
	assertEquals(List.of(1, 2), Collections.list(e));
	UniqueVector<String> uv = new UniqueVector<>(List.of("a", "b"));
	assertEquals(List.of("a", "b"), Collections.list(uv.elements()));
    }
}
