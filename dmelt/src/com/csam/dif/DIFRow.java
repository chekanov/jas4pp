/*
 * DIFRow.java
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

import java.util.Iterator;
import java.util.TreeMap;

/**
 * This class represents a row within a spreadsheet which contains cells of data.
 * Most of the methods here are centered around manipulating this cell relationship.
 * @author Nathan Crause
 */
public final class DIFRow {
    
    private TreeMap<Integer, DIFCell> cells = new TreeMap<Integer, DIFCell>();
    
    private DIFSheet sheet;
    
    private int rowNumber;
    
    /**
     * Creates a new instance of DIFRow
     * @param sheet the spreadsheet with contains this row
     * @param rowNumber the row number within the spreadsheet
     */
    protected DIFRow(DIFSheet sheet, int rowNumber) {
        this.sheet = sheet;
        this.rowNumber = rowNumber;
    }
    
    /**
     * Returns the spreadsheet which contains this row
     * @return parent spreadsheet
     */
    protected DIFSheet getSheet() {
        return sheet;
    }
    
    /**
     * Returns the physical row number within the spreadsheet of this row. A
     * "physical" number is defined as the visual position of the row as opposed to
     * a "logical" position. What this means in real-terms is that in any given sheet,
     * several rows may be "empty". Thus, these rows may not be represented in the
     * sheet model. If the first row within a sheet which contains cells is located on
     * the third row of the spreadsheet, then the "physical number" is 2 (the numbers
     * are zero-based, meaning the first row is 0, not 1).
     * @return <CODE>int</CODE> row number
     */
    public int getRowNumber() {
        return rowNumber;
    }
    
    /**
     * 
     * @param cellNumber 
     * @param cellType 
     * @throws com.csam.dif.DIFCellExistsException 
     * @return 
     */
    public DIFCell createCell(int cellNumber, DIFCell.CellType cellType) throws DIFCellExistsException {
        if (hasCell(cellNumber))
            throw new DIFCellExistsException(sheet, rowNumber, cellNumber);
        DIFCell cell = new DIFCell(sheet, rowNumber, cellNumber, cellType);
        cells.put(cellNumber, cell);
        return cell;
    }
    
    /**
     * 
     * @param cellNumber 
     * @throws com.csam.dif.DIFCellExistsException 
     * @return 
     */
    public DIFCell createCell(int cellNumber) throws DIFCellExistsException {
        return createCell(cellNumber, DIFCell.CellType.BLANK);
    }
    
    /**
     * 
     * @param cellNumber 
     * @return 
     */
    public boolean hasCell(int cellNumber) {
        return cells.containsKey(cellNumber);
    }
    
    /**
     * 
     * @return 
     */
    public Iterator<DIFCell> cellIterator() {
        return cells.values().iterator();
    }
    
    /**
     * 
     * @return 
     */
    public int getPhysicalNumberOfCells() {
        return cells.size();
    }
    
    /**
     * 
     * @return 
     */
    public int getFirstCellNumber() {
        return cells.firstKey();
    }
    
    /**
     * 
     * @return 
     */
    public int getLastCellNumber() {
        return cells.lastKey();
    }
    
    /**
     * 
     * @param cellNumber 
     * @return 
     */
    public DIFCell getCell(int cellNumber) {
        return cells.get(cellNumber);
    }
    
    /**
     * 
     * @param cellNumber 
     */
    public void deleteCell(int cellNumber) {
        DIFCell cell = cells.remove(cellNumber);
        cell.cleanUp();
    }

    protected void cleanUp() {
        while (getPhysicalNumberOfCells() > 0) {
            int cellNum = getLastCellNumber();
            deleteCell(cellNum);
        }
    }
    
}
