// CombineIterator.java, created Wed Oct 14 08:50:22 1998 by cananian
// Copyright (C) 1998 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
/**
 * A {@link CombineIterator} combines several different
 * {@link Iterator}s into one.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: CombineIterator.java,v 1.4 2006-10-30 19:58:05 cananian Exp $
 */

public class CombineIterator<E> implements Iterator<E> {
    final Iterator<Iterator<E>> iti;
    Iterator<E> cur=null, last=null;

    /** Creates a {@link CombineIterator} from an array of Iterators. */
    public CombineIterator(List<Iterator<E>> itl) {
        this(itl.iterator());
    }
    /** Creates a {@link CombineIterator} from a pair of
	Iterators. 
    */
    public CombineIterator(final Iterator<E> i1, final Iterator<E> i2) {
	this(new UnmodifiableIterator<Iterator<E>>() {
	    int i=0;
	    public Iterator<E> next() {
		switch(i++) {
		case 0: return i1;
		case 1: return i2;
		default: throw new NoSuchElementException();
		}
	    }
	    public boolean hasNext() {
		return i<2;
	    }
	});
    }
    /** Creates a {@link CombineIterator} from three
	Iterators. 
    */
    public CombineIterator(final Iterator<E> i1, final Iterator<E> i2,
			   final Iterator<E> i3) {
	this(new UnmodifiableIterator<Iterator<E>>() {
	    int i=0;
	    public Iterator<E> next() {
		switch(i++) {
		case 0: return i1;
		case 1: return i2;
		case 2: return i3;
		default: throw new NoSuchElementException();
		}
	    }
	    public boolean hasNext() {
		return i<3;
	    }
	});
    }

    /** Creates a {@link CombineIterator} from an
     *  Iterator over Iterators. */
    public CombineIterator(Iterator<Iterator<E>> it) {
	this.iti = it;
    }

    public E next() {
	if (hasNext())
	    return (last=cur).next();
	else
	    throw new NoSuchElementException();
    }
    public boolean hasNext() {
	while ((cur==null || !cur.hasNext()) && iti.hasNext())
	    cur=iti.next();
	return (cur!=null && cur.hasNext());
    }
    public void remove() {
	last.remove();
    }
}
