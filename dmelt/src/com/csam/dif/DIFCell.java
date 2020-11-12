/*
 * DIFCell.java
 *
 * Created on January 28, 2008, 8:10 PM
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

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * <p>
 *    This class represents a single data cell in the spreadsheet. It is comprised
 *    primarily of a type marker and a value holder.
 * </p>
 * <p>
 *    Any of the <CODE>setXXX</CODE> methods will also set the cell's type
 *    to more closely represent the data within the cell. However, this dynamic
 *    typing of the data does mean that using <CODE>setCellType</CODE> may lead
 *    to unexpected behavior, as this method makes no attempt at type-casting
 *    of any existing values.
 * </p>
 * @author Nathan Crause
 */
public final class DIFCell {
    
    /**
     * enum of the available cell types supported
     */
    public enum CellType {
        /**
         * "empty" cell type - should typically not be used directly
         */
        BLANK, 
        /**
         * boolean (true/false) cell type
         */
        BOOLEAN, 
        /**
         * numeric cell type
         */
        NUMERIC, 
        /**
         * string cell type
         */
        STRING, 
        /**
         * date (yyyy/mm/dd) cell type (excludes time)
         */
        DATE
    }
    
    private Object value;
    
    private DIFSheet sheet;
    
    private int rowNumber;
    
    private int cellNumber;
    
    private CellType cellType;
    
    /**
     * Creates a new instance of DIFCell, with a default value (based on the type).
     * If the cell type is BLANK or STRING, the default value is an empty string. If
     * the cell type is BOOLEAN, the default value is <CODE>false</CODE>. If the
     * cell type is NUMERIC, the default value is 0 (zero). If the cell type is
     * DATE, the default is the current date.
     * @param sheet The sheet to which this cell belongs.
     * @param rowNumber The row number which contains this cell.
     * @param cellNumber The cell number within the parent row of this cell.
     * @param cellType The type of data stored in this cell.
     */
    protected DIFCell(DIFSheet sheet, int rowNumber, int cellNumber, CellType cellType) {
        this.sheet = sheet;
        this.rowNumber = rowNumber;
        this.cellNumber = cellNumber;
        setCellType(cellType);
        
        if (cellType.equals(CellType.BLANK) || cellType.equals(CellType.STRING))
            value = "";
        else if (cellType.equals(CellType.BOOLEAN))
            value = Boolean.FALSE;
        else if (cellType.equals(CellType.NUMERIC))
            value = BigDecimal.ZERO;
        else if (cellType.equals(CellType.DATE))
            value = new Date(System.currentTimeMillis());
    }
    
    /**
     * Returns the sheet to which this cell belongs.
     * @return sheet to which this cell belongs.
     */
    protected DIFSheet getSheet() {
        return sheet;
    }
    
