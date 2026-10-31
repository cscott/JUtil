// IntervalTree.java, created Tue Jul  24 23:17:23 2001 by pnkfelix
// Copyright (C) 2001 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/** An {@link IntervalTree} is a mutable collection
    of {@link Interval}s.  {@link IntervalTree}s support 
    efficient lookup of overlapping elements.

    <p> Every element in an {@link IntervalTree} must be an
    {@link Interval}.  Thus element lookup is done based upon
    {@link Interval}s, not on the datum assocatied with each
    {@link Interval}.
    
    <p> The intervals maintained by this structure are considered to
    be closed intervals.

    <p> To make use of an {@link IntervalTree} cleaner, a
    convenience method, <code>addInterval</code> is provided.
    @see IntervalTree#addInterval

    @see "CLR section 15.3, (page 290)."  */
public class IntervalTree extends RedBlackTree<IntervalTree.Interval> {

    /** Immutable record representing a closed interval
        [<code>low</code>,<code>high</code>] holding an object
        <code>obj</code>. 
    */
    public static class Interval {
	static final Comparator<Interval> COMPARE =
	    Comparator.comparingInt(i -> i.low);

	public final int low; 
	public final int high;
	public final Object datum;
	public Interval(Object datum, int low, int high) {
	    this.low = low; 
	    this.high = high;
	    this.datum = datum;
	}
	
	public boolean overlaps(Interval i) {
	    return this.low <= i.high && this.high >= i.low;
	}
    }

    private class IntervalNode extends RBNode {
	// max value of any interval endpoint stored in the
	// subtree rooted at this.
	protected int max;
	IntervalNode(Interval i) {
	    super(i);
	    if (i != null)
		this.max = i.high;
	}
    }
    @Override protected Node makeNode(Interval i) { return new IntervalNode(i); }

    @Override
    protected Node setLeft(Node p, Node l) { 
	Node n = super.setLeft(p,l); fixMax(p); return n;
    }
    @Override
    protected Node setRight(Node p, Node r) { 
	Node n = super.setRight(p,r); fixMax(p); return n;
    }
    @Override
    protected Node setParent(Node c, Node p) { 
	Node n = super.setParent(c,p); fixMax(p); return n;
    }
    // bug fix from: Kim Roland Rasmussen <kim.rasmussen@medgen.ku.dk>
    // thanks!
    private void fixMax(Node p) {
        if (p==NIL)
            return;

	int m = interval(p).high;
	if (p.left() != NIL) m = Math.max(m, max(p.left()));
	if (p.right() != NIL) m = Math.max(m, max(p.right()));
	setMax(p, m);
    }
    /** Fixes the max of <code>p</code> and all of its ancestors. */
    private void fixMaxToRoot(Node p) {
	for ( ; p != NIL; p = p.parent())
	    fixMax(p);
    }
    // The superclass changes the tree without calling setLeft(),
    // setRight() and setParent() for every link, and rotations fix
    // the max of a node before that of its new parent, so fix the
    // max all the way up from the change.
    @Override
    protected void insertNode(Node z) {
	super.insertNode(z);
	fixMaxToRoot(z);
    }
    @Override
    protected void deleteNode(Node z) {
	super.deleteNode(z);
	// z is no longer in the tree, but still points to its last parent.
	fixMaxToRoot(z.parent());
    }
    private void setMax(Node n, int m) { ((IntervalNode) n).max = m; }
    
    private Interval interval(Node n) { return n.key; }
    private int max(Node n) { return ((IntervalNode) n).max; }

    /** Constructs a new empty {@link IntervalTree}. */
    public IntervalTree() { super(Interval.COMPARE); }
    
    /** Returns some {@link Interval} in <code>this</code> which
	overlaps the bounds defined by the argument interval
	<code>i</code>, or <code>null</code> if no such interval
	exists.  
	<p> This operation is named "Interval-Search" in CLR
	@see "CLR, pg 291"
    */
    public Interval searchOverlapping(Interval i) {
	return searchOverlapping(root(),i);
    }
    // finds an overlapping interval in the tree rooted at x, if one exists. 
    private Interval searchOverlapping(Node x, Interval i) {
	while (x != NIL && !i.overlaps(interval(x))) {
	    if (x.left() != NIL && max(x.left()) >= i.low) 
		x = x.left();
	    else
		x = x.right();
	}

	if (x == NIL) 
	    return null;
	else 
	    return interval(x);
    }

    /** Returns an {@link Iterator} over {@link Interval}s that 
	yields every interval in this that overlaps <code>i</code>. 
    */
    public Iterator<Interval> allOverlapping(Interval i) {
	return allOverlapping( root(), i );
    }
    private Iterator<Interval> allOverlapping(Node x, Interval i) {
	if (x == NIL) {
	    return Collections.emptyIterator();
	} else if (interval(x).overlaps(i)) {
	    return new CombineIterator<>(
		Default.singletonIterator(interval(x)),
		allOverlapping(x.left(), i),
		allOverlapping(x.right(), i)
	    );
	} else if (x.left() != NIL && max(x.left()) >= i.low) {
	    // FSK: I suspect there is a asymptotically more efficient
	    // way to handle this case, but haven't worked it out yet.
	    return new CombineIterator<>( allOverlapping(x.left(), i),
					allOverlapping(x.right(), i));
	} else {
	    // see CLR, pg. 293, Theorem 15.2, case 2. for justification. 
	    return allOverlapping(x.right(), i);
	}
    }
    /** Constructs a new {@link Interval} <i>i</i> and adds
	<i>i</i> to <code>this</code>. 
	Convenience method.
	Requires: high > low.
	<code>datum</code> can be null.
    */
    public Interval addInterval(Object datum, int low, int high) {
	Interval i = new Interval(datum, low, high);
	add(i);
	return i;
    }
}
