// MultiMapFactory.java, created Wed Feb 27 13:14:06 2002 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.*;
/** <code>MultiMapFactory</code> is a <code>MultiMap</code> generator.
 *  Subclasses should implement constructions of specific types of
 *  <code>MultiMap</code>s.  Subclasses *must* implement at least one of
 *  the <code>makeMultiMap</code> methods.
 *
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: MultiMapFactory.java,v 1.4 2005-01-20 01:14:35 cananian Exp $
 */
public abstract class MultiMapFactory<K,V> extends MapFactory<K,V> {
    
    /** Creates a <code>MultiMapFactory</code>. */
    public MultiMapFactory() {
    }
    // MapFactory interface
    public final MultiMap<K,V> makeMap() {
	return makeMultiMap();
    }
    public final MultiMap<K,V> makeMap(Map<? extends K,? extends V> map) {
	MultiMap<K,V> mm = makeMap();
	mm.putAll(map);
	return mm;
    }
    // MultiMapFactory interface.
    /** Create a new, empty, <code>MultiMap</code>. */
    public MultiMap<K,V> makeMultiMap() {
	return makeMultiMap(Default.<K,V>EMPTY_MULTIMAP());
    }

    /** Creates a new <code>MultiMap</code> initialized with all 
	of the <code>Map.Entry</code>s in <code>map</code>
    */
    public MultiMap<K,V> makeMultiMap(MultiMap<? extends K,? extends V> map) {
	MultiMap<K,V> mm = makeMultiMap();
	mm.addAll(map);
	return mm;
    }
}