    /**
     * Sets the current cell type. This method should generally be avoided, as no
     * type-conversion takes place when the cell type is being set. If there is
     * already data stored in the cell, the results of accessing this object will
     * become unreliable.
     * @param cellType <CODE>CellType</CODE> of data in this cell.
     */
    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }
    
    /**
     * Returns an indication of the type of data stored in this cell.
     * @return type of data stored in this cell.
     */
    public CellType getCellType() {
        return cellType;
    }
    
    /**
     * Returns the physical row number within the sheet where this cell exists
     * (see <CODE>DIFRow.getRowNumber()</CODE> for a detailed description of this).
     * @return <CODE>int</CODE> row number
     */
    public int getRowNumber() {
        return rowNumber;
    }
    
    /**
     * Returns the physical cell number within the row where this cell exists. A
     * "physical" number is defined as the visual position of the cell as opposed to
     * a "logical" position. What this means in real-terms is that in any given row,
     * several cells may be "empty". Thus, these cells may not be represented in the
     * row model. If the first cell within a row which contains data is located on
     * the third cell of the spreadsheet, then the "physical number" is 2 (the numbers
     * are zero-based, meaning the first cell is 0, not 1).
     * @return <CODE>int</CODE> cell number
     */
    public int getCellNumber() {
        return cellNumber;
    }
    
    /**
     * Sets this cell's value, and changes the <CODE>CellType</CODE> to <CODE>BOOLEAN</CODE>
     * @param value the boolean value of this cell
     */
    public void setCellValue(boolean value) {
        setCellType(CellType.BOOLEAN);
        this.value = Boolean.valueOf(value);
    }
    
    /**
     * Sets this cell's value, and changes the <CODE>CellType</CODE> to <CODE>NUMERIC</CODE>
     * @param value the numeric value of this cell
     */
    public void setCellValue(BigDecimal value) {
        setCellType(CellType.NUMERIC);
        this.value = value;
    }
    
    /**
     * Identical to <CODE>setCellValue(BigDecimal)</CODE>, except using a native
     * data type of <CODE>double</CODE>.
     * @param value the numeric value of this cell
     */
    public void setCellValue(double value) {
        setCellValue(BigDecimal.valueOf(value));
    }
    
    /**
     * Identical to <CODE>setCellValue(BigDecimal)</CODE>, except using a native
     * data type of <CODE>long</CODE>.
     * @param value the numeric value of this cell
     */
    public void setCellValue(long value) {
        setCellValue((double)value);
    }
    
    /**
     * Identical to <CODE>setCellValue(BigDecimal)</CODE>, except using a native
     * data type of <CODE>int</CODE>.
     * @param value the numeric value of this cell
     */
    public void setCellValue(int value) {
        setCellValue((double)value);
    }
    
    /**
     * Sets this cell's value, and changes the <CODE>CellType</CODE> to <CODE>STRING</CODE>
     * @param value the string value of this cell
     */
    public void setCellValue(String value) {
        setCellType(CellType.STRING);
        this.value = value;
    }
    
    /**
     * Sets this cell's value, and changes the <CODE>CellType</CODE> to <CODE>DATE</CODE>
     * @param value the date value of this cell
     */
    public void setCellValue(Date value) {
        setCellType(CellType.DATE);
        this.value = value;
    }
    
    /**
     * Returns the value of this cell expressed as a <CODE>boolean</CODE> value. If 
     * the cell type is <CODE>BOOLEAN</CODE>, the value returned is an exact reflection
     * of this. This the cell type is <CODE>BLANK</CODE> then <CODE>false</CODE> is 
     * returned. If the cell type is <CODE>NUMERIC</CODE> then <CODE>true</CODE> is 
     * returned for any non-zero value, otherwise <CODE>false</CODE>. If the cell type
     * is <CODE>STRING</CODE>, then the string value is parsed (using 
     * <CODE>Boolean.valueOf(String)</CODE>) and the result returned.
     * @return <CODE>true</CODE> or <CODE>alse</CODE>
     * @throws com.csam.dif.DIFDataException if the underlying data type cannot be expressed as a boolean value
     */
    public boolean getBooleanCellValue() throws DIFDataException {
        if (getCellType().equals(CellType.BLANK))
            return false;
        if (getCellType().equals(CellType.BOOLEAN))
            return (Boolean)value;
        if (getCellType().equals(CellType.NUMERIC))
            return getNumericCellValue().compareTo(BigDecimal.ZERO) != 0;
        if (getCellType().equals(CellType.STRING))
            return Boolean.valueOf(getStringCellValue());
        
        throw new DIFDataException("Type '" + getCellType().name() + "' cannot be cast to boolean");
    }
    
    /**
     * Returns the value of this cell expressed as a numeric value (enclosed in a
     * <CODE>BigDecimal</CODE>). If the cell type is <CODE>BLANK</CODE> the 0 (zero)
     * is returned. If the cell type is <CODE>BOOLEAN</CODE> then 1 (one) is returned
     * for <CODE>true</CODE> values, otherwise 0 (zero). If the cell type is
     * <CODE>NUMERIC</CODE> then the value is returned as-is. If the cell type is
     * <CODE>STRING</CODE>, then a conversion is attempted (using
     * <CODE>new BigDecimal(String)</CODE>) and the value of this returned.
     * @return numeric value enclosed in a <CODE>BigDecimal</CODE>
     * @throws com.csam.dif.DIFDataException if the underlying data type cannot be expressed as a numeric value
     */
    public BigDecimal getNumericCellValue() throws DIFDataException {
        if (getCellType().equals(CellType.BLANK))
            return BigDecimal.ZERO;
        if (getCellType().equals(CellType.BOOLEAN))
            return (Boolean)value ? BigDecimal.ONE : BigDecimal.ZERO;
        if (getCellType().equals(CellType.NUMERIC))
            return (BigDecimal)value;
        if (getCellType().equals(CellType.STRING))
            return new BigDecimal(getStringCellValue());
        
        throw new DIFDataException("Type '" + getCellType().name() + "' cannot be cast to boolean");
    }
    
    /**
     * Returns the value of this cell expressed as a numeric. If the cell type is
     * <CODE>STRING</CODE> then the value is returned as-is. All other data types
     * are converted using <CODE>String.valueOf(Object)</CODE>.
     * @return string representation of cell data
     * @throws com.csam.dif.DIFDataException if the underlying data type cannot be expressed as a string value
     */
    public String getStringCellValue() throws DIFDataException {
        if (getCellType().equals(CellType.BLANK))
            return "";
        else if (getCellType().equals(CellType.DATE)) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            return fmt.format(getDateCellValue());
        }
        return String.valueOf(value);
    }
    
    /**
     * Returns the <CODE>Date</CODE> value stored at this cell. No other type
     * conversion occurs here. Any other cell type will cause an exception.
     * @return <CODE>Date</CODE> value of this cell
     * @throws com.csam.dif.DIFDataException if the underlying data type cannot be expressed as a date value (i.e. anything
     * other than <CODE>DATE</CODE>).
     */
    public Date getDateCellValue() throws DIFDataException {
        if (getCellType().equals(CellType.DATE))
            return (Date)value;
        
        throw new DIFDataException("Type '" + getCellType().name() + "' cannot be cast to boolean");
    }
    
    /**
     * Identical to <CODE>getNumericCellValue()</CODE> except the result is expressed
     * as a native <CODE>double</CODE> type.
     * @throws com.csam.dif.DIFDataException if the underlying data type cannot be expressed as a numeric value
     * @return numeric value expressed as a <CODE>double</CODE>
     */
    public double getDoubleCellValue() throws DIFDataException {
        return getNumericCellValue().doubleValue();
    }
    
    /**
     * Identical to <CODE>getNumericCellValue()</CODE> except the result is expressed
     * as a native <CODE>double</CODE> type
     * @throws com.csam.dif.DIFDataException if the underlying data type cannot be expressed as a numeric value
     * @return numeric value expressed a <CODE>long</CODE>
     */
    public long getLongCellValue() throws DIFDataException {
        return getNumericCellValue().longValueExact();
    }
    
    /**
     * Identical to <CODE>getNumericCellValue()</CODE> except the result is expressed
     * as a native <CODE>double</CODE> type
     * @throws com.csam.dif.DIFDataException if the underlying data type cannot be expressed as a numeric value
     * @return numeric value expressed an <CODE>int</CODE>
     */
    public long getIntCellValue() throws DIFDataException {
        return getNumericCellValue().intValueExact();
    }

    /**
     * Used internally to remove the value of this cell.
     */
    protected void cleanUp() {
        setCellValue("");
    }
    
}
