// IteratorEnumerator.java, created Tue Feb 23 02:06:53 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Enumeration;
import java.util.Iterator;
/**
 * An {@link IteratorEnumerator} converts an {@link Iterator}
 * into an {@link Enumeration}.  To enumerate a {@link java.util.Collection},
 * {@link java.util.Collections#enumeration(java.util.Collection)} does
 * the same; {@link Enumeration#asIterator()} converts the other way.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
@SuppressWarnings("JdkObsolete") // making an Enumeration is its job
public class IteratorEnumerator<E> implements Enumeration<E> {
    private final Iterator<? extends E> i;
    /** Creates a {@link IteratorEnumerator}. */
    public IteratorEnumerator(Iterator<? extends E> i) { this.i = i; }
    @Override public boolean hasMoreElements() { return i.hasNext(); }
    @Override public E nextElement() { return i.next(); }
}
