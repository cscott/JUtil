// Util.java, created Mon Aug  3  2:42:35 1998 by cananian
// Copyright (C) 1998 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

/** 
 * Miscellaneous static utility functions.
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: Util.java,v 1.2 2006-10-29 16:27:20 cananian Exp $
 */
public abstract class Util {
  /** Repeat a given string a certain number of times.
   *  @return a string consisting of <code>s</code> repeated <code>n</code>
   *          times. */
  public static final String repeatString(String s, int n) {
    assert n>=0;
    StringBuffer sb = new StringBuffer();
    for (int bit=fls(n)-1; bit>=0; bit--) {
      sb = sb.append(sb.toString());
      if ( (n & (1<<bit)) != 0)
	sb = sb.append(s);
    }
    return sb.toString();
  }
  /** Highest bit set in a byte. */
  static final byte bytemsb[] = {
    0, 1, 2, 2, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5,
    5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
    6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7,
    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
    7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7,
    7, 7, 7, 7, 7, 7, 7, 7, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
    8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
    8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
    8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
    8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8,
    8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8 /* 256 */
  };
  static { assert bytemsb.length==0x100; }
  /** Lowest bit set in a byte. */
  static final byte bytelsb[] = {
    0, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1, 5, 1, 2, 1, 3, 1, 2, 1,
    4, 1, 2, 1, 3, 1, 2, 1, 6, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,
    5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1, 7, 1, 2, 1, 3, 1, 2, 1,
    4, 1, 2, 1, 3, 1, 2, 1, 5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,
    6, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1, 5, 1, 2, 1, 3, 1, 2, 1,
    4, 1, 2, 1, 3, 1, 2, 1, 8, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,
    5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1, 6, 1, 2, 1, 3, 1, 2, 1,
    4, 1, 2, 1, 3, 1, 2, 1, 5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,
    7, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1, 5, 1, 2, 1, 3, 1, 2, 1,
    4, 1, 2, 1, 3, 1, 2, 1, 6, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1,
    5, 1, 2, 1, 3, 1, 2, 1, 4, 1, 2, 1, 3, 1, 2, 1  };
  static { assert bytelsb.length==0x100; }
  /** Number of zeros in a byte. */
  static final byte bytezeros[] = {
    8, 7, 7, 6, 7, 6, 6, 5, 7, 6, 6, 5, 6, 5, 5, 4, 7, 6, 6, 5, 6, 5, 5, 4,
    6, 5, 5, 4, 5, 4, 4, 3, 7, 6, 6, 5, 6, 5, 5, 4, 6, 5, 5, 4, 5, 4, 4, 3,
    6, 5, 5, 4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 3, 3, 2, 7, 6, 6, 5, 6, 5, 5, 4,
    6, 5, 5, 4, 5, 4, 4, 3, 6, 5, 5, 4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 3, 3, 2,
    6, 5, 5, 4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 3, 3, 2, 5, 4, 4, 3, 4, 3, 3, 2,
    4, 3, 3, 2, 3, 2, 2, 1, 7, 6, 6, 5, 6, 5, 5, 4, 6, 5, 5, 4, 5, 4, 4, 3,
    6, 5, 5, 4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 3, 3, 2, 6, 5, 5, 4, 5, 4, 4, 3,
    5, 4, 4, 3, 4, 3, 3, 2, 5, 4, 4, 3, 4, 3, 3, 2, 4, 3, 3, 2, 3, 2, 2, 1,
    6, 5, 5, 4, 5, 4, 4, 3, 5, 4, 4, 3, 4, 3, 3, 2, 5, 4, 4, 3, 4, 3, 3, 2,
    4, 3, 3, 2, 3, 2, 2, 1, 5, 4, 4, 3, 4, 3, 3, 2, 4, 3, 3, 2, 3, 2, 2, 1,
    4, 3, 3, 2, 3, 2, 2, 1, 3, 2, 2, 1, 2, 1, 1, 0
  };
  static { assert bytezeros.length==0x100; }
  
  /** Find first set (least significant bit).
   *  @return the first bit set in the argument.  
   *          <code>ffs(0)==0</code> and <code>ffs(1)==1</code>. */
  public static final int ffs(int v) {
    if ( (v & 0x0000FFFF) != 0)
      if ( (v & 0x000000FF) != 0)
        return bytelsb[v&0xFF];
      else
        return 8 + bytelsb[(v>>8)&0xFF];
    else
      if ( (v & 0x00FFFFFF) != 0)
        return 16 + bytelsb[(v>>16)&0xFF];
      else
        return 24 + bytelsb[(v>>24)&0xFF];
  }
  /** Find first set (least significant bit).
   *  @return the first bit set in the argument.  
   *          <code>ffs(0)==0</code> and <code>ffs(1)==1</code>. */
  public static final int ffs(long v) {
    if ( (v & 0xFFFFFFFFL) != 0) 
      return ffs( (int) (v & 0xFFFFFFFFL) );
    else
      return 32 + ffs( (int) ( v >> 32 ) );
  }
  /** Find last set (most significant bit).
   *  @return the last bit set in the argument.
   *          <code>fls(0)==0</code> and <code>fls(1)==1</code>. */
  public static final int fls(int v) {
    if ( (v & 0xFFFF0000) != 0)
      if ( (v & 0xFF000000) != 0)
        return 24 + bytemsb[(v>>24) & 0xFF];
      else
  	  return 16 + bytemsb[v>>16];
    if ( (v & 0x0000FF00) != 0)
      return 8 + bytemsb[v>>8];
    else
      return bytemsb[v];
  }
  /** Find last set (most significant bit).
   *  @return the last bit set in the argument.
   *          <code>fls(0)==0</code> and <code>fls(1)==1</code>. */
  public static final int fls(long v) {
    if ((v & 0xFFFFFFFF00000000L) != 0)
      return 32 + fls( (int) (v>>32) );
    else
      return fls( (int) v );
  }
  /** Returns ceil(log2(n)) */
  public static final int log2c(int v) {
    return (v==0)?-1:fls(v-1);
  }
  /** Return the number of zeros in the binary representation of the
   *  value of the argument. */
  public static final int zerocount(int v) {
    return bytezeros[v&0xFF] + bytezeros[(v>>8)&0xFF] +
      bytezeros[(v>>16)&0xFF] + bytezeros[(v>>24)&0xFF];
  }
  /** Return the number of zeros in the binary representation of the
   *  value of the argument. */
  public static final int zerocount(long v) {
    return zerocount((int)v)+zerocount((int)(v>>32));
  }
  /** Return the number of ones in the binary representation of the
   *  value of the argument. */
  public static final int popcount(int v) {
    return 32-zerocount(v);
  }
  /** Return the number of ones in the binary representation of the
   *  value of the argument. */
  public static final int popcount(long v) {
    return 64-zerocount(v);
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
