// -*-java-*-
package net.cscott.jutil;

/**
 * The <code>Version</code> object contains fields naming the current version
 * (@PACKAGE_VERSION@) of the @PACKAGE_NAME@ library.
 * @version $Id: Version.java,v 1.3 2006-10-29 16:51:43 cananian Exp $
 */
public abstract class Version {
    /** The name of the package: "JUtil". */
    public static final String PACKAGE_NAME = "JUtil";
    /** The version of the package: "1.3". */
    public static final String PACKAGE_VERSION = "1.3";
    /** The package name and version as one string. */
    public static final String PACKAGE_STRING=PACKAGE_NAME+" "+PACKAGE_VERSION;
    /** The address to which bug reports should be sent. */
    public static final String PACKAGE_BUGREPORT = "jutil@cscott.net";

    /** Prints the package version if invoked. */
    public static void main(String[] args) {
	System.out.println(PACKAGE_STRING);
	System.out.println("Bug reports to "+PACKAGE_BUGREPORT);
    }
}
