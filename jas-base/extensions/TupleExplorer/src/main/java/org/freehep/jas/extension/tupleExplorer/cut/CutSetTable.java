package org.freehep.jas.extension.tupleExplorer.cut;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author tonyj
 * @version $Id: CutSetTable.java 13893 2011-09-28 23:42:34Z tonyj $ 
 */
public class CutSetTable extends AbstractTableModel implements ListDataListener
{
   private final static String[] header = { "Cut" , "Disabled", "Inverted" };
   private CutSet cutSet;
   /** Creates new CutSetTable */
    public CutSetTable(CutSet cs) 
    {
       this.cutSet = cs;
       cs.addListDataListener(this);
    }
    public Object getValueAt(int row, int column)
    {
	Cut cut = cutSet.getCut(row);
      if (column == 0) return cut.getName();
      else
      {
         int state = cutSet.getCutState(cut);
         if   (column == 1) return state == cutSet.CUT_DISABLED ? Boolean.TRUE : Boolean.FALSE;
         else               return state == cutSet.CUT_INVERTED ? Boolean.TRUE : Boolean.FALSE;
      }
    }
    public int getRowCount()
    {
       return cutSet.getNCuts();
    }
    public int getColumnCount()
    {
       return 3;
    }
    public void setValueAt(Object value, int row, int col)
    {
       Cut cut = cutSet.getCut(row);
       int state = cutSet.CUT_ENABLED;
       if (col == 1 && ((Boolean) value).booleanValue()) state = cutSet.CUT_DISABLED;
       if (col == 2 && ((Boolean) value).booleanValue()) state = cutSet.CUT_INVERTED;
       cutSet.setCutState(cut,state);
    }
    public String getColumnName(int index)
    {
       return header[index];
    }
    public boolean isCellEditable(int row, int column)
    {
       return column > 0;
    }
    public Class getColumnClass(int column)
    {
       return column == 0 ? String.class : Boolean.class;
    }
    public void intervalAdded(ListDataEvent listDataEvent)
    {
       fireTableRowsInserted(listDataEvent.getIndex0(),listDataEvent.getIndex1());
    }
    public void intervalRemoved(ListDataEvent listDataEvent)
    {
       fireTableRowsDeleted(listDataEvent.getIndex0(),listDataEvent.getIndex1());
    }
    public void contentsChanged(ListDataEvent listDataEvent)
    {
       fireTableStructureChanged();
    }
}
