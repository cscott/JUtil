// ReverseListIterator.java, created Fri Mar 15 19:39:14 2002 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
/**
 * {@link ReverseListIterator} takes a {@link List} and gives
 * you an {@link Iterator} that traverses the list in reverse
 * order.  Similar to using <code>ReverseIterator(l.iterator())</code>
 * but more efficient as it does not need to create a snapshot.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public class ReverseListIterator<E> implements ListIterator<E> {
    private final ListIterator<E> it;
    /** Creates a {@link ReverseListIterator}. */
    public ReverseListIterator(List<E> l) {
	this.it = l.listIterator(l.size());
    }
    @Override public void add(E o) { it.add(o); }
    @Override public boolean hasNext() { return it.hasPrevious(); }
    @Override public boolean hasPrevious() { return it.hasNext(); }
    @Override public E next() { return it.previous(); }
    @Override public int nextIndex() { return it.previousIndex(); }
    @Override public E previous() { return it.next(); }
    @Override public int previousIndex() { return it.nextIndex(); }
    @Override public void remove() { it.remove(); }
    @Override public void set(E o) { it.set(o); }
    
}
