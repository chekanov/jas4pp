/*
 * DIFReader.java
 *
 * Created on January 29, 2008, 10:42 AM
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class reads a DIF file, thus producing a <CODE>DIFSheet</CODE> object
 * containing the single sheet of information stored in the file.
 * @author Nathan Crause
 */
public class DIFReader {
    
    private SimpleDateFormat usFmt = new SimpleDateFormat("MM/dd/yyyy");
    
    /** Creates a new instance of DIFReader */
    public DIFReader() {
        
    }
    
    /**
     * Reads in a DIF layout from the stream and reproduces the data in the supplied
     * spreadsheet object. The original data in the spreadsheet is wiped out.
     * @param sheet the spreadsheet (represented as a <CODE>DIFSheet</CODE> object) to read the
     * data into
     * @param inStream the stream from which to read the data
     * @return the original spreadsheet object, but now containing the new data
     * @throws java.io.IOException if an I/O error occurs while reading the spreadsheet
     * @throws com.csam.dif.DIFKeywordException if an anticipated keyword is missing
     * @throws com.csam.dif.DIFDataPairParsingException if a data pair (e.g. 0,0) cannot be properly parsed
     * @throws com.csam.dif.DIFNumberPairInfoException if the data in a data pair (which is expected to be 2 numbers) is not what is
     * expected
     * @throws com.csam.dif.DIFStringFormatException if a string value is malformed in the file
     * @throws java.text.ParseException if there is some problem parsing data, such as dates
     */
    public static DIFSheet readSheet(DIFSheet sheet, InputStream inStream)
    throws IOException, DIFKeywordException, DIFDataPairParsingException, 
            DIFNumberPairInfoException, DIFStringFormatException, ParseException {
        DIFReader reader = new DIFReader();
        return reader.read(sheet, inStream);
    }
    
    /**
     * Reads in a DIF layout from the stream and reproduces the data in the supplied
     * spreadsheet object. The original data in the spreadsheet is wiped out.
     * @param sheet the spreadsheet (represented as a <CODE>DIFSheet</CODE> object) to read the
     * data into
     * @param inStream the stream from which to read the data
     * @return the original spreadsheet object, but now containing the new data
     * @throws java.io.IOException if an I/O error occurs while reading the spreadsheet
     * @throws com.csam.dif.DIFKeywordException if an anticipated keyword is missing
     * @throws com.csam.dif.DIFDataPairParsingException if a data pair (e.g. 0,0) cannot be properly parsed
     * @throws com.csam.dif.DIFNumberPairInfoException if the data in a data pair (which is expected to be 2 numbers) is not what is
     * expected
     * @throws com.csam.dif.DIFStringFormatException if a string value is malformed in the file
     * @throws java.text.ParseException if there is some problem parsing data, such as dates
     */
    public DIFSheet read(DIFSheet sheet, InputStream inStream)
    throws IOException, DIFKeywordException, DIFDataPairParsingException, 
            DIFNumberPairInfoException, DIFStringFormatException, ParseException {
        
        sheet.cleanUp();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        readTitle(reader, sheet);
        int vectors = readVectors(reader);
        int tuples = readTuples(reader);
        readData(reader);
        
        // construct the data area
        try {
            for (int i = 0; i < tuples; ++i) {
                DIFRow row = sheet.createRow(i);
                for (int j = 0; j < vectors; ++j)
                    row.createCell(j);
            }
        } catch (DIFException ex) {
            throw new RuntimeException("PANIC!", ex);
        }
        
        // we should start with "-1,0"
        int rowCount = 0;
        readExpectedNumberPair(reader, -1, 0);
        while(true) {
            String line = reader.readLine();
            
            if (line.equals(DIFKeywords.EOD.name()))
                break;
            if (!line.equals(DIFKeywords.BOT.name()))
                throw new DIFKeywordException("Unknown keyword '" + line + "'", "BOT/EOD", line);
            
            DIFRow row = sheet.getRow(rowCount++);
            int colCount = 0;
            while (true) {
                DIFCell cell = row.getCell(colCount++);
                
                line = reader.readLine();
                String[] parts = simpleSplit(line);
                int code = Integer.parseInt(parts[0]);
                    
                if (code == -1) {
                    if (Integer.parseInt(parts[1]) != 0)
                        throw new DIFNumberPairInfoException(-1, 0, code, Integer.parseInt(parts[1]));
                    break;
                } else if (code == 1) {
                    if (Integer.parseInt(parts[1]) != 0)
                        throw new DIFNumberPairInfoException(-1, 0, code, Integer.parseInt(parts[1]));
                    
                    String val = readString(reader);
                    cell.setCellValue(val);
                } else if (code == 0) {
                    String indicator = reader.readLine();
                    if (indicator.equals("V")) {
                        if (parts[1].matches("\\A[0-9]{2}/[0-9]{2}/[0-9]{4}\\z")) {
                            Date parsed = usFmt.parse(parts[1]);
                            cell.setCellValue(new java.sql.Date(parsed.getTime()));
                        } else {
                            cell.setCellValue(new BigDecimal(parts[1]));
                        }
                    } else if (indicator.equals("TRUE")) {
                        if (Integer.parseInt(parts[1]) != 1)
                            throw new DIFNumberPairInfoException(code, 1, code, Integer.parseInt(parts[1]));
                        cell.setCellValue(true);
                    } else if (indicator.equals("FALSE")) {
                        if (Integer.parseInt(parts[1]) != 0)
                            throw new DIFNumberPairInfoException(code, 0, code, Integer.parseInt(parts[1]));
                        cell.setCellValue(false);
                    } else {
                        throw new DIFKeywordException("Unsupported type indicator", "V/TRUE/FALSE", indicator);
                    }
                }
            }
        }
        
        return sheet;
    }
    
