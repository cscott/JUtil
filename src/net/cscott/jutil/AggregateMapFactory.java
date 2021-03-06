// AggregateMapFactory.java, created Fri Nov 10 16:58:26 2000 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
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
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: AggregateMapFactory.java,v 1.6 2006-10-30 19:58:05 cananian Exp $
 */
public class AggregateMapFactory<K,V> extends MapFactory<K,V>
    implements java.io.Serializable {
    private static final class ID { }
    private final Map<Map.Entry<ID,K>,DoublyLinkedList<K,V>> m =
	new HashMap<Map.Entry<ID,K>,DoublyLinkedList<K,V>>();

    /** Creates an {@link AggregateMapFactory}. */
    public AggregateMapFactory() { /* nothing to do here */ }

    /** Generates a new mutable {@link Map} which is a 
     *  subset of the backing set of this
     *  {@link AggregateMapFactory}.
     */
    public Map<K,V> makeMap(final Map<? extends K,? extends V> mm) {
	return new AggregateMap(mm);
    }
    class AggregateMap extends AbstractMap<K,V> {
	// TODO: use list header as IDENTITY object.
	final ID IDENTITY = new ID();
	/* backing store for efficient iteration */
	DoublyLinkedList<K,V> entries=null;
	int size=0;

	AggregateMap(Map<? extends K,? extends V> mm) { putAll(mm); }

	private void unlink(DoublyLinkedList<K,V> entry) {
	    if (entries==entry) { // first element.
		entries = entry.next; // reset first element.
	    } else {
		entry.prev.next = entry.next;
		if (entry.next!=null) // maybe the last element.
		    entry.next.prev = entry.prev;
	    }
	    size--;
	}
	private void link(DoublyLinkedList<K,V> entry) {
	    entry.next = entries;
	    if (entries!=null) // maybe nothing on list.
		entries.prev = entry;
	    entries = entry;
	    size++;
	}

	public V put(K key, V value) {
	    DoublyLinkedList<K,V> entry=new DoublyLinkedList<K,V>(key, value);
	    DoublyLinkedList<K,V> old=m.put(Default.entry(IDENTITY,key),entry);
	    if (old!=null) unlink(old);
	    link(entry);
	    return (old==null) ? null : old.getValue();
	}
	public boolean containsKey(Object key) {
	    return m.containsKey(Default.entry(IDENTITY, key));
	}
	public boolean containsValue(Object value) {
	    for (DoublyLinkedList<K,V> dll=entries; dll!=null; dll=dll.next)
		if (value==null ?
		    (value==dll.getValue()) :
		    value.equals(dll.getValue()))
		    return true;
	    return false;
	}
	public Set<Map.Entry<K,V>> entrySet() {
	    return new AbstractMapSet<K,V>() {
		    public Iterator<Map.Entry<K,V>> iterator() {
			return new Iterator<Map.Entry<K,V>>() {
				DoublyLinkedList<K,V> dll=entries, last=null;
				public boolean hasNext() { return dll!=null; }
				public Map.Entry<K,V> next() {
				    if (dll==null)
					throw new NoSuchElementException();
				    last = dll;
				    dll=dll.next;
				    return last;
				}
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
		    public int size() { return size; }
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
		    public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) return false;
			Map.Entry me = (Map.Entry) o;
			Map.Entry pair = Default.entry(IDENTITY, me.getKey());
			if (!m.containsKey(pair)) return false;
			return me.equals(m.get(pair));
		    }
		    public boolean remove(Object o) {
			if (!contains(o)) return false;
			Map.Entry me = (Map.Entry) o;
			AggregateMap.this.remove(me.getKey());
			return true;
		    }
		    public Map<K,V> asMap() { return AggregateMap.this; }
		};
	}
	public V get(Object key) {
	    DoublyLinkedList<K,V> entry = m.get(Default.entry(IDENTITY, key));
	    return (entry==null)?null:entry.getValue();
	}
	public V remove(Object key) {
	    DoublyLinkedList<K,V> entry = m.remove(Default.entry(IDENTITY, key));
	    if (entry!=null) unlink(entry);
	    return (entry==null)?null:entry.getValue();
	}
	public int size() { return size; }
	// garbage-collect entries in backing store, too!
	protected void finalize() {
	    clear();
	}
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
