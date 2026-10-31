// HashEnvironment.java, created Sat Aug 28 23:02:21 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
/**
 * A {@link HashEnvironment} is an {@link Environment} using
 * a {@link HashMap} as the backing store.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public class HashEnvironment<K,V> extends AbstractMap<K,V>
    implements Environment<K,V> {
    final Map<K,LList<V>> back = new HashMap<K,LList<V>>();
    final List<K> scope = new ArrayList<K>();
    int size = 0;
    
    /** Creates a {@link HashEnvironment}. */
    public HashEnvironment() { }
    /** Creates a {@link HashEnvironment} with all the mappings in
     *  the given map. */
    public HashEnvironment(Map<? extends K,? extends V> m) {
	this();
	putAll(m);
    }

    // -- MAP INTERFACE
    /** Returns <code>true</code> if this map contains a mapping for the
     *  specified key. */
    @Override
    public boolean containsKey(Object key) {
	LList<V> l = back.get(key);
	return (l==null) ? false : l.hasValue();
    }
    /** Returns the value to which this map maps the specified key. */
    @Override
    public V get(Object key) {
	LList<V> l = back.get(key);
	return (l==null) ? null : l.getValue();
    }
    /** Associates the specified value with the specified key in this map. */
    @Override
    public V put(K key, V value) {
	LList<V> l = back.get(key);
	back.put(key, new Valued<V>(value, l));
	scope.add(key);
	if (l==null || !l.hasValue()) { size++; return null; }
	return l.getValue();
    }
    /** Removes the mapping for this key from this map if present. */
    @Override
    public V remove(Object key) {
	LList<V> l = back.get(key);
	if (l==null || !l.hasValue()) return null;
	K k = (K) key; // safe if back.get(key) returned non-null.
	back.put(k, new NoValue<V>(l));
	scope.add(k);
	size--;
	return l.getValue();
    }
    /** Removes the top mapping for this key. */
    void pop(K key) {
	LList<V> l = back.get(key);
	assert l!=null;
	assert l instanceof Valued || l.next instanceof Valued;
	if (l.next==null) back.remove(key);
	else back.put(key, l.next);
	if (l.hasValue()) size--;
	if (l.next!=null && l.next.hasValue()) size++;
    }
    /** Returns the number of key-value mappings in this map. */
    @Override public int size() { return size; }
    /** Clears all mappings. */
    @Override
    public void clear() {
	for (K k: keySet())
	    remove(k);
    }
    
    // --- ENVIRONMENT INTERFACE
    @Override
    public Environment.Mark getMark() {
	return new Mark(scope.size());
    }
    @Override
    public void undoToMark(Environment.Mark m) {
	for (int i = ((Mark)m).i; scope.size() > i; ) {
	    pop(scope.get(scope.size()-1));
	    scope.remove(scope.size()-1);
	}
	assert scope.size()==((Mark)m).i : "undoToMark not repeatable!";
    }

    // --- EVIL EVIL SET VIEW
    /** The {@link Set} returned by this method is really a
     *  {@link MapSet}. */
    @Override
    public MapSet<K,V> entrySet() {
	return new AbstractMapSet<K,V>() {
	    @Override public int size() { return HashEnvironment.this.size; }
	    @Override
	    public Iterator<Map.Entry<K,V>> iterator() {
		final Iterator<Map.Entry<K,LList<V>>> it =
		    HashEnvironment.this.back.entrySet().iterator();
		return new Iterator<Map.Entry<K,V>>() {
		    Map.Entry<K,LList<V>> next = advance(), last = null;
		    private Map.Entry<K,LList<V>> advance() {
			while (it.hasNext()) {
			    Map.Entry<K,LList<V>> e = it.next();
			    if (e.getValue().hasValue()) return e;
			}
			return null;
		    }
		    @Override public boolean hasNext() { return next!=null; }
		    @Override
		    public Map.Entry<K,V> next() {
			if (next==null) throw new NoSuchElementException();
			final Map.Entry<K,LList<V>> e = last = next;
			next = advance();
			return new AbstractMapEntry<K,V>() {
			    @Override public K getKey() { return e.getKey(); }
			    @Override public V getValue() { return e.getValue().getValue(); }
			    // put() replaces the value of an existing key in
			    // the backing map, which doesn't disturb the
			    // iteration.
			    @Override
			    public V setValue(V value) {
				return HashEnvironment.this.put(e.getKey(), value);
			    }
			};
		    }
		    @Override
		    public void remove() {
			if (last==null) throw new IllegalStateException();
			// as above, this does not disturb the iteration.
			HashEnvironment.this.remove(last.getKey());
			last = null;
		    }
		};
	    }
	    @Override public HashEnvironment<K,V> asMap(){ return HashEnvironment.this; }
	};
    }
    // needed to make the anonymous class declaration above work.
    static abstract class AbstractMapSet<K,V>
	extends AbstractSet<Map.Entry<K,V>> implements MapSet<K,V> { }
	
    private static class Mark implements Environment.Mark {
	final int i;
	Mark(int i) { this.i = i; }
    }
    private static abstract class LList<T> {
	final LList<T> next;
	LList(LList<T> next) { this.next=next; }
	abstract boolean hasValue();
	abstract T getValue();
    }
    private static class NoValue<T> extends LList<T> {
	NoValue(LList<T> next) { super(next); }
	@Override boolean hasValue() { return false; }
	@Override T getValue() { return null; }
    }
    private static class Valued<T> extends LList<T> {
	final T value;
	Valued(T value, LList<T> next) { super(next); this.value=value; }
	@Override boolean hasValue() { return true; }
	@Override T getValue() { return value; }
    }

}
