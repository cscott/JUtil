// InvertibleMultiMap.java, created Sun Jun 17 16:19:35 2001 by cananian
// Copyright (C) 2001 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

/**
 * An {@link InvertibleMultiMap} is an extension of the
 * {@link MultiMap} interface to allow users to do reverse lookups on
 * the mappings maintained.  
 * If, for {@link MultiMap} <code>m</code>,
 * <code>m.contains(a, b)</code>, then
 * <code>m.invert().contains(b, a)</code>.
 *
 * If the {@link InvertibleMultiMap} is mutable, the
 * {@link InvertibleMultiMap} returned by its <code>invert()</code>
 * method should also be mutable.  Moreover, for any
 * {@link InvertibleMultiMap}, 
 * <code>this.invert().invert()==this</code>.
 *
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: InvertibleMultiMap.java,v 1.2 2006-10-30 19:58:06 cananian Exp $
 */
public interface InvertibleMultiMap<K,V>
    extends MultiMap<K,V>, InvertibleMap<K,V> {
    /** Returns a inverted view of <code>this</code>.
	Thus, if <code>this</code> is a {@link MultiMap} with domain A
	and range B, the returned {@link MultiMap},
	<code>imap</code>, will be a {@link MultiMap} with domain
	B and range A, such that <em>b</em> in B will map in
	<code>imap</code> to a collection containing <em>a</em>,
	if and only if <em>a</em> in <code>this</code> maps to
	a collection containing <em>b</em>.
     */
    public InvertibleMultiMap<V,K> invert();
}
