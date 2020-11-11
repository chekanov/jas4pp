package org.freehep.jas.extension.tupleExplorer.mutableTuple;

import hep.aida.ref.tuple.FTuple;
import hep.aida.ref.tuple.HasFTuple;
import hep.aida.ref.tuple.Tuple;
import hep.aida.ref.tuple.FTupleColumn;
import hep.aida.ref.tuple.FTupleCursor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.event.EventListenerList;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleEvent;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.util.Value;

/**
 * An interface implemented by all tuples handled by the
 * Tuple Explorer plugin.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class MutableTuple implements FTuple {
    
    private FTuple source;
    private List columns = new LinkedList();
    private String title;
    private MutableTupleTree mutableTupleTree;
    private int initNColumns;
    
    //This variable is used to avoid loading the same tuple in memory in
    //case of failure
    private boolean canLoad = true;
    
    
    private EventListenerList listeners = new EventListenerList();
    
    protected MutableTuple(FTuple source, String title, MutableTupleTree mutableTupleTree) {
        setTuple(source);
        this.title = title;
        this.mutableTupleTree = mutableTupleTree;
        initNColumns = source.columns();
        for ( int i = 0; i < initNColumns; i++ ) {
            FTupleColumn column = source.columnByIndex( i );
            MutableTupleColumn col = new MutableTupleColumn( column, this );
            columns.add( col );
        }
    }
    
    protected MutableTuple(String title, MutableTupleTree mutableTupleTree) {
        this.title = title;
        this.mutableTupleTree = mutableTupleTree;
    }
    
    /**
     * Add a new MutableTupleColumn to the MutableTuple.
     *
     */
    public void addMutableTupleColumn(MutableTupleColumn column) {
        columns.add(column);
        fireColumnsAdded(columns.size()-1,columns.size()-1);
    }
    
    /**
     * Remove a MutableTupleColumn from the MutableTuple.
     *
     */
    public void removeMutableTupleColumn(MutableTupleColumn column) {
        int index = columns.indexOf( column );
        fireColumnsRemoved(index,index);
        columns.remove(column);
    }
    
    /**
     * Remove an FTupleListener from the list of registered
     * listeners of this tuple.
     * @param listener The FTupleListener to be added.
     *
     */
    public void removeMutableTupleListener(MutableTupleListener listener) {
        listeners.remove(MutableTupleListener.class,listener);
    }
    
    /**
     * Add a MutableTupleListener to this tuple.
     * The listener will be notified when the tuple is modified.
     * @param listener The MutableTupleListener to be added.
     *
     */
    public void addMutableTupleListener(MutableTupleListener listener) {
        listeners.add(MutableTupleListener.class,listener);
    }
    
    
    /**
     * Methods for the FTuple interface
     *
     */
    public String title() {
        return title == null ? source.title() : title;
    }
    
    public String name() {
        return source.name();
    }
    
    public int rows() {
        return source.rows();
    }
    
    public int columns() {
        return columns.size();
    }
    
    public int columnIndexByName(String name) {
        for ( int i = 0; i < columns(); i++ )
            if ( column(i).name().equals(name) ) return i;
        throw new IllegalArgumentException("Column "+name+" does not exist!");
    }
    
    public boolean supportsRandomAccess() {
        if ( mutableTupleTree.mutableTupleForPath( treePath().getParentPath() ) != null )
            return false;
        return source.supportsRandomAccess();
    }
    
    public boolean supportsMultipleCursors() {
        return source.supportsMultipleCursors();
    }
    
    public FTupleColumn columnByName(String name) {
        for ( int i = 0; i < columns(); i++ )
            if ( column(i).name().equals(name) ) return column(i);
        throw new IllegalArgumentException("Column "+name+" does not exist!");
    }
    
    public FTupleColumn columnByIndex(int n) {
        return column(n);
    }

    public FTupleColumn column(int n) {
        if ( n < 0 || n > columns.size() ) throw new IllegalArgumentException("Index "+n+" is out of the allowed range 0-"+columns.size());
        return (FTupleColumn) columns.get(n);
    }
    
    public FTupleCursor cursor() {
        return source.cursor();
    }
    
    public void columnMaxValue(int column, Value value) {
        column(column).maxValue(value);
    }
    
    public void columnMeanValue(int column, Value value) {
        column(column).meanValue(value);
    }
    
    public void columnMinValue(int column, Value value) {
        column(column).minValue(value);
    }
    
    public String columnName(int index) {
        return column(index).name();
    }
    
    public void columnRmsValue(int column, Value value) {
        column(column).rmsValue(value);
    }
    
    public Class columnType(int index) {
        return column(index).type();
    }
    
    public void columnValue(int index, FTupleCursor cursor, Value value) {
        if ( index < initNColumns )
            source.columnValue( index, cursor, value );
        else {
            MutableTupleColumn col = (MutableTupleColumn) column(index);
            col.value( cursor, value );
        }
    }
    
    public FTuple tuple(int index) {
        if ( FTuple.class.isAssignableFrom(source.columnType(index) ) ) 
            return source.tuple(index);
        return ( (HasFTuple)source.columnByIndex(index) ).fTuple();
    }
    
    public MutableTupleTreeNavigator treeCursor() {
        return new MutableTupleTreeNavigator( mutableTupleTree );
    }

    public MutableTupleTree mutableTupleTree() {
        return mutableTupleTree;
    }
    
    public FTreePath treePath() {
        return mutableTupleTree.treePathForMutableTuple(this);
    }
    
    public void setTuple( FTuple tuple ) {
        source = tuple;
    }
    
    protected void changedMutableTupleColumn(MutableTupleColumn column) {
        int index = columns.indexOf( column );
        fireColumnsChanged(index, index);
    }
    
    private void fireColumnsRemoved(int first, int last) {
        if (listeners.getListenerCount(MutableTupleListener.class) == 0) return;
        MutableTupleEvent e = new MutableTupleEvent(this);
        e.setRange(first,last);
        MutableTupleListener[] l = (MutableTupleListener[]) listeners.getListeners(MutableTupleListener.class);
        for (int i=0; i<l.length; i++)
            l[i].columnsRemoved(e);
    }
    private void fireColumnsAdded(int first, int last) {
        if (listeners.getListenerCount(MutableTupleListener.class) == 0) return;
        MutableTupleEvent e = new MutableTupleEvent(this);
        e.setRange(first,last);
        MutableTupleListener[] l = (MutableTupleListener[]) listeners.getListeners(MutableTupleListener.class);
        for (int i=0; i<l.length; i++)
            l[i].columnsAdded(e);
    }
    
    private void fireColumnsChanged(int first, int last) {
        if (listeners.getListenerCount(MutableTupleListener.class) == 0) return;
        MutableTupleEvent e = new MutableTupleEvent(this);
        e.setRange(first,last);
        MutableTupleListener[] l = (MutableTupleListener[]) listeners.getListeners(MutableTupleListener.class);
        for (int i=0; i<l.length; i++)
            l[i].columnsChanged(e);
    }
    
    EventListenerList listeners() {
        return listeners;
    }

    public boolean isInMemory() {
        if ( canLoad )
            return source.isInMemory();
        else
            return true;
    }
    
    public void loadTupleInMemory() {
        if ( canLoad ) {
            try {
                FTuple tmp = createInMemoryTuple(source);
                source = tmp;
            } catch (Throwable t) {
                canLoad = false;
                throw new RuntimeException("Error while loading tuple in memory",t);
            }
        }
    }
    
    public double estimatedSize() {
        double size = 0;
        for ( int i = 0; i < source.columns(); i++ )
            if ( source.columnType(i).isPrimitive() )
                size += 0.000001;
            else
                size += 0.0001;
        return source.rows()*size;
    }
    
    public FTuple getFTuple() {
        return source;
    }
    
    private FTuple createInMemoryTuple( FTuple tuple ) {

        Value value = new Value();
        
        int nColumns = tuple.columns();
	String[] columnNames = new String[nColumns];
	Class[] columnTypes  = new Class[nColumns];
	for (int i=0; i<nColumns; i++) {
	    columnNames[i] = tuple.columnName(i);
	    columnTypes[i] = tuple.columnType(i);
        }

        Tuple newTuple = new Tuple(tuple.name(),tuple.title(),columnNames, columnTypes,"");
        
        FTupleCursor cursor = tuple.cursor();
        cursor.start();
        while( cursor.next() ) {
            for (int i=0; i<nColumns; i++) {
                tuple.columnValue(i,cursor,value);
                newTuple.fill(i, value);
            }
            newTuple.addRow();
        }
        return (FTuple)newTuple;
    }
        
    
    
}
