// GenericMultiMapFactory.java, created Wed Jan 19 13:53:00 2005 by cananian
// Copyright (C) 2005 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.*;
/** {@link GenericMultiMapFactory} implements {@link MultiMapFactory}
 *  using instances of {@link GenericMultiMap}.
 *
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: GenericMultiMapFactory.java,v 1.2 2006-10-30 19:58:05 cananian Exp $
 */
public class GenericMultiMapFactory<K,V> extends MultiMapFactory<K,V> {
    final MapFactory<K,Collection<V>> mf;
    final CollectionFactory<V> cf;
    /** Create a new {@link GenericMultiMapFactory}, specifying
     * the {@link MapFactory} and {@link CollectionFactory}
     * to be used by the underlying {@link GenericMultiMap}. */
    public GenericMultiMapFactory(MapFactory<K,Collection<V>> mf, CollectionFactory<V> cf) {
	this.mf = mf;  this.cf = cf;
    }
    /** Create a new {@link GenericMultiMapFactory}, using a
     * {@link HashMap} factory for the map and an
     * {@link AggregateSetFactory} for the value collections. */
    public GenericMultiMapFactory() {
	this(Factories.<K,Collection<V>>hashMapFactory(), new AggregateSetFactory<V>());
    }

    /** Create a new {@link GenericMultiMap}. */
    public GenericMultiMap<K,V> makeMultiMap() {
	return new GenericMultiMap<K,V>(mf, cf);
    }
}
