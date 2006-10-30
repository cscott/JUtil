// MapFactory.java, created Tue Oct 19 22:42:28 1999 by pnkfelix
// Copyright (C) 1999 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Collections;
import java.util.Map;

/** {@link MapFactory} is a {@link Map} generator.
    Subclasses should implement constructions of specific types of
    {@link Map}s.
     <p>
    Note also that the current limitations on parametric types in
    Java mean that we can't easily type this class as
    <code>MapFactory&lt;M extends Map&lt;K,V&gt;,K,V&gt;</code>,
    as <code>MapFactory&lt;HashMap&lt;K,V&gt;,K,V&gt;</code> is not
    a subtype of <code>MapFactory&lt;Map&lt;K,V&gt;,K,V&gt;</code>,
    even though {@link java.util.HashMap} is a subtype of {@link Map}.

    @author  Felix S. Klock II <pnkfelix@mit.edu>
    @version $Id: MapFactory.java,v 1.4 2006-10-30 19:58:06 cananian Exp $
 */
public abstract class MapFactory<K,V> {
    
    /** Creates a {@link MapFactory}. */
    public MapFactory() {
        
    }

    /** Generates a new, mutable, empty {@link Map}. */
    public Map<K,V> makeMap() {
	return this.makeMap(Collections.<K,V>emptyMap());
    }

    /** Generates a new {@link Map}, using the entries of
	<code>map</code> as a template for its initial mappings. 
    */
    public abstract Map<K,V> makeMap(Map<? extends K,? extends V> map);

    
}
