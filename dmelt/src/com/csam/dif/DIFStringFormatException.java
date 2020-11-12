/*
 * DIFStringFormatException.java
 *
 * Created on January 28, 2008, 10:13 PM
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
public class DIFStringFormatException extends DIFFormatException {
    
    /**
     * Creates a new instance of <code>DIFStringFormatException</code> without detail message.
     */
    public DIFStringFormatException() {
    }
    
    
    /**
     * Constructs an instance of <code>DIFStringFormatException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DIFStringFormatException(String msg) {
        super(msg);
    }
    
    
    /**
     * Constructs an instance of <code>DIFStringFormatException</code> with the specified root cause.
     * @param cause the root cause of the exception.
     */
    public DIFStringFormatException(Throwable cause) {
        super(cause);
    }
    
    
    /**
     * Constructs an instance of <code>DIFStringFormatException</code> with the specified detail message and root cause.
     * @param msg the detail message.
     * @param cause the root cause of the exception.
     */
    public DIFStringFormatException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
