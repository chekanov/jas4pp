/*
 * DIFKeywordException.java
 *
 * Created on January 28, 2008, 9:49 PM
 *
 * Copyright 2008 Clarke, Solomou & Associates Microsystems Ltd.
 *
 * This file is part of DIF
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.csam.dif;

/**
 *
 * @author Nathan Crause
 */
public class DIFKeywordException extends DIFFormatException {
    
    /**
     * Creates a new instance of <code>DIFKeywordException</code> without detail message.
     */
    public DIFKeywordException(String expected, String actual) {
        super();
        set(expected, actual);
    }
    
    
    /**
     * Constructs an instance of <code>DIFKeywordException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DIFKeywordException(String msg, String expected, String actual) {
        super(msg);
        set(expected, actual);
    }
    
    
    /**
     * Constructs an instance of <code>DIFKeywordException</code> with the specified root cause.
     * @param cause the root cause of the exception.
     */
    public DIFKeywordException(Throwable cause, String expected, String actual) {
        super(cause);
        set(expected, actual);
    }
    
    
    /**
     * Constructs an instance of <code>DIFKeywordException</code> with the specified detail message and root cause.
     * @param msg the detail message.
     * @param cause the root cause of the exception.
     */
    public DIFKeywordException(String msg, Throwable cause, String expected, String actual) {
        super(msg, cause);
        set(expected, actual);
    }
    
    private void set(String expected, String actual) {
        this.expected = expected;
        this.actual = actual;
    }

    /**
     * Holds value of property expected.
     */
    private String expected;

    /**
     * Getter for property expected.
     * @return Value of property expected.
     */
    public String getExpected() {
        return this.expected;
    }

    /**
     * Holds value of property actual.
     */
    private String actual;

    /**
     * Getter for property actual.
     * @return Value of property actual.
     */
    public String getActual() {
        return this.actual;
    }
}
