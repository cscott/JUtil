# Process this file with autoconf to produce a configure script.
AC_INIT(JUtil, 1.0, cscott@cscott.net)
AC_COPYRIGHT(Copyright (c) 2003 C. Scott Ananian)
AC_REVISION($Revision: 1.2 $)
AC_CONFIG_SRCDIR(configure.ac) # double check --srcdir validity.
AC_CONFIG_AUX_DIR(scripts)
AM_INIT_AUTOMAKE

# Checks for programs.
### only some versions of jikes are okay.
AC_CHECK_PROGS(JAR, fastjar jar, not-found)
# find a version of javac that compiles generic java.
AC_MSG_CHECKING([for a jsr-14-compliant java 1.4 compiler])
JAVAC=not-found
CLS=f$$
echo "abstract class $CLS extends java.util.AbstractList<Integer> { }" \
     > /tmp/$CLS.java
for jc in jikes javac ${HOME}/jsr14_adding_generics-1_{3,2,0}-ea/scripts/javac ; do
    if $jc -d /tmp -source 1.5 /tmp/$CLS.java 1>/dev/null 2>/dev/null; then
      JAVAC="$jc -source 1.5"
      break
    fi
    if $jc -d /tmp -source 1.4 -gj /tmp/$CLS.java 1>/dev/null 2>/dev/null; then
      JAVAC="$jc -source 1.4 -gj"
      break
    fi
done
/bin/rm -f /tmp/$CLS.java /tmp/$CLS.class
AC_MSG_RESULT($JAVAC)
AC_SUBST(JAVAC)
if test "$JAVAC" = "not-found" -o "$JAR" = "not-found" ; then
   AC_MSG_ERROR([Can't find tools to build java programs.])
fi

# Checks for libraries.

# Checks for header files.

# Checks for typedefs, structures, and compiler characteristics.

# Checks for library functions.

AC_CONFIG_FILES([Makefile src/Makefile src/Version.java])
AC_OUTPUT
