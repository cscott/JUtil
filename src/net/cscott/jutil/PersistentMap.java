// PersistentTree.java, created Wed Mar 31 18:41:03 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
/**
 * {@link PersistentMap} implements a persistent map, based on a
 * persistent randomized treap.  Unlike the {@link Map}s returned
 * by {@link PersistentMapFactory}, {@link PersistentMap}
 * does not implement that standard {@link java.util.Map} API
 * but instead exposes the underlying functional operations.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: PersistentMap.java,v 1.7 2006-10-30 19:58:06 cananian Exp $
 */
public class PersistentMap<K,V> implements java.io.Serializable {
    final Node<K,V> root;
    final Comparator<K> c;
    final Node.Allocator<K,V> allocator;

    /** Creates an empty {@link PersistentMap} whose
     *  key objects will all implement {@link java.lang.Comparable}. 
     *  Note that good hashcode implementations for the key objects are
     *  still required.
     */
    public PersistentMap() {
	// cast below is safe iff K implements Comparable.
	this((Comparator<K>)Default.comparator); // safe iff K is Comparable
    }
    /** Creates an empty {@link PersistentMap} whose
     *  key objects are ordered by the given {@link Comparator}.
     *  Note that good hashcode implementations for the key objects are
     *  still required.
     */
    public PersistentMap(Comparator<K> c) {
	this(null, c, new Node.Allocator<K,V>());
    }
    /** Creates a {@link PersistentMap} from a root {@link Node}
     *  and a {@link Comparator}.*/
    private PersistentMap(Node<K,V> root, Comparator<K> c,
			  Node.Allocator<K,V> allocator) {
	this.root = root; this.c = c; this.allocator = allocator;
    }

    /** Determines if this {@link PersistentMap} has any mappings. */
    public boolean isEmpty() { return (root==null); }

    /** Count the number of key->value mappings in this
     *  {@link PersistentMap}. */
    public int size() { return (root==null)?0:root.size; }

    /** Creates and returns a new {@link PersistentMap} identical to
     *  this one, except it contains a mapping from <code>key</code> to
     *  <code>value. */
    public PersistentMap<K,V> put(K key, V value) {
	Node<K,V> new_root =
	    Node.put(this.root, this.c, key, value, allocator);
	return (this.root == new_root) ? this :
	    new PersistentMap<K,V>(new_root, c, allocator);
    }
    /** Gets the value which <code>key</code> maps to. */
    public V get(K key) {
	Node<K,V> np = 
	    Node.get(this.root, this.c, key);
	return (np==null)?null:np.value;
    }
    /** Determines if there is a mapping for the given <code>key</code>. */
    public boolean containsKey(K key) {
	return (Node.get(this.root, this.c, key)!=null);
    }

    /** Make a new {@link PersistentMap} identical to this one,
     *  except that it does not contain a mapping for <code>key</code>. */
    public PersistentMap<K,V> remove(K key) {
	Node<K,V> new_root = 
	    Node.remove(this.root, this.c, key, allocator);
	return (this.root == new_root) ? this :
	    new PersistentMap<K,V>(new_root, c, allocator);
    }
    /** Put all the mappings in the given map into this map, throwing
     *  out conflicting mappings from this set as necessary. */
    public PersistentMap<K,V> putAll(PersistentMap<K,V> map) {
	if (this.c.equals(map.c) && this.allocator==map.allocator) {
	    Node<K,V> new_root =
		Node.putAll(this.root, map.root, this.c, allocator);
	    return (new_root==this.root) ? this :
		(new_root==map.root) ? map :
		new PersistentMap<K,V>(new_root, c, allocator);
	}
	// slow way
	Node<K,V> new_root = this.root;
	for (Iterator<Node<K,V>> it=Node.iterator(map.root); it.hasNext(); ) {
	    Node<K,V> n = it.next();
	    new_root = Node.put(new_root, this.c, n.key, n.value, allocator);
	}
	return (this.root == new_root) ? this :
	    new PersistentMap<K,V>(new_root, c, allocator);
    }
    
    /** Cloning takes constant time, regardless of the size of the map. */
    public PersistentMap<K,V> clone() {
	return new PersistentMap<K,V>(root, c, allocator);
    }
    /** Human-readable representation of the map. */
    public String toString() { return asMap().toString(); }
    // fast Map.hashCode implementation.
    public int hashCode() { return (root==null)?0:root.mapHashCode; }

    /*---------------------------------------------------------------*/
    /** {@link java.util.Collection}s view of the mapping. */
    public Map<K,V> asMap() {
	return new AbstractMap<K,V>() {
	    // constant-time clone.
	    public Map<K,V> clone() { return PersistentMap.this.asMap(); }
	    public boolean containsKey(Object key) {
		// ack, not safe if key is not a K!
		return PersistentMap.this.containsKey((K)key);
	    }
	    public V get(Object key) {
		// ack, not safe is key is not a K!
		return PersistentMap.this.get((K)key);
	    }
	    public boolean isEmpty() {
		return PersistentMap.this.isEmpty();
	    }
	    public int size() {
		return PersistentMap.this.size();
	    }
	    // xxx implement putAll()?
	    public Set<Map.Entry<K,V>> entrySet() {
		return new AbstractSet<Map.Entry<K,V>>() {
		    public int size() {
			return PersistentMap.this.size();
		    }
		    public Iterator<Map.Entry<K,V>> iterator() {
			final Iterator<Node<K,V>> it = Node.iterator(root);
			return new Iterator<Map.Entry<K,V>>() {
			    Node<K,V> last=null;
			    public boolean hasNext() { return it.hasNext(); }
			    public Map.Entry<K,V> next() {
				last = it.next();
				return last;
			    }
			    public void remove() {
				if (last==null)
				    throw new IllegalStateException();
				// XXX doesn't update the Map view
				// changes behind the iterator's back don't
				// matter, because everything's persistent!
				PersistentMap.this.remove(last.getKey());
				last=null;
			    }
			};
		    }
		};
	    }
	};
    }
    // PersistentTreeNode subclass
    private static class Node<K,V>
	extends PersistentTreeNode<Node<K,V>,K,V> 
	implements java.io.Serializable {
	final V value;
	/** The hash code of a {@link java.util.Map} with the
	 *  contents of the tree rooted at this node. */
	final int mapHashCode;
	/** Size of the tree rooted at this node. */
	final int size;
	Node(K key, V value, Node<K,V> left, Node<K,V> right) {
	    super(key, left, right);
	    this.value = value;
	    this.mapHashCode = this.hashCode() + // this entry
		((left==null)?0:left.mapHashCode) + // hash of left tree
		((right==null)?0:right.mapHashCode); // hash of right tree
	    this.size = 1 + // this entry
		((left==null)?0:left.size) + // size of left tree
		((right==null)?0:right.size); // size of right tree
	}
	public V getValue() { return value; }
	static class Allocator<K,V>
	    extends PersistentTreeNode.Allocator<Node<K,V>,K,V> 
	    implements java.io.Serializable {
	    Node<K,V> newNode(K key, V value,
			      Node<K,V> left, Node<K,V> right) {
		return new Node<K,V>(key, value, left, right);
	    }
	}
    }
}
