// UnmodifiableMultiMap.java, created Wed Jun 21  3:22:34 2000 by pnkfelix
// Copyright (C) 2001 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;

/** <code>UnmodifiableMultiMap</code> is an abstract superclass to
    save developers the trouble of implementing the various mutator
    methds of the <code>MultiMap</code> interface.

    @author  Felix S. Klock II <pnkfelix@mit.edu>
    @version $Id: UnmodifiableMultiMap.java,v 1.3 2006-10-29 16:27:21 cananian Exp $
*/
public abstract class UnmodifiableMultiMap<K,V> 
    extends AbstractMap<K,V> implements MultiMap<K,V> {

    /** Constructs and returns an unmodifiable <code>MultiMap</code>
	backed by <code>mmap</code>.
    */
    public static <K,V> MultiMap<K,V> proxy(final MultiMap<K,V> mmap) {
	return new UnmodifiableMultiMap<K,V>() {
		public V get(Object key) { 
		    return mmap.get(key); 
		}
		public Collection<V> getValues(K key) { 
		    return mmap.getValues(key);
		}
		public boolean contains(Object a, Object b) { 
		    return mmap.contains(a, b);
		}
		public MultiMapSet<K,V> entrySet() { return mmap.entrySet(); }
	    };
    }
    /** Returns a <code>Set</code> view that allows you to recapture
     *  the <code>MultiMap</code> view. */
    public abstract MultiMapSet<K,V> entrySet();

    /** Throws UnsupportedOperationException. */
    public V put(K key, V value) { die(); return null; }
    /** Throws UnsupportedOperationException. */
    public V remove(Object key) { die(); return null; }
    /** Throws UnsupportedOperationException. */
    public boolean remove(Object key, Object value) { return die(); }
    /** Throws UnsupportedOperationException. */
    public void putAll(Map<? extends K,? extends V> t) { die(); }
    /** Throws UnsupportedOperationException. */
    public void clear() { die(); }
    /** Throws UnsupportedOperationException. */
    public boolean add(K key, V value) { return die(); }
    /** Throws UnsupportedOperationException. */
    public boolean addAll(K key, Collection<? extends V> values) { return die(); }
    /** Throws UnsupportedOperationException. */
    public boolean addAll(MultiMap<? extends K,? extends V> mm) { return die(); }
    /** Throws UnsupportedOperationException. */
    public boolean retainAll(K key, Collection<?> values) { return die(); }
    /** Throws UnsupportedOperationException. */
    public boolean removeAll(K key, Collection<?> values) { return die(); }
    private boolean die() {
	if (true) throw new UnsupportedOperationException();
	return false;
    }
}
