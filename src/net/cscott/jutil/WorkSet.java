// WorkSet.java, created Tue Feb 23 01:18:37 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

/**
 * A {@link WorkSet} is a {@link Set} offering constant-time
 * access to the first/last element inserted, and an iterator whose speed
 * is not dependent on the total capacity of the underlying hashtable.
 * <p>Conforms to the JDK 1.2 Collections API.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: WorkSet.java,v 1.7 2007-04-09 17:31:28 cananian Exp $
 * @deprecated Use {@link java.util.LinkedHashSet LinkedHashSet} instead.
 */
public class WorkSet<E> extends java.util.AbstractSet<E> implements Serializable
{
    private final HashMap<E,EntryList<E>> hm;
    private EntryList<E> listhead = EntryList.init(); // header and footer nodes.
    private EntryList<E> listfoot = listhead.next;
    private final static boolean debug=false; // turn on expensive consistency checks.

    // assertion helper function; checks that the data structures are
    // consistent.
    private boolean isValid() {
	assert listhead != null;
	assert listfoot != null;
	assert listhead.prev == null;
	assert listfoot.next == null;
	assert listhead.next != null;
	assert listfoot.prev != null;
	if (debug) assert EntryList.equals(listhead, hm.keySet());
	assert isEmpty() == hm.isEmpty();
	if (debug) assert _checkSize() == hm.size();
	return true;
    }
    // helper method for isValid(); does the expensive O(N) checks
    private int _checkSize() {
	int size=0;
	for (EntryList<E> elp=listhead.next; elp!=listfoot; elp=elp.next) {
	    assert elp.next.prev == elp;
	    assert elp.prev.next == elp;
	    assert hm.get(elp.o) == elp;
	    size++;
	}
	return size;
    }
    
    /** Creates a new, empty {@link WorkSet} with a default capacity
     *  and load factor. */
    public WorkSet() {
	hm = new HashMap<E,EntryList<E>>();
    }
    /** Constructs a new, empty {@link WorkSet} with the specified
     *  initial capacity and default load factor. */
    public WorkSet(int initialCapacity) {
	hm = new HashMap<E,EntryList<E>>(initialCapacity);
    }
    /** Constructs a new, empty {@link WorkSet} with the specified
     *  initial capacity and the specified load factor. */
    public WorkSet(int initialCapacity, float loadFactor) {
	hm = new HashMap<E,EntryList<E>>(initialCapacity, loadFactor);
    }
    /** Constructs a new {@link WorkSet} with the contents of the
     *  specified {@link Collection}. */
    public WorkSet(java.util.Collection<? extends E> c) {
	// make hash map about twice as big as the collection.
	hm = new HashMap<E,EntryList<E>>(Math.max(2*c.size(),11));
	addAll(c);
    }

    /** Adds an element to the front of the (ordered) set and returns true,
     *  if the element is not already present in the set.  Makes no change
     *  to the set and returns false if the element is already in the set.
     */
    public boolean addFirst(E o) {
	assert isValid();
	if (o==null) throw new NullPointerException();
	if (hm.containsKey(o)) return false;
	EntryList<E> nel = new EntryList<E>(o);
	listhead.add(nel);
	EntryList<E> old = hm.put(o, nel);
	assert old==null : old;
	assert isValid();
	return true;
    }
    /** Adds an element to the end of the (ordered) set and returns true,
     *  if the element is not already present in the set.  Makes no change
     *  to the set and returns false if the element is already in the set.
     */
    public boolean addLast(E o) {
	assert isValid();
	if (o==null) throw new NullPointerException();
	if (hm.containsKey(o)) return false;
	EntryList<E> nel = new EntryList<E>(o);
	listfoot.prev.add(nel);
	EntryList<E> old = hm.put(o, nel);
	assert old==null : old;
	assert isValid();
	return true;
    }
    /** Returns the first element in the ordered set. */
    public E getFirst() {
	assert isValid();
	if (isEmpty()) throw new java.util.NoSuchElementException();
	return listhead.next.o;
    }
    /** Returns the last element in the ordered set. */
    public E getLast() {
	assert isValid();
	if (isEmpty()) throw new java.util.NoSuchElementException();
	return listfoot.prev.o;
    }
    /** Removes the first element in the ordered set and returns it. */
    public E removeFirst() {
	assert isValid();
	if (isEmpty()) throw new java.util.NoSuchElementException();
	E o = listhead.next.o;
	EntryList<E> old = hm.remove(o);
	assert old == listhead.next : o;
	listhead.next.remove();
	assert isValid();
	return o;
    }
    /** Removes the last element in the ordered set and returns it. */
    public E removeLast() {
	assert isValid();
	if (isEmpty()) throw new java.util.NoSuchElementException();
	E o = listfoot.prev.o;
	EntryList<E> old = hm.remove(o);
	assert old == listfoot.prev : o;
	listfoot.prev.remove();
	assert isValid();
	return o;
    }

