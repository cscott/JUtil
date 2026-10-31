// BitStringTest.java, created from the self-test in BitString.
// Copyright (C) 1998 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BitStringTest {
    @Test void empty() {
	BitString bs = new BitString(100);
	assertEquals(0, bs.length());
	assertEquals(-1, bs.firstSet());
	assertEquals(-1, bs.lastSet());
	assertEquals(-1, bs.firstSet(100));
	assertEquals(-1, bs.firstSet(-100));
	assertEquals(-1, bs.lastSet(100));
	assertEquals(-1, bs.lastSet(-100));
	assertTrue(bs.size() >= bs.length());
	assertEquals("{}", bs.toString());
    }

    @Test void firstAndLastSet() {
	BitString bs = new BitString(100);
	bs.set(52); bs.set(53); bs.set(76); bs.set(77);
	// get()
	assertTrue(bs.get(52) && bs.get(53) && bs.get(76) && bs.get(77));
	assertFalse(bs.get(51) || bs.get(54) || bs.get(75) || bs.get(78));
	// length()
	assertEquals(78, bs.length());
	assertTrue(bs.size() >= bs.length());
	// firstSet()
	assertEquals(bs.firstSet(-100), bs.firstSet());
	assertEquals(52, bs.firstSet(-1));
	assertEquals(53, bs.firstSet(52));
	assertEquals(76, bs.firstSet(53));
	assertEquals(77, bs.firstSet(76));
	assertEquals(-1, bs.firstSet(77));
	assertEquals(-1, bs.firstSet(1000));
	// lastSet()
	assertEquals(bs.lastSet(99), bs.lastSet());
	assertEquals(77, bs.lastSet(99));
	assertEquals(76, bs.lastSet(77));
	assertEquals(53, bs.lastSet(76));
	assertEquals(52, bs.lastSet(53));
	assertEquals(-1, bs.lastSet(52));
	assertEquals(-1, bs.lastSet(-100));
	// toString()
	assertEquals("{52, 53, 76, 77}", bs.toString());
    }
}
