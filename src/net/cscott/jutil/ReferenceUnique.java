// ReferenceUnique.java, created Sun Sep 12 18:11:40 1999 by cananian
// Copyright (C) 1999 C. Scott Ananian <cananian@alumni.princeton.edu>
// Licensed under the terms of the GNU GPL; see COPYING for details.
package net.cscott.jutil;

/**
 * {@link ReferenceUnique} is a property indicating that, for
 * all instances of a class, <code>(a==b)==(a.equals(b))</code>.
 * That is, two equal objects are always reference equal.
 * <p>
 * Tagging classes with {@link ReferenceUnique} allows
 * automated checkers to more accurately discrimate legitimate
 * uses of <code>==</code> on objects from unsafe uses.
 * 
 * @author  C. Scott Ananian <cananian@alumni.princeton.edu>
 * @version $Id: ReferenceUnique.java,v 1.2 2006-10-30 19:58:06 cananian Exp $
 */
public interface ReferenceUnique { }
