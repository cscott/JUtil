// Default.java, created Thu Apr  8 02:22:56 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.function.Consumer;

/**
 * {@link Default} contains one-off or 'standard, no-frills'
 * implementations of simple {@link Iterator}s,
 * {@link Collection}s, and {@link Comparator}s.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public abstract class Default  {
    /** The natural order, with <code>null</code> before everything
     *  else. */
    @SuppressWarnings({"rawtypes","unchecked"})
    private static final Comparator NULLS_FIRST =
	Comparator.nullsFirst(Comparator.<Comparable>naturalOrder());
    /** Returns the natural order of the elements, with
     *  <code>null</code> before everything else, for the classes
     *  whose constructors default to it.  The elements must be
     *  {@link Comparable} (checked dynamically at run-time). */
    @SuppressWarnings("unchecked")
    static <T> Comparator<T> nullsFirst() {
	return (Comparator<T>) NULLS_FIRST;
    }
    /** A {@link Comparator} for objects that implement 
     *   {@link Comparable} (checked dynamically at run-time).
     *  <code>null</code> is less than everything else.
     * @deprecated Use
     *  {@link Comparator#nullsFirst(Comparator) Comparator.nullsFirst}<code>(</code>{@link Comparator#naturalOrder()}<code>)</code>,
     *  which this is, or {@link #comparator()}.
     */
    @Deprecated
    @SuppressWarnings("rawtypes")
    public static final Comparator comparator = NULLS_FIRST;
    /** A {@link Comparator} for objects that implement 
     *  {@link Comparable} (checked dynamically at run-time).
     *  <code>null</code> is less than everything else.
     *  This version is parameterized to play nicely with Java's
     *  type system.
     * @deprecated Use
     *  {@link Comparator#nullsFirst(Comparator) Comparator.nullsFirst}<code>(</code>{@link Comparator#naturalOrder()}<code>)</code>,
     *  which this returns.
     */
    @Deprecated
    public static final <T extends Comparable<T>> Comparator<T> comparator() {
	return nullsFirst();
    }
    /** An {@link Iterator} over the empty set.
     * @deprecated Use {@link Collections#emptyIterator()}, or
     *  {@link #nullIterator()}.  Unlike this iterator, the
     *  <code>remove()</code> method of
     *  {@link Collections#emptyIterator()} throws
     *  {@link IllegalStateException}, not
     *  {@link UnsupportedOperationException}.
     */
    @Deprecated
    @SuppressWarnings("rawtypes")
    public static final Iterator nullIterator = nullIterator();
    /** An {@link Iterator} over the empty set, parameterized to
     *  play nicely with Java's type system.
     * @deprecated Use {@link Collections#emptyIterator()}.  Unlike
     *  this iterator, the <code>remove()</code> method of
     *  {@link Collections#emptyIterator()} throws
     *  {@link IllegalStateException}, not
     *  {@link UnsupportedOperationException}.
     */
    @Deprecated
    public static final <E> Iterator<E> nullIterator() {
	return new Iterator<E>() {
	    @Override public boolean hasNext() { return false; }
	    @Override public E next() { throw new NoSuchElementException(); }
	};
    }
    /** An {@link Iterator} over a singleton set. */
    public static final <E> Iterator<E> singletonIterator(E o) {
	return Collections.singletonList(o).iterator();
    } 
    /** An unmodifiable version of the given iterator: its
     *  <code>remove()</code> method throws
     *  {@link UnsupportedOperationException}. */
    public static final <E> Iterator<E> unmodifiableIterator(final Iterator<? extends E> i) {
	return new Iterator<E>() {
	    @Override public boolean hasNext() { return i.hasNext(); }
	    @Override public E next() { return i.next(); }
	    @Override
	    public void forEachRemaining(Consumer<? super E> action) {
		i.forEachRemaining(action);
	    }
	};
    }
    /** An empty set.
     * @deprecated Use {@link Collections#emptySortedSet()}, or
     *  {@link Collections#emptySet()} when the set need not be a
     *  {@link SortedSet}.
     */
    @Deprecated
    @SuppressWarnings("rawtypes")
    public static final SortedSet EMPTY_SET = EMPTY_SET();
    /** An empty set; the parameterized version.
     *  Plays nicely with Java's type system.
     * @deprecated Use {@link Collections#emptySortedSet()}, or
     *  {@link Collections#emptySet()} when the set need not be a
     *  {@link SortedSet}.
     */
    @Deprecated
    public static final <E> SortedSet<E> EMPTY_SET() {
	return new EmptySortedSet<E>();
    }
    /** Implementation of an immutable empty set. */
    private static class EmptySet<E> implements Set<E>, java.io.Serializable {
	    @java.io.Serial
	    private static final long serialVersionUID = 1L;
	    @Override public int size() { return 0; }
	    @Override public boolean isEmpty() { return true; }
	    @Override public boolean contains(Object e) { return false; }
	    @Override public Iterator<E> iterator() { return nullIterator(); }
	    @Override public Object[] toArray() { return new Object[0]; }
	    @Override
	    public <T> T[] toArray(T[] a) {
		if (a.length>0) a[0]=null;
		return a;
	    }
	    @Override
	    public boolean add(E e) {
		throw new UnsupportedOperationException();
	    }
	    @Override public boolean remove(Object e) { return false; }
	    @Override
	    public boolean containsAll(Collection<?> c) {
		return c.isEmpty();
	    }
	    @Override
	    public boolean addAll(Collection<? extends E> c) {
		if (c.isEmpty()) return false;
		throw new UnsupportedOperationException();
	    }
	    @Override public boolean removeAll(Collection<?> c) { return false; }
	    @Override public boolean retainAll(Collection<?> c) { return false; }
	    @Override public void clear() { }
	    @Override
	    public boolean equals(Object o) {
		// note we implement Set, not Collection, interface
		if (!(o instanceof Set)) return false;
		return ((Set)o).size()==0;
	    }
	    @Override public int hashCode() { return 0; }
    }
    /** Implementation of an immutable empty sorted set. */
    private static class EmptySortedSet<E> extends EmptySet<E>
	implements SortedSet<E> {
	    @java.io.Serial
	    private static final long serialVersionUID = 1L;
	    // sorted set interface:
	    @Override public Comparator<E> comparator() { return null; }
	    @Override public SortedSet<E> subSet(E fromEl, E toEl) { return this; }
	    @Override public SortedSet<E> headSet(E toEl) { return this; }
	    @Override public SortedSet<E> tailSet(E fromEl) { return this; }
	    @Override public E first() { throw new NoSuchElementException(); }
	    @Override public E last() { throw new NoSuchElementException(); }
    }
    /** An empty list.  The parameterized version.
     *  Made necessary by limitations in GJ's type system.
     * @deprecated Use {@link Collections#emptyList()}, which this
     *  returns.
     */
    @Deprecated
    public static final <E> List<E> EMPTY_LIST() {
	return Collections.<E>emptyList();
    }
    /** An empty map. Missing from {@link java.util.Collections} in
     *  Java 1.2.
     * @deprecated Use {@link Collections#emptySortedMap()}, or
     *  {@link Collections#emptyMap()} when the map need not be a
     *  {@link SortedMap}.
     */
    @Deprecated
    @SuppressWarnings("rawtypes")
    public static final SortedMap EMPTY_MAP = EMPTY_MAP();
    /** An empty map, parameterized to play nicely with Java's type system.
     * @deprecated Use {@link Collections#emptySortedMap()}, or
     *  {@link Collections#emptyMap()} when the map need not be a
     *  {@link SortedMap}.
     */
    @Deprecated
    public static final <K,V> SortedMap<K,V> EMPTY_MAP() {
	return new EmptySortedMap<K,V>();
    }
    /** Implementation of an immutable empty map. */
    private static class EmptyMap<K,V>
	implements Map<K,V>, java.io.Serializable {
	@java.io.Serial
	private static final long serialVersionUID = 1L;
	@Override public void clear() { }
	@Override public boolean containsKey(Object key) { return false; }
	@Override public boolean containsValue(Object value) { return false; }
	@Override
	public MapSet<K,V> entrySet() {
	    return new EmptyMapSet();
	}
	private class EmptyMapSet extends EmptySortedSet<Map.Entry<K,V>>
	    implements MapSet<K,V> {
	    @java.io.Serial
	    private static final long serialVersionUID = 1L;
	    @Override public Map<K,V> asMap() { return EmptyMap.this; }
	}
	@Override
	public boolean equals(Object o) {
	    if (!(o instanceof Map)) return false;
	    return ((Map)o).size()==0;
	}
	@Override public V get(Object key) { return null; }
	@Override public int hashCode() { return 0; }
	@Override public boolean isEmpty() { return true; }
	@Override public Set<K> keySet() { return Collections.<K>emptySet(); }
	@Override
	public V put(K key, V value) {
	    throw new UnsupportedOperationException();
	}
	@Override
	public void putAll(Map<? extends K,? extends V> t) {
	    if (t.size()==0) return;
	    throw new UnsupportedOperationException();
	}
	@Override public V remove(Object key) { return null; }
	@Override public int size() { return 0; }
	@Override public Collection<V> values() { return Collections.<V>emptySet(); }
	@Override public String toString() { return "{}"; }
	// this should always be a singleton.
	//private Object readResolve() { return Default.EMPTY_MAP; }
    }
    /** Implementation of an immutable empty sorted map. */
    private static class EmptySortedMap<K,V> extends EmptyMap<K,V>
	implements SortedMap<K,V> {
	@java.io.Serial
	private static final long serialVersionUID = 1L;
	// SortedMap interface.
	@Override public Comparator<? super K> comparator() { return null; }
	@Override public K firstKey() { throw new NoSuchElementException(); }
	@Override public K lastKey() { throw new NoSuchElementException(); }
	@Override public SortedMap<K,V> headMap(K toKey) { return this; }
	@Override public SortedMap<K,V> tailMap(K fromKey) { return this; }
	@Override public SortedMap<K,V> subMap(K fromKey, K toKey) { return this; }
    }
    /** An empty multi-map.
     * @deprecated Use {@link #EMPTY_MULTIMAP()}, which is
     *  parameterized.
     */
    @Deprecated
    @SuppressWarnings("rawtypes")
    public static final MultiMap EMPTY_MULTIMAP = EMPTY_MULTIMAP();
    /** An empty multi-map, parameterized to play nicely with Java's
     *  type system. */
    public static final <K,V> MultiMap<K,V> EMPTY_MULTIMAP() {
	return new EmptyMultiMap<K,V>();
    }
    /** Implementation of an immutable empty multimap. */
    private static class EmptyMultiMap<K,V> extends EmptyMap<K,V>
	implements MultiMap<K,V> {
	@java.io.Serial
	private static final long serialVersionUID = 1L;
	@Override
	public MultiMapSet<K,V> entrySet() {
	    return new EmptyMultiMapSet();
	}
	private class EmptyMultiMapSet extends EmptySortedSet<Map.Entry<K,V>>
	    implements MultiMapSet<K,V> {
	    @java.io.Serial
	    private static final long serialVersionUID = 1L;
	    @Override public MultiMap<K,V> asMap() { return EmptyMultiMap.this; }
	    @Override public MultiMap<K,V> asMultiMap() { return EmptyMultiMap.this; }
	}
	// this should always be a singleton.
	//private Object readResolve() { return Default.EMPTY_MULTIMAP; }
	// MultiMap interface.
	@Override
	public boolean remove(Object key, Object value) {
	    return false;
	}
	@Override
	public boolean add(K key, V value) {
	    throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll(K key, Collection<? extends V> values) {
	    if (values.size()==0) return false;
	    throw new UnsupportedOperationException();
	}
	@Override
	public boolean addAll(MultiMap<? extends K,? extends V> mm) {
	    if (mm.size()==0) return false;
	    throw new UnsupportedOperationException();
	}
	@Override
	public boolean retainAll(K key, Collection<?> values) {
	    return false;
	}
	@Override
	public boolean removeAll(K key, Collection<?> values) {
	    return false;
	}
	@Override
	public Set<V> getValues(K key) {
	    return Collections.<V>emptySet();
	}
	@Override
	public boolean contains(Object key, Object value) {
	    return false;
	}
    }
    /**
     * Improved <code>unmodifiableCollection()</code> class that
     * helps w/ covariant subtyping. */
    public static <A> Collection<A> unmodifiableCollection(final Collection<? extends A> cc) {
	return new AbstractCollection<A>() {
	    @Override
	    public boolean containsAll(Collection<?> c) {
		return cc.containsAll(c);
	    }
	    @Override
	    public boolean removeAll(Collection<?> c) {
		return cc.removeAll(c);
	    }
	    @Override
	    public boolean retainAll(Collection<?> c) {
		return cc.retainAll(c);
	    }
	    @Override public boolean contains(Object o) { return cc.contains(o); }
	    @Override public boolean isEmpty() { return cc.isEmpty(); }
	    @Override
	    public Iterator<A> iterator() {
		return unmodifiableIterator(cc.iterator());
	    }
	    @Override public int size() { return cc.size(); }
	};
    }
    /** A pair constructor method.  Pairs implement <code>hashCode()</code>
     *  and <code>equals()</code> "properly" so they can be used as keys
     *  in hashtables and etc.  They are implemented as mutable lists of
     *  fixed size 2.
     *  @deprecated Use {@link Pair}, an immutable record:
     *  <code>new Pair&lt;&gt;(left, right)</code>.  For an immutable
     *  list, use {@link List#of(Object,Object)}, which does not permit
     *  <code>null</code>s.
     */
    @Deprecated(since="2.0")
    @SuppressWarnings("deprecation") // returns a PairList
    public static <A,B> PairList<A,B> pair(final A left, final B right) {
	// this can't be an anonymous class because we want to make it
	// serializable.
	return new PairList<A,B>(left, right);
    }
    /** Pairs, implemented as a {@link List}.
     *  The {@link PairList} implements <code>hashCode()</code>
     *  and <code>equals()</code> "properly" so they can be used as keys
     *  in hashtables and etc.  They are implemented as mutable lists of
     *  fixed size 2.  Note that the <code>hashCode()</code> implementation
     *  differs from pairs implemented as {@link java.util.Map.Entry}s; the
     *  parameterization is different as well.
     *  @deprecated Use {@link Pair}, an immutable record whose
     *  {@link Pair#left()} and {@link Pair#right()} replace the methods
     *  of the same name.  Unlike a {@link PairList}, a {@link Pair} is
     *  not a {@link List}, and its <code>hashCode()</code> and
     *  <code>toString()</code> differ.
     */
    @Deprecated(since="2.0")
    public static class PairList<A,B> extends AbstractList<Object>
	implements java.io.Serializable {
	@java.io.Serial
	private static final long serialVersionUID = -8393870012647259789L; // as in 1.5
	@SuppressWarnings("serial") // serializable if its contents are
	private A left;
	@SuppressWarnings("serial") // serializable if its contents are
	private B right;
	public PairList(A left, B right) {
	    this.left = left; this.right = right;
	}
	/** Return the left element of the pair (head). */
	public A left() { return left; }
	/** Return the right element of the pair (tail). */
	public B right() { return right; }
	@Override public int size() { return 2; }
	@Override
	public Object get(int index) {
	    switch(index) {
	    case 0: return this.left;
	    case 1: return this.right;
	    default: throw new IndexOutOfBoundsException();
	    }
	}
	@Override
	@SuppressWarnings("unchecked") // the caller must pass an A or a B.
	public Object set(int index, Object element) {
	    Object prev;
	    switch(index) {
	    case 0: prev=this.left; this.left=(A)element; return prev;
	    case 1: prev=this.right; this.right=(B)element; return prev;
	    default: throw new IndexOutOfBoundsException();
	    }
	}
    }
    /** A pair constructor method more appropriate for {@link Set}
     *  views of {@link Map}s and {@link MultiMap}s.
     *  The returned object is an instance of {@link java.util.Map.Entry};
     *  the only (real) difference from the pairs returned by
     *  <code>Default.pair()</code> is the definition of
     *  <code>hashCode()</code>, which corresponds to {@link java.util.Map.Entry}
     *  (being <code>key.hashCode() ^ value.hashCode()</code> ) rather
     *  than {@link List} (which would be 
     *  <code>31*(31+key.hashCode())+value.hashCode()</code> ). This is
     *  an annoying distinction; I wish the JDK API authors had made
     *  these consistent. The {@link java.util.Map.Entry} returned is immutable.
     *  @deprecated Use
     *  {@link java.util.AbstractMap.SimpleImmutableEntry}, which this
     *  method now returns, or {@link Map#entry(Object,Object)}, which
     *  does not permit <code>null</code>s.
     */
    @Deprecated(since="2.0")
    public static <K,V> Map.Entry<K,V> entry(final K key, final V value) {
	return new AbstractMap.SimpleImmutableEntry<K,V>(key, value);
    }
}
