// CombineIterator.java, created Wed Oct 14 08:50:22 1998 by cananian
// Copyright (C) 1998 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
/**
 * A {@link CombineIterator} combines several different
 * {@link Iterator}s into one.  Its {@link #remove()} removes the
 * last element returned from the {@link Iterator} it came from.
 * A {@link java.util.stream.Stream} of streams does the same, with
 * {@link java.util.stream.Stream#flatMap}.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */

public class CombineIterator<E> implements Iterator<E> {
    final Iterator<? extends Iterator<? extends E>> iti;
    Iterator<? extends E> cur=null, last=null;

    /** Creates a {@link CombineIterator} from a list of Iterators. */
    public CombineIterator(List<? extends Iterator<? extends E>> itl) {
        this(itl.iterator());
    }
    /** Creates a {@link CombineIterator} from a pair of
	Iterators. 
    */
    public CombineIterator(final Iterator<? extends E> i1,
			   final Iterator<? extends E> i2) {
	this(List.of(i1, i2));
    }
    /** Creates a {@link CombineIterator} from three
	Iterators. 
    */
    public CombineIterator(final Iterator<? extends E> i1,
			   final Iterator<? extends E> i2,
			   final Iterator<? extends E> i3) {
	this(List.of(i1, i2, i3));
    }

    /** Creates a {@link CombineIterator} from an
     *  Iterator over Iterators. */
    public CombineIterator(Iterator<? extends Iterator<? extends E>> it) {
	this.iti = it;
    }

    @Override
    public E next() {
	if (hasNext())
	    return (last=cur).next();
	else
	    throw new NoSuchElementException();
    }
    @Override
    public boolean hasNext() {
	while ((cur==null || !cur.hasNext()) && iti.hasNext())
	    cur=iti.next();
	return (cur!=null && cur.hasNext());
    }
    @Override
    public void remove() {
	if (last==null) throw new IllegalStateException();
	last.remove();
    }
}
