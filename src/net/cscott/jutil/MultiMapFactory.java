// MultiMapFactory.java, created Wed Feb 27 13:14:06 2002 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Map;
/** {@link MultiMapFactory} is a {@link MultiMap} generator.
 *  Implementations construct specific types of {@link MultiMap}s.
 *  It is a functional interface, whose method is
 *  {@link #makeMultiMap()}, so a constructor of an empty
 *  {@link MultiMap} is a {@link MultiMapFactory}:
 *  <code>GenericMultiMap::new</code>.
 *
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
@FunctionalInterface
public interface MultiMapFactory<K,V> extends MapFactory<K,V> {
    // MapFactory interface
    @Override
    default MultiMap<K,V> makeMap() {
	return makeMultiMap();
    }
    // A lambda for a MapFactory implements this method, and one for a
    // MultiMapFactory implements makeMultiMap(), which this method
    // calls: the factories of 1.x did the same.
    @Override
    @SuppressWarnings("FunctionalInterfaceMethodChanged")
    default MultiMap<K,V> makeMap(Map<? extends K,? extends V> map) {
	MultiMap<K,V> mm = makeMap();
	mm.putAll(map);
	return mm;
    }
    // MultiMapFactory interface.
    /** Create a new, empty, {@link MultiMap}. */
    MultiMap<K,V> makeMultiMap();

    /** Creates a new {@link MultiMap} initialized with all 
	of the {@link java.util.Map.Entry}s in <code>map</code>
    */
    default MultiMap<K,V> makeMultiMap(MultiMap<? extends K,? extends V> map) {
	MultiMap<K,V> mm = makeMultiMap();
	mm.addAll(map);
	return mm;
    }
}
