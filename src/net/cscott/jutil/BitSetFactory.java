// BitSetFactory.java, created Mon Nov  1 11:18:52 1999 by pnkfelix
// Copyright (C) 1999 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.HashMap;

/** {@link BitSetFactory} is a {@link SetFactory} that,
    given a complete universe of possible values, produces low space
    overhead representations of {@link Set}s. 
    
    Notably, the {@link Set}s produced should have union,
    intersection, and difference operations that, while still O(n),
    have <b>blazingly</b> low constant factors.

    The addition operations ({@link Set#add(Object)} and its
    cousins) are only defined for objects that are part of the
    universe of values given to the constructor; other Objects will
    cause {@link IllegalArgumentException} to be thrown.

    @author  Felix S. Klock II <pnkfelix@mit.edu>
    @version $Id: BitSetFactory.java,v 1.6 2006-10-30 20:14:41 cananian Exp $
 */
public class BitSetFactory<V> extends SetFactory<V> {
    
    /** Maps each object in the universe for <code>this</code> to an
	index in the {@link BitString} for the {@link Set}s
	produced.
    */
    private Indexer<V> indexer;

    /** Size that each bit string needs to be.  Does not necessarily 
	equal the size of the universe itself, because the indices of
	the universe can skip values.   
    */
    private int bitStringSize;

    /** Universe of values for this. */
    private Set<V> universe; 

    /** Universe of values for this, represented as a BitSet.  (Used
	for makeFullSet). */
    private BitStringSet<V> bitUniverse = null;
    
    /** Creates a {@link BitSetFactory}, given a
	<code>universe</code> of values and an {@link Indexer}
	for the elements of <code>universe</code>. 
    */
    public BitSetFactory(final Collection<V> universe, final Indexer<V> indexer) {
        final Iterator<V> vals = universe.iterator();
	this.indexer = indexer;
	this.universe = new HashSet<V>(universe);
	int max = 0;
	while(vals.hasNext()) {
	    int i = indexer.getID(vals.next());
	    if (i > max) max = i;
	}
	this.bitStringSize = max+1;
    }

    /** Creates a {@link BitSetFactory}, given a
	<code>universe</code> of values.  Makes a new
	{@link Indexer} for <code>universe</code>; the
	created {@link Indexer} will implement the
	<code>Indexer.getByID()</code> method to allow
	efficient iteration over sets.
    */
    public BitSetFactory(final Collection<V> universe) {
	final HashMap<V,Integer> obj2int = new HashMap<V,Integer>();
	final ArrayList<V> int2obj = new ArrayList<V>();
	final Iterator<V> iter = universe.iterator();
	this.universe = new HashSet<V>(universe);
	int i;
	for(i=0; iter.hasNext(); i++) {
	    V o = iter.next();
	    obj2int.put(o, i);
	    int2obj.add(i, o); 
	}
	this.bitStringSize = i+1;
	this.indexer = new Indexer<V>() {
	    public int getID(V o) {
		return obj2int.get(o);
	    }
	    public V getByID(int id) {
		return int2obj.get(id);
	    }
	    public boolean implementsReverseMapping() { return true; }
	};

    }
    
    /** Generates a new mutable {@link Set}, using the elements
	of <code>c</code> as a template for its initial contents. 
	<BR> <B>requires:</B> All of the elements of <code>c</code>
	     must have been part of the universe for
	     <code>this</code>. 
	<BR> <B>effects:</B> Constructs a lightweight
	     {@link Set} with the elements from <code>c</code>.
    */ 
    public BitStringSet<V> makeSet(Collection<? extends V> c) {
	BitStringSet<V> bss = new BitStringSet<V>(bitStringSize, this);
	bss.addAll(c);
	return bss;
    }
    
    /** Generates a new mutable {@link Set}, using the elements
	of the universe for <code>this</code> as its initial contents.
    */
    public Set<V> makeFullSet() {
	if (bitUniverse == null) 
	    bitUniverse = makeSet(universe);

	return (Set<V>) bitUniverse.clone();
    }

