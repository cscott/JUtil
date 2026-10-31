// PersistentEnvironment.java, created Sat Aug 28 22:23:47 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
/**
 * {@link PersistentEnvironment} is an {@link Environment}
 * built on a {@link PersistentMap}.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
// TODO: reimplement with Map returned by PersistentMapFactory as backing store
// simply put Map.clone() in the Environment.Mark and replace the delegate
// on undoToMark().
public class PersistentEnvironment<K,V> extends AbstractMap<K,V>
    implements Environment<K,V> {
    PersistentMap<K,V> m = new PersistentMap<K,V>();
    
    /** Creates a {@link PersistentEnvironment} with no mappings. */
    public PersistentEnvironment() { }
    /** Creates a {@link PersistentEnvironment} with the same
     *  mappings as the given {@link Map}. */
    public <K2 extends K, V2 extends V> PersistentEnvironment(Map<K2,V2> m) {
	putAll(m);
    }

    // ------------- MAP INTERFACE ---------------
    /** Remove all mappings from this map. */
    @Override public void clear() { this.m = new PersistentMap<K,V>(); }
    /** Returns <code>true</code> if this map contains no key-value mappings.*/
    @Override public boolean isEmpty() { return m.isEmpty(); }
    /** Returns the numer of key-value mappings in this map. */
    @Override public int size() { return m.size(); }
    /** Returns the value to which this map maps the specified key. */
    @Override
    @SuppressWarnings("unchecked") // a key of another type makes the comparator throw, as in TreeMap
    public V get(Object key) { return m.get((K)key); }
    /** Associates the specified value with the specified key in this map. */
    @Override
    public V put(K key, V value) {
	V prev = m.get(key);
	this.m = m.put(key, value);
	return prev;
    }
    /** Returns <code>true</code> if this map contains a mapping for the
     *  specified key. */
    @Override
    @SuppressWarnings("unchecked") // a key of another type makes the comparator throw, as in TreeMap
    public boolean containsKey(Object key) { return m.containsKey((K)key); }
    /** Removes the mapping for this key from this map if present. */
    @Override
    @SuppressWarnings("unchecked") // a key of another type makes the comparator throw, as in TreeMap
    public V remove(Object key) {
	K k = (K) key; // not safe, since we depend on a Comparator<K>
	V prev = m.get(k);
	this.m = m.remove(k);
	return prev;
    }

    // ------------- ENVIRONMENT INTERFACE ---------------
    /** A mark into an {@link PersistentEnvironment}. */
    private static class Mark<K,V> implements Environment.Mark {
	final PersistentMap<K,V> m;
	Mark(PersistentMap<K,V> m) { this.m = m; }
    }
    /** Get a mark that will allow you to restore the current state of
     *  this environment. */
    @Override public Environment.Mark getMark() { return new Mark<K,V>(m); }
    /** Undo all changes since the supplied mark, restoring the map to
     *  its state at the time the mark was taken. */
    @Override
    @SuppressWarnings("unchecked") // the mark must come from this environment.
    public void undoToMark(Environment.Mark m) { this.m = ((Mark<K,V>)m).m; }

    // ------------- THE DREADED ENTRYSET ---------------
    /** Returns a set view of the mappings contained in this map.
     *  The returned set is immutable. */
    @Override
    public Set<Map.Entry<K,V>> entrySet() {
	return Collections.unmodifiableSet(m.asMap().entrySet());
    }
}
