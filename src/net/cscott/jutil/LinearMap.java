// LinearMap.java, created Wed Aug  4 11:59:14 1999 by pnkfelix
// Copyright (C) 1999 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * {@link LinearMap} is a simplistic light-weight
 * {@link Map} designed for use when the number of entries is
 * small.  It is backed by a {@link LinearSet}.
 *
 * @author  Felix S. Klock II <pnkfelix@mit.edu>
 * @version $Id: LinearMap.java,v 1.4 2006-10-30 19:58:06 cananian Exp $
 */
public class LinearMap<K,V> extends AbstractMap<K,V> {
    private LinearSet<Map.Entry<K,V>> set;

    /** Creates a {@link LinearMap}. */
    public LinearMap() {
        set = new LinearSet<Map.Entry<K,V>>();
    }

    public LinearMap(Map<K,V> map) {
	set = new LinearSet<Map.Entry<K,V>>(map.entrySet());
    }

    /** Creates a {@link LinearMap} with specified capacity. */
    public LinearMap(int capacity) {
        set = new LinearSet<Map.Entry<K,V>>(capacity);
    }

    public Set<Map.Entry<K,V>> entrySet() {
	// support remove operations only; otherwise allows multimaps.
	return new AbstractSet<Map.Entry<K,V>>() {
	    public int size() { return set.size(); }
	    public Iterator<Map.Entry<K,V>> iterator() {return set.iterator();}
	};
    }

    public V put(K key, V value) {
	Iterator<Map.Entry<K,V>> entries = set.iterator();
	V oldValue = null;
	while(entries.hasNext()) {
	    Map.Entry<K,V> entry = entries.next();
	    if (keysMatch(key, entry.getKey())) {
		oldValue = entry.getValue();
		entry.setValue(value);
		return oldValue;
	    }
	}
	set.add(new PairMapEntry<K,V>(key, value));
	return oldValue;
    }

    private boolean keysMatch(Object k1, K k2) {
	return ((k1 == null && k2 == null) ||
		(k1 != null && k2 != null && 
		 k1.equals(k2)));
    }

    public V remove(Object key) {
	Iterator<Map.Entry<K,V>> entries = set.iterator();
	V oldValue = null;
	while(entries.hasNext()) {
	    Map.Entry<K,V> entry = entries.next();
	    if (keysMatch(key, entry.getKey())) {
		oldValue = entry.getValue();
		set.remove(entry);
		break;
	    }
	}
	return oldValue;
    }
} 