    private static class BitStringSet<V> extends AbstractSet<V>
	implements Cloneable {
	/** internal representation for set. */
	final BitString bs;
	/** Cached hash code for this set (sum of hashcodes of elements). */
	int hashCode ;
	/** Is the cached hashCode valid? */
	boolean hashCodeValid;

	// ensure that sets come from same factory
	// when doing optimized operations. 
	final BitSetFactory<V> fact; 

	BitStringSet(int size, BitSetFactory<V> fact) {
	    this.bs = new BitString(size);
	    this.fact = fact;
	    this.hashCode = 0;
	    this.hashCodeValid=true;
	}
	/** Clone constructor. */
	private BitStringSet(BitStringSet<V> s) {
	    this.bs = s.bs.clone();
	    this.hashCode = s.hashCode;
	    this.hashCodeValid = s.hashCodeValid;
	    this.fact = s.fact;
	}

	public boolean add(V o) {
	    if (!fact.universe.contains(o)) 
		throw new IllegalArgumentException
		    ("Attempted to add an object: "+o+
		     "that was not part of the "+
		     "original universe of values.");
	    
	    int ind = fact.indexer.getID(o);
	    boolean alreadySet = this.bs.get(ind);
	    if (alreadySet) {
		return false;
	    } else {
		this.bs.set(ind);
		if (this.hashCodeValid) this.hashCode += elemHashCode(o);
		return true;
	    }
	}

	public boolean addAll(Collection<? extends V> c) {
	    if (c instanceof BitStringSet &&
		((BitStringSet)c).fact == this.fact) {
		BitStringSet bss = (BitStringSet) c;
		this.hashCodeValid=false; // invalidate cache.
		return this.bs.or(bss.bs);
	    } else return super.addAll(c);
	}
	
	public void clear() {
	    this.bs.clearAll();
	    this.hashCode=0;
	    this.hashCodeValid=true;
	}
	
	public boolean contains(Object o) {
	    if (fact.universe.contains(o)) {
		int i = fact.indexer.getID((V)o);
		return this.bs.get(i);
	    } else {
		// not part of original universe, therefore cannot be
		// a member of the set.
		return false;
	    }
	}

	public boolean containsAll(Collection<?> c) {
	    // check that ('c' - this) is nullset
	    // (which is the same as C /\ NOT(this) )
	    if (c instanceof BitStringSet &&
		((BitStringSet)c).fact == this.fact) {
		BitString notBS = 
		    (BitString) this.bs.clone();
		notBS.setAll(); // -> string of ones
		notBS.xor(this.bs);  // -> complement(bs)

		BitStringSet bss = (BitStringSet) c;
		return bss.bs.intersectionEmpty(notBS);
	    } else return super.containsAll(c);
	}
	
	public BitStringSet<V> clone() {
	    return new BitStringSet<V>(this);
	}

	public boolean equals(Object o) {
	    if (o==null) return false;
	    if (o==this) return true;
	    if (o instanceof BitStringSet) {
		BitStringSet bss = (BitStringSet) o;
		if (this.fact == bss.fact)
		    return this.bs.equals(bss.bs);
	    }
	    return super.equals(o);
	}
	// follow spec for Set.hashCode()
	public int hashCode() {
	    if (!this.hashCodeValid) {
		// recompute hashcode from scratch.
		this.hashCode = super.hashCode();
		this.hashCodeValid=true;
	    }
	    return this.hashCode; // valid cached copy.
	}
	
	public boolean isEmpty() {
	    return this.bs.isZero();
	}
	
	public Iterator<V> iterator() {
	    return fact.indexer.implementsReverseMapping() ?
	      (Iterator<V>) new Iterator<V>() { // fast bit-set iterator
		int lastindex=-1;
		public boolean hasNext() {
		    return BitStringSet.this.bs.firstSet(lastindex)!=-1;
		}
		public V next() {
		    lastindex = BitStringSet.this.bs.firstSet(lastindex);
		    if (lastindex<0) throw new NoSuchElementException();
		    return fact.indexer.getByID(lastindex);
		}
		public void remove() {
		    if (lastindex<0 || !BitStringSet.this.bs.get(lastindex))
			throw new IllegalStateException();
		    BitStringSet.this.bs.clear(lastindex);
		    if (BitStringSet.this.hashCodeValid)
			BitStringSet.this.hashCode -= // adjust hashCode
			    elemHashCode(fact.indexer.getByID(lastindex));
		}
	    } : new Iterator<V>() { // slower fall-back
		    // need to wrap a *modifiable* iterator 
		    // around an internal filter iterator...
		    Iterator<V> internIter = new FilterIterator<V,V>
			(fact.universe.iterator(), 
			 new FilterIterator.Filter<V,V>() {
				 public boolean isElement(V o) {
				     return BitStringSet.this.bs.get
					 (fact.indexer.getID(o));
				 }});
		    V last = null;
		    public V next() {
			last = internIter.next();
			return last;
		    }
		    public boolean hasNext() {
			return internIter.hasNext();
		    }
		    public void remove() {
			BitStringSet.this.bs.clear(fact.indexer.getID(last));
			if (BitStringSet.this.hashCodeValid)
			    BitStringSet.this.hashCode -= // adjust hashCode
				elemHashCode(last);
		    }};
	}
	
	public boolean remove(Object o) {
	    if (!fact.universe.contains(o)) {
		// o is not member of universe, therefore cannot be in set.
		return false;
	    } else {
		int i = fact.indexer.getID((V)o);
		boolean alreadySet = bs.get(i);
		if (alreadySet) {
		    this.bs.clear(i);
		    if (this.hashCodeValid) this.hashCode -= elemHashCode(o);
		    return true;
		} else {
		    return false;
		}
	    }
	}

	public boolean removeAll(Collection<?> c) {
	    if (c instanceof BitStringSet &&
		((BitStringSet)c).fact == this.fact) {
		this.hashCodeValid=false;
		BitStringSet bss = (BitStringSet) c;
		BitString notBSS = (BitString) bss.bs.clone();
		notBSS.setAll(); // -> string of ones
		notBSS.xor(bss.bs); // -> complement(bss)
		return this.bs.and(notBSS);
	    } else if (c.size() < this.size()) {
		// optimization hack; super.removeAll takes time
		// proportional to this.size()
		boolean changed = false;
		for(Iterator<?> i=c.iterator(); i.hasNext();) {
		    changed |= remove(i.next());
		}
		return changed;
	    } else {		
		return super.removeAll(c); // slower generic implementation
	    }
	}

	public boolean retainAll(Collection<?> c) {
	    if (c instanceof BitStringSet &&
		((BitStringSet)c).fact == this.fact) {
		this.hashCodeValid=false;
		BitStringSet bss = (BitStringSet) c;
		return this.bs.and(bss.bs);
	    } else {
		return super.retainAll(c); // slower generic implementation
	    }
	}

	public int size() {
	    return this.bs.numberOfOnes();
	}

	// inherit implementations for toArray() and
	// toArray(Object[]) methods from AbstractSet
    }
    
    private static final int elemHashCode(Object obj) {
	return (obj==null)?0:obj.hashCode();
    }
}
