// UnmodifiableListIterator.java, created Sun Oct 10 19:56:40 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.ListIterator;
/**
 * {@link UnmodifiableListIterator} is an abstract superclass to
 * save you the trouble of implementing the {@link #remove()},
 * {@link #add(Object)} and {@link #set(Object)} methods over and over again
 * for those list iterators which don't implement them.  The name's a
 * bit clunky, but fits with the JDK naming in
 * {@link java.util.Collections} and such.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: UnmodifiableListIterator.java,v 1.3 2006-10-30 20:14:41 cananian Exp $
 */
public abstract class UnmodifiableListIterator<E> implements ListIterator<E> {
    /** Returns <code>true</code> if the list iterator has more elements
     *  in the forward direction. */
    public abstract boolean hasNext();
    /** Returns the next element in the list.  This method may be
     *  called repeatedly to iterate through the list, or intermixed
     *  with calls to {@link #previous()} to go back and forth.
     *  (Note that alternating calls to {@link #next()} and 
     *  {@link #previous()} will return the same element
     *   repeatedly.)
     * @exception java.util.NoSuchElementException if the iteration has no next element.
     */
    public abstract E next();
    /** Returns <code>true</code> if the list iterator has more elements
     *  in the reverse direction. */
    public abstract boolean hasPrevious();
    /** Returns the previous element in the list. This method may be called
     *  repeatedly to iterate through the list backwards, or intermixed
     *  with calls to {@link #next()} to go back and forth.
     *  (Note that alternating calls to {@link #next()} and 
     *  {@link #previous()} will return the same element repeatedly.)
     * @exception java.util.NoSuchElementException if the iteration has no previous
     *            element.
     */
    public abstract E previous();
    /** Returns the index of the element that would be returned by a
     *  subsequent call to {@link #next()}. (Returns list size if the
     *  list iterator is at the end of the list.)
     */
    public abstract int nextIndex();
    /** Returns the index of the element that would be returned by a
     *  subsequent call to {@link #previous()}. (Returns -1 if the
     *  list iterator is at the beginning of the list.)
     */
    public int previousIndex() { return nextIndex()-1; }
    /** Always throws an {@link UnsupportedOperationException}.
     * @exception UnsupportedOperationException always.
     */
    public final void remove() {
	throw new UnsupportedOperationException("Unmodifiable ListIterator");
    }
    /** Always throws an {@link UnsupportedOperationException}.
     * @exception UnsupportedOperationException always.
     */
    public final void set(E o) {
	throw new UnsupportedOperationException("Unmodifiable ListIterator");
    }
    /** Always throws an {@link UnsupportedOperationException}.
     * @exception UnsupportedOperationException always.
     */
    public final void add(E o) {
	throw new UnsupportedOperationException("Unmodifiable ListIterator");
    }
}
