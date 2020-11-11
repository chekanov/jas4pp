package org.freehep.jas.extensions.text.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.freehep.jas.extensions.text.core.LineSource;
import org.freehep.jas.extensions.text.core.TextMetaData;
import org.freehep.jas.extensions.text.core.TokenSource;
import org.freehep.jas.extensions.text.core.Tokenizer;


/**
 * A model used for the data preview table.
 * @author Tony Johnson
 */
public class PreviewModel extends AbstractTableModel implements PropertyChangeListener
{
   private TokenSource tokens;
   private GUIUtilities util;

   public PreviewModel(GUIUtilities util)
   {
      this.util = util;     
      util.getMetaData().getDelimiterManager().addPropertyChangeListener(this);
      LineSource ls = util.getPreview();
      if (util.getMetaData().hasColumnHeadersInFile())
      {
         ls.setStartLine(0);
         ls.setRow(util.getMetaData().getColumnHeaderRow()-1);
         util.setHeader(ls.getLine());
      }
      ls.setStartLine(util.getMetaData().getFirstDataRow()-1);
      tokens = new TokenSource(ls,util.getMetaData().getTokenizer());
   }
 
   public Class getColumnClass(int columnIndex)
   {
      return String.class;
   }
   
   public int getColumnCount()
   {
      return tokens.columns(true);
   }
   
   public String getColumnName(int columnIndex)
   {
      return util.getColumnName(columnIndex);
   }
 
   public int getRowCount()
   {
      return util.getPreview().rows(true);
   }
   
   public Object getValueAt(int rowIndex, int columnIndex)
   {
      tokens.setRow(rowIndex);
      return tokens.getToken(columnIndex);
   }
    
   public void propertyChange(PropertyChangeEvent evt)
   {
      util.headersChanged();
      tokens = new TokenSource(util.getPreview(),util.getMetaData().getTokenizer());
      fireTableStructureChanged();
   }
}
