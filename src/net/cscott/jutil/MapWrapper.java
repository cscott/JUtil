// MapWrapper.java, created Wed Jun 21  3:22:34 2000 by pnkfelix
// Copyright (C) 2001 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Map;

/** {@link MapWrapper} is a class that acts as a proxy for
    another backing map, to allow for easy extension of
    {@link Map} functionality while not restricting developers to
    one particular {@link Map} implementation. 

    @author  Felix S. Klock II <pnkfelix@mit.edu>
    @version $Id: MapWrapper.java,v 1.4 2006-10-30 19:58:06 cananian Exp $
*/
public abstract class MapWrapper<K,V> implements Map<K,V> {
    public MapWrapper() { }
    protected abstract Map<K,V> wrapped();
    public int size() { return wrapped().size(); }
    public boolean isEmpty() { return wrapped().isEmpty(); }
    public boolean containsKey(Object o) { return wrapped().containsKey(o); }
    public boolean containsValue(Object o) { return wrapped().containsValue(o); }
    public V get(Object o) { return wrapped().get(o); }
    public V put(K k, V v) { return wrapped().put(k, v); }
    public V remove(Object o) { return wrapped().remove(o); }
    public void putAll(Map<? extends K,? extends V> m) { wrapped().putAll(m); }
    public void clear() { wrapped().clear(); }
    public java.util.Set<K> keySet() { return wrapped().keySet(); }
    public java.util.Set<Map.Entry<K,V>> entrySet() { return wrapped().entrySet(); }
    public java.util.Collection<V> values() { return wrapped().values(); }
    @Override
    public String toString() { return wrapped().toString(); }
    @Override
    public boolean equals(Object o) { return wrapped().equals(o); }
    @Override
    public int hashCode() { return wrapped().hashCode(); }
}
