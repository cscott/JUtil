// MultiMapFactory.java, created Wed Feb 27 13:14:06 2002 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.*;
/** {@link MultiMapFactory} is a {@link MultiMap} generator.
 *  Subclasses should implement constructions of specific types of
 *  {@link MultiMap}s.  Subclasses *must* implement at least one of
 *  the <code>makeMultiMap</code> methods.
 *
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: MultiMapFactory.java,v 1.5 2006-10-30 19:58:06 cananian Exp $
 */
public abstract class MultiMapFactory<K,V> extends MapFactory<K,V> {
    
    /** Creates a {@link MultiMapFactory}. */
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
    /** Create a new, empty, {@link MultiMap}. */
    public MultiMap<K,V> makeMultiMap() {
	return makeMultiMap(Default.<K,V>EMPTY_MULTIMAP());
    }

    /** Creates a new {@link MultiMap} initialized with all 
	of the {@link java.util.Map.Entry}s in <code>map</code>
    */
    public MultiMap<K,V> makeMultiMap(MultiMap<? extends K,? extends V> map) {
	MultiMap<K,V> mm = makeMultiMap();
	mm.addAll(map);
	return mm;
    }
}
