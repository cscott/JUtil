// SetTest.java, created Wed Nov  7 17:44:45 2001 by cananian
// Copyright (C) 2000 C. Scott Ananian <cananian@alumni.princeton.edu>
// Portions copyright (C) 2001 ACUNIA; borrowed from Mauve.
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * {@link SetTest} tests our various {@link Set}
 * implementations for correctness.  Large portions borrowed from Mauve.
 * 
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
@SuppressWarnings({"rawtypes", "unchecked"})
class SetTest {
    private final static boolean DEBUG=false;
    /** Makes the sets to test.  For sets which don't allow
     *  <code>null</code>, <code>nullValue</code> stands in for it. */
    record Factory(String name, Supplier<Set> empty,
		   Function<Collection,Set> copy, String nullValue) {
	Factory(String name, Supplier<Set> empty,
		Function<Collection,Set> copy) {
	    this(name, empty, copy, null);
	}
	Set build() { return empty.get(); }
	Set build(Collection c) { return copy.apply(c); }
	@Override public String toString() { return name; }
    }
    static Factory of(String name, SetFactory sf) {
	return new Factory(name, sf::makeSet, sf::makeSet);
    }
    // these sets don't handle 'null's.
    private static final String NOT_NULL = " this is a null.  No, really! ";
    static final Object ONE_OF_THESE = new Object();

    @SuppressWarnings("deprecation") // WorkSet is still tested
    static Stream<Factory> factories() {
	return Stream.of(
	    new Factory("HashSet", HashSet::new, HashSet::new),
	    new Factory("UniqueVector",
			UniqueVector::new, UniqueVector::new, NOT_NULL),
	    new Factory("UniqueStack",
			UniqueStack::new, UniqueStack::new, NOT_NULL),
	    new Factory("WorkSet", WorkSet::new, WorkSet::new, NOT_NULL),
	    new Factory("LinearSet", LinearSet::new, LinearSet::new),
	    of("AggregateSetFactory", new AggregateSetFactory()),
	    of("synchronized HashSet",
	       Factories.synchronizedSetFactory(Factories.hashSetFactory())),
	    of("PersistentSetFactory",
	       new PersistentSetFactory(MapTest.NULL_FIRST)));
    }

    // my local state.
    final SetTest th = this;
    private Factory f;
    private String NULL;
    /** Returns a test instance for the given factory.  (JUnit's own
     *  instance, made with the no-argument constructor, only runs the
     *  parameterized tests.) */
    static SetTest of(Factory f) {
	SetTest t = new SetTest(); t.f = f; t.NULL = f.nullValue(); return t;
    }

    // methods of test harness.
    private final List<String> failures = new ArrayList<>();
    void check(boolean cond) {
	if (!cond) failures.add(section+": "+last_check);
    }
    void debug(String s) { if (DEBUG) System.err.println(s); }
    /** Fails if any check failed, listing each failed check. */
    void assertPassed() {
	if (!failures.isEmpty())
	    Assertions.fail(failures.size()+" check(s) failed:\n"+
			    String.join("\n", failures));
    }
    void checkPoint(String name) {
	section = name; 
    }
    void checkPoint2(String name) {
	last_check = name;
    }
    String section = "NONE";
    String last_check = "NONE";
    // checking methods.
    // start code from gnu.testlet.TestHarness

  public void check (Object result, Object expected)
    {
      boolean ok = (result == null
		    ? expected == null
		    : result.equals(expected));
      check (ok);
      // This debug message may be misleading, depending on whether
      // string conversion produces same results for unequal objects.
      if (! ok)
	debug ("got " + result + " but expected " + expected);
    }
  public void check (double result, double expected)
    {
      // This triple check overcomes the fact that == does not
      // compare NaNs, and cannot tell between 0.0 and -0.0;
      // and all without relying on java.lang.Double (which may
      // itself be buggy - else why would we be testing it? ;)
      // For 0, we switch to infinities, and for NaN, we rely
      // on the identity in JLS 15.21.1 that NaN != NaN is true.
      boolean ok = (result == expected
		    ? (result != 0) || (1/result == 1/expected)
		    : (result != result) && (expected != expected));
      check (ok);
      if (! ok)
	// If Double.toString() is buggy, this debug statement may
	// accidentally show the same string for two different doubles!
	debug ("got " + result + " but expected " + expected);
    }
  public void check (long result, long expected)
    {
      boolean ok = (result == expected);
      check (ok);
      if (! ok)
	debug ("got " + result + " but expected " + expected);
    }
  public void check (int result, int expected)
    {
      boolean ok = (result == expected);
      check (ok);
      if (! ok)
	debug ("got " + result + " but expected " + expected);
    }

  // These methods are like the above, but checkpoint first.
  public void check (boolean result, String name)
    {
      checkPoint2 (name);
      check (result);
    }
  public void check (Object result, Object expected, String name)
    {
      checkPoint2 (name);
      check (result, expected);
    }
  public void check (int result, int expected, String name)
    {
      checkPoint2 (name);
      check (result, expected);
    }
  public void check (long result, long expected, String name)
    {
      checkPoint2 (name);
      check (result, expected);
    }
  public void check (double result, double expected, String name)
    {
      checkPoint2 (name);
      check (result, expected);
    }
  public void fail (String name)
    {
      checkPoint2 (name);
      check (false);
    }
    // end bit from gnu.testlet.TestHarness

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void equals(Factory f) {
	SetTest t = of(f);
	t.test_equals();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void hashCode(Factory f) {
	SetTest t = of(f);
	t.test_hashCode();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void add(Factory f) {
	SetTest t = of(f);
	t.test_add();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void addAll(Factory f) {
	SetTest t = of(f);
	t.test_addAll();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void clear(Factory f) {
	SetTest t = of(f);
	t.test_clear();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void remove(Factory f) {
	SetTest t = of(f);
	t.test_remove();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void removeAll(Factory f) {
	SetTest t = of(f);
	t.test_removeAll();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void retainAll(Factory f) {
	SetTest t = of(f);
	t.test_retainAll();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void contains(Factory f) {
	SetTest t = of(f);
	t.test_contains();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void containsAll(Factory f) {
	SetTest t = of(f);
	t.test_containsAll();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void isEmpty(Factory f) {
	SetTest t = of(f);
	t.test_isEmpty();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void size(Factory f) {
	SetTest t = of(f);
	t.test_size();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void iterator(Factory f) {
	SetTest t = of(f);
	t.test_iterator();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void toArray(Factory f) {
	SetTest t = of(f);
	t.test_toArray();
	t.assertPassed();
    }

    @ParameterizedTest(name="{0}")
    @MethodSource("factories")
    void toString(Factory f) {
	SetTest t = of(f);
	t.test_toString();
	t.assertPassed();
    }

    // from AcuniaAbstractSetTest.java: ///////////////////////////////

/**
* implemented. <br>
*
*/
  public void test_equals(){
    th.checkPoint("equals(java.lang.Object)boolean");
    Set xas1 = f.build();
    Set xas2 = f.build();
    th.check( xas1.equals(xas2) , "checking equality -- 1");
    th.check(!xas1.equals(null) , "checking equality -- 2");
    th.check(!xas1.equals(this) , "checking equality -- 3");
    th.check( xas1.equals(xas1) , "checking equality -- 4");
    xas1.add(NULL);
    xas1.add("a");
    xas2.add("b");
    xas2.add(NULL);
    xas2.add("a");
    xas1.add("b");
    th.check( xas1.equals(xas2) , "checking equality -- 5");
    th.check( xas1.equals(xas1) , "checking equality -- 6");


  }
/**
* implemented. <br>
*
*/
  public void test_hashCode(){
    th.checkPoint("hashCode()int");
    Set xas = f.build();
    th.check(xas.hashCode() == 0 ,"checking hc-algorithm -- 1");
    if (NULL==null) {
	xas.add(NULL);
	th.check(xas.hashCode() == 0 ,"checking hc-algorithm -- 2");
    }
    xas.add("a");
    int hash = "a".hashCode();
    th.check(xas.hashCode() == hash ,"checking hc-algorithm -- 3");
    hash += "b".hashCode();
    xas.add("b");
    th.check(xas.hashCode() == hash ,"checking hc-algorithm -- 4");
    hash += "c".hashCode();
    xas.add("c");
    th.check(xas.hashCode() == hash ,"checking hc-algorithm -- 5");
    hash += "d".hashCode();
    xas.add("d");
    th.check(xas.hashCode() == hash ,"checking hc-algorithm -- 6");
  }

    // from AcuniaAbstractCollectionTest.java: ///////////////////////////////
/**
*  implemented. <br>
*
*/
  public void test_add(){
    th.checkPoint("add(java.lang.Object)boolean");
    Set ac = f.build();
    if (!ac.add(ONE_OF_THESE))
	th.fail("should return true.");
  }

/**
* implemented. <br>
*
*/
  public void test_addAll(){
    th.checkPoint("addAll(java.util.Collection)boolean");
    Vector v = new Vector();
    v.add("a"); 	v.add("b");
    v.add("c");         v.add("d");
    Set ac = f.build();
    th.check( ac.addAll(v) , "should return true, v is modified");
    th.check( ac.equals(new HashSet(v)) , "check everything is added");
    try {
    	ac.addAll(null);
    	th.fail("should throw a NullPointerException");
    	}
    catch (NullPointerException ne) { th.check(true);}
  }

/**
* implemented. <br>
*
*/
  public void test_clear(){
    th.checkPoint("clear()void");
    Set ac = f.build();
    ac.add("a"); 	ac.add("b");
    ac.add("c");      ac.add("d");
    ac.clear();
    th.check(ac.size()==0 , "all elements are removed -- 1");
    ac.clear();
    th.check(ac.size()==0 , "all elements are removed -- 2");
  }

/**
* implemented. <br>
*
*/
  public void test_remove(){
    th.checkPoint("remove(java.lang.Object)boolean");
    Set ac = f.build();
    ac.add("a");      ac.add(NULL);
    ac.add("c");      ac.add("a");
    th.check(ac.size()==3 , "no duplicates at start -- 1");
    th.check(ac.remove("a"), "returns true if removed -- 2");
    th.check(ac.size()==2 , "one element was removed -- 2");
    th.check(!ac.remove("a"), "returns false if not removed -- 3");
    th.check(ac.size()==2 , "no elements were removed -- 3");
    th.check(ac.remove(NULL), "returns true if removed -- 4");
    th.check(ac.size()==1 , "one element was removed -- 4");
    th.check(!ac.remove(NULL), "returns false if not removed -- 5");
    th.check(ac.size()==1 , "no elements were removed -- 5");
    th.check(ac.contains("c"), "\"c\" is left");
    th.check(ac.remove("c"), "returns true if removed -- 6");
    th.check(ac.size()==0 , "no elements were removed -- 6");
  }

/**
* implemented. <br>
*
*/
  public void test_removeAll(){
    th.checkPoint("removeAll(java.util.Collection)boolean");
    Set ac = f.build();
    ac.add("a"); 	ac.add(NULL);
    ac.add("c");      ac.add("a");
    try {
    	ac.removeAll(null);
    	th.fail("should throw a NullPointerException");
    	}
    catch (NullPointerException ne) { th.check(true);}
    Vector v = new Vector();
    v.add("a"); v.add(NULL); v.add("de"); v.add("fdf");
    th.check( ac.removeAll(v) , "should return true");
    th.check( ac.size() == 1 , "duplicate elements are removed");
    th.check(ac.contains("c"), "check if correct elements were removed");
    th.check(! ac.removeAll(v) , "should return false");
    th.check( ac.size() == 1 , "no elements were removed");

  }

/**
* implemented. <br>
*
*/
  public void test_retainAll(){
    th.checkPoint("retainAll(java.util.Collection)boolean");
    Set ac = f.build();
    ac.add("a"); 	ac.add(NULL);
    ac.add("c");      ac.add("a");
    th.check( ac.size() == 3 , "duplicate elements are not present");
    try {
    	ac.retainAll(null);
    	th.fail("should throw a NullPointerException");
    	}
    catch (NullPointerException ne) { th.check(true);}
    Vector v = new Vector();
    v.add("a"); v.add(NULL); v.add("de"); v.add("fdf");
    th.check( ac.retainAll(v) , "should return true");
    th.check( ac.size() == 2 , "proper elements are retained");
    th.check(! ac.retainAll(v) , "should return false");
    th.check( ac.size() == 2 , "all elements were retained");
    th.check( ac.contains(NULL) && ac.contains("a"));
  }

/**
* implemented. <br>
*
*/
  public void test_contains(){
    th.checkPoint("contains(java.lang.Object)boolean");
    Set ac = f.build();
    ac.add("a"); 	ac.add(NULL);
    ac.add("c");      ac.add("a");
    th.check(ac.contains("a") , "true -- 1");
    th.check(ac.contains(NULL) , "true -- 2");
    th.check(ac.contains("c") , "true -- 3");
    th.check(!ac.contains("ab") , "false -- 4");
    th.check(!ac.contains("b") , "false -- 5");
    ac.remove(NULL);
    th.check(!ac.contains(NULL) , "false -- 4");
	
  }

/**
* implemented. <br>
*
*/
  public void test_containsAll(){
    th.checkPoint("containsAll(java.util.Collection)boolean");
    Set ac = f.build();
    ac.add("a"); 	ac.add(NULL);
    ac.add("c");      ac.add("a");
    try {
    	ac.containsAll(null);
    	th.fail("should throw a NullPointerException");
    	}
    catch (NullPointerException ne) { th.check(true);}
    Vector v = new Vector();
    th.check( ac.containsAll(v) , "should return true -- 1");
    v.add("a"); v.add(NULL); v.add("a"); v.add(NULL); v.add("a");
    th.check( ac.containsAll(v) , "should return true -- 2");
    v.add("c");
    th.check( ac.containsAll(v) , "should return true -- 3");
    v.add("c+");
    th.check(! ac.containsAll(v) , "should return false -- 4");
    v.clear();
    ac.clear();
    th.check( ac.containsAll(v) , "should return true -- 5");

  }

/**
* implemented. <br>
*
*/
  public void test_isEmpty(){
    th.checkPoint("isEmpty()boolean");
    Set ac = f.build();
    th.check(ac.isEmpty() , "should return true -- 1");
    th.check(ac.isEmpty() , "should return true -- 2");
    ac.add(NULL);
    th.check(!ac.isEmpty() , "should return false -- 3");
    ac.clear();
    th.check(ac.isEmpty() , "should return true -- 4");

  }

/**
*   not implemented. <br>
*   Abstract Method
*/
  public void test_size(){
    th.checkPoint("()");
  }
/**
*   not implemented. <br>
*   Abstract Method
*/
  public void test_iterator(){
    th.checkPoint("()");
  }

/**
* implemented. <br>
*
*/
  public void test_toArray(){
   th.checkPoint("toArray()[java.lang.Object");
    Set ac = f.build();
    Object [] oa = ac.toArray();
    th.check( oa != null , "returning null is not allowed");
    if (oa != null) th.check(oa.length == 0 , "empty array");
    ac.add("a"); 	ac.add(NULL);
    ac.add("c");      ac.add("a");
    th.check( ac.size()==3 , "duplicate adds are ignored");
    oa = ac.toArray();
    th.check(oa.length==3, "output array is same size as set");
    th.check(Arrays.asList(oa).contains("a"), "checking elements: a");
    th.check(Arrays.asList(oa).contains("c"), "checking elements: c");
    th.check(Arrays.asList(oa).contains(NULL), "checking elements: null");

   th.checkPoint("toArray([java.lang.Object)[java.lang.Object");
    try {
    	ac.toArray((Object[]) null);
    	th.fail("should throw a NullPointerException");
    	}
    catch (NullPointerException ne) { th.check(true);}
    String [] sa = new String[4];
    for (int i = 0 ; i < sa.length ; i++ ){ sa[i] ="ok"; }
    oa = ac.toArray(sa);
    th.check(oa.length>=3, "output array is as least as large as set");
    th.check(Arrays.asList(oa).contains("a"), "checking elements: a");
    th.check(Arrays.asList(oa).contains("c"), "checking elements: c");
    th.check(Arrays.asList(oa).contains(NULL), "checking elements: null");
    th.check(!Arrays.asList(oa).contains("ok"), "checking elements: not 'ok'");
    th.check(oa == sa , "array large enough --> fill + return it");
    th.check(sa[3] == null ,  "element at 'size' is set to null");

    sa = new String[2];
    for (int i = 0 ; i < sa.length ; i++ ){ sa[i] ="ok"; }
    oa = ac.toArray(sa);
    th.check(oa.length>=3, "output array is as least as large as set");
    th.check(Arrays.asList(oa).contains("a"), "checking elements: a");
    th.check(Arrays.asList(oa).contains("c"), "checking elements: c");
    th.check(Arrays.asList(oa).contains(NULL), "checking elements: null");
    th.check ( oa instanceof String[] , "checking  class type of returnvalue");
    sa = new String[3];
    Class asc = sa.getClass();
    for (int i = 0 ; i < sa.length ; i++ ){ sa[i] ="ok"; }
    oa = ac.toArray(sa);
    th.check(oa.length>=3, "output array is as least as large as set");
    th.check(Arrays.asList(oa).contains("a"), "checking elements: a");
    th.check(Arrays.asList(oa).contains("c"), "checking elements: c");
    th.check(Arrays.asList(oa).contains(NULL), "checking elements: null");
    th.check ( oa instanceof String[] , "checking  class type of returnvalue");
    th.check(oa == sa , "array large enough --> fill + return it");
  }
/**
* implemented. <br>
*
*/
  public void test_toString(){
    th.checkPoint("toString()java.lang.String");
    Set ac = f.build();
    ac.add("smartmove"); 	ac.add(NULL);
    ac.add("rules");      	ac.add("cars");
    String s = ac.toString();
    th.check( s.indexOf("smartmove") != -1 , "checking representations");
    th.check( s.indexOf("rules") != -1 , "checking representations");
    th.check( s.indexOf("cars") != -1 , "checking representations");
    th.check( s.indexOf("null") != -1 , "checking representations");
    th.debug(s);
  }
}
