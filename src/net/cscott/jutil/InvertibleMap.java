// InvertibleMap.java, created Wed Jun 21  3:22:34 2000 by pnkfelix
// Copyright (C) 2001 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Map;

/** An {@link InvertibleMap} is an extension of the
    {@link Map} interface to allow users to do reverse lookups on
    the mappings maintained.  
    Since {@link Map}s are allowed to map multiple keys to a
    single value, the inversion of a {@link Map} is not
    necessarily a {@link Map} itself; thus we return a
    {@link MultiMap} for the inverted view.  The returned
    {@link MultiMap} is not guaranteed to be modfiable, even if
    <code>this</code> is (ie, changes to the data structure may still
    have to be made through <code>this</code> rather than directly to
    the returned {@link MultiMap}).

    @author  Felix S. Klock II <pnkfelix@mit.edu>
    @version $Id: InvertibleMap.java,v 1.2 2006-10-30 19:58:06 cananian Exp $
*/
public interface InvertibleMap<K,V> extends java.util.Map<K,V> {
    /** Returns a inverted view of <code>this</code>.
	Thus, if <code>this</code> is a {@link Map} with domain A
	and range B, the returned {@link MultiMap},
	<code>imap</code>, will be a {@link MultiMap} with domain
	B and range A, such that each <em>b</em> in B will map in
	<code>imap</code> to Collection of A, <em>c</em>, if and only if
	each <em>a</em> in <em>c</em> maps to <em>b</em> in
	<code>this</code>. 
     */
    public MultiMap<V,K> invert();
}
