/*
 * DIFException.java
 *
 * Created on January 28, 2008, 8:47 PM
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
 * This is the top-level exception which all other exception inherit
 *
 * @author Nathan Crause
 */
public class DIFException extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>DIFException</code> without detail message.
     */
    public DIFException() {
    }
    
    
    /**
     * Constructs an instance of <code>DIFException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DIFException(String msg) {
        super(msg);
    }
    
    
    /**
     * Constructs an instance of <code>DIFException</code> with the specified root cause.
     * @param cause the root cause of the exception.
     */
    public DIFException(Throwable cause) {
        super(cause);
    }
    
    
    /**
     * Constructs an instance of <code>DIFException</code> with the specified detail message and root cause.
     * @param msg the detail message.
     * @param cause the root cause of the exception.
     */
    public DIFException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
