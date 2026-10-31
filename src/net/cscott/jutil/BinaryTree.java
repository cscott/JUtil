// BinaryTree.java, created Tue Jul  24 11:46:23 2001 by pnkfelix
// Copyright (C) 2001 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Comparator;

/** A {@link BinaryTree} is a {@code Tree} where each node has
    at most two children.
    
    <p> All elements of a given {@link BinaryTree} <i>t</i> must
    be mutually comparable, either inherently or through an external
    {@link Comparator}.
    
    <p><i>Unlike</i> a {@link java.util.TreeSet}, duplicate elements
    <b>are</b> allowed in a {@link BinaryTree}.
    
    <p> FSK: We should probably have a Tree interface by
    now... Sometimes you want to expose the fact that you're working
    with a Tree, instead of abstract it into a Set or what-have-you.
    Have to think about adding that.
    
    @param <K> the type of the elements in this tree
    @see "CLR section 13, (page 244)."
*/
public class BinaryTree<K> {
    
    /** A {@link Node} is an element of this tree.
     */
    public class Node {
	public final K key;
	private Node left; 
	private Node right; 
	private Node parent; // called 'p' in CLR.  NIL if this is root. 
	
	protected final Node left() { return left; }
	protected final Node right() { return right; }
	protected final Node parent(){ return parent; }

	/** Can only be called by makeNode(K)
	    or by Node subclass constructors.
	 */
	protected Node(K k) {
	    this.key = k; 
	    left = right = parent = NIL;
	}

	// FSK: consider changing to "setLeft/Right(..)" form to ease
	// addition of call-outs during tree structure modifications
	// Note that methods need not be in node class itself; can be
	// kept in BinaryTree.
	
	@Override
	public String toString() {
	    return "node: "+key
		// +" lft:"+left.key
		// +" rgt:"+right.key
		// +" par:"+parent.key
		;
	}
    }
    
    /** Creates a {@link Node} n for this such that n.key == k.
	Factory method for {@link Node}.  Every construction of a
	{@link Node} takes place through this method; thus
	subclasses of {@link RedBlackTree} can associate new data
	with their own nodes by extending the {@link Node} class
	and overriding this method. 
	
	<p> Note that makeNode <b>must</b> deal with the case when key
	is <code>null</code>, regardless of whether the tree itself
	allows null elements, because the NIL sentinel node has
	<code>null</code> as its key.
    */
    protected Node makeNode(K key) { return new Node(key); }

    /** Sets the left child of p. 
	Modifies: this, p, l
	Effects: p.left_post = l.  
	Returns: p.left_pre. 
    */
    protected Node setLeft(Node p, Node l) { 
	Node n = p.left; p.left = l; return n;
    }

    /** Sets the right child of p.
	Modifies: this, p, r
	Effects: p.right_post = r.
	Returns: p.right_pre.
    */
    protected Node setRight(Node p, Node r) { 
	Node n = p.right; p.right = r; return n;
    }

    /** Sets the parent of p.
	Modifies: this, c, p
	Effects: c.parent_post = p.
	Returns: c.parent_pre.
    */
    protected Node setParent(Node c, Node p) {
	Node n = c.parent; c.parent = p; return n;
    }
    
    /** Switches the positions of <code>a</code> and <code>b</code>
	within <code>this</code>.  Both must be in the tree; one may be
	the other's parent.  Subclasses are expected to swap any
	data derived from the positions as well.
    */
    protected void swapPositions(Node a, Node b) { 
	Node al = a.left, ar = a.right, ap = a.parent;
	Node bl = b.left, br = b.right, bp = b.parent;
	boolean aWasLeft = (ap != NIL && ap.left == a);
	boolean bWasLeft = (bp != NIL && bp.left == b);

	// each takes the other's links; if they were adjacent, the
	// link from one to the other is reversed.
	setLeft(  a, (bl == a) ? b : bl);
	setRight( a, (br == a) ? b : br);
	setParent(a, (bp == a) ? b : bp);
	setLeft(  b, (al == b) ? a : al);
	setRight( b, (ar == b) ? a : ar);
	setParent(b, (ap == b) ? a : ap);

	// now point the neighbors at the new nodes.
	if (a.left  != NIL && a.left  != b) setParent(a.left,  a);
	if (a.right != NIL && a.right != b) setParent(a.right, a);
	if (b.left  != NIL && b.left  != a) setParent(b.left,  b);
	if (b.right != NIL && b.right != a) setParent(b.right, b);
	if (a.parent == NIL) root = a;
	else if (a.parent != b) {
	    if (bWasLeft) setLeft(a.parent, a); else setRight(a.parent, a);
	}
	if (b.parent == NIL) root = b;
	else if (b.parent != a) {
	    if (aWasLeft) setLeft(b.parent, b); else setRight(b.parent, b);
	}
    } 

