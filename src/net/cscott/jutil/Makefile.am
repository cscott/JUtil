## Process this file with automake to produce Makefile.in
MAINTAINERCLEANFILES = Makefile.in
#SUBDIRS = 

AM_JAVACFLAGS = -g -warnunchecked
# JAVAROOT directory must exist!
JAVAROOT=$(top_builddir)/classes

noinst_JAVA = \
Version.java

# automake 1.6.3 is broken: it doesn't actually include java sources
# in the distribution.  The following line works around this.
EXTRA_DIST = $(noinst_JAVA)
