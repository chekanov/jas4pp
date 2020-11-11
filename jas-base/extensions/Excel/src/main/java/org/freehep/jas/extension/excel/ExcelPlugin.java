package org.freehep.jas.extension.excel;

import jxl.Workbook;
import org.freehep.application.studio.Plugin;
import javax.swing.filechooser.FileFilter;
import org.freehep.swing.ExtensionFileFilter;
import java.io.IOException;
import java.io.File;
import jxl.Cell;
import jxl.NumberCell;
import jxl.NumberFormulaCell;
import jxl.Sheet;
import jxl.biff.formula.FormulaException;
import jxl.read.biff.BiffException;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.freehep.jas.extension.spreadsheet.SpreadsheetFactory;
import org.freehep.jas.extension.spreadsheet.SpreadsheetIO;
import org.sharptools.spreadsheet.JSpreadsheet;

/**
 *
 * @author tonyj
 */
public class ExcelPlugin extends Plugin implements SpreadsheetIO {
    //*************************************//
    // Methods for the FileHandler service //
    //*************************************//

    @Override
    public FileFilter getFileFilter() {
        return new ExtensionFileFilter("xls", "Excel File");
    }

    @Override
    public boolean accept(File file) throws IOException {
        return file.getName().endsWith(".xls");
    }

    @Override
    public void openFile(File file) throws IOException {
        SpreadsheetFactory factory = (SpreadsheetFactory) getApplication().getLookup().lookup(SpreadsheetFactory.class);
        try {
            Workbook workbook = Workbook.getWorkbook(file);
            try {
                int nSheets = workbook.getNumberOfSheets();
                for (int i = 0; i < nSheets; i++) {
                    Sheet sheet = workbook.getSheet(i);
                    String name = sheet.getName();
                    int rows = sheet.getRows();
                    int cols = sheet.getColumns();
                    if (rows * cols == 0) {
                        continue;
                    }
                    // kludge, because we don't currently support saving multiple sheets into
                    // a separate Excel file. TODO: Fix this, but how?
                    JSpreadsheet ss = nSheets == 1 ? factory.createSpreadsheet(rows, cols, this, file) : factory.createSpreadsheet(rows, cols, null, null);
                    for (int c = 0; c < cols; c++) {
                        for (int r = 0; r < rows; r++) {
                            Cell cell = sheet.getCell(c, r);
                            if (cell instanceof NumberFormulaCell) {
                                try {
                                    ss.setValueAt("=" + ((NumberFormulaCell) cell).getFormula(), JSpreadsheet.baseRow + r, JSpreadsheet.baseCol + c);
                                } catch (FormulaException x) {
                                    IOException xx = new IOException("Invalid formula in excel spreadsheet (c,r,f=" + c + " " + r + " " + cell.getContents() + ")");
                                    System.out.println();
                                    xx.initCause(x);
                                    throw xx;
                                }
                            } else if (cell instanceof NumberCell) {
                                ss.setValueAt(String.valueOf(((NumberCell) cell).getValue()), JSpreadsheet.baseRow + r, JSpreadsheet.baseCol + c);
                            } else {
                                ss.setValueAt(cell.getContents(), JSpreadsheet.baseRow + r, JSpreadsheet.baseCol + c);
                            }
                        }
                    }
                    ss.setModified(false);
                    factory.showSpreadsheet(ss, name, null);
                }
            } finally {
                workbook.close();
            }
        } catch (BiffException x) {
            throw new IOException("Biff exception", x);
        }
    }

    //****************************************//
    @Override
    public void write(File file, JSpreadsheet spreadsheet) throws IOException {
        WritableWorkbook workbook = Workbook.createWorkbook(file);
        WritableSheet sheet = workbook.createSheet("Sheet 0", 0);

        int rows = spreadsheet.getRowCount() - JSpreadsheet.baseRow;
        int cols = spreadsheet.getColumnCount() - JSpreadsheet.baseCol;
        try {
            for (int c = 0; c < cols; c++) {
                for (int r = 0; r < rows; r++) {
                    org.sharptools.spreadsheet.Cell cell = spreadsheet.getCellAt(JSpreadsheet.baseRow + r, JSpreadsheet.baseCol + c);
                    int type = cell.getType();
                    if (type == org.sharptools.spreadsheet.Cell.NUMBER) {
                        java.lang.Number value = (java.lang.Number) cell.getValue();
                        sheet.addCell(new Number(c, r, value.doubleValue()));
                    } else if (type == org.sharptools.spreadsheet.Cell.FORMULA) {
                        String string = cell.getFormulaString();
                        sheet.addCell(new Formula(c, r, string));
                    } else {
                        String value = (String) cell.getValue();
                        if (value.length() != 0) {
                            sheet.addCell(new Label(c, r, value));
                        }
                    }
                }
            }
            workbook.write();
            workbook.close();
        } catch (WriteException x) {
            throw new IOException("Error writing excel spreadsheet", x);
        }
    }

    @Override
    protected void init() throws org.xml.sax.SAXException, java.io.IOException {
        getApplication().getLookup().add(this);
    }
}