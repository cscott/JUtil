// Util.java, created Mon Aug  3  2:42:35 1998 by cananian
// Copyright (C) 1998 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

/** 
 * Miscellaneous static utility functions.
 * @author  C. Scott Ananian &lt;cananian@alumni.princeton.edu&gt;
 */
public abstract class Util {
  /** Repeat a given string a certain number of times.
   *  @return a string consisting of <code>s</code> repeated <code>n</code>
   *          times.
   *  @deprecated Use {@link String#repeat(int)}. */
  @Deprecated(since="2.0")
  public static final String repeatString(String s, int n) {
    assert n>=0;
    return s.repeat(n);
  }

  /** Find first set (least significant bit).
   *  @return the first bit set in the argument.  
   *          <code>ffs(0)==0</code> and <code>ffs(1)==1</code>. */
  public static final int ffs(int v) {
    return (v==0) ? 0 : Integer.numberOfTrailingZeros(v) + 1;
  }
  /** Find first set (least significant bit).
   *  @return the first bit set in the argument.  
   *          <code>ffs(0)==0</code> and <code>ffs(1)==1</code>. */
  public static final int ffs(long v) {
    return (v==0) ? 0 : Long.numberOfTrailingZeros(v) + 1;
  }
  /** Find last set (most significant bit).
   *  @return the last bit set in the argument.
   *          <code>fls(0)==0</code> and <code>fls(1)==1</code>. */
  public static final int fls(int v) {
    return Integer.SIZE - Integer.numberOfLeadingZeros(v);
  }
  /** Find last set (most significant bit).
   *  @return the last bit set in the argument.
   *          <code>fls(0)==0</code> and <code>fls(1)==1</code>. */
  public static final int fls(long v) {
    return Long.SIZE - Long.numberOfLeadingZeros(v);
  }
  /** Returns ceil(log2(n)) */
  public static final int log2c(int v) {
    return (v==0)?-1:fls(v-1);
  }
  /** Return the number of zeros in the binary representation of the
   *  value of the argument.
   *  @deprecated Use {@link Integer#bitCount(int) Integer.bitCount(~v)}. */
  @Deprecated(since="2.0")
  public static final int zerocount(int v) {
    return Integer.bitCount(~v);
  }
  /** Return the number of zeros in the binary representation of the
   *  value of the argument.
   *  @deprecated Use {@link Long#bitCount(long) Long.bitCount(~v)}. */
  @Deprecated(since="2.0")
  public static final int zerocount(long v) {
    return Long.bitCount(~v);
  }
  /** Return the number of ones in the binary representation of the
   *  value of the argument.
   *  @deprecated Use {@link Integer#bitCount(int)}. */
  @Deprecated(since="2.0")
  public static final int popcount(int v) {
    return Integer.bitCount(v);
  }
  /** Return the number of ones in the binary representation of the
   *  value of the argument.
   *  @deprecated Use {@link Long#bitCount(long)}. */
  @Deprecated(since="2.0")
  public static final int popcount(long v) {
    return Long.bitCount(v);
  }
  // Use the 'binary Euclidean algorithm' to compute gcd.
  /** Returns the greatest common divisor of a pair of numbers. */
  public static final long gcd(long u, long v) { // long version.
    assert u>0 && v>0;
    int u2s = ffs(u)-1, v2s = ffs(v)-1;
    u>>=u2s; v>>=v2s; // cast out twos.
    // binary gcd algorithm; u and v must be odd at this point.
    while (u != v) {
      while (u<v) {
	v-=u;
	v>>=(ffs(v)-1);
      }
      long t=u; u=v; v=t;
    }
    // u,v have gcd
    return u << Math.min(u2s,v2s); // restore cast out twos.
  }
  /** Returns the greatest common divisor of a pair of numbers. */
  public static final int gcd(int u, int v) { // integer version.
    assert u>0 && v>0;
    int u2s = ffs(u)-1, v2s = ffs(v)-1;
    u>>=u2s; v>>=v2s; // cast out twos.
    // binary gcd algorithm; u and v must be odd at this point.
    while (u != v) {
      while (u<v) {
	v-=u;
	v>>=(ffs(v)-1);
      }
      int t=u; u=v; v=t;
    }
    // u,v have gcd
    return u << Math.min(u2s,v2s); // restore cast out twos.
  }
}

// set emacs indentation style.
// Local Variables:
// c-basic-offset:2
// End:
