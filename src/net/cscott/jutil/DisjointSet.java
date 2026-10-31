// DisjointSet.java, created Wed Feb 23 13:06:57 2000 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/**
 * {@link DisjointSet} is an implementation of disjoint-set forests
 * using the path compression and union-by-rank heuristics to achieve
 * O(m * alpha(m, n)) runtime, where 'm' is the total number of
 * operations, 'n' is the total number of elements in the set, and
 * 'alpha' denotes the *extremely* slowly-growing inverse Ackermann
 * function.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public class DisjointSet<E>  {
    private final Map<E,Node<E>> elmap = new HashMap<E,Node<E>>();

    /** Creates a {@link DisjointSet}. */
    public DisjointSet() { }

    /** Unites the dynamic sets that contain <code>o1</code> and
     *  <code>o2</code>, say S1 and S2, into a new set that is the
     *  union of these two sets.  The two sets are assumed to be
     *  disjoint prior to the operation.  The representative of the
     *  resulting set is the representative of either S1 or S2; if
     *  both S1 and S2 were previously singletons, the representative
     *  of S1 union S2 is the representative of S2. */
    public void union(E o1, E o2) { // this is UNION
	Node<E> x = elmap.get(o1);
	if (x==null) x=_make_set(o1);
	Node<E> y = elmap.get(o2);
	if (y==null) y=_make_set(o2);
	assert !x.equals(y) : "Sets assumed to be disjoint";
	_union(x, y);
    }
    /** Returns the representative of the (unique) set containing
     *  <code>o</code>. */
    public E find(E o) { // this is FIND-SET
	Node<E> x = elmap.get(o);
	if (x==null) return o;
	return _find_set(x).element;
    }
    /** Determines if there is a set of more than one element containing
     *  <code>o</code>. */
    public boolean contains(Object o) {
	return elmap.containsKey(o);
    }
    // these are the routines according to CLR
    private Node<E> _make_set(E o) {
	assert !elmap.containsKey(o);
	Node<E> x = new Node<E>(o);
	elmap.put(o, x);
	return x;
    }
    private Node<E> _find_set(Node<E> x) {
	if (x.parent != x)
	    x.parent = _find_set(x.parent);
	return x.parent;
    }
    private void _union(Node<E> x, Node<E> y) {
	_link(_find_set(x), _find_set(y));
    }
    private void _link(Node<E> x, Node<E> y) {
	if (x.rank > y.rank)
	    y.parent = x;
	else {
	    x.parent = y;
	    if (x.rank == y.rank)
		y.rank++;
	}
    }
    /** Returns an unmodifiable {@link Map} view of the disjoint
     *  set, where every element is mapped to its canonical representative.
     */
    public Map<E,E> asMap() {
	return new AbstractMap<E,E>() {
	    @Override
	    public boolean containsKey(Object key) {
		return elmap.containsKey(key);
	    }
	    // XXX: returns identity mapping for objects not in set.
	    @Override
	    @SuppressWarnings("unchecked") // as Map.get(), may throw ClassCastException
	    public E get(Object key) { return find((E)key); }
	    @Override
	    public Set<Map.Entry<E,E>> entrySet() {
		final Set<E> objects = elmap.keySet();
		return new AbstractSet<Map.Entry<E,E>>() {
		    @Override public int size() { return objects.size(); }
		    @Override
		    public Iterator<Map.Entry<E,E>> iterator() {
			final Iterator<E> objit = objects.iterator();
			return new Iterator<Map.Entry<E,E>>() {
			    @Override public boolean hasNext(){ return objit.hasNext(); }
			    @Override
			    public Map.Entry<E,E> next() {
				final E key = objit.next();
				return new AbstractMapEntry<E,E>() {
				    @Override public E getKey() { return key; }
				    @Override
				    public E getValue() {
					// note deferred for efficiency.
					return find(key);
				    }
				};
			    }
			};
		    }
		};
	    }
	};
    }
    /** Returns a human-readable representation of the DisjointSet. */
    @Override
    public String toString() {
	MultiMap<E,E> mm = new GenericMultiMap<E,E>();
	for (Node<E> n : elmap.values()) {
	    Node<E> r = _find_set(n);
	    if (n!=r) mm.add(r.element, n.element);
	}
	return mm.toString();
    }
    // node representation.
    private static class Node<E> {
	Node<E> parent;
	final E element;
	int rank;
	Node(E element) {
	    this.parent = this; this.element = element; this.rank = 0;
	}
    }
}
