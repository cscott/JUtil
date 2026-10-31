// PersistentTreeNodeTest.java, created from the self-test in
// PersistentTreeNode.
// Copyright (C) 2002 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Comparator;

import org.junit.jupiter.api.Test;

class PersistentTreeNodeTest {
    /** Sample implementation of {@link PersistentTreeNode}. */
    static class WithValue<K,V>
	extends PersistentTreeNode<WithValue<K,V>,K,V> {
	@java.io.Serial
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("serial") // serializable if its contents are
	final V value;
	WithValue(K key, V value, WithValue<K,V> left, WithValue<K,V> right) {
	    super(key, left, right);
	    this.value = value;
	}
	@Override public V getValue() { return value; }
	static class Allocator<K,V>
	    extends PersistentTreeNode.Allocator<WithValue<K,V>,K,V> {
	    WithValue<K,V> newNode(K key, V value,
				   WithValue<K,V> left, WithValue<K,V> right) {
		return new WithValue<>(key, value, left, right);
	    }
	}
    }
    private static final Comparator<Integer> C = Integer::compare;
    private static final WithValue.Allocator<Integer,Integer> ALLOC =
	new WithValue.Allocator<>();
    private static final int N = 1000;

    /** Inserts and removes keys in order, which would make an
     *  unbalanced binary tree a list. */
    @Test void balance() {
	WithValue<Integer,Integer> root = null;
	// a perfectly balanced tree of i nodes has depth ceil(log2(i+1))
	for (int i=1; i<=N; i++) {
	    root = PersistentTreeNode.put(root, C, i, -i, ALLOC);
	    assertEquals(i, PersistentTreeNode.size(root));
	    int depth = PersistentTreeNode.depth(root);
	    assertTrue(depth <= 4 * Util.log2c(i+1), i+" nodes, depth "+depth);
	}
	for (int i=1; i<=N; i++)
	    assertEquals(-i, PersistentTreeNode.get(root, C, i).getValue());
	assertNull(PersistentTreeNode.get(root, C, 0));
	for (int i=N; i>=1; i--) {
	    root = PersistentTreeNode.remove(root, C, i, ALLOC);
	    assertEquals(i-1, PersistentTreeNode.size(root));
	    int depth = PersistentTreeNode.depth(root);
	    assertTrue(depth <= 4 * Util.log2c(i), (i-1)+" nodes, depth "+depth);
	}
	assertNull(root);
    }

    /** A change makes a new tree, and leaves the old one as it was. */
    @Test void persistence() {
	WithValue<Integer,Integer> t1 = null;
	for (int i=0; i<10; i++)
	    t1 = PersistentTreeNode.put(t1, C, i, i, ALLOC);
	WithValue<Integer,Integer> t2 = PersistentTreeNode.put(t1, C, 5, 50, ALLOC);
	WithValue<Integer,Integer> t3 = PersistentTreeNode.remove(t1, C, 3, ALLOC);
	assertEquals(5, PersistentTreeNode.get(t1, C, 5).getValue());
	assertEquals(50, PersistentTreeNode.get(t2, C, 5).getValue());
	assertEquals(3, PersistentTreeNode.get(t1, C, 3).getValue());
	assertNull(PersistentTreeNode.get(t3, C, 3));
	assertEquals(10, PersistentTreeNode.size(t1));
	assertEquals(10, PersistentTreeNode.size(t2));
	assertEquals(9, PersistentTreeNode.size(t3));
    }
}
