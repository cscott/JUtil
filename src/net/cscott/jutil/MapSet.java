// MapSet.java, created Sat Nov  3 15:05:04 2001 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Map;
import java.util.Set;

/**
 * A {@link MapSet} is a {@link java.util.Set} of
 * {@link java.util.Map.Entry}s which can also be accessed as a
 * {@link java.util.Map}.  Use the {@link Map#entrySet()}
 * method of the {@link Map} to get back the {@link MapSet}.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: MapSet.java,v 1.3 2006-10-30 20:14:41 cananian Exp $
 */
public interface MapSet<K,V> extends Set<Map.Entry<K,V>> {
    public Map<K,V> asMap();
}
