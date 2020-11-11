package org.freehep.jas.extensions.text.gui;

import java.awt.Component;
import java.awt.Font;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Tony Johnson
 */
class PreviewTable extends JTable
{
   private final static int MARGIN = 2;
   private final static int MAXSCAN = 50;
   
   PreviewTable()
   {
      setAutoResizeMode(AUTO_RESIZE_OFF);
      setShowHorizontalLines(false);
      Font f = getFont();
      setFont(new Font("Monospaced",f.getStyle(),f.getSize()));
   }
   private void packColumns(int margin)
   {
      for (int c=0; c<getColumnCount(); c++)
      {
         packColumn( c, margin);
      }
   }
   
   // Sets the preferred width of the visible column specified by vColIndex. The column
   // will be just wide enough to show the column head and the widest cell in the column.
   // margin pixels are added to the left and right
   // (resulting in an additional width of 2*margin pixels).
   private void packColumn(int vColIndex, int margin)
   {
      TableModel model = getModel();
      TableColumnModel colModel = getColumnModel();
      TableColumn col = colModel.getColumn(vColIndex);
      int width = 0;
      
      // Get width of column header
      TableCellRenderer renderer = col.getHeaderRenderer();
      if (renderer == null)
      {
         renderer = getTableHeader().getDefaultRenderer();
      }
      Component comp = renderer.getTableCellRendererComponent(this, col.getHeaderValue(), false, false, 0, 0);
      width = comp.getPreferredSize().width;
      
      // Get maximum width of column data
      // To save time we dont scan all rows, instead we scan the first and last MAXSCAN rows
      int rowCount = getRowCount();
      int stop = Math.min(MAXSCAN,rowCount);
      int start = Math.max(rowCount-MAXSCAN,stop);
      
      for (int r=0; r<stop; r++)
      {
         renderer = getCellRenderer(r, vColIndex);
         comp = renderer.getTableCellRendererComponent(this, getValueAt(r, vColIndex), false, false, r, vColIndex);
         width = Math.max(width, comp.getPreferredSize().width);
      }
      for (int r=start; r<rowCount; r++)
      {
         renderer = getCellRenderer(r, vColIndex);
         comp = renderer.getTableCellRendererComponent(this, getValueAt(r, vColIndex), false, false, r, vColIndex);
         width = Math.max(width, comp.getPreferredSize().width);
      }      
      // Add margin
      width += 2*margin;
      
      // Set the width
      col.setPreferredWidth(width);
   }   
   
   public void setModel(TableModel dataModel)
   {
      super.setModel(dataModel);
      packColumns(MARGIN);
   }
   
   public void tableChanged(javax.swing.event.TableModelEvent e)
   {
      super.tableChanged(e);
      packColumns(MARGIN);
   }   
   
}