    private Node root;
    protected Node root() { return root; }
    protected Node setRoot(Node r) { Node n = root; root = r; return n; }

    protected final Node NIL;

    protected Comparator<? super K> comp;

    // remove() and contains() take an Object, as Collection's do; an
    // element of another type makes the comparator throw a
    // ClassCastException.
    @SuppressWarnings("unchecked")
    private boolean lessThan(Object a, Object b) {
	return comp.compare((K) a, (K) b) < 0;
    }
    @SuppressWarnings("unchecked")
    private boolean eq(Object a, Object b) {
	return comp.compare((K) a, (K) b) == 0;
    }
    
    /** Creates an empty tree which accepts only <i>mutually
        comparable</i> elements. 
	@see java.lang.Comparable
    */
    @SuppressWarnings("unchecked")
    public BinaryTree() { this((Comparator<? super K>) Default.comparator); }
    
    /** Creates an empty tree which uses <code>c</code> to determine
        element ordering. 
    */
    public BinaryTree(Comparator<? super K> c) {
	NIL = makeNode(null);
	root = NIL;
	comp = c; 
    }

    /** Constructs a node for <code>k</code> and inserts it into this.  
	Uses nodes' own methods to dispatch 
     */
    public Node add(K k) {
	Node n = makeNode(k); 
	insertNode(n);
	return n; 
    }
    public void remove(Object k) {
	Node n = search(root,k);
	if (n != NIL) 
	    deleteNode(n);
    }
    /** Returns true if <code>k</code> is present in <code>this</code>. */
    public boolean contains(Object k) { return search(root,k) != NIL; }
    
    /** Returns the minimum element of <code>this</code>. 
	Requires: this is non-empty.
     */
    public K minimum() { return minimum(root).key; }

    /** Returns the maximum element of <code>this</code>. 
	Requires: this is non-empty.
     */
    public K maximum() { return maximum(root).key; }

    /** Finds the Node n (in the subtree rooted at x) 
	such that n.key = k.
	From CLR, pg 248.
    */ 
    protected Node search(Node x, Object k) {
	while (x != NIL && !eq(k, x.key)) {
	    if (lessThan(k, x.key)) {
		x = x.left;
	    } else {
		x = x.right;
	    }
	}
	return x;
    }
    
    /** Finds the minimum Node n (in the subtree rooted at x).
	From CLR, pg 248.
    */ 
    protected Node minimum(Node x) {
	while (x.left != NIL) 
	    x = x.left;
	return x;
    }

    /** Finds the maximum Node n (in the subtree rooted at x).
	From CLR, pg 248.
    */ 
    protected Node maximum(Node x) {
	while (x.right != NIL) 
	    x = x.right;
	return x;
    }

    /** Returns the successor of x in the sorted order determined by
        an inorder tree walk.  
	From CLR, pg 249.
    */
    protected Node successor(Node x) { 
	if (x.right != NIL) 
	    return minimum(x.right); 
	Node y = x.parent; 
	while(y!=NIL && x == y.right) { 
	    x = y; 
	    y = y.parent; 
	} 
	return y; 
    } 

    /** Inserts z into some appropriate position in <code>this</code>. 
	requires: (z.left == z.right == NIL)
	modifies: this, z
	From CLR, pg 251.
    */
    protected void insertNode(Node z) {
	assert z != NIL;
	Node y = NIL;
	Node x = root;
	while (x != NIL) {
	    y = x;
	    if (lessThan(z.key, x.key)) 
		x = x.left;
	    else 
		x = x.right;
	}
	z.parent = y;

	if (y == NIL) 
	    root = z;
	else if (lessThan(z.key, y.key)) 
	    y.left = z;
	else 
	    y.right = z;
    }
    
    /** Splices <code>z</code> out from this.
	Based on CLR, pg 253.
	modifies: this, z
     */
    protected void deleteNode(Node z) {
	// deviation from CLR; Node data is immutable, so we 
	// shift the nodes themselves around instead.
	if (z.left != NIL && z.right != NIL)
	    // fun fact: (z has 2 children) ==> (y has at most 1 child)
	    swapPositions(successor(z), z);

	// z now has at most one child, x
	Node x = (z.left != NIL) ? z.left : z.right;
	if (x != NIL) 
	    x.parent = z.parent;

	if (z.parent == NIL) 
	    root = x;
	else if (z == z.parent.left) 
	    z.parent.left = x;
	else
	    z.parent.right = x;
    }

    public String dump() { return dump(root); }
    public String dump(Node x) {
	if (x == NIL) 
	    return ".";
	else 
	    return "("+x +" "
		+ dump(x.left) +" "
		+ dump(x.right) +")"; 
    }
}