    /** Looks at the object as the top of this {@link WorkSet}
     *  (treating it as a {@link Stack}) without removing it
     *  from the set/stack. */
    public E peek() { return getLast(); }

    /** Removes the item at the top of this {@link WorkSet}
     *  (treating it as a {@link Stack}) and returns that object
     *  as the value of this function. */
    public E pop() { return removeLast(); }

    /** Pushes item onto the top of this {@link WorkSet} (treating
     *  it as a {@link Stack}), if it is not already there.
     *  If the <code>item</code> is already in the set/on the stack,
     *  then this method does nothing.
	<BR> <B>modifies:</B> <code>this</code>
	<BR> <B>effects:</B> If <code>item</code> is not already an
	                     element of <code>this</code>, adds
			     <code>item</code> to <code>this</code>.
			     Else does nothing. 
    */
    public void push(E item) {
	this.add(item);
    }

    /** Adds the object to the set and returns true if the element
     *  is not already present.  Otherwise makes no change to the
     *  set and returns false. */
    public boolean add(E o) { return addLast(o); }

    /** Removes all elements from the set. */
    public void clear() {
	assert isValid();
	hm.clear(); listhead.next = listfoot; listfoot.prev = listhead;
	assert isValid();
    }

    /** Determines if this contains an item.
	<BR> <B>effects:</B> If <code>o</code> is an element of 
	                     <code>this</code>, returns true.
			     Else returns false.
    */
    public boolean contains(Object o) {
	return hm.containsKey(o);
    }

    /** Determines if there are any more items left in this. 
	<BR> <B>effects:</B> If <code>this</code> has any elements,
	                     returns true.  Else returns false.
    */
    public boolean isEmpty() {
	return (listhead.next == listfoot);
    }
    /** Efficient set iterator. */
    public Iterator<E> iterator() {
	return new Iterator<E>() {
	    EntryList<E> elp = listhead;
	    public boolean hasNext() {
		return elp.next.next!=null; /* remember to skip FOOTER node */
	    }
	    public E next() {
		if (!hasNext())
		    throw new java.util.NoSuchElementException();
		E o=elp.next.o; elp=elp.next; return o;
	    }
	    public void remove() {
		assert isValid();
		if (elp==listhead) throw new IllegalStateException();
		hm.remove(elp.o);
		(elp = elp.prev).next.remove();
		assert isValid();
	    }
	};
    }
    public boolean remove(Object o) {
	assert isValid();
	if (!hm.containsKey(o)) return false;
	// remove from hashmap
	EntryList<E> elp = hm.remove(o);
	// remove from linked list.
	elp.remove();
	assert isValid();
	return true;
    }
    public int size() { return hm.size(); }

    // INNER CLASS. -------------------------------------------
    private static final class EntryList<E> implements Serializable {
	final E o;
	EntryList<E> prev=null, next=null;
	EntryList(E o) { this.o = o; }

	public String toString() {
	    StringBuffer sb = new StringBuffer("[");
	    for (EntryList<E> elp = this; elp!=null; elp=elp.next) {
		sb.append(elp.o);
		if (elp.next!=null)
		    sb.append(", ");
	    }
	    sb.append(']');
	    return sb.toString();
	}
	static <T1,T2> boolean equals(EntryList<T1> el, java.util.Collection<T2> c) {
	    int size=0;
	    // remember that header and footer are skipped!
	    for (EntryList<T1> elp=el.next; elp.next!=null; elp=elp.next, size++)
		if (!c.contains(elp.o)) return false;
	    if (size!=c.size()) return false;
	    return true;
	}
	// utility.
	/** Remove this entry from the list. */
	void remove() {
	    // always a predecessor and successor.
	    this.prev.next = this.next;
	    this.next.prev = this.prev;
	    this.next = this.prev = null; // safety.
	}
	/** Link in the supplied entry after this one. */
	void add(EntryList<E> nel) {
	    // always a predecessor and successor.
	    nel.next = this.next;
	    nel.prev = this;
	    this.next = nel.next.prev = nel;
	}
	// return a list with only a header and footer node.
	static <T> EntryList<T> init() {
	    EntryList<T> header = new EntryList<T>(null);
	    EntryList<T> footer = new EntryList<T>(null);
	    header.next = footer;
	    footer.prev = header;
	    return header;
	}
    }
}
