// AggregateSetFactory.java, created Fri Nov 10 16:58:26 2000 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
/**
 * {@link AggregateSetFactory} uses a single {@link HashMap}
 * as backing store for the many smaller {@link Set}s created
 * by this {@link SetFactory}.  This means that we use much
 * less space and rehash less frequently than if we were using
 * the standard {@link Factories#hashSetFactory()}.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public class AggregateSetFactory<V> extends SetFactory<V>
    implements java.io.Serializable {
    private final Map<EntryList<V>,EntryList<V>> m =
	new HashMap<EntryList<V>,EntryList<V>>();

    /** Creates an {@link AggregateSetFactory}. */
    public AggregateSetFactory() { /* nothing to do here */ }

    /** Generates a new mutable {@link Set} which is a 
     *  subset of the backing set of this
     *  {@link AggregateSetFactory}.
     *  <b>WARNING:</b>
     *  The <code>remove()</code> method of the returned {@link Set}
     *  is very slow.
     */
    @Override
    public Set<V> makeSet(Collection<? extends V> c) {
	return new AggregateSet(c);
    }
    private class AggregateSet extends AbstractSet<V> {
	// list header is the IDENTITY object for the set.
	// this is also the backing store for efficient iteration.
	final ID<V> IDENTITY = new ID<V>();
	int size=0;

	AggregateSet(Collection<? extends V> c) {
	    addAll(c);
	}
	@Override
	public boolean add(V o) {
	    EntryList<V> entry = new EntryList<V>(IDENTITY, o);
	    if (m.containsKey(entry)) return false;
	    IDENTITY.add(entry);
	    m.put(entry, entry);
	    size++;
	    return true;
	}
	@Override
	public boolean contains(Object o) {
	    return m.containsKey(new EntryList(IDENTITY, o));
	}
	@Override
	public boolean isEmpty() {
	    assert (size==0)==(IDENTITY.next==null);
	    return size==0;
	}
	@Override
	public Iterator<V> iterator() {
	    return new Iterator<V>() {
		Header<V> entry = IDENTITY;
		@Override public boolean hasNext() { return entry.next!=null; }
		@Override
		public V next() {
		    if (!hasNext()) throw new NoSuchElementException();
		    removeValid = true;
		    EntryList<V> nentry = entry.next;
		    entry = nentry;
		    return nentry.value;
		}
		@Override
		public void remove() {
		    if (!removeValid) throw new IllegalStateException();
		    EntryList<V> oentry = (EntryList<V>) entry;
		    entry = oentry.prev;
		    AggregateSet.this.remove(oentry);
		    removeValid = false;
		}
		private boolean removeValid = false;
	    };
	}
	private void remove(EntryList<V> entry) {
	    EntryList<V> e = m.remove(entry);
	    assert e == entry;
	    e.remove();
	    size--;
	}
	@Override
	public boolean remove(Object o) {
	    EntryList<V> entry = m.get(new EntryList(IDENTITY, o));
	    if (entry==null) return false;
	    remove(entry);
	    return true;
	}
	@Override public int size() { return size; }
	// garbage-collect entries in backing store, too!
	protected void finalize() {
	    clear();
	}
    }
    /** Header for linked list of set elements. */
    private static abstract class Header<E>
	implements java.io.Serializable {
	EntryList<E> next=null;
	Header() { }
	/** Link in the supplied entry after this one. */
	void add(EntryList<E> nel) {
	    nel.next = this.next;
	    nel.prev = this;
	    if (nel.next!=null) nel.next.prev = nel;
	    this.next = nel;
	}
	/** Return the set identifier which this list belongs to. */
	abstract ID<E> identity();
    }
    /** Linked list of set elements. */
    private static final class EntryList<E> extends Header<E> {
	final ID<E> id;
	final E value;
	Header<E> prev=null;
	EntryList(ID<E> id, E value) { this.id = id; this.value = value; }

	// utility.
	/** Remove this entry from the list. */
	void remove() {
	    // always a predecessor; maybe no successor.
	    this.prev.next = this.next;
	    if (this.next!=null) this.next.prev = this.prev;
	    this.prev = this.next = null; // safety.
	}
	@Override ID<E> identity() { return id; }

	@Override
	public boolean equals(Object o) {
	    if (!(o instanceof EntryList)) return false;
	    EntryList e = (EntryList) o;
	    return this.id==e.id &&
		(this.value==null? e.value==null : this.value.equals(e.value));
	}
	@Override
	public int hashCode() {
	    return this.id.hashCode() +
		((this.value==null) ? 0 : this.value.hashCode());
	}
    }
    /** Type for 'set identity' object. */
    private static final class ID<E> extends Header<E> {
	@Override ID<E> identity() { return this; }
    }
}
