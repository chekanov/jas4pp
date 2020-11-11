package org.freehep.jas.extensions.text.gui;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.freehep.jas.extensions.text.core.ColumnFormat;
import org.freehep.jas.extensions.text.core.TextMetaData;
import org.freehep.jas.extensions.text.core.TypeScanner;

/**
 *
 * @author  Tony Johnson
 */
class ColumnTableModel extends AbstractTableModel
{
   private Class[] classes = { Boolean.class, String.class, ColumnFormat.class };
   private String[] header = { "Skip", "Name", "Type" };
   private TableModel preview;
   private GUIUtilities util;
   
   ColumnTableModel(TableModel preview, GUIUtilities util)
   {
      this.preview = preview;
      this.util = util;
   }
   
   public Class getColumnClass(int columnIndex)
   {
      return classes[columnIndex];
   }
   
   public int getColumnCount()
   {
      return header.length;
   }
   
   public String getColumnName(int columnIndex)
   {
      return header[columnIndex];
   }
   
   public int getRowCount()
   {
      return preview.getColumnCount();
   }
   
   public Object getValueAt(int rowIndex, int columnIndex)
   {
      if      (columnIndex == 0) return util.getMetaData().getColumnSkip(rowIndex) ? Boolean.TRUE : Boolean.FALSE;
      else if (columnIndex == 1) return preview.getColumnName(rowIndex);
      else                       return util.getColumnFormat(rowIndex);
   }
   
   public boolean isCellEditable(int rowIndex, int columnIndex)
   {
      return true;
   }
   
   public void setValueAt(Object aValue, int rowIndex, int columnIndex)
   {
      if      (columnIndex == 0)
      {
         util.getMetaData().setColumnSkip(rowIndex, ((Boolean) aValue).booleanValue());
      }
      else if (columnIndex == 1)
      {
         util.getMetaData().setColumnHeaders(rowIndex, aValue.toString());
      }
      else
      {
         util.getMetaData().setColumnFormats(rowIndex, (ColumnFormat) aValue);
      }
   }
}
