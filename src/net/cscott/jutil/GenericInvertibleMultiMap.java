// GenericInvertibleMultiMap.java, created Sun Jun 17 16:19:35 2001 by cananian
// Copyright (C) 2001 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * <code>GenericInvertibleMultiMap</code> is a default implementation of
 * <code>InvertibleMultiMap</code>.  It returns modifiable inverted
 * views of the mappings it maintains.  Note that a
 * <code>GenericInvertibleMultiMap</code> can directly replace a
 * <code>GenericInvertibleMap</code>, because <code>MultiMap</code>
 * correctly extends <code>Map</code>.
 *
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: GenericInvertibleMultiMap.java,v 1.2 2004-01-13 20:47:05 cananian Exp $
 */
public class GenericInvertibleMultiMap<K,V> implements InvertibleMultiMap<K,V> {
    private final MultiMap<K,V> map;
    private final MultiMap<V,K> imap;
    private final InvertibleMultiMap<V,K> inverse;
    private GenericInvertibleMultiMap(MultiMap<K,V> map, MultiMap<V,K> imap,
				      InvertibleMultiMap<V,K> inverse) {
	this.map = map; this.imap = imap; this.inverse=inverse;
    }
    private GenericInvertibleMultiMap(MultiMap<K,V> map, MultiMap<V,K> imap) {
	this.map = map; this.imap = imap;
	this.inverse = new GenericInvertibleMultiMap<V,K>(imap, map, this);
    }
    
    /* have to put up w/ unchecked-type warnings on these; no way to say
     * 'this method only makes sense if K==V'. */
    public GenericInvertibleMultiMap(MultiMapFactory mmf) {
	this((MultiMap<K,V>)mmf.makeMultiMap(), (MultiMap<V,K>)mmf.makeMultiMap());
    }
    public GenericInvertibleMultiMap(MapFactory mf, CollectionFactory cf) {
	this((MultiMap<K,V>)new GenericMultiMap(mf,cf), (MultiMap<V,K>)new GenericMultiMap(mf,cf));
    }
    public GenericInvertibleMultiMap(CollectionFactory cf) {
	this(Factories.hashMapFactory, cf);
    }
    public GenericInvertibleMultiMap() {
	this(Factories.hashSetFactory);
    }

    /* Collections API */
    public GenericInvertibleMultiMap(Map<? extends K,? extends V> m) {
	this();
	putAll(m);
    }
    public GenericInvertibleMultiMap(MultiMap<? extends K,? extends V> mm) {
	this();
	addAll(mm);
    }

    /** Returns an unmodifiable inverted view of <code>this</code>.
     */
    public InvertibleMultiMap<V,K> invert() { return inverse; }

    public boolean add(K key, V value) {
	imap.add(value, key);
	return map.add(key, value);
    }
    public boolean addAll(K key, Collection<? extends V> values) {
	boolean changed = false;
	for (Iterator<? extends V> it=values.iterator(); it.hasNext(); )
	    if (this.add(key, it.next()))
		changed = true;
	return changed;
    }
    public boolean addAll(MultiMap<? extends K,? extends V> mm) {
	boolean changed = false;
	for (Iterator<? extends Map.Entry<? extends K,? extends V>> it=mm.entrySet().iterator(); it.hasNext(); ) {
	    Map.Entry<? extends K,? extends V> me = it.next();
	    if (add(me.getKey(), me.getValue()))
		changed = true;
	}
	return changed;
    }
    public void clear() { map.clear(); imap.clear(); }
    public boolean contains(Object a, Object b) {
	return map.contains(a, b);
    }
    public boolean containsKey(Object key) {
	return map.containsKey(key);
    }
    public boolean containsValue(Object value) {
	return imap.containsKey(value);
    }
    /** The <code>Set</code> returned by this method is actually an
     *  instance of <code>MultiMapSet</code>. */
    public MultiMapSet<K,V> entrySet() {
	// value field of entry set contains a single value.
	return new AbstractMultiMapSet<K,V>() {
		public Iterator<Map.Entry<K,V>> iterator() {
		    final Iterator<Map.Entry<K,V>> it =
			map.entrySet().iterator();
		    return new Iterator<Map.Entry<K,V>>() {
			    Map.Entry<K,V> last;
			    public boolean hasNext() { return it.hasNext(); }
			    public Map.Entry<K,V> next() {
				last = it.next();
				return last;
			    }
			    public void remove() {
				// do it here.
				it.remove();
				// mirror op in imap.
				imap.remove(last.getValue(), last.getKey());
			    }
			};
		}
		public void clear() { GenericInvertibleMultiMap.this.clear(); }
		public boolean contains(Object o) {
		    if (!(o instanceof Map.Entry)) return false;
		    Map.Entry<K,V> me = (Map.Entry) o;
		    return GenericInvertibleMultiMap.this.contains
			(me.getKey(), me.getValue());
		}
		public int size() {
		    return GenericInvertibleMultiMap.this.size();
		}
		public boolean remove(Object o) {
		    if (!(o instanceof Map.Entry)) return false;
		    Map.Entry<K,V> me = (Map.Entry) o;
		    return GenericInvertibleMultiMap.this.remove
			(me.getKey(), me.getValue());
		}
		public MultiMap<K,V> asMap() { return asMultiMap(); }
		public MultiMap<K,V> asMultiMap() {
		    return GenericInvertibleMultiMap.this;
		}
	    };
    }
    // this declaration is necessary to make the anonymous class above work.
    static abstract class AbstractMultiMapSet<K,V>
	extends AbstractSet<Map.Entry<K,V>>
	implements MultiMapSet<K,V> { }

