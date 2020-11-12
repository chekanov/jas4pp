/*
 * DIFRowException.java
 *
 * Created on January 28, 2008, 8:48 PM
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
public class DIFRowException extends DIFException {
    
    /**
     * Creates a new instance of <code>DIFRowException</code> without detail message.
     */
    public DIFRowException(DIFSheet sheet, int rowNumber) {
        super();
        set(sheet, rowNumber);
    }
    
    
    /**
     * Constructs an instance of <code>DIFRowException</code> with the specified detail message.
     * 
     * @param msg the detail message.
     */
    public DIFRowException(String msg, DIFSheet sheet, int rowNumber) {
        super(msg);
        set(sheet, rowNumber);
    }
    
    
    /**
     * Constructs an instance of <code>DIFRowException</code> with the specified root cause.
     * 
     * @param cause the root cause of the exception.
     */
    public DIFRowException(Throwable cause, DIFSheet sheet, int rowNumber) {
        super(cause);
        set(sheet, rowNumber);
    }
    
    
    /**
     * Constructs an instance of <code>DIFRowException</code> with the specified detail message and root cause.
     * 
     * @param msg the detail message.
     * @param cause the root cause of the exception.
     */
    public DIFRowException(String msg, Throwable cause, DIFSheet sheet, int rowNumber) {
        super(msg, cause);
        set(sheet, rowNumber);
    }
    
    private void set(DIFSheet sheet, int rowNumber) {
        this.sheet = sheet;
        this.rowNumber = rowNumber;
    }

    /**
     * Holds value of property sheet.
     */
    private DIFSheet sheet;

    /**
     * Getter for property sheet.
     * @return Value of property sheet.
     */
    public DIFSheet getSheet() {
        return this.sheet;
    }

    /**
     * Holds value of property rowNumber.
     */
    private int rowNumber;

    /**
     * Getter for property rowNumber.
     * @return Value of property rowNumber.
     */
    public int getRowNumber() {
        return this.rowNumber;
    }
}
