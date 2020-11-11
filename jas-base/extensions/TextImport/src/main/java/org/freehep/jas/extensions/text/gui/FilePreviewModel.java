package org.freehep.jas.extensions.text.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.table.AbstractTableModel;
import org.freehep.jas.extensions.text.core.LineSource;
import org.freehep.jas.extensions.text.core.TextMetaData;
import org.freehep.jas.extensions.text.core.TokenSource;


/**
 * A model used for the data preview table.
 * @author Tony Johnson
 */
public class FilePreviewModel extends AbstractTableModel
{
   private LineSource source;

   public FilePreviewModel(LineSource source)
   {
      this.source = source;
   }
   LineSource getSource()
   {
      return source;
   }

   public Class getColumnClass(int columnIndex)
   {
      return String.class;
   }
   
   public int getColumnCount()
   {
      return 1;
   }
   
   public String getColumnName(int columnIndex)
   {
      return null;
   }
   
   public int getRowCount()
   {
      return source.rows(true);
   }
 
   public Object getValueAt(int rowIndex, int columnIndex)
   {
      source.setRow(rowIndex);
      return source.getLine();
   }
}
