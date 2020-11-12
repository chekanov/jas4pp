/*
 * DIFWriter.java
 *
 * Created on January 29, 2008, 10:53 AM
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

import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Iterator;

/**
 *
 * @author Nathan Crause
 */
public class DIFWriter {
    
    private SimpleDateFormat usFmt = new SimpleDateFormat("MM/dd/yyyy");
    
    /** Creates a new instance of DIFWriter */
    public DIFWriter() {
    }
    
    /**
     * 
     * @param sheet 
     * @param outStream 
     */
    public static void writeSheet(DIFSheet sheet, OutputStream outStream) {
        DIFWriter writer = new DIFWriter();
        writer.write(sheet, outStream);
    }
    
    /**
     * 
     * @param sheet 
     * @param outStream 
     */
    public void write(DIFSheet sheet, OutputStream outStream) {
        PrintWriter writer = new PrintWriter(outStream);
        writeTitle(writer, sheet);
        int vectors = writeVectors(writer, sheet);
        int tuples = writeTuples(writer, sheet);
        writeData(writer);
        
        for (int i = 0; i < tuples; ++i) {
            writer.println("-1,0");
            writer.println(DIFKeywords.BOT.name());
            if (!sheet.hasRow(i)) {
                // there is no row here, so fill it with empty strings
                for (int j = 0; j < vectors; ++i) {
                    writer.println("1,0");
                    writeString(writer, "");
                }
            } else {
                DIFRow row = sheet.getRow(i);
                // write out the data
                for (int j = 0; j < vectors; ++j) {
                    if (!row.hasCell(j)) {
                        // write empty cell
                        writer.println("1,0");
                        writeString(writer, "");
                    } else {
                        DIFCell cell = row.getCell(j);
                        
                        try {
                            if (cell.getCellType().equals(DIFCell.CellType.BLANK) ||
                                    cell.getCellType().equals(DIFCell.CellType.STRING)) {
                                writer.println("1,0");
                                writeString(writer, cell.getStringCellValue());
                            } else if (cell.getCellType().equals(DIFCell.CellType.BOOLEAN)) {
                                writer.println("0," + (cell.getBooleanCellValue() ? 1 : 0));
                                writer.println(Boolean.toString(cell.getBooleanCellValue()).toUpperCase());
                            } else if (cell.getCellType().equals(DIFCell.CellType.NUMERIC)) {
                                writer.println("0," + cell.getNumericCellValue().toPlainString());
                                writer.println("V");
                            } else if (cell.getCellType().equals(DIFCell.CellType.DATE)) {
                                writer.println("0," + usFmt.format(cell.getDateCellValue()));
                                writer.println("V");
                            }
                        } catch (DIFDataException ex) {
                            throw new RuntimeException("PANIC!", ex);
                        }
                    }
                }
            }
        }
        
        writer.flush();
    }
    
    private void writeTitle(PrintWriter writer, DIFSheet sheet) {
        writer.println("TABLE");
        writer.println("0," + sheet.getVersion());
        writeString(writer, sheet.getTitle());
    }
    
    private void writeString(PrintWriter writer, String data) {
        writer.println("\"" + data.replace("\"", "\"\"") + "\"");
    }

    private int writeVectors(PrintWriter writer, DIFSheet sheet) {
        writer.println(DIFKeywords.VECTORS.name());
        
        // determine max vectors
        int vectors = 0;
        for (Iterator<DIFRow> i = sheet.rowIterator(); i.hasNext(); ) {
            DIFRow row = i.next();
            vectors = Math.max(vectors, row.getLastCellNumber() + 1);
        }
        writer.println("0," + vectors);
        writeString(writer, "");
        
        return vectors;
    }

    private int writeTuples(PrintWriter writer, DIFSheet sheet) {
        int tuples = sheet.getLastRowNumber() + 1;
        
        writer.println(DIFKeywords.TUPLES.name());
        writer.println("0," + tuples);
        writeString(writer, "");
        
        return tuples;
    }

    private void writeData(PrintWriter writer) {
        writer.println(DIFKeywords.DATA.name());
        writer.println("0,0");
        writeString(writer, "");
    }
    
    
}
