package org.sharptools.spreadsheet;

/*
 * @(#)ParserException.java
 * 
 * $Id: ParserException.java 13895 2011-10-01 00:11:54Z tonyj $
 * 
 * Created on October 28, 2000, 6:26 PM
 */

/**
 * This Exception is raised when Formula fails in tokenizing or parsing the
 * formula.
 *
 * @author Hua Zhong <huaz@cs.columbia.edu>
 * @version $Revision: 13895 $
 */
class ParserException extends Exception {
    private boolean quiet;
    private String msg;

    /**
     * Contructor for ParserException.  By default, sets quiet to true.
     */
    public ParserException() { quiet = true; };

    /**
     * @param msg the error message string 
     */
    public ParserException(String msg) { super(msg); this.msg = msg; };

    /**
     * @param msg the error object
     */
    public ParserException(Object msg) {
	super(msg.toString());
	this.msg = msg.toString();
    };

    /**
     * This returns the value of quiet.
     *
     * @return true if quiet is true, false otherwise
     */
    public boolean isQuiet() { return quiet; }

    /**
     * toString method for ParserException.
     *
     * @return the error message string
     */
    public String toString() { return msg; }
}










