// MapSet.java, created Sat Nov  3 15:05:04 2001 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Map;
import java.util.Set;

/**
 * A <code>MapSet</code> is a <code>java.util.Set</code> of
 * <code>Map.Entry</code>s which can also be accessed as a
 * <code>java.util.Map</code>.  Use the <code>entrySet()</code>
 * method of the <code>Map</code> to get back the <code>MapSet</code>.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: MapSet.java,v 1.1 2003-03-20 01:58:20 cananian Exp $
 */
public interface MapSet<K,V> extends Set<Map.Entry<K,V>> {
    public Map<K,V> asMap();
}
