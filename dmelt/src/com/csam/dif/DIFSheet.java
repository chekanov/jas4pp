/*
 * DIFSheet.java
 *
 * Created on January 28, 2008, 7:31 PM
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Iterator;
import java.util.TreeMap;

/**
 *
 * @author Nathan Crause
 */
public final class DIFSheet {
    
    private TreeMap<Integer, DIFRow> rows = new TreeMap<Integer, DIFRow>();
    
    /** Creates a new instance of DIFSheet */
    public DIFSheet() {
    }
    
    /**
     * 
     * @param inStream 
     * @throws java.io.IOException 
     * @throws com.csam.dif.DIFKeywordException 
     * @throws com.csam.dif.DIFDataPairParsingException 
     * @throws com.csam.dif.DIFNumberPairInfoException 
     * @throws com.csam.dif.DIFStringFormatException 
     * @throws java.text.ParseException 
     */
    public DIFSheet(InputStream inStream) 
    throws IOException, DIFKeywordException, DIFDataPairParsingException, 
            DIFNumberPairInfoException, DIFStringFormatException, ParseException {
        this();
        
        DIFReader.readSheet(this, inStream);
    }
    
    /**
     * 
     * @return 
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(getClass().getName() + " {\n");
        ret.append("    title=" + getTitle() + "\n");
        for (Iterator<DIFRow> rows = rowIterator(); rows.hasNext(); ) {
            DIFRow row = rows.next();
            ret.append("    " + row.getRowNumber() + " = {\n");
            for (Iterator<DIFCell> cells = row.cellIterator(); cells.hasNext(); ) {
                DIFCell cell = cells.next();
                try {
                    ret.append("        " + cell.getCellNumber() + "(" + cell.getCellType().name() + ") = " + cell.getStringCellValue() + "\n");
                } catch (DIFDataException ex) {
                    ex.printStackTrace();
                    ret.append(ex.toString());
                }
            }
            ret.append("    }\n");
        }
        ret.append("}");
        
        return ret.toString();
    }
    
    
    /**
     * 
     * @param rowNumber 
     * @throws com.csam.dif.DIFRowExistsException 
     * @return 
     */
    public DIFRow createRow(int rowNumber) throws DIFRowExistsException {
        if (hasRow(rowNumber))
            throw new DIFRowExistsException(this, rowNumber);
        DIFRow row = new DIFRow(this, rowNumber);
        rows.put(rowNumber, row);
        return row;
    }
    
    /**
     * 
     * @param rowNumber 
     * @return 
     */
    public boolean hasRow(int rowNumber) {
        return rows.containsKey(rowNumber);
    }
    
    /**
     * 
     * @return 
     */
    public Iterator<DIFRow> rowIterator() {
        return rows.values().iterator();
    }
    
    /**
     * 
     * @return 
     */
    public int getPhysicalNumberOfRows() {
        return rows.size();
    }
    
    /**
     * 
     * @return 
     */
    public int getFirstRowNumber() {
        return rows.firstKey();
    }
    
    /**
     * 
     * @return 
     */
    public int getLastRowNumber() {
        return rows.lastKey();
    }
    
    /**
     * 
     * @param rowNumber 
     * @return 
     */
    public DIFRow getRow(int rowNumber) {
        return rows.get(rowNumber);
    }
    
    /**
     * 
     * @param rowNumber 
     */
    public void deleteRow(int rowNumber) {
        DIFRow row = rows.remove(rowNumber);
        row.cleanUp();
    }

    /**
     * Holds value of property title.
     */
    private String title;

    /**
     * Getter for property title.
     * @return Value of property title.
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Setter for property title.
     * @param title New value of property title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Holds value of property version.
     */
    private int version;

    /**
     * Getter for property version.
     * @return Value of property version.
     */
    public int getVersion() {
        return this.version;
    }

    /**
     * Setter for property version.
     * @param version New value of property version.
     */
    public void setVersion(int version) {
        this.version = version;
    }
    
    protected void cleanUp() {
        while (getPhysicalNumberOfRows() > 0) {
            int rowNum = getLastRowNumber();
            deleteRow(rowNum);
        }
    }
    
    /**
     * 
     * @param outStream 
     */
    public void write(OutputStream outStream) {
        DIFWriter.writeSheet(this, outStream);
    }
    
}
