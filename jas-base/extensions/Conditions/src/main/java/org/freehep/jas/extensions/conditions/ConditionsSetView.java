package org.freehep.jas.extensions.conditions;

import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import org.freehep.conditions.ConditionsSet;

/**
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class ConditionsSetView extends JSplitPane {

// -- Private parts : ----------------------------------------------------------
  
  private ConditionsSet _conditions;
  
  final private JTable _mapTable;
  final private JScrollPane _mapPane;
  final private JTable _tableTable;
  final private JScrollPane _tablePane;

// -- Construction and initialization : ----------------------------------------
  
   ConditionsSetView() {
     super(JSplitPane.VERTICAL_SPLIT, true);
     setOneTouchExpandable(true);
     setResizeWeight(0.5);
     _mapTable = new JTable();
     _mapPane = new JScrollPane(_mapTable);
     this.setTopComponent(_mapPane);
     _tableTable = new JTable();
     _tablePane = new JScrollPane(_tableTable);
     this.setBottomComponent(_tablePane);
   }
   
// -- Updating : ---------------------------------------------------------------
   
  public void set(ConditionsSet conditions) {
    _conditions = conditions;
    _mapTable.setModel(new MapTableModel());
    if (_mapTable.getRowCount() == 0) {
      this.setDividerLocation(0.);
    } else {
      _mapPane.setPreferredSize(_mapTable.getPreferredScrollableViewportSize());
      resetToPreferredSizes();
    }
    _tableTable.setModel(new TableTableModel());
    if (_tableTable.getRowCount() == 0) {
      setDividerLocation(1.);
    } else {
      _tablePane.setPreferredSize(_tableTable.getPreferredScrollableViewportSize());
    }
    revalidate();
  }

// -- Table model wrappers : ---------------------------------------------------
  
  private class MapTableModel extends AbstractTableModel {
    
    final private ArrayList<String> keys;
    
    MapTableModel() {
      keys = new ArrayList<String>(_conditions.keySet());
    }

    @Override
    public int getRowCount() {
      return keys.size();
    }

    @Override
    public int getColumnCount() {
      return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      return columnIndex == 0 ? keys.get(rowIndex) : _conditions.getString(keys.get(rowIndex), "");
    }

    @Override
    public String getColumnName(int column) {
      return column == 0 ? " key " : " value ";
    }
    
  }
  
  private class TableTableModel extends AbstractTableModel {
    
    final private ArrayList<String> columns;
    
    TableTableModel() {
      columns = new ArrayList<String>(_conditions.columnSet());
    }

    @Override
    public int getRowCount() {
      return _conditions.getRowCount();
    }

    @Override
    public int getColumnCount() {
      return _conditions.getColumnCount();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      return _conditions.getString(rowIndex, getColumnName(columnIndex), "");
    }

    @Override
    public String getColumnName(int column) {
      return columns.get(column);
    }
    
  }

}
