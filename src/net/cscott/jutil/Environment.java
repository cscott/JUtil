// Environment.java, created Sat Aug 28 22:12:08 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

/**
 * An <code>Environment</code> is a <code>Map</code> with scoping:
 * you can save marks into the environment and undo all changes
 * since a mark.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: Environment.java,v 1.1 2003-03-20 01:58:20 cananian Exp $
 */
public interface Environment<K,V> extends java.util.Map<K,V> {
    /** A abstract property for marks into an environment. */
    public interface Mark { }

    /** Get a mark that will allow you to restore the current state of
     *  this environment. */
    Mark getMark();
    /** Undo all changes since the supplied mark, restoring the map to
     *  its state at the time the mark was taken.  The undoToMark()
     *  operation must be repeatable. */
    void undoToMark(Mark m);
}
