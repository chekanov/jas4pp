package org.freehep.jas.extension.tupleExplorer.table;

import hep.aida.ref.tuple.FTuple;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import org.freehep.application.studio.Studio;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.table.MutableTupleTableModel;
import org.freehep.util.Value;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class MutableTupleTable extends JTable {
    
    private MutableTupleTree tupleTree;
    private MutableTuple mutableTuple;
    private Studio application;
    
    public MutableTupleTable(MutableTuple mutableTuple, MutableTupleTree tupleTree, Studio application) {
        this(mutableTuple, tupleTree, null, application);
    }

    private MutableTupleTable(MutableTuple mutableTuple, MutableTupleTree tupleTree, Studio application, boolean isChild) {
        this(mutableTuple, tupleTree, null, application, isChild);
    }
    
    
    public MutableTupleTable(MutableTuple mutableTuple, MutableTupleTree tupleTree, List selectedColumns, Studio application) {
        this(mutableTuple, tupleTree, selectedColumns, application, false);
    }
    
    private MutableTupleTable(MutableTuple mutableTuple, MutableTupleTree tupleTree, List selectedColumns, Studio application, boolean isChild) {
        super( new MutableTupleTableModel(mutableTuple, tupleTree, selectedColumns, isChild) );
        this.tupleTree = tupleTree;
        this.mutableTuple = mutableTuple;
        setDefaultRenderer(MutableTuple.class, new MutableTupleCellRenderer());
        setDefaultEditor(MutableTuple.class,  new MutableTupleCellEditor() );
        this.application = application;
        application.getPageManager().openPage(new JScrollPane(this),mutableTuple.title(),null, "TupleTable");
    }
    
    private class MutableTupleCellRenderer extends JButton implements TableCellRenderer {
        MutableTupleCellRenderer() {
            super("SubTuple");
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    private class MutableTupleCellEditor extends JButton implements TableCellEditor {
        
        private MutableTuple tuple = null;
        
        MutableTupleCellEditor() {
            super("SubTuple");
        }
        
        public void addCellEditorListener(CellEditorListener l) {
        }
        
        public void cancelCellEditing() {
        }
        
        public Object getCellEditorValue() {
            return null;
        }
        
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            value = table.getValueAt(row,column);
            tuple = tupleTree.mutableTupleForPath( mutableTuple.treePath().pathByAddingChild(table.getColumnName(column)) );
            tuple.setTuple( (FTuple) ((Value)value).getObject() );
            MutableTupleTable mt;
            if ( tuple == null  || tuple.rows() < 1 )
                JOptionPane.showMessageDialog(MutableTupleTable.this,"Empty tuple");
            else
                mt = new MutableTupleTable(tuple, tupleTree, application, true);
            return this;
        }
        
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }
        
        public void removeCellEditorListener(CellEditorListener l) {
        }
        
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }
        
        public boolean stopCellEditing() {
            return true;
        }
    }
}
