/*
 * DIFDataPairParsingException.java
 *
 * Created on January 28, 2008, 9:57 PM
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
public class DIFDataPairParsingException extends DIFFormatException {
    
    /**
     * Creates a new instance of <code>DIFDataPairParsingException</code> without detail message.
     */
    public DIFDataPairParsingException(String line) {
        super();
        this.line = line;
    }
    
    
    /**
     * Constructs an instance of <code>DIFDataPairParsingException</code> with the specified detail message.
     * 
     * 
     * @param msg the detail message.
     */
    public DIFDataPairParsingException(String msg, String line) {
        super(msg);
        this.line = line;
    }
    
    
    /**
     * Constructs an instance of <code>DIFDataPairParsingException</code> with the specified root cause.
     * 
     * 
     * @param cause the root cause of the exception.
     */
    public DIFDataPairParsingException(Throwable cause, String line) {
        super(cause);
        this.line = line;
    }
    
    
    /**
     * Constructs an instance of <code>DIFDataPairParsingException</code> with the specified detail message and root cause.
     * 
     * 
     * @param msg the detail message.
     * @param cause the root cause of the exception.
     */
    public DIFDataPairParsingException(String msg, Throwable cause, String line) {
        super(msg, cause);
        this.line = line;
    }

    /**
     * Holds value of property line.
     */
    private String line;

    /**
     * Getter for property line.
     * @return Value of property line.
     */
    public String getLine() {
        return this.line;
    }
}