    /**
     * Reads in a DIF layout from the stream and reproduces the data in a new
     * spreadsheet object.
     * @return the original spreadsheet object, but now containing the new data
     * @param inStream the stream from which to read the data
     * @throws java.io.IOException if an I/O error occurs while reading the spreadsheet
     * @throws com.csam.dif.DIFKeywordException if an anticipated keyword is missing
     * @throws com.csam.dif.DIFDataPairParsingException if a data pair (e.g. 0,0) cannot be properly parsed
     * @throws com.csam.dif.DIFNumberPairInfoException if the data in a data pair (which is expected to be 2 numbers) is not what is
     * expected
     * @throws com.csam.dif.DIFStringFormatException if a string value is malformed in the file
     * @throws java.text.ParseException if there is some problem parsing data, such as dates
     */
    public DIFSheet read(InputStream inStream) 
    throws IOException, DIFKeywordException, DIFDataPairParsingException, 
            DIFNumberPairInfoException, DIFStringFormatException, ParseException {
        return read(new DIFSheet(), inStream);
    }
    
    private String readExpectedKeyword(BufferedReader reader, String expected) 
    throws IOException, DIFKeywordException {
        String actual = reader.readLine();
        if (!actual.equals(expected))
            throw new DIFKeywordException("Expected keyword '" + expected + "' but found '" + actual + "'", expected, actual);
        
        return actual;
    }
    
    private String[] simpleSplit(String line) throws DIFDataPairParsingException {
        int index = line.indexOf(',');
        if (index == -1)
            throw new DIFDataPairParsingException("Missing ','", line);
        
        return new String[] {
            line.substring(0, index),
            line.substring(index + 1)
        };
    }
    
    private int[] readNumberPair(BufferedReader reader) 
    throws IOException, DIFDataPairParsingException {
        String line = reader.readLine();
        String[] parts = simpleSplit(line);
        
        try {
            int[] result = new int[parts.length];
            for (int i = 0; i < result.length; ++i)
                result[i] = Integer.parseInt(parts[i]);
            
            return result;
        } catch (NumberFormatException ex) {
            throw new DIFDataPairParsingException(ex, line);
        }
    }
    
    private int[] readExpectedNumberPair(BufferedReader reader, int vector, int tuple) 
    throws IOException, DIFDataPairParsingException, DIFNumberPairInfoException {
        int[] numbers = readNumberPair(reader);
        if (numbers[0] != vector || numbers[1] != tuple)
            throw new DIFNumberPairInfoException("Bad vector/tuple info", vector, tuple, numbers[0], numbers[1]);
        
        return numbers;
    }
    
    private String readString(BufferedReader reader) 
    throws IOException, DIFStringFormatException {
        String line = reader.readLine();
        if (!line.startsWith("\"") || !line.startsWith("\""))
            throw new DIFStringFormatException("Missing leading and trailing '\"' character");
        return line.substring(1, line.length() - 1).replace("\"\"", "\"");
    }
    
    private void readTitle(BufferedReader reader, DIFSheet sheet) 
    throws IOException, DIFKeywordException, DIFDataPairParsingException, 
            DIFNumberPairInfoException, DIFStringFormatException {
        
        readExpectedKeyword(reader, DIFKeywords.TABLE.name());
        readExpectedNumberPair(reader, 0, 1);
        sheet.setTitle(readString(reader));
        sheet.setVersion(1);
    }
    
    private int readVectors(BufferedReader reader) 
    throws IOException, DIFKeywordException, DIFDataPairParsingException, 
            DIFNumberPairInfoException, DIFStringFormatException {
        
        readExpectedKeyword(reader, DIFKeywords.VECTORS.name());
        int[] np = readNumberPair(reader);
        if (np[0] != 0)
            throw new DIFNumberPairInfoException("Invalid formatted 'VECTORS' info",
                    0, np[1], np[0], np[1]);
        
        if (readString(reader).length() != 0)
            throw new DIFStringFormatException("Expected empty string");
        
        return np[1];
    }
    
    private int readTuples(BufferedReader reader) 
    throws IOException, DIFKeywordException, DIFDataPairParsingException, 
            DIFNumberPairInfoException, DIFStringFormatException {
        
        readExpectedKeyword(reader, DIFKeywords.TUPLES.name());
        int[] np = readNumberPair(reader);
        if (np[0] != 0)
            throw new DIFNumberPairInfoException("Invalid formatted 'TUPLES' info",
                    0, np[1], np[0], np[1]);
        
        if (readString(reader).length() != 0)
            throw new DIFStringFormatException("Expected empty string");
        
        return np[1];
    }
    
    private void readData(BufferedReader reader) 
    throws IOException, DIFKeywordException, DIFDataPairParsingException, 
            DIFNumberPairInfoException, DIFStringFormatException {
        
        readExpectedKeyword(reader, DIFKeywords.DATA.name());
        readExpectedNumberPair(reader, 0, 0);
        if (readString(reader).length() != 0)
            throw new DIFStringFormatException("Expected empty string");
    }
}
