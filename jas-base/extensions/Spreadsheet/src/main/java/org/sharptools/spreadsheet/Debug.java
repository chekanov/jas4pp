package org.sharptools.spreadsheet;
/*
 * This is a class used for print out debug information and can be
 * easily turned on/off.
 */
import java.io.*;

class Debug {

    static private boolean debug = false;
    
    static void setDebug(boolean flag) {
	debug = flag;
    }
    
    static boolean isDebug() {
	return debug;
    }
    
    static void println(Object s) {
	if (debug)
	    System.out.println(s.toString());
    }        
}
