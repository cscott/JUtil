// UnmodifiableMultiMap.java, created Wed Jun 21  3:22:34 2000 by pnkfelix
// Copyright (C) 2001 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/** {@link UnmodifiableMultiMap} is an abstract superclass to
    save developers the trouble of implementing the various mutator
    methds of the {@link MultiMap} interface.

    @author  Felix S. Klock II <pnkfelix@mit.edu>
    @version $Id: UnmodifiableMultiMap.java,v 1.5 2006-10-30 19:58:07 cananian Exp $
*/
public abstract class UnmodifiableMultiMap<K,V> 
    extends UnmodifiableMap<K,V> implements MultiMap<K,V> {
    protected UnmodifiableMultiMap() { }
    // narrow type
    protected abstract MultiMap<K,V> wrapped();
    /** Constructs and returns an unmodifiable {@link MultiMap}
	backed by <code>mmap</code>.
    */
    public static <K,V> MultiMap<K,V> proxy(final MultiMap<K,V> mmap) {
        final MultiMapSet<K,V> mms;
        if (mmap.entrySet() instanceof MultiMapSet) {
            mms = (MultiMapSet<K,V>) mmap.entrySet();
        } else {
            mms = new Factories.MultiMapSetWrapper<K,V>() {
                @Override
                protected Set<Map.Entry<K,V>> wrapped(){return mmap.entrySet();}
                public MultiMap<K, V> asMultiMap() { return mmap; }
            };
        }
	return new UnmodifiableMultiMap<K,V>() {
            @Override
            protected MultiMap<K,V> wrapped() { return mmap; }
            // next is a work-around: java doesn't have a way to reference
            // 'UnmodifiableMap.this' from the inner context: it thinks we are
            // referring to the outer abstract class UnmodifiableMap, not the
            // anonymous class inside the proxy method.
            private final UnmodifiableMultiMap<K,V> umm=this;
            private final UnmodifiableMultiMapSet<K,V> mapSet =
                new UnmodifiableMultiMapSet<K,V>() {
                    @Override
                    protected MultiMapSet<K,V> wrapped() { return mms; }
                    @Override
                    public UnmodifiableMultiMap<K, V> asMultiMap(){return umm;}
            };
            @Override
            public UnmodifiableMultiMapSet<K, V> entrySet() { return mapSet; }
        };
    }
    /** Returns a {@link Set} view that allows you to recapture
     *  the {@link MultiMap} view. */
    public abstract UnmodifiableMultiMapSet<K,V> entrySet();
    public Collection<V> getValues(K key) {
        return Collections.unmodifiableCollection(wrapped().getValues(key));
    }
    public boolean contains(Object a, Object b) {
        return wrapped().contains(a,b);
    }

    /** Throws {@link UnsupportedOperationException}. */
    public boolean remove(Object key, Object value) { return die(); }
    /** Throws {@link UnsupportedOperationException}. */
    public boolean add(K key, V value) { return die(); }
    /** Throws {@link UnsupportedOperationException}. */
    public boolean addAll(K key, Collection<? extends V> values) { return die(); }
    /** Throws {@link UnsupportedOperationException}. */
    public boolean addAll(MultiMap<? extends K,? extends V> mm) { return die(); }
    /** Throws {@link UnsupportedOperationException}. */
    public boolean retainAll(K key, Collection<?> values) { return die(); }
    /** Throws {@link UnsupportedOperationException}. */
    public boolean removeAll(K key, Collection<?> values) { return die(); }
    /** Helper function: throws {@link UnsupportedOperationException}. */
    private boolean die() {
	throw new UnsupportedOperationException();
    }
}
