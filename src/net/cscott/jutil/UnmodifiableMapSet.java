package net.cscott.jutil;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import net.cscott.jutil.Factories.MapSetWrapper;

/**
 * {@link UnmodifiableMapSet} prevents mutation of a wrapped
 * {@link MapSet}.
 * @author C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: UnmodifiableMapSet.java,v 1.1 2006-10-29 20:15:47 cananian Exp $
 */
public abstract class UnmodifiableMapSet<K,V> extends MapSetWrapper<K,V> {
    protected UnmodifiableMapSet() { }
    /** Implementations should return the wrapped {@link MapSet} here. */ 
    @Override
    protected abstract MapSet<K,V> wrapped();
    public abstract UnmodifiableMap<K,V> asMap();
    @Override
    public Iterator<Entry<K, V>> iterator() {
        return UnmodifiableIterator.proxy(wrapped().iterator());
    }
    // disallowed methods.
    @Override
    public boolean add(Entry<K, V> o) { return die(); }
    @Override
    public boolean addAll(Collection<? extends Entry<K, V>> c) { return die();}
    @Override
    public void clear() { die(); }
    @Override
    public boolean remove(Object o) { return die(); }
    @Override
    public boolean removeAll(Collection<?> c) { return die(); }
    @Override
    public boolean retainAll(Collection<?> c) { return die(); }
    // helper function, for brevity.
    private static boolean die() {
        throw new UnsupportedOperationException();
    }
}
