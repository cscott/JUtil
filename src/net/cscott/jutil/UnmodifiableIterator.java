// UnmodifiableIterator.java, created Tue Jun 15 22:00:21 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Iterator;
/**
 * <code>UnmodifiableIterator</code> is an abstract superclass to save
 * you the trouble of implementing the <code>remove()</code> method
 * over and over again for those iterators which don't implement it.
 * The name's a bit clunky, but fits with the JDK naming in
 * <code>java.util.Collections</code> and etc.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: UnmodifiableIterator.java,v 1.2 2006-10-29 20:10:08 cananian Exp $
 */
public abstract class UnmodifiableIterator<E> implements Iterator<E> {
    /** Create an {@link UnmodifiableIterator} from the given (potentailly
     * modifiable) {@link Iterator}.
     */
    public static <E> UnmodifiableIterator<E> proxy(final Iterator<E> it) {
        return new UnmodifiableIterator<E>() {
            @Override
            public boolean hasNext() { return it.hasNext(); }
            @Override
            public E next() { return it.next(); }
        };
    }
    /** Returns <code>true</code> if the iteration has more elements.
     * @return <code>true</code> if the iterator has more elements.
     */
    public abstract boolean hasNext();
    /** Returns the next element in the iteration.
     * @exception java.util.NoSuchElementException iteration has no more elements.
     */
    public abstract E next();
    /** Always throws an <code>UnsupportedOperationException</code>.
     * @exception UnsupportedOperationException always.
     */
    public final void remove() {
	throw new UnsupportedOperationException("Unmodifiable Iterator");
    }
}
