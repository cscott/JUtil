// IteratorEnumerator.java, created Tue Feb 23 02:06:53 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Enumeration;
import java.util.Iterator;
/**
 * An {@link IteratorEnumerator} converts an {@link Iterator}
 * into an {@link Enumeration}.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: IteratorEnumerator.java,v 1.2 2006-10-30 19:58:06 cananian Exp $
 */
public class IteratorEnumerator<E> implements Enumeration<E> {
    private final Iterator<E> i;
    /** Creates a {@link IteratorEnumerator}. */
    public IteratorEnumerator(Iterator<E> i) { this.i = i; }
    public boolean hasMoreElements() { return i.hasNext(); }
    public E nextElement() { return i.next(); }
}
