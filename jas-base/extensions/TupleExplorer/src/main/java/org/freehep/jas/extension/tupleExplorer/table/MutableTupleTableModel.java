package org.freehep.jas.extension.tupleExplorer.table;

import hep.aida.ref.tuple.FTupleCursor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.freehep.util.Value;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.plugin.tree.FTreePath;

/**
 * Allows a MutableTuple to be displayed in a JTable.
 * Currently only supports Random Access Tuples.
 * @author  The FreeHEP team @ SLAC.
 * @version $Id: MutableTupleTableModel.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public class MutableTupleTableModel extends AbstractTableModel {
    
    private MutableTuple mutableTuple;
    private MutableTupleTree tupleTree;
    private MutableTupleTreeNavigator treeNavigator;
    private List selectedColumns;
    private boolean isChild;
    private boolean isRootTuple;
    private boolean hasRandomAccess;
    private int rows = -1;
    private Value[][] values;
    private Value value = new Value();
    
    protected MutableTupleTableModel(MutableTuple mutableTuple, MutableTupleTree tupleTree) {
        this(mutableTuple,tupleTree,null);
    }
    
    protected MutableTupleTableModel(MutableTuple mutableTuple, MutableTupleTree tupleTree, List selectedColumns) {
        this(mutableTuple, tupleTree, selectedColumns, false);
    }
    
    protected MutableTupleTableModel(MutableTuple mutableTuple, MutableTupleTree tupleTree, List selectedColumns, boolean isChild) {
        if (mutableTuple.rows() == mutableTuple.ROWS_UNKNOWN) throw new IllegalArgumentException("NTuple does not have well defined number of rows");
        this.hasRandomAccess = mutableTuple.supportsRandomAccess();
        this.mutableTuple = mutableTuple;
        this.treeNavigator = mutableTuple.treeCursor();
        this.tupleTree = tupleTree;
        
        if ( selectedColumns == null ) {
            selectedColumns = new ArrayList();
            for ( int i = 0; i < mutableTuple.columns(); i++ )
                selectedColumns.add( mutableTuple.column(i) );
        }
        
        this.selectedColumns = selectedColumns;
        this.isChild = isChild;
        this.isRootTuple = tupleTree.mutableTupleForPath( mutableTuple.treePath().getParentPath() ) == null;
        values = new Value[getRowCount()][getColumnCount()];
    }
    
    public Object getValueAt(int row, int column) {
        if ( hasRandomAccess ) {
            treeNavigator.setRow(row);
            ((MutableTupleColumn) selectedColumns.get(column)).value(treeNavigator, value);
            return value;
        } else {
            if ( values[row][column] == null || getColumnClass(column) != Value.class ) {

                values[row][column] = new Value();

                treeNavigator.disableAllChild();
                MutableTupleColumn col = (MutableTupleColumn) selectedColumns.get(column);
                FTreePath colPath = col.treePath();
//                if ( getColumnClass(column) == Value.class )                
                    colPath = colPath.getParentPath();

                FTreePath parentPath = colPath.getParentPath();
                treeNavigator.enablePath( colPath );

                treeNavigator.start();
                int rowCount = 0;
                while( treeNavigator.next() )
                    if ( treeNavigator.advanced( colPath ) ) {
                        if ( rowCount == row ) {
                            MutableTuple t = (MutableTuple)treeNavigator.tuple(colPath);
                            t.columnValue( t.columnIndexByName(col.name()), treeNavigator.cursorForPath(colPath.toString()), values[row][column]);
                            return values[row][column];
                        }
                        rowCount++;
                    }
                return values[row][column];
            }       
            return values[row][column];
        }
    }
    
    public int getRowCount() {
        if ( rows != -1 ) return rows;
        if ( isChild || isRootTuple )
            return rows = mutableTuple.rows();
        treeNavigator.disableAllChild();
        FTreePath tuplePath = mutableTuple.treePath();
        FTreePath parentPath = tuplePath.getParentPath();
        treeNavigator.enablePath( tuplePath );
        treeNavigator.start();
        rows = 0;
        while( treeNavigator.next() )
            if ( treeNavigator.advanced( parentPath ) )
                rows += treeNavigator.tuple( tuplePath ).rows();
        return rows;
    }
    public int getColumnCount() {
        return selectedColumns.size();
    }
    public String getColumnName(int column) {
        return ((MutableTupleColumn) selectedColumns.get(column)).name();
    }
    
    public boolean isCellEditable(int r, int c) {
//        if ( getColumnClass(c) == MutableTuple.class )
//            return true;
        return false;
    }
    
    public Class getColumnClass(int column) {
        MutableTupleColumn col = (MutableTupleColumn) selectedColumns.get(column);
//        if ( MutableTuple.class.isAssignableFrom( col.type() ) )
//            return MutableTuple.class;
        return Value.class;
    }
}
