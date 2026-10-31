// UtilTest.java, created Sat Oct 31 2026 by cananian
// Copyright (C) 2026 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import org.junit.jupiter.api.Test;

class UtilTest {
    /** Edge cases, then random values of every bit length. */
    static long[] samples() {
	Random r = new Random(42);
	LongStream edges = LongStream.of(0, 1, 2, 3, -1, -2, 5,
					 Integer.MAX_VALUE, Integer.MIN_VALUE,
					 Long.MAX_VALUE, Long.MIN_VALUE);
	LongStream powers = IntStream.range(0, 64).mapToLong(i -> 1L << i);
	LongStream random = IntStream.range(0, 64)
	    .mapToLong(i -> r.nextLong() >>> i);
	return LongStream.concat(edges, LongStream.concat(powers, random))
	    .toArray();
    }
    // the definitions, bit by bit.
    static int ffs(long v, int size) {
	for (int i = 0; i < size; i++)
	    if ((v & (1L << i)) != 0) return i + 1;
	return 0;
    }
    static int fls(long v, int size) {
	for (int i = size; i-- > 0; )
	    if ((v & (1L << i)) != 0) return i + 1;
	return 0;
    }

    @Test void ffsAndFls() {
	assertEquals(0, Util.ffs(0));
	assertEquals(0, Util.ffs(0L));
	assertEquals(0, Util.fls(0));
	assertEquals(0, Util.fls(0L));
	for (long v : samples()) {
	    int i = (int) v;
	    assertEquals(ffs(i, 32), Util.ffs(i), "ffs(" + i + ")");
	    assertEquals(fls(i, 32), Util.fls(i), "fls(" + i + ")");
	    assertEquals(ffs(v, 64), Util.ffs(v), "ffs(" + v + "L)");
	    assertEquals(fls(v, 64), Util.fls(v), "fls(" + v + "L)");
	}
    }

    @Test void log2c() {
	assertEquals(-1, Util.log2c(0));
	assertEquals(0, Util.log2c(1));
	assertEquals(1, Util.log2c(2));
	assertEquals(2, Util.log2c(3));
	assertEquals(2, Util.log2c(4));
	assertEquals(3, Util.log2c(5));
	assertEquals(30, Util.log2c(1 << 30));
	assertEquals(31, Util.log2c((1 << 30) + 1));
    }

    @Test void gcd() {
	Random r = new Random(42);
	for (int n = 0; n < 1000; n++) {
	    long u = 1 + (r.nextLong() >>> (1 + r.nextInt(63)));
	    long v = 1 + (r.nextLong() >>> (1 + r.nextInt(63)));
	    long g = BigInteger.valueOf(u).gcd(BigInteger.valueOf(v))
		.longValueExact();
	    assertEquals(g, Util.gcd(u, v), "gcd(" + u + "," + v + ")");
	    int ui = (int) Math.min(u, Integer.MAX_VALUE);
	    int vi = 1 + r.nextInt(1 << r.nextInt(31));
	    assertEquals(BigInteger.valueOf(ui).gcd(BigInteger.valueOf(vi))
			 .intValueExact(), Util.gcd(ui, vi),
			 "gcd(" + ui + "," + vi + ")");
	}
	assertEquals(12, Util.gcd(36, 48));
	assertEquals(1, Util.gcd(1L, Long.MAX_VALUE));
    }

    @SuppressWarnings("deprecation") // the deprecated methods still work
    @Test void deprecated() {
	assertEquals("", Util.repeatString("ab", 0));
	assertEquals("ababab", Util.repeatString("ab", 3));
	for (long v : samples()) {
	    int i = (int) v;
	    assertEquals(Integer.bitCount(i), Util.popcount(i));
	    assertEquals(32 - Integer.bitCount(i), Util.zerocount(i));
	    assertEquals(Long.bitCount(v), Util.popcount(v));
	    assertEquals(64 - Long.bitCount(v), Util.zerocount(v));
	}
    }
}
