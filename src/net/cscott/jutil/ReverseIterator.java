// ReverseIterator.java, created Wed Dec 23 04:56:20 1998 by cananian
// Copyright (C) 1998 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A {@link ReverseIterator} iterates through an {@link Iterator}
 * in reverse order.  It extends {@link SnapshotIterator}, so is
 * insensitive to changes in the underlying collection once construction
 * is complete.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public class ReverseIterator<E> extends SnapshotIterator<E> {
    /** Creates a {@link ReverseIterator} of {@link Iterator}
     *  <code>it</code>. */
    public ReverseIterator(Iterator<E> it) {
	super(it);
	i = l.size()-1;
    }
    public boolean hasNext() { return ( i >= 0 ); }
    public E  next() {
	try { return l.get(i--); }
	catch (IndexOutOfBoundsException e)
	{ throw new NoSuchElementException(); }
    }
}
