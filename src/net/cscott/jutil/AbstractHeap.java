// AbstractHeap.java, created Sat Feb 12 09:41:17 2000 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
/**
 * {@link AbstractHeap} provides a skeletal implementation of
 * the {@link Heap} interface, to minimize the effort required
 * to implement this interface.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: AbstractHeap.java,v 1.5 2006-10-30 19:58:05 cananian Exp $
 */
public abstract class AbstractHeap<K,V> implements Heap<K,V> {
    /** A comparator for the keys in {@link java.util.Map.Entry}s, based
     *  on the key comparator given to the constructor.  But
     *  <code>keyComparator</code> is never null! */
    private final Comparator<K> keyComparator;
    /** A comparator for {@link java.util.Map.Entry}s, based on the
     *  key comparator given to the constructor. */
    private final EntryComparator<K,V> entryComparator;
    /** Sole constructor, for invocation by subclass constructors. */
    protected AbstractHeap(Comparator<K> c) {
	if (c==null) c = (Comparator) Default.comparator; // JDK1.1 hack.
	this.keyComparator = c; // should never be null, JDK1.1 hack or no.
	this.entryComparator = new EntryComparator<K,V>(c);
    }

    // abstract methods:
    public abstract Map.Entry<K,V> insert(K key, V value);
    public abstract Map.Entry<K,V> minimum();
    public abstract void decreaseKey(Map.Entry<K,V> me, K newkey);
    public abstract void delete(Map.Entry<K,V> me);
    public abstract int size();
    public abstract Collection<Map.Entry<K,V>> entries();
    public abstract void clear();

    // methods which we helpfully provide for you:
    public void updateKey(Map.Entry<K,V> me, K newkey) {
	int r = keyComparator.compare(me.getKey(), newkey);
	if (r>0) decreaseKey(me, newkey); // usually faster.
	if (r>=0) return; // done.
	delete(me);
	setKey(me, newkey);
	insert(me);
    }
    /** This method should insert the specified {@link java.util.Map.Entry},
     *  which is not currently in the {@link Heap}, into the
     *  {@link Heap}.  Implementation is optional, but it is required
     *  if you use the default implementation of <code>updateKey()</code>. */
    protected void insert(Map.Entry<K,V> me) {
	throw new UnsupportedOperationException();
    }
    /** This method should set the key for the specified
     *  {@link java.util.Map.Entry} to the given <code>newkey</code>.
     *  Implementation is optional, but it is required if you use the
     *  default implementation of <code>updateKey()</code>. */
    protected K setKey(Map.Entry<K,V> me, K newkey) {
	throw new UnsupportedOperationException();
    }
    public Map.Entry<K,V> extractMinimum() {
	Map.Entry<K,V> e = minimum();
	delete(e);
	return e;
    }
    public void union(Heap<? extends K,? extends V> h) {
	for (Map.Entry<? extends K,? extends V> e : h.entries())
	    insert(e.getKey(), e.getValue());
	h.clear();
    }
    public boolean isEmpty() { return size()==0; }
    public int hashCode() { return 1+entries().hashCode(); }
    public boolean equals(Object o) {
	if (o instanceof Heap) return entries().equals(((Heap)o).entries());
	return false;
    }
    public String toString() { return entries().toString(); }
    /** Returns the comparator used to compare keys in this {@link Heap},
     *  or <code>null</code> if the keys' natural ordering is used. */
    public Comparator<K> comparator() { return entryComparator.cc; }
    /** Returns the comparator used to compare keys in this {@link Heap}.
     *  <strong>Will never return <code>null</code>.</strong> */
    protected Comparator<K> keyComparator() { return keyComparator; }
    /** Returns a comparator which can be used to compare
     *  {@link java.util.Map.Entry}s. Will never return <code>null</code>. */
    protected Comparator<Map.Entry<K,V>> entryComparator() { return entryComparator; }

    /** Compares {@link java.util.Map.Entry}s by key. */
    private static class EntryComparator<K,V> implements Comparator<Map.Entry<K,V>> {
	final Comparator<K> cc;
	EntryComparator(Comparator<K> cc) { this.cc = cc; }
	public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
	    K k1 = e1.getKey(), k2 = e2.getKey();
	    return (cc==null) ?
		((Comparable)k1).compareTo(k2) :
		cc.compare(k1, k2);
	}
	public boolean equals(Object obj) {
	    if (obj instanceof EntryComparator) {
		EntryComparator ec = (EntryComparator) obj;
		return (cc==null) ? (ec.cc==null) : cc.equals(ec.cc);
	    }
	    return false;
	}
    }
}
