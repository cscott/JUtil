// SnapshotIterator.java, created Wed Dec 23 04:56:20 1998 by cananian
// Copyright (C) 1998 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A {@link SnapshotIterator} takes a "snapshot" of an iterator,
 * and iterates over that snapshot.  So subsequent modifications to
 * the collection underlying the original iterator do not modify the
 * snapshot or the {@link SnapshotIterator}.   This is very
 * useful for modifying collections via a visitor class: we want
 * to make sure that every element of the original collection is
 * visited, while still permitting the visitor to make changes to
 * the collection as it operates.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: SnapshotIterator.java,v 1.3 2006-10-30 19:58:07 cananian Exp $
 */
public class SnapshotIterator<E> extends UnmodifiableIterator<E> {
    final ArrayList<E> l = new ArrayList<E>();
    int i = 0;

    /** Creates a {@link SnapshotIterator} from {@link Iterator}
     *  <code>it</code>. */
    public SnapshotIterator(Iterator<E> it) {
	while (it.hasNext()) l.add(it.next());
	l.trimToSize();
    }
    public boolean hasNext() { return ( i < l.size() ); }
    public E  next() {
	try { return l.get(i++); }
	catch (IndexOutOfBoundsException e)
	{ throw new NoSuchElementException(); }
    }
}
