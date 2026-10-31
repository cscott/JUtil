// RedBlackTree.java, created Tue Jul  24 11:46:23 2001 by pnkfelix
// Copyright (C) 2001 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Comparator;

/** A {@link RedBlackTree} is a {@link BinaryTree} that uses
    red-black properties to maintain a balanced form.
    
    @see "CLR section 14, (page 263)."
*/
public class RedBlackTree extends BinaryTree {  
    // red black properties
    // 1. Every node is either red or black
    // 2. Every leaf (nil) is black
    // 3. If a node is red, then both its children are black
    // 4. Every simple path from a node to a descendant leaf contains
    // the same number of black nodes.

    private static final boolean DEBUG = false;
    private void checkRep() { if (DEBUG) checkTree(root()); }
    /** Checks the red-black properties of the subtree rooted at
	<code>n</code>, and returns its black-height. */
    int checkTree(Node n) {
	if (n == NIL) {
	    assert color(n) == BLACK : "(2) viol.";
	    return 0;
	}
	if (color(n) == RED) { // (3)
	    assert color(n.left()) == BLACK : "(3) left viol. on "+n;
	    assert color(n.right()) == BLACK : "(3) right viol. on "+n;
	}
	int lh = checkTree(n.left()), rh = checkTree(n.right());
	assert lh == rh : "(4) viol. on "+n;
	return lh + ((color(n) == BLACK) ? 1 : 0);
    }

    private static final int RED = 0;
    private static final int BLACK = 1;

    /** A {@link RBNode} is an element of this tree.
     */
    protected class RBNode extends Node {
	private int color = BLACK;
	protected RBNode(Object key) { super(key); }
	@Override
	public String toString() {
	    return super.toString()+" col:"+ ((color ==BLACK)?"b":"r");
	}
    }
    
    /** Factory method for {@link Node}.  Every construction of a
	{@link Node} takes place through this method; thus
	subclasses of {@link RedBlackTree} can associate new data
	with their own nodes by extending the {@link Node} class
	and overriding this method. */
    @Override protected Node makeNode(Object o) { return new RBNode(o); }

    public RedBlackTree() { super(); }
    public RedBlackTree(Comparator c) { super(c); }

    // helper method to clear out casts in code.  I can't wait for GJ
    // so I'll be able to get around this in better ways.
    private int color(Node n) { 
	if (n == NIL)
	    return BLACK; // see r.b. property (2) above.
	else 
	    return ((RBNode)n).color; 
    }
    private void setColor(Node n, int c) { ((RBNode)n).color = c; }

    @Override
    protected void insertNode(Node x) {
	checkRep();
	super.insertNode(x);
	setColor(x, RED);
	while(x != root() && color(x.parent()) == RED) {
	    if (x.parent() == x.parent().parent().left()) {
		Node y = x.parent().parent().right();
		if (color(y) == RED) {
		    setColor(x.parent(), BLACK);
		    setColor(y, BLACK);
		    setColor(x.parent().parent(), RED);
		    x = x.parent().parent();
		} else {
		    if (x == x.parent().right()) {
			x = x.parent();
			leftRotate(x);
		    }
		    setColor(x.parent(), BLACK);
		    setColor(x.parent().parent(), RED);
		    rightRotate(x.parent().parent());
		}
	    } else {
		Node y = x.parent().parent().left();
		if (color(y) == RED) {
		    setColor(x.parent(), BLACK);
		    setColor(y, BLACK);
		    setColor(x.parent().parent(), RED);
		    x = x.parent().parent();
		} else {
		    if (x == x.parent().left()) {
			x = x.parent();
			rightRotate(x);
		    }
		    setColor(x.parent(), BLACK);
		    setColor(x.parent().parent(), RED);
		    leftRotate(x.parent().parent());
		}
	    }
	}
	setColor(root(), BLACK);

	checkRep();
    }

    @Override
    protected void swapPositions(Node a, Node b) {
	super.swapPositions(a,b);
	int c = color(a);
	setColor(a, color(b));
	setColor(b, c);
    }
    @Override
    protected void deleteNode(Node z) {
	checkRep();

	if (z.left() != NIL && z.right() != NIL)
	    // swaps the colors as well
	    swapPositions(successor(z), z);

	// z now has at most one child, x
	Node x = (z.left() != NIL) ? z.left() : z.right();
	setParent(x, z.parent());

	if (z.parent() == NIL)
	    setRoot(x);
	else if (z == z.parent().left())
	    setLeft(z.parent(), x);
	else
	    setRight(z.parent(), x);

	if (color(z) == BLACK)
	    rbDeleteFixup(x);

	checkRep();
    }
    
    /** Post delete fixup routine.  
	@see "CLR, pg. 274"
    */
    protected void rbDeleteFixup(Node x) {
	while(x != root() && color(x) == BLACK) {
	    if (x == x.parent().left()) {
		Node w = x.parent().right();
		assert w != NIL;
		if (color(w) == RED) {
		    setColor(w, BLACK);
		    setColor(x.parent(), RED);
		    leftRotate(x.parent());
		    w = x.parent().right();
		}
		if (color(w.left()) == BLACK && color(w.right()) == BLACK) {
		    setColor(w, RED);
		    x = x.parent();
		} else {
		    if (color(w.right()) == BLACK) {
			setColor(w.left(), BLACK);
			setColor(w, RED);
			rightRotate(w);
			w = x.parent().right();
		    }
		    setColor(w, color(x.parent()));
		    setColor(x.parent(), BLACK);
		    setColor(w.right(), BLACK);
		    leftRotate(x.parent());
		    x = root();
		}
	    } else {
		Node w = x.parent().left();
		if (color(w) == RED) {
		    setColor(w, BLACK);
		    setColor(x.parent(), RED);
		    rightRotate(x.parent());
		    w = x.parent().left();
		}
		if (color(w.right()) == BLACK && color(w.left()) == BLACK) {
		    setColor(w, RED);
		    x = x.parent();
		} else {
		    if (color(w.left()) == BLACK) {
			setColor(w.right(), BLACK);
			setColor(w, RED);
			leftRotate(w);
			w = x.parent().left();
		    }
		    setColor(w, color(x.parent()));
		    setColor(x.parent(), BLACK);
		    setColor(w.left(), BLACK);
		    rightRotate(x.parent());
		    x = root();
		}
	    }
	}
	setColor(x, BLACK);
    }

    /** Pivots around the edge (x,x.right). 
	Assumes x.right != null.
    */
    protected void leftRotate(Node x) {
	Node y = x.right();
	setRight(x, y.left());
	if (y.left() != NIL) {
	    setParent(y.left(), x);
	}
	setParent(y, x.parent());
	if (x.parent() == NIL) {
	    setRoot(y);
	} else {
	    if (x == x.parent().left()) {
		setLeft(x.parent(), y);
	    } else {
		setRight(x.parent(), y);
	    }
	}
	setLeft(y, x);
	setParent(x, y);
    }

    /** Pivots around the edge (x,x.left). 
	Assumes x.left != NIL.
    */
    protected void rightRotate(Node x) {
	Node y = x.left();
	setLeft(x, y.right());
	if (y.right() != NIL) {
	    setParent(y.right(), x);
	}
	setParent(y, x.parent());
	if (x.parent() == NIL) {
	    setRoot(y);
	} else {
	    if (x == x.parent().right()) {
		setRight(x.parent(), y);
	    } else {
		setLeft(x.parent(), y);
	    }
	}
	setRight(y, x);
	setParent(x, y);
    }

}
