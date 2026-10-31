// AggregateMapFactory.java, created Fri Nov 10 16:58:26 2000 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * {@link AggregateMapFactory} uses a single {@link HashMap}
 * as backing store for the many smaller {@link Map}s created
 * by this {@link MapFactory}.  This means that we use much
 * less space and rehash less frequently than if we were using
 * the standard {@link Factories#hashMapFactory()}.
 * The iterators of the submaps are fast, unlike those of
 * {@link HashMap}.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public class AggregateMapFactory<K,V> extends MapFactory<K,V>
    implements java.io.Serializable {
    /** The identity of a submap, which also heads its list of
     *  entries. */
    private static final class ID<K,V> {
	/* backing store for efficient iteration */
	DoublyLinkedList<K,V> entries=null;
    }
    private final Map<Map.Entry<ID<K,V>,K>,DoublyLinkedList<K,V>> m =
	new HashMap<Map.Entry<ID<K,V>,K>,DoublyLinkedList<K,V>>();
    /** The submaps that are garbage, queued by the garbage collector. */
    private transient ReferenceQueue<Object> collected;
    /** Keeps the {@link MapRef}s reachable until they are queued. */
    private transient Set<MapRef<K,V>> refs;
    private static final class MapRef<K,V> extends WeakReference<Object> {
	final ID<K,V> identity;
	MapRef(Object map, ID<K,V> identity, ReferenceQueue<Object> q) {
	    super(map, q);
	    this.identity = identity;
	}
    }
    /** Removes the entries of the submaps that are garbage from the
     *  backing store.  This runs on the thread that uses this factory,
     *  so the backing store needs no synchronization; the
     *  <code>finalize()</code> method it replaces ran on the
     *  finalizer thread. */
    private void expunge() {
	if (collected==null) {
	    collected = new ReferenceQueue<Object>();
	    refs = new HashSet<MapRef<K,V>>();
	}
	for (Reference<?> r; (r = collected.poll()) != null; ) {
	    @SuppressWarnings("unchecked")
	    MapRef<K,V> mr = (MapRef<K,V>) r;
	    refs.remove(mr);
	    for (DoublyLinkedList<K,V> dll=mr.identity.entries; dll!=null;
		 dll=dll.next)
		m.remove(Default.entry(mr.identity, dll.getKey()));
	}
    }

    /** Creates an {@link AggregateMapFactory}. */
    public AggregateMapFactory() { /* nothing to do here */ }

    /** Generates a new mutable {@link Map} which is a 
     *  subset of the backing set of this
     *  {@link AggregateMapFactory}.
     */
    @Override
    public Map<K,V> makeMap(final Map<? extends K,? extends V> mm) {
	expunge();
	AggregateMap am = new AggregateMap(mm);
	refs.add(new MapRef<K,V>(am, am.IDENTITY, collected));
	return am;
    }
    class AggregateMap extends AbstractMap<K,V> {
	final ID<K,V> IDENTITY = new ID<K,V>();
	int size=0;

	AggregateMap(Map<? extends K,? extends V> mm) { putAll(mm); }

	private void unlink(DoublyLinkedList<K,V> entry) {
	    if (IDENTITY.entries==entry) { // first element.
		IDENTITY.entries = entry.next; // reset first element.
	    } else {
		entry.prev.next = entry.next;
		if (entry.next!=null) // maybe the last element.
		    entry.next.prev = entry.prev;
	    }
	    size--;
	}
	private void link(DoublyLinkedList<K,V> entry) {
	    entry.next = IDENTITY.entries;
	    if (IDENTITY.entries!=null) // maybe nothing on list.
		IDENTITY.entries.prev = entry;
	    IDENTITY.entries = entry;
	    size++;
	}

	@Override
	public V put(K key, V value) {
	    expunge();
	    DoublyLinkedList<K,V> entry=new DoublyLinkedList<K,V>(key, value);
	    DoublyLinkedList<K,V> old=m.put(Default.entry(IDENTITY,key),entry);
	    if (old!=null) unlink(old);
	    link(entry);
	    return (old==null) ? null : old.getValue();
	}
	@Override
	public boolean containsKey(Object key) {
	    return m.containsKey(Default.entry(IDENTITY, key));
	}
	@Override
	public boolean containsValue(Object value) {
	    for (DoublyLinkedList<K,V> dll=IDENTITY.entries; dll!=null; dll=dll.next)
		if (value==null ?
		    (value==dll.getValue()) :
		    value.equals(dll.getValue()))
		    return true;
	    return false;
	}
	@Override
	public Set<Map.Entry<K,V>> entrySet() {
	    return new AbstractMapSet<K,V>() {
		    @Override
		    public Iterator<Map.Entry<K,V>> iterator() {
			return new Iterator<Map.Entry<K,V>>() {
				DoublyLinkedList<K,V> dll=IDENTITY.entries, last=null;
				@Override public boolean hasNext() { return dll!=null; }
				@Override
				public Map.Entry<K,V> next() {
				    if (dll==null)
					throw new NoSuchElementException();
				    last = dll;
				    dll=dll.next;
				    return last;
				}
				@Override
				public void remove() {
				    if (last==null)
					throw new
					    UnsupportedOperationException();
				    m.remove(Default.entry(IDENTITY,
							  last.getKey()));
				    unlink(last);
				    last=null;
				}
			    };
		    }
		    @Override public int size() { return size; }
		    @Override
		    public boolean add(Map.Entry<K,V> me) {
			if (me==null)
			    throw new UnsupportedOperationException();
			if (contains(me)) return false; // already here.
			if (AggregateMap.this.containsKey(me.getKey()))
			    // this is not a multimap!
			    throw new UnsupportedOperationException();
			AggregateMap.this.put(me.getKey(), me.getValue());
			return true;
		    }
		    @Override
		    public boolean contains(Object o) {
			if (!(o instanceof Map.Entry<?,?> me)) return false;
			Map.Entry<?,?> pair = Default.entry(IDENTITY, me.getKey());
			if (!m.containsKey(pair)) return false;
			return me.equals(m.get(pair));
		    }
		    @Override
		    public boolean remove(Object o) {
			if (!contains(o)) return false;
			AggregateMap.this.remove(((Map.Entry<?,?>) o).getKey());
			return true;
		    }
		    @Override public Map<K,V> asMap() { return AggregateMap.this; }
		};
	}
	@Override
	public V get(Object key) {
	    DoublyLinkedList<K,V> entry = m.get(Default.entry(IDENTITY, key));
	    return (entry==null)?null:entry.getValue();
	}
	@Override
	public V remove(Object key) {
	    DoublyLinkedList<K,V> entry = m.remove(Default.entry(IDENTITY, key));
	    if (entry!=null) unlink(entry);
	    return (entry==null)?null:entry.getValue();
	}
	@Override public int size() { return size; }
    }

    private static class DoublyLinkedList<K,V> extends PairMapEntry<K,V> {
	DoublyLinkedList<K,V> next, prev;
	DoublyLinkedList(K key, V value) {
	    super(key, value);
	}
    }
    private static abstract class AbstractMapSet<K,V>
	extends AbstractSet<Map.Entry<K,V>> implements MapSet<K,V> { }
}
