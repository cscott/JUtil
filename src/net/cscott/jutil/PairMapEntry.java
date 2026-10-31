// PairMapEntry.java, created Wed Aug  4 12:16:20 1999 by pnkfelix
// Copyright (C) 1999 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

/**
 * {@link PairMapEntry} is the easiest implementation of a
 * {@link java.util.Map.Entry} ever: a pair!  Basically saves coders the
 * drugery of writing an inner class at the expense of an import
 * statement.
 *
 * Note that {@link PairMapEntry}s <b>are</b> mutable:
 * {@link #setValue(Object)} is defined in this class.
 *
 * Using <code>null</code> as a key or value will not cause this class 
 * or {@link AbstractMapEntry} to fail, but be warned that
 * several {@link java.util.Map} implementations do not like 
 * <code>null</code>s in their internal structures.
 * 
 * @author  Felix S. Klock II &lt;pnkfelix@mit.edu&gt;
 */
public class PairMapEntry<K,V> extends AbstractMapEntry<K,V> {
    private K key;
    private V value;

    /** Creates a {@link PairMapEntry}. */
    public PairMapEntry(K key, V value) {
        this.key = key;
	this.value = value;
    }

    @Override
    public K getKey() {
	return key;
    }
    
    @Override
    public V getValue() {
	return value;
    }

    /** For use in subclass implementations *only*. */
    protected K setKey(K newKey) {
	K old = key;
	key = newKey;
	return old;
    }

    @Override
    public V setValue(V newValue) {
	V old = value;
	value = newValue;
	return old;
    }
}
