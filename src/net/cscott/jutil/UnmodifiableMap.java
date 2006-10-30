package net.cscott.jutil;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * This wrapper for {@link Map}s implements {@link Map#entrySet()}
 * so that it returns an {@link UnmodifiableMapSet}.
 * @author C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: UnmodifiableMap.java,v 1.2 2006-10-30 16:19:00 cananian Exp $
 */
public abstract class UnmodifiableMap<K,V> extends MapWrapper<K,V> {
    public static <K,V> UnmodifiableMap<K,V> proxy(final Map<K,V> m) {
        final MapSet<K,V> ms;
        if (m.entrySet() instanceof MapSet) {
            ms = (MapSet<K,V>) m.entrySet();
        } else {
            ms = new Factories.MapSetWrapper<K,V>() {
                @Override
                protected Set<Map.Entry<K,V>> wrapped() {return m.entrySet();}
                public Map<K,V> asMap() { return m; }
            };
        }
        return new UnmodifiableMap<K,V>() {
            @Override
            protected Map<K, V> wrapped() { return m; }
            // next is a work-around: java doesn't have a way to reference
            // 'UnmodifiableMap.this' from the inner context: it thinks we are
            // referring to the outer abstract class UnmodifiableMap, not the
            // anonymous class inside the proxy method.
            private final UnmodifiableMap<K,V> um=this;
            private final UnmodifiableMapSet<K,V> mapSet =
                new UnmodifiableMapSet<K,V>() {
                    @Override
                    protected MapSet<K,V> wrapped() { return ms; }
                    @Override
                    public UnmodifiableMap<K, V> asMap() { return um; }
            };
            @Override
            public UnmodifiableMapSet<K, V> entrySet() { return mapSet; }
        };
    }

    protected UnmodifiableMap() { }
    @Override
    public Collection<V> values() {
        return Collections.unmodifiableCollection(wrapped().values());
    }
    @Override
    public abstract /*Unmodifiable*/MapSet<K,V> entrySet(); // XXX bug in JDK1.5/1.6 compiler

    // protect map mutators.
    /** Throws {@link UnsupportedOperationException}. */
    @Override
    public void clear() { die(); }
    /** Throws {@link UnsupportedOperationException}. */
    @Override
    public Set<K> keySet() { die(); return null; }
    /** Throws {@link UnsupportedOperationException}. */
    @Override
    public V put(K k, V v) { die(); return null; }
    /** Throws {@link UnsupportedOperationException}. */
    @Override
    public void putAll(Map<? extends K, ? extends V> m) { die(); }
    /** Throws {@link UnsupportedOperationException}. */
    @Override
    public V remove(Object o) { die(); return null; }
    /** Helper function: throws {@link UnsupportedOperationException}. */
    private static void die() {
        throw new UnsupportedOperationException();
    }
}
