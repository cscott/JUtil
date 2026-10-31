// BinaryHeap.java, created Sat Feb 12 10:04:53 2000 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
/**
 * {@link BinaryHeap} is an implementation of a binary heap.
 * The implementation in CLR is followed, except the comparisons
 * are reversed to keep the <b>minimum</b> element on the top of
 * the heap.  In addition, the function names {@link #downheap(int)}
 * (for what CLR calls 'heapify') and {@link #upheap(int)} (which
 * is part of the INSERT operation) have been adopted from
 * Sedgewick's book.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 * @see Heap
 */
public final class BinaryHeap<K,V> extends AbstractHeap<K,V> {
    private final boolean debug=false;

    final ArrayList<Entry<K,V>> A;
    final Comparator<Map.Entry<K,V>> c;
    
    /** Creates a new, empty {@link BinaryHeap}, which will
     *  use the keys' natural order. */
    public BinaryHeap() { this(Collections.<Map.Entry<K,V>>emptySet(), null); }
    /** Creates a new, empty {@link BinaryHeap} with the
     *  specified comparator. */
    public BinaryHeap(Comparator<K> c) { this(Collections.<Map.Entry<K,V>>emptySet(), c); }
    /** Builds a binary heap from the given heap, using
     *  the same key comparator as the given heap.  O(n) time. */
    public BinaryHeap(Heap<K,? extends V> h) {
	this(h.entries(), h.comparator());
    }
    /** Builds a binary heap from a collection of {@link java.util.Map.Entry}s
     *  and a key comparator.
     *  O(n) time. */
    public BinaryHeap(Collection<? extends Map.Entry<? extends K,? extends V>> collection, Comparator<K> comparator) {
	super(comparator);
	// initialize comparator
	c = entryComparator(); // cache in field.
	// initialize A
	A = new ArrayList<Entry<K,V>>(1+collection.size());
	A.add(null); /* element zero is reserved */
	// use BUILD-HEAP
	union(collection);
	if (debug) checkHeap();
    }
    public Map.Entry<K,V> insert(K key, V value) {
	int index=A.size();
	Entry<K,V> e=new Entry<K,V>(key, value, index);
	A.add(e);
	upheap(index);
	if (debug) checkHeap();
	return e;
    }
    public Map.Entry<K,V> minimum() {
	if (size() < 1) throw new java.util.NoSuchElementException();
	return A.get(1);
    }
    public Map.Entry<K,V> extractMinimum() {
	if (size() < 1) throw new java.util.NoSuchElementException();
	Entry<K,V> min = A.get(1);
	set(1, A.get(size()));
	A.remove(size());
	downheap(1);
	if (debug) checkHeap();
	return min;
    }
    public void union(Heap<? extends K,? extends V> h) {
	union(h.entries());
    }
    /** Union a collection of {@link java.util.Map.Entry}s, using BUILD-HEAP. */
    private void union(Collection<? extends Map.Entry<? extends K,? extends V>> coll) {
	// this is the BUILD-HEAP function. pg 145 in CLR.
	for (Map.Entry<? extends K,? extends V> e : coll)
	    A.add(new Entry<K,V>(e.getKey(), e.getValue(), A.size()));
	// okay, now heapify
	for (int i=size()/2; i>0; i--)
	    downheap(i);
	// done!
	if (debug) checkHeap();
    }
    public void decreaseKey(Map.Entry<K,V> me, K newkey) {
	updateKey(me, newkey);
    }
    public void updateKey(Map.Entry<K,V> me, K newkey) {
	Entry<K,V> e = (Entry<K,V>) me;
	if (keyComparator().compare(newkey, setKey(e, newkey)) < 0)
	    upheap(e.index);
	else
	    downheap(e.index);
	if (debug) checkHeap();
    }
    public void delete(Map.Entry<K,V> me) {
	int index = ((Entry<K,V>)me).index;
	// replace this entry with one at end of heap & shrink heap.
	Entry<K,V> newE = A.get(size());
	set(index, newE);
	A.remove(size());
	// now fixup the heap by calling either downheap or upheap.
	if (c.compare(newE, me) < 0)
	    upheap(index); // we've decreased the key
	else
	    downheap(index); // we've increased the key
	// done.
	if (debug) checkHeap();
    }
    public void clear() { A.clear(); A.add(null); }
    public int size() { return A.size()-1; /* 0'th element unused. */ }
    public Collection<Map.Entry<K,V>> entries() {
	return new AbstractCollection<Map.Entry<K,V>>() {
	    public int size() { return BinaryHeap.this.size(); }
	    public Iterator<Map.Entry<K,V>> iterator() {
		final Iterator<Entry<K,V>> it = A.iterator();
		it.next(); // filter out element 0 of the array list.
		// weakness of the type system: we need to wrap this
		// iterator to make it return Map.Entry instead of Entry.
		return new UnmodifiableIterator<Map.Entry<K,V>>() {
		    public boolean hasNext() { return it.hasNext(); }
		    public Map.Entry<K,V> next() { return it.next(); }
		};
	    }
	};
    }


    /** The guts of the binary heap algorithms: DOWNHEAP and UPHEAP */
    private final void downheap(int i) { // aka HEAPIFY
	int l = LEFT(i), r = RIGHT(i);
	int smallest = i;
	if (l<=size() && c.compare(A.get(l), A.get(smallest)) < 0)
	    smallest = l;
	if (r<=size() && c.compare(A.get(r), A.get(smallest)) < 0)
	    smallest = r;
	if (smallest != i) {
	    exchange(i, smallest);
	    downheap(smallest);
	}
    }
    private final void upheap(int i) {
	int p = PARENT(i);
	if (p>0 && i<=size() && c.compare(A.get(p), A.get(i)) > 0) {
	    exchange(p, i);
	    upheap(p);
	}
    }
    // exchange helper
    private final void exchange(int i, int j) {
	if (i==j) return; // efficiency hack.
	Entry<K,V> ei = A.get(i), ej = A.get(j);
	A.set(i, ej); ej.index = i;
	A.set(j, ei); ei.index = j;
    }
    // set helper
    private final void set(int i, Entry<K,V> e) {
	A.set(i, e); e.index = i;
    }
    // macros.  i sure hope the compiler is smart enough to inline these.
    private static final int LEFT(int i) { return 2*i; }
    private static final int RIGHT(int i) { return 2*i+1; }
    private static final int PARENT(int i) { return i/2; }

    // verify the heap condition.
    private final void checkHeap() {
	for (int i=2; i<A.size(); i++)
	    assert c.compare(A.get(PARENT(i)), A.get(i)) <= 0;
    }

    /** Our {@link BinaryHeap} {@link java.util.Map.Entry}s look like this: */
    private static class Entry<K,V> extends PairMapEntry<K,V> {
	int index;
	Entry(K key, V value, int index) {
	    super(key, value);
	    this.index = index;
	}
	K _setKey(K newKey) { return setKey(newKey); }
    }
    // to implement updateKey, etc...
    protected final K setKey(Map.Entry<K,V> me, K newkey) {
	Entry<K,V> e = (Entry<K,V>) me;
	return e._setKey(newkey);
    }

    //--------------------------------------------------
}
