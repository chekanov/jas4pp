/*
 * SpreadsheetFactory.java
 *
 * Created on January 7, 2003, 5:12 PM
 */

package org.freehep.jas.extension.spreadsheet;

import java.io.File;
import javax.swing.Icon;
import org.freehep.jas.extension.spreadsheet.SpreadsheetIO;
import org.sharptools.spreadsheet.JSpreadsheet;

/**
 *
 * @author tonyj
 */
public interface SpreadsheetFactory
{
   public JSpreadsheet createSpreadsheet(int rows, int cols, SpreadsheetIO io, File file);
   public void showSpreadsheet(JSpreadsheet ss, String name, Icon icon);
}