    public boolean equals(Object o) {
	return map.equals(o);
    }
    public V get(Object key) {
	return map.get(key);
    }
    public Collection<V> getValues(final K key) {
	return new AbstractCollection<V>() {
	    public Iterator<V> iterator() {
		final Iterator<V> it=map.getValues(key).iterator();
		return new Iterator<V>() {
		    V last;
		    public boolean hasNext() { return it.hasNext(); }
		    public V next() { last=it.next(); return last; }
		    public void remove() {
			imap.remove(last, key);
			it.remove();
		    }
		};
	    }
	    public boolean add(V o) {
		return GenericInvertibleMultiMap.this.add(key, o);
	    }
	    public void clear() { map.remove(key); }
	    public boolean contains(Object o) {
		return GenericInvertibleMultiMap.this.contains(key, o);
	    }
	    public boolean remove(Object o) {
		return GenericInvertibleMultiMap.this.remove(key, o);
	    }
	    public int size() { return map.getValues(key).size(); }
	};
    }
    public int hashCode() {
	return map.hashCode();
    }
    public boolean isEmpty() {
	return map.isEmpty();
    }
    public Set<K> keySet() {
	return new AbstractSet<K>() {
	    public Iterator<K> iterator() {
		final Iterator<K> it = map.keySet().iterator();
		return new Iterator<K>() {
		    K last;
		    public boolean hasNext() { return it.hasNext(); }
		    public K next() { last=it.next(); return last; }
		    public void remove() {
			// mirror op in imap.
			for (Iterator<V> it2=map.getValues(last).iterator();
			     it2.hasNext(); )
			    imap.remove(it2.next(), last);
			// do it here.
			it.remove();
		    }
		};
	    }
	    public void clear() { GenericInvertibleMultiMap.this.clear(); }
	    public boolean contains(Object o) { return containsKey(o); }
	    public int size() { return GenericInvertibleMultiMap.this.size(); }
	    public boolean remove(Object o) {
		boolean changed = containsKey(o);
		GenericInvertibleMultiMap.this.remove(o);
		return changed;
	    }
	};
    }
    public V put(K key, V value) {
	V old = this.remove(key);
	this.add(key, value);
	return old;
    }
    public void putAll(Map<? extends K,? extends V> t) {
	for (Iterator<? extends Map.Entry<? extends K, ? extends V>> it =
		 t.entrySet().iterator(); it.hasNext(); ) {
	    Map.Entry<? extends K, ? extends V> me = it.next();
	    this.put(me.getKey(), me.getValue());
	}
    }
    public V remove(Object key) {
	V old = null;
	for (Iterator<V> it=this.getValues((K)key).iterator(); it.hasNext(); ){
	    old = it.next();
	    it.remove();
	}
	return old;
    }
    public boolean remove(Object key, Object value) {
	imap.remove(value, key);
	return map.remove(key, value);
    }
    public boolean removeAll(K key, Collection<?> values) {
	boolean changed = false;
	for (Iterator<?> it=values.iterator(); it.hasNext(); )
	    if (this.remove(key, it.next()))
		changed = true;
	return changed;
    }
    public boolean retainAll(K key, Collection<?> values) {
	boolean changed = false;
	for (Iterator<V> it=this.getValues(key).iterator(); it.hasNext(); )
	    if (!values.contains(it.next())) {
		it.remove();
		changed = true;
	    }
	return changed;
    }
    public int size() { return map.size(); }
    public String toString() { return map.toString(); }
    // this is a little unexpected: only one copy of each value.
    public Collection<V> values() { return inverse.keySet(); }
}
