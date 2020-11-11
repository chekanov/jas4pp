package org.freehep.jas.extension.spreadsheet;

import java.io.File;
import java.io.IOException;
import org.freehep.jas.services.FileHandler;
import org.sharptools.spreadsheet.JSpreadsheet;

/**
 * 
 * @author tonyj
 */
public interface SpreadsheetIO extends FileHandler
{
   void write(File file, JSpreadsheet spreadsheet) throws IOException;
}