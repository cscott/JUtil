// Factories.java, created Tue Oct 19 23:21:25 1999 by pnkfelix
// Copyright (C) 1999 Felix S. Klock II <pnkfelix@mit.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/** {@link Factories} consists exclusively of static methods that
    operate on or return {@link CollectionFactory}s. 
 
    @author  Felix S. Klock II <pnkfelix@mit.edu>
    @version $Id: Factories.java,v 1.8 2006-10-30 19:58:05 cananian Exp $
 */
public final class Factories {
    
    /** Private ctor so no one will instantiate this class. */
    private Factories() {
        
    }
    
    /** A {@link MapFactory} that generates {@link HashMap}s. */ 
    public static final MapFactory hashMapFactory = hashMapFactory();
    public static final <K,V> MapFactory<K,V> hashMapFactory() {
	return new SerialMapFactory<K,V>() {
	    public HashMap<K,V> makeMap(Map<? extends K,? extends V> map) {
		return new HashMap<K,V>(map);
	    }
	};
    }
    
    /** A {@link MapFactory} that generates {@link LinkedHashMap}s. */ 
    public static final MapFactory linkedHashMapFactory = linkedHashMapFactory();
    public static final <K,V> MapFactory<K,V> linkedHashMapFactory() {
	return new SerialMapFactory<K,V>() {
	    public LinkedHashMap<K,V> makeMap(Map<? extends K,? extends V> map) {
		return new LinkedHashMap<K,V>(map);
	    }
	};
    }

    /** A {@link MapFactory} that generates {@link EnumMap}s. */ 
    public static final <K extends Enum<K>,V> MapFactory<K,V> enumMapFactory
                                                   (final Class<K> enumClass) {
        return new SerialMapFactory<K,V>() {
            public EnumMap<K,V> makeMap(Map<? extends K,? extends V> map) {
                EnumMap<K,V> m = new EnumMap<K,V>(enumClass);
                m.putAll(map);
                return m;
            }
        };
    }
    
    /** A {@link SetFactory} that generates {@link HashSet}s. */
    public static final SetFactory hashSetFactory = hashSetFactory();
    public static final <V> SetFactory<V> hashSetFactory() {
	return new SerialSetFactory<V>() {
	    public HashSet<V> makeSet(Collection<? extends V> c) {
		return new HashSet<V>(c);
	    }
	    public HashSet<V> makeSet(int i) {
		return new HashSet<V>(i);
	    }
	};
    }
    
    /** A {@link SetFactory} that generates {@link LinkedHashSet}s. */
    public static final SetFactory linkedHashSetFactory = linkedHashSetFactory();
    public static final <V> SetFactory<V> linkedHashSetFactory() {
	return new SerialSetFactory<V>() {
	    public LinkedHashSet<V> makeSet(Collection<? extends V> c) {
		return new LinkedHashSet<V>(c);
	    }
	    public LinkedHashSet<V> makeSet(int i) {
		return new LinkedHashSet<V>(i);
	    }
	};
    }
    /** A {@link SetFactory} that generates {@link EnumSet}s. */
    public static final <V extends Enum<V>> SetFactory<V> enumSetFactory
                                                   (final Class<V> enumClass) {
        return new SerialSetFactory<V>() {
            public EnumSet<V> makeSet(Collection<? extends V> c) {
                EnumSet<V> s = makeSet(0);
                s.addAll(c);
                return s;
            }
            public EnumSet<V> makeSet(int i) {
                // ignore the 'i' parameter.
                return EnumSet.noneOf(enumClass);
            }
        };
    }
    
    /** A {@link SetFactory} that generates {@link WorkSet}s. */
    public static final SetFactory workSetFactory = workSetFactory();
    public static final <V> SetFactory<V> workSetFactory() {
	return new SerialSetFactory<V>() {
	    public WorkSet<V> makeSet(Collection<? extends V> c) {
		return new WorkSet<V>(c);
	    }
	    public WorkSet<V> makeSet(int i) {
		return new WorkSet<V>(i);
	    }
	};
    }
    
    /** A {@link SetFactory} that generates
	{@link LinearSet}s backed by {@link ArrayList}s. */
    public static final SetFactory linearSetFactory = linearSetFactory();
    public static final <V> SetFactory<V> linearSetFactory() {
      return new SerialSetFactory<V>() {
	public LinearSet<V> makeSet(Collection<? extends V> c) {
	    LinearSet<V> ls;
	    if (c instanceof Set) {
		ls = new LinearSet<V>((Set<? extends V>)c);
	    } else {
		ls = new LinearSet<V>(c.size());
		ls.addAll(c);
	    }
	    return ls;
	}
	public LinearSet<V> makeSet(int i) {
	    return new LinearSet<V>(i);
	}
      };
    }

