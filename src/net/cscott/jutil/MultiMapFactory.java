// MultiMapFactory.java, created Wed Feb 27 13:14:06 2002 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.*;
/** <code>MultiMapFactory</code> is a <code>MultiMap</code> generator.
 *  Subclasses should implement constructions of specific types of
 *  <code>MultiMap</code>s.
 *
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: MultiMapFactory.java,v 1.2 2004-01-13 20:47:05 cananian Exp $
 */
public class MultiMapFactory<K,V> extends MapFactory<K,V> {
    
    /** Creates a <code>MultiMapFactory</code>. */
    public MultiMapFactory() {
    }
    // MapFactory interface
    public final MultiMap<K,V> makeMap() {
	return makeMultiMap();
    }
    public final MultiMap<K,V> makeMap(Map<? extends K,? extends V> map) {
	return makeMultiMap(map);
    }
    // MultiMapFactory interface.
    // XXX: why do we have default implementations here but not in the other
    //  *Factory classes?
    public MultiMap<K,V> makeMultiMap() {
	return makeMultiMap(Default.EMPTY_MULTIMAP);
    }

    /** Creates a new <code>MultiMap</code> initialized with all 
	of the <code>Map.Entry</code>s in <code>map</code>
    */
    public MultiMap<K,V> makeMultiMap(Map<? extends K,? extends V> map) {
	return new GenericMultiMap<K,V>(map);
    }
    public MultiMap<K,V> makeMultiMap(MapFactory<K,Collection<V>> mf, CollectionFactory<V> cf) {
	return new GenericMultiMap<K,V>(mf, cf);
    }
}
