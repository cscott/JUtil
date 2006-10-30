// FilterIterator.java, created Tue Feb 23 06:17:30 1999 by cananian
// Copyright (C) 1998 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A {@link FilterIterator} filters and maps a source
 * {@link Iterator} to generate a new one.
 *
 * Note that this implementation reads one element ahead, so if the
 * Filter changes for an object 'o' between the time that is read
 * (when next() is called, returning the object preceding 'o', and
 * checking that 'o' satisfies the current Filter) and the time when
 * hasNext() is called, 'o' will still be returned, regardless of what
 * Filter.isElement(o) returns.  Thus, it is recommended that only
 * Filters which remain consistent throughout the iteration be used. 
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: FilterIterator.java,v 1.2 2006-10-30 19:58:05 cananian Exp $
 */
public class FilterIterator<A,B> extends UnmodifiableIterator<B> {
    final Iterator<A> i;
    final Filter<A,B> f;
    /** Creates a {@link FilterIterator}. */
    public FilterIterator(Iterator<A> i, Filter<A,B> f) {
        this.i = i; this.f = f; advance();
    }

    private A next = null;
    private boolean done = false;

    private void advance() {
	while (i.hasNext()) {
	    next = i.next();
	    if (f.isElement(next))
		return; // found next element.
	}
	done = true; // found end of enumeration.
    }

    public B next() {
	if (done) throw new NoSuchElementException();
	A o = next; advance(); return f.map(o);
    }
    public boolean hasNext() {
	return !done;
    }

    public static class Filter<A,B> { // default is an identity mapping.
	/** Return <code>true</code> if the specified element should be
	    included in the filtered enumeration. 
	 
	    <BR> Default implementation returns true for all
	    {@link Object}s (no filter).   
	 */
	public boolean isElement(A o) { return true; }

	/** Perform a mapping on elements from the source enumeration. 

	    <BR> Default implementation returns <code>o</code>
	    (identity mapping). 
	 */
	public B map(A o) { return (B) o; } // cast is only safe if A extends B
    }
}