    /** A {@link SetFactory} that generates {@link TreeSet}s. */
    public static final SetFactory treeSetFactory = Factories.<Comparable>treeSetFactory();
    public static final <V extends Comparable<V>> SetFactory<V> treeSetFactory() {
	return new SerialSetFactory<V>() {
	    public TreeSet<V> makeSet(Collection<? extends V> c) {
		return new TreeSet<V>(c);
	    }
	};
    }
    public static final <V> SetFactory<V> treeSetFactory(final Comparator<? super V> comp) {
	return new SerialSetFactory<V>() {
	    public TreeSet<V> makeSet(Collection<? extends V> c) {
		TreeSet<V> result = new TreeSet<V>(comp);
		result.addAll(c);
		return result;
	    }
	};
    }

    /** A {@link MapFactory} that generates {@link TreeMap}s. */
    public static final MapFactory treeMapFactory = Factories.<Comparable,Object>treeMapFactory();
    public static final <K extends Comparable<K>,V> MapFactory<K,V> treeMapFactory() {
	return new SerialMapFactory<K,V>() {
	    public TreeMap<K,V> makeMap(Map<? extends K,? extends V> map) {
		return new TreeMap<K,V>(map);
	    }
	};
    }
    public static final <K,V> MapFactory<K,V> treeMapFactory(final Comparator<? super K> comp) {
	return new SerialMapFactory<K,V>() {
	    public TreeMap<K,V> makeMap(Map<? extends K,? extends V> map) {
		TreeMap<K,V> result = new TreeMap<K,V>(comp);
		result.putAll(map);
		return result;
	    }
	};
    }
    
    /** A {@link ListFactory} that generates {@link LinkedList}s. */
    public static final ListFactory linkedListFactory = linkedListFactory();
    public static final <V> ListFactory<V> linkedListFactory() {
	return new SerialListFactory<V>() {
	    public LinkedList<V> makeList(Collection<? extends V> c) {
		return new LinkedList<V>(c);
	    }
	};
    }

    /** Returns a {@link ListFactory} that generates
	{@link ArrayList}s. */
    public static final ListFactory arrayListFactory = arrayListFactory();
    public static final <V> ListFactory<V> arrayListFactory() {
	return new SerialListFactory<V>() {
	    public ArrayList<V> makeList(Collection<? extends V> c) {
		return new ArrayList<V>(c);
	    }
	    public ArrayList<V> makeList(int i) {
		return new ArrayList<V>(i);
	    }
	};
    }

    /** Returns a {@link SetFactory} that generates {@link MapSet}
     *  views of maps generated by the given {@link MapFactory}.  These
     *  can be passed in as arguments to a {@link GenericMultiMap},
     *  for example, to make a multimap of maps. */
    public static <K,V> SetFactory<Map.Entry<K,V>> mapSetFactory(final MapFactory<K,V> mf) {
	return new SerialSetFactory<Map.Entry<K,V>>() {
		public Set<Map.Entry<K,V>> makeSet(Collection<? extends Map.Entry<K,V>> c) {
		    final Map<K,V> m = mf.makeMap();
		    // we could call addAll on the result, but we'll be
		    // gentle on entrySet()s which might not allow 'add'.
		    for (Map.Entry<? extends K,? extends V> me : c)
			m.put(me.getKey(), me.getValue());

		    final Set<Map.Entry<K,V>> s = m.entrySet();
		    if (s instanceof MapSet && ((MapSet<K,V>)s).asMap()==m)
			return s; // optimize!
		    return new MapSetWrapper<K,V>() {
                            protected Set<Map.Entry<K,V>> wrapped() {return s;}
			    public Map<K,V> asMap() { return m; }
			};
		}
	    };
    }
    static abstract class MapSetWrapper<K,V> extends SetWrapper<Map.Entry<K,V>> implements MapSet<K,V> {
	protected MapSetWrapper() { }
    }

