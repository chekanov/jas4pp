/*
 * DIFNumberPairInfoException.java
 *
 * Created on January 28, 2008, 10:03 PM
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
public class DIFNumberPairInfoException extends DIFFormatException {
    
    /**
     * Creates a new instance of <code>DIFNumberPairInfoException</code> without detail message.
     */
    public DIFNumberPairInfoException(int expectedVector, int expectedTuple, int actualVector, int actualTuple) {
        super();
        set(expectedVector, expectedTuple, actualVector, actualTuple);
    }
    
    
    /**
     * Constructs an instance of <code>DIFNumberPairInfoException</code> with the specified detail message.
     * 
     * @param msg the detail message.
     */
    public DIFNumberPairInfoException(String msg, int expectedVector, int expectedTuple, int actualVector, int actualTuple) {
        super(msg);
        set(expectedVector, expectedTuple, actualVector, actualTuple);
    }
    
    
    /**
     * Constructs an instance of <code>DIFNumberPairInfoException</code> with the specified root cause.
     * 
     * @param cause the root cause of the exception.
     */
    public DIFNumberPairInfoException(Throwable cause, int expectedVector, int expectedTuple, int actualVector, int actualTuple) {
        super(cause);
        set(expectedVector, expectedTuple, actualVector, actualTuple);
    }
    
    
    /**
     * Constructs an instance of <code>DIFNumberPairInfoException</code> with the specified detail message and root cause.
     * 
     * @param msg the detail message.
     * @param cause the root cause of the exception.
     */
    public DIFNumberPairInfoException(String msg, Throwable cause, int expectedVector, int expectedTuple, int actualVector, int actualTuple) {
        super(msg, cause);
        set(expectedVector, expectedTuple, actualVector, actualTuple);
    }
    
    private void set(int expectedVector, int expectedTuple, int actualVector, int actualTuple) {
        this.expectedVector = expectedVector;
        this.expectedTuple = expectedTuple;
        this.actualVector = actualVector;
        this.actualTuple = actualTuple;
    }

    /**
     * Holds value of property expectedVector.
     */
    private int expectedVector;

    /**
     * Getter for property expectedVector.
     * @return Value of property expectedVector.
     */
    public int getExpectedVector() {
        return this.expectedVector;
    }

    /**
     * Holds value of property actualVector.
     */
    private int actualVector;

    /**
     * Getter for property actualVector.
     * @return Value of property actualVector.
     */
    public int getActualVector() {
        return this.actualVector;
    }

    /**
     * Holds value of property expectedTuple.
     */
    private int expectedTuple;

    /**
     * Getter for property expectedTuple.
     * @return Value of property expectedTuple.
     */
    public int getExpectedTuple() {
        return this.expectedTuple;
    }

    /**
     * Holds value of property actualTuple.
     */
    private int actualTuple;

    /**
     * Getter for property actualTuple.
     * @return Value of property actualTuple.
     */
    public int getActualTuple() {
        return this.actualTuple;
    }
}
