// -*-java-*-
package net.cscott.jutil;

/**
 * The {@link Version} object contains fields naming the current version
 * of the library.
 * @version $Id: Version.java,v 1.4 2006-10-30 19:12:05 cananian Exp $
 */
public abstract class Version {
    /** The name of the package: "JUtil". */
    public static final String PACKAGE_NAME = "JUtil";
    /** The version of the package: "1.3". */
    public static final String PACKAGE_VERSION = "1.3";
    /** The package name and version as one string. */
    public static final String PACKAGE_STRING=PACKAGE_NAME+" "+PACKAGE_VERSION;
    /** The address to which bug reports should be sent. */
    public static final String PACKAGE_BUGREPORT =
        new String("jutil@cscott.net").intern(); // hide from the web.

    /** Prints the package version if invoked. */
    public static void main(String[] args) {
	System.out.println(PACKAGE_STRING);
	System.out.println("Bug reports to "+PACKAGE_BUGREPORT);
    }
}
