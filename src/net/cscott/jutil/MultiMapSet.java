// MultiMapSet.java, created Sat Nov  3 15:36:03 2001 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

/**
 * A {@link MultiMapSet} is a {@link java.util.Set} of
 * {@link java.util.Map.Entry}s which can also be accessed as a
 * {@link MultiMap}.  Use the {@link MultiMap#entrySet()} method
 * of the {@link MultiMap} to get back the {@link MultiMapSet}.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: MultiMapSet.java,v 1.4 2006-10-30 20:14:41 cananian Exp $
 */
public interface MultiMapSet<K,V> extends MapSet<K,V> {
    public MultiMap<K,V> asMap();
    public MultiMap<K,V> asMultiMap();
}
