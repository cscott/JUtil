// GenericInvertibleMap.java, created Wed Jun 21  3:22:34 2000 by pnkfelix
// Copyright (C) 2001 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Map;
import java.util.Iterator;

/** <code>GenericInvertibleMap</code> is a default implementation of
    <code>InvertibleMap</code>.  It returns unmodifiable inverted
    views of the mappings it maintains.

    @author  Felix S. Klock II <pnkfelix@mit.edu>
    @version $Id: GenericInvertibleMap.java,v 1.2 2004-01-13 20:47:05 cananian Exp $
*/
public class GenericInvertibleMap<K,V>
    extends MapWrapper<K,V> implements InvertibleMap<K,V> {
    // inverted map
    private MultiMap<V,K> imap;

    /** Constructs an invertible map backed by a HashMap.
     */
    public GenericInvertibleMap() {
	this(Factories.<K,V>hashMapFactory(), new MultiMapFactory<V,K>());
    }

    /** Constructs an invertible map backed by a map constructed by
	<code>mf</code> and an inverted map constructed by
	<code>mmf</code>. 
     */
    public GenericInvertibleMap(MapFactory<K,V> mf, MultiMapFactory<V,K> mmf) {
	super(mf.makeMap());
	imap = mmf.makeMultiMap();
    }

    public GenericInvertibleMap(Map<K,V> m) {
	this();
	putAll(m);
    }

    /** Returns an unmodifiable inverted view of <code>this</code>.
     */
    public MultiMap<V,K> invert() {
	return UnmodifiableMultiMap.proxy(imap);
    }

    public V put(K key, V value) {
	V old = super.put(key, value);
	imap.remove(old, key);
	imap.add(value, key);
	return old;
    }

    public void putAll(Map<? extends K,? extends V> m) {
	super.putAll(m);
	Iterator<? extends Map.Entry<? extends K,? extends V>> entries = m.entrySet().iterator();
	while(entries.hasNext()) {
	    Map.Entry<? extends K,? extends V> e = entries.next();
	    imap.add(e.getValue(), e.getKey());
	}
    }
    
    public V remove(Object key) {
	V r = super.remove(key);
	imap.remove(r, key);
	return r;
    }
}