    /** Returns a {@link SetFactory} that generates
     *  {@link MultiMapSet} views of {@link MultiMap}s
     *  generated by the given {@link MultiMapFactory}.  These can be
     *  passed in as arguments to a {@link GenericMultiMap}, for
     *  example, to make a multimap of multimaps. */
    public static <K,V> SetFactory<Map.Entry<K,V>> multiMapSetFactory(final MultiMapFactory<K,V> mf) {
	return new SerialSetFactory<Map.Entry<K,V>>() {
		public Set<Map.Entry<K,V>> makeSet(Collection<? extends Map.Entry<K,V>> c) {
		    final MultiMap<K,V> m = mf.makeMultiMap();
		    // we could call addAll on the result, but we'll be
		    // gentle on entrySet()s which might not allow 'add'.
		    for (Map.Entry<? extends K,? extends V> me : c)
			m.add(me.getKey(), me.getValue());

		    final Set<Map.Entry<K,V>> s = m.entrySet();
		    if (s instanceof MultiMapSet &&
			((MultiMapSet<K,V>)s).asMultiMap()==m)
			return s; // optimize!
		    return new MultiMapSetWrapper<K,V>() {
                            protected Set<Map.Entry<K,V>> wrapped() {return s;}
			    public MultiMap<K,V> asMultiMap() { return m; }
			};
		}
	    };
    }
    static abstract class MultiMapSetWrapper<K,V> extends SetWrapper<Map.Entry<K,V>>
	implements MultiMapSet<K,V> {
        protected MultiMapSetWrapper() { }
        public final MultiMap<K,V> asMap() { return asMultiMap(); }
    }

    /** Returns a {@link CollectionFactory} that generates
	synchronized (thread-safe) {@link Collection}s.  
	The {@link Collection}s generated are backed by the 
	{@link Collection}s generated by <code>cf</code>. 
	@see Collections#synchronizedCollection
    */
    public static <V> CollectionFactory<V>
	synchronizedCollectionFactory(final CollectionFactory<V> cf) { 
	return new SerialCollectionFactory<V>() {
	    public Collection<V> makeCollection(Collection<? extends V> c) {
		return Collections.synchronizedCollection
		    (cf.makeCollection(c));
	    }
	};
    }

    /** Returns a {@link SetFactory} that generates synchronized
	(thread-safe) {@link Set}s.  The {@link Set}s
	generated are backed by the {@link Set}s generated by
	<code>sf</code>. 
	@see Collections#synchronizedSet
    */
    public static <V> SetFactory<V>
	synchronizedSetFactory(final SetFactory<V> sf) {
	return new SerialSetFactory<V>() {
	    public Set<V> makeSet(Collection<? extends V> c) {
		return Collections.synchronizedSet(sf.makeSet(c));
	    }
	};
    }

    /** Returns a {@link ListFactory} that generates synchronized
	(thread-safe) {@link List}s.   The {@link List}s
	generated are backed by the {@link List}s generated by
	<code>lf</code>. 
	@see Collections#synchronizedList
    */
    public static <V> ListFactory<V>
	synchronizedListFactory(final ListFactory<V> lf) {
	return new SerialListFactory<V>() {
	    public List<V> makeList(Collection<? extends V> c) {
		return Collections.synchronizedList(lf.makeList(c));
	    }
	};
    }

    /** Returns a {@link MapFactory} that generates synchronized
	(thread-safe) {@link Map}s.  The {@link Map}s
	generated are backed by the {@link Map} generated by
	<code>mf</code>.
	@see Collections#synchronizedMap
    */
    public static <K,V> MapFactory<K,V>
	synchronizedMapFactory(final MapFactory<K,V> mf) {
	return new SerialMapFactory<K,V>() {
	    public Map<K,V> makeMap(Map<? extends K,? extends V> map) {
		return Collections.synchronizedMap(mf.makeMap(map));
	    }
	};
    }

    public static <V> CollectionFactory<V>
	noNullCollectionFactory(final CollectionFactory<V> cf) {
	return new SerialCollectionFactory<V>() {
	    public Collection<V> makeCollection(final Collection<? extends V> c) {
		assert noNull(c);
		final Collection<V> back = cf.makeCollection(c);
		return new CollectionWrapper<V>() {
                    protected Collection<V> wrapped() { return back; }
		    public boolean add(V o) {
			assert o != null;
			return super.add(o);
		    }
		    public boolean addAll(Collection<? extends V> c2) {
			assert Factories.noNull(c2);
			return super.addAll(c2);
		    }
		};
	    }
	};
    }

    private static <V> boolean noNull(Collection<V> c) {
	Iterator<V> iter = c.iterator();
	while(iter.hasNext()) {
	    if(iter.next() == null) return false;
	}
	return true;
    }

    // private classes to add java.io.Serializable to *Factories.
    // if we could make anonymous types w/ multiple inheritance, we wouldn't
    // need these.
    private static abstract class SerialMapFactory<K,V>
	extends MapFactory<K,V> implements java.io.Serializable { }
    private static abstract class SerialSetFactory<V>
	extends SetFactory<V> implements java.io.Serializable { }
    private static abstract class SerialListFactory<V>
	extends ListFactory<V> implements java.io.Serializable { }
    private static abstract class SerialCollectionFactory<V>
	extends CollectionFactory<V> implements java.io.Serializable { }
}
