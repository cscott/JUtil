// AbstractMultiMap.java, created Wed Jan 19 14:14:02 2005 by cananian
// Copyright (C) 2005 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.AbstractMap;

/**
 * This class provides a skeletal implementation of the <code>MultiMap</code>
 * interface, to minimize the effort requires to implement this interface.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: AbstractMultiMap.java,v 1.2 2005-01-20 20:54:41 cananian Exp $ */
public abstract class AbstractMultiMap<K,V> extends AbstractMap<K,V>
    implements MultiMap<K,V> {

    public String toString() {
	StringBuffer sb = new StringBuffer();
	sb.append('{');
	for(K k : keySet()) {
	    Collection<V> values = getValues(k);
	    if (values.size()==0) continue;
	    sb.append(k.toString());
	    sb.append('=');
	    if (values.size()==1)
		sb.append(values.iterator().next().toString());
	    else
		sb.append(values.toString());
	}
	sb.append('}');
	return sb.toString();
    };
    public int hashCode() {
	int sum = 0;
	for (Map.Entry<K,V> entry : entrySet())
	    sum += entry.hashCode();
	return sum;
    }
    /** Ensures that <code>this</code> contains an association from
	<code>key</code> to <code>value</code>.

	(<code>MultiMap</code> specific operation).

	@return <code>true</code> if this mapping changed as a result of
	        the call
    */
    public boolean add(K key, V value) {
	return getValues(key).add(value);
    }
    
    /** Adds to the current mappings: associations for
	<code>key</code> to each value in <code>values</code>.  

	(<code>MultiMap</code> specific operation). 

	@return <code>true</code> if this mapping changed as a result
	        of the call
    */
    public boolean addAll(K key, Collection<? extends V> values) {
	return getValues(key).addAll(values);
    }
    /** Add all mappings in the given multimap to this multimap. */
    public boolean addAll(MultiMap<? extends K,? extends V> mm) {
	boolean changed = false;
	for (Map.Entry<? extends K, ? extends V> me : mm.entrySet())
	    if (add(me.getKey(), me.getValue()))
		changed = true;
	return changed;
    }
	
    /** Removes from the current mappings: associations for
	<code>key</code> to any value not in <code>values</code>. 

	(<code>MultiMap</code> specific operation). 

	@return <code>true</code> if this mapping changed as a result
	        of the call
    */
    public boolean retainAll(K key, Collection<?> values) {
	return getValues(key).retainAll(values);
    }

    /** Removes from the current mappings: associations for
	<code>key</code> to any value in <code>values</code>.

	(<code>MultiMap</code> specific operation). 

	@return <code>true</code> if this mapping changed as a result
	        of the call
    */
    public boolean removeAll(K key, Collection<?> values) {
	return getValues(key).removeAll(values);
    }

    public boolean equals(Object o) {
	if (o==null) return false;
	if (o==this) return true;
	try {
	    Set entrySet = ((Map) o).entrySet();
	    return this.entrySet().equals(entrySet);
	} catch (ClassCastException e) {
	    return false;
	}
    }

    /** Copies the mappings from the specified map to this
	map.  These mappings will replace any mappings that this map
	had for any of the keys currently in the specified map.  Note
	that <code>putAll(mm)</code> where <code>mm</code> is a
	<code>MultiMap</code> will NOT add all of the mappings in
	<code>mm</code>; it will only add all of the Keys in
	<code>mm</code>, mapping each Key to one of the Values it
	mapped to in <code>mm</code>.  To add all of the mappings from
	another <code>MultiMap</code>, use
	<code>addAll(MultiMap)</code>.  */
    public void putAll(Map<? extends K,? extends V> t) {
	for (Map.Entry<? extends K, ? extends V> e : t.entrySet())
	    this.put( e.getKey(), e.getValue() );
    }

    /** Associates the specified value with the specified key in this
	map.  If the map previously contained any mappings for this
	key, all of the old values are replaced.  Returns some value
	that was previous associated with the specified key, or
	<code>null</code> if no values were associated previously. 
    */
    public V put(K key, V value) {
	Collection<V> c = getValues(key);
	V prev = c.size()==0 ? null : c.iterator().next();
	c.clear();
	c.add(value);
	return prev;
    }
    public boolean containsKey(Object key) {
	return keySet().contains(key);
    }
    public boolean containsValue(Object value) {
	return values().contains(value);
    }
    public boolean isEmpty() { return size()==0; }
    public int size() { return entrySet().size(); }

    public V get(Object key) {
	if (!containsKey(key)) return null;
	return getValues((K)key).iterator().next();
    }

    public abstract Collection<V> getValues(K key);
    public abstract boolean contains(Object a, Object b);

    public abstract V remove(Object key);
    public abstract boolean remove(Object key, Object value);
    public abstract void clear();
    public abstract MultiMapSet<K,V> entrySet();
}
