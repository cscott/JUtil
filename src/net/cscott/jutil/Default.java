// Default.java, created Thu Apr  8 02:22:56 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * <code>Default</code> contains one-off or 'standard, no-frills'
 * implementations of simple <code>Iterator</code>s,
 * <code>Collection</code>s, and <code>Comparator</code>s.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: Default.java,v 1.6 2006-02-20 19:21:12 cananian Exp $
 */
public abstract class Default  {
    /** A <code>Comparator</code> for objects that implement 
     *   <code>Comparable</code> (checked dynamically at run-time). */
    public static final Comparator comparator =
	 Default.<Comparable>comparator();
    /** A <code>Comparator</code> for objects that implement 
     *  <code>Comparable</code> (checked dynamically at run-time).
     *  This version is parameterized to play nicely with Java's
     *  type system. */
    public static final <T extends Comparable<T>> Comparator<T> comparator() {
	return new SerializableComparator<T>() {
	    public int compare(T t1, T t2) {
		if (t1==null && t2==null) return 0;
		// null is less than everything
		if (t1==null) return -1;
		if (t2==null) return 1;
		// okay, cast to Comparable.
		return t1.compareTo(t2);
	    }
	};
    }
    /** An <code>Iterator</code> over the empty set. */
    public static final Iterator nullIterator = nullIterator();
    /** An <code>Iterator</code> over the empty set, parameterized to
     *  play nicely with Java's type system. */
    public static final <E> Iterator<E> nullIterator() {
	return new UnmodifiableIterator<E>() {
	    public boolean hasNext() { return false; }
	    public E next() { throw new NoSuchElementException(); }
	};
    }
    /** An <code>Iterator</code> over a singleton set. */
    public static final <E> Iterator<E> singletonIterator(E o) {
	return Collections.singletonList(o).iterator();
    } 
    /** An unmodifiable version of the given iterator. */
    public static final <E> Iterator<E> unmodifiableIterator(final Iterator<E> i) {
	return new UnmodifiableIterator<E>() {
	    public boolean hasNext() { return i.hasNext(); }
	    public E next() { return i.next(); }
	};
    }
    /** An empty set.
     * @deprecated Use Collections.EMPTY_SET
     */
    public static final SortedSet EMPTY_SET = EMPTY_SET();
    /** An empty set; the parameterized version.
     *  Plays nicely with Java's type system.
     * @deprecated Use Collections.emptySet()
     */
    public static final <E> SortedSet<E> EMPTY_SET() {
	return new EmptySortedSet<E>();
    }
    /** Implementation of an immutable empty set. */
    private static class EmptySet<E> implements Set<E>, java.io.Serializable {
	    public int size() { return 0; }
	    public boolean isEmpty() { return true; }
	    public boolean contains(Object e) { return false; }
	    public Iterator<E> iterator() { return nullIterator(); }
	    public Object[] toArray() { return new Object[0]; }
	    public <T> T[] toArray(T[] a) {
		if (a.length>0) a[0]=null;
		return a;
	    }
	    public boolean add(E e) {
		throw new UnsupportedOperationException();
	    }
	    public boolean remove(Object e) { return false; }
	    public boolean containsAll(Collection<?> c) {
		return c.isEmpty();
	    }
	    public boolean addAll(Collection<? extends E> c) {
		if (c.isEmpty()) return false;
		throw new UnsupportedOperationException();
	    }
	    public boolean removeAll(Collection<?> c) { return false; }
	    public boolean retainAll(Collection<?> c) { return false; }
	    public void clear() { }
	    public boolean equals(Object o) {
		// note we implement Set, not Collection, interface
		if (!(o instanceof Set)) return false;
		return ((Set)o).size()==0;
	    }
	    public int hashCode() { return 0; }
    }
    /** Implementation of an immutable empty sorted set. */
    private static class EmptySortedSet<E> extends EmptySet<E>
	implements SortedSet<E> {
	    // sorted set interface:
	    public Comparator<E> comparator() { return null; }
	    public SortedSet<E> subSet(E fromEl, E toEl) { return this; }
	    public SortedSet<E> headSet(E toEl) { return this; }
	    public SortedSet<E> tailSet(E fromEl) { return this; }
	    public E first() { throw new NoSuchElementException(); }
	    public E last() { throw new NoSuchElementException(); }
    }
    /** An empty list.  The parameterized version.
     *  Made necessary by limitations in GJ's type system.
     * @deprecated Use Collections.emptyList()
     */
    public static final <E> List<E> EMPTY_LIST() {
	return Collections.<E>emptyList();
    }
    /** An empty map. Missing from <code>java.util.Collections</code> in
     *  Java 1.2.
     * @deprecated Use Collections.EMPTY_MAP
     */
    public static final SortedMap EMPTY_MAP = EMPTY_MAP();
    /** An empty map, parameterized to play nicely with Java's type system.
     * @deprecated Use Collections.emptyMap()
     */
    public static final <K,V> SortedMap<K,V> EMPTY_MAP() {
	return new EmptySortedMap<K,V>();
    }
    /** Implementation of an immutable empty map. */
    private static class EmptyMap<K,V>
	implements Map<K,V>, java.io.Serializable {
	public void clear() { }
	public boolean containsKey(Object key) { return false; }
	public boolean containsValue(Object value) { return false; }
	public MapSet<K,V> entrySet() {
	    return new EmptyMapSet();
	}
	private class EmptyMapSet extends EmptySortedSet<Map.Entry<K,V>>
	    implements MapSet<K,V> {
	    public Map<K,V> asMap() { return EmptyMap.this; }
	}
	public boolean equals(Object o) {
	    if (!(o instanceof Map)) return false;
	    return ((Map)o).size()==0;
	}
	public V get(Object key) { return null; }
	public int hashCode() { return 0; }
	public boolean isEmpty() { return true; }
	public Set<K> keySet() { return Collections.<K>emptySet(); }
	public V put(K key, V value) {
	    throw new UnsupportedOperationException();
	}
	public void putAll(Map<? extends K,? extends V> t) {
	    if (t.size()==0) return;
	    throw new UnsupportedOperationException();
	}
	public V remove(Object key) { return null; }
	public int size() { return 0; }
	public Collection<V> values() { return Collections.<V>emptySet(); }
	public String toString() { return "{}"; }
	// this should always be a singleton.
	//private Object readResolve() { return Default.EMPTY_MAP; }
    }
    /** Implementation of an immutable empty sorted map. */
    private static class EmptySortedMap<K,V> extends EmptyMap<K,V>
	implements SortedMap<K,V> {
	// SortedMap interface.
	public Comparator<? super K> comparator() { return null; }
	public K firstKey() { throw new NoSuchElementException(); }
	public K lastKey() { throw new NoSuchElementException(); }
	public SortedMap<K,V> headMap(K toKey) { return this; }
	public SortedMap<K,V> tailMap(K fromKey) { return this; }
	public SortedMap<K,V> subMap(K fromKey, K toKey) { return this; }
    }
    /** An empty multi-map. */
    public static final MultiMap EMPTY_MULTIMAP = EMPTY_MULTIMAP();
    /** An empty multi-map, parameterized to play nicely with Java's
     *  type system. */
    public static final <K,V> MultiMap<K,V> EMPTY_MULTIMAP() {
	return new EmptyMultiMap<K,V>();
    }
    /** Implementation of an immutable empty multimap. */
    private static class EmptyMultiMap<K,V> extends EmptyMap<K,V>
	implements MultiMap<K,V> {
	public MultiMapSet<K,V> entrySet() {
	    return new EmptyMultiMapSet();
	}
	private class EmptyMultiMapSet extends EmptySortedSet<Map.Entry<K,V>>
	    implements MultiMapSet<K,V> {
	    public MultiMap<K,V> asMap() { return EmptyMultiMap.this; }
	    public MultiMap<K,V> asMultiMap() { return EmptyMultiMap.this; }
	}
	// this should always be a singleton.
	//private Object readResolve() { return Default.EMPTY_MULTIMAP; }
	// MultiMap interface.
	public boolean remove(Object key, Object value) {
	    return false;
	}
	public boolean add(K key, V value) {
	    throw new UnsupportedOperationException();
	}
	public boolean addAll(K key, Collection<? extends V> values) {
	    if (values.size()==0) return false;
	    throw new UnsupportedOperationException();
	}
	public boolean addAll(MultiMap<? extends K,? extends V> mm) {
	    if (mm.size()==0) return false;
	    throw new UnsupportedOperationException();
	}
	public boolean retainAll(K key, Collection<?> values) {
	    return false;
	}
	public boolean removeAll(K key, Collection<?> values) {
	    return false;
	}
	public Set<V> getValues(K key) {
	    return Collections.<V>emptySet();
	}
	public boolean contains(Object key, Object value) {
	    return false;
	}
    }
    /**
     * Improved <code>unmodifiableCollection()</code> class that
     * helps w/ covariant subtyping. */
    public static <A> Collection<A> unmodifiableCollection(final Collection<? extends A> cc) {
	return new AbstractCollection<A>() {
	    public boolean containsAll(Collection<?> c) {
		return cc.containsAll(c);
	    }
	    public boolean removeAll(Collection<?> c) {
		return cc.removeAll(c);
	    }
	    public boolean retainAll(Collection<?> c) {
		return cc.retainAll(c);
	    }
	    public boolean contains(Object o) { return cc.contains(o); }
	    public boolean isEmpty() { return cc.isEmpty(); }
	    public Iterator<A> iterator() {
		final Iterator<? extends A> it = cc.iterator();
		return new UnmodifiableIterator<A>() {
		    public boolean hasNext() { return it.hasNext(); }
		    public A next() { return it.next(); }
		};
	    }
	    public int size() { return cc.size(); }
	};
    }
    /** A pair constructor method.  Pairs implement <code>hashCode()</code>
     *  and <code>equals()</code> "properly" so they can be used as keys
     *  in hashtables and etc.  They are implemented as mutable lists of
     *  fixed size 2. */
    public static <A,B> PairList<A,B> pair(final A left, final B right) {
	// this can't be an anonymous class because we want to make it
	// serializable.
	return new PairList<A,B>(left, right);
    }
    /** Pairs, implemented as a <code>List</code>.
     *  The <code>PairList</code> implements <code>hashCode()</code>
     *  and <code>equals()</code> "properly" so they can be used as keys
     *  in hashtables and etc.  They are implemented as mutable lists of
     *  fixed size 2.  Note that the <code>hashCode()</code> implementation
     *  differs from pairs implemented as <code>Map.Entry</code>s; the
     *  parameterization is different as well.
     */
    public static class PairList<A,B> extends AbstractList
	implements java.io.Serializable {
	private A left;
	private B right;
	public PairList(A left, B right) {
	    this.left = left; this.right = right;
	}
	/** Return the left element of the pair (head). */
	public A left() { return left; }
	/** Return the right element of the pair (tail). */
	public B right() { return right; }
	public int size() { return 2; }
	public Object get(int index) {
	    switch(index) {
	    case 0: return this.left;
	    case 1: return this.right;
	    default: throw new IndexOutOfBoundsException();
	    }
	}
	public Object set(int index, Object element) {
	    Object prev;
	    switch(index) {
	    case 0: prev=this.left; this.left=(A)element; return prev;
	    case 1: prev=this.right; this.right=(B)element; return prev;
	    default: throw new IndexOutOfBoundsException();
	    }
	}
    }
    /** A pair constructor method more appropriate for <code>Set</code>
     *  views of <code>Map</code>s and <code>MultiMap</code>s.
     *  The returned object is an instance of <code>Map.Entry</code>;
     *  the only (real) difference from the pairs returned by
     *  <code>Default.pair()</code> is the definition of
     *  <code>hashCode()</code>, which corresponds to <code>Map.Entry</code>
     *  (being <code>key.hashCode() ^ value.hashCode()</code> ) rather
     *  than <code>List</code> (which would be 
     *  <code>31*(31+key.hashCode())+value.hashCode()</code> ). This is
     *  an annoying distinction; I wish the JDK API authors had made
     *  these consistent. The <code>Map.Entry</code> returned is immutable.
     */
    public static <K,V> Map.Entry<K,V> entry(final K key, final V value) {
	return new AbstractMapEntry<K,V>() {
		public K getKey() { return key; }
		public V getValue() { return value; }
	    };
    }

    /** A serializable comparator. */
    private static interface SerializableComparator<A>
	extends Comparator<A>, java.io.Serializable { /* only declare */ }
}
