// GenericMultiMapFactory.java, created Wed Jan 19 13:53:00 2005 by cananian
// Copyright (C) 2005 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.*;
/** <code>GenericMultiMapFactory</code> implements <code>MultiMapFactory</code>
 *  using instances of <code>GenericMultiMap</code>.
 *
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: GenericMultiMapFactory.java,v 1.1 2005-01-20 01:14:35 cananian Exp $
 */
public class GenericMultiMapFactory<K,V> extends MultiMapFactory<K,V> {
    final MapFactory<K,Collection<V>> mf;
    final CollectionFactory<V> cf;
    /** Create a new <code>GenericMultiMapFactory</code>, specifying
     * the <code>MapFactory</code> and <code>CollectionFactory</code>
     * to be used by the underlying <code>GenericMultiMap</code>. */
    public GenericMultiMapFactory(MapFactory<K,Collection<V>> mf, CollectionFactory<V> cf) {
	this.mf = mf;  this.cf = cf;
    }
    /** Create a new <code>GenericMultiMapFactory</code>, using a
     * <code>HashMap</code> factory for the map and an
     * <code>AggregateSetFactory</code> for the value collections. */
    public GenericMultiMapFactory() {
	this(Factories.<K,Collection<V>>hashMapFactory(), new AggregateSetFactory<V>());
    }

    /** Create a new <code>GenericMultiMap</code>. */
    public GenericMultiMap<K,V> makeMultiMap() {
	return new GenericMultiMap<K,V>(mf, cf);
    }
}
