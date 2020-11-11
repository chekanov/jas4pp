package org.freehep.jas.extension.tupleExplorer.jel;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeNavigator;
import org.freehep.util.Value;

/**
 *
 * @author  tonyj
 * @version
 */
public class NTupleColumnEvaluator {
    
    private MutableTupleTreeNavigator cursor;
    private MutableTupleTree tupleTree;
    private Vector columns;
    private MutableTuple[] tuples;
    private int[] indeces;
    private ArrayList columnNames;
    private String[] tupleTreePaths;
    private Value result = new Value();
    private MutableTupleTreeNavigator simpleCursor = null;
    private MutableTupleTreeNavigator mainCursor = null;
    private MutableTuple tuple;
    private String defaultTreePath;
    
    NTupleColumnEvaluator(MutableTupleTree tupleTree, Vector cols) {
        this.tuple = tupleTree.rootMutableTuple();
        this.defaultTreePath = tuple.treePath().toString();
        this.tupleTree = tupleTree;
        this.columns = cols;
        tuples = new MutableTuple[ columns.size() ];
        indeces = new int[ columns.size() ];
        tupleTreePaths = new String[ columns.size() ];
        columnNames = new ArrayList();
        for ( int i = 0; i < columns.size(); i++ ) {
            MutableTupleColumn col = (MutableTupleColumn) columns.elementAt(i);
            tuples[i] = tupleTree.mutableTupleForPath( col.treePath().getParentPath() );
            indeces[i] = tuples[i].columnIndexByName( col.name() );
            tupleTreePaths[i] = tuples[i].treePath().toString();
            columnNames.add( col.name() );
        }
    }

    
    void setCursor(MutableTupleTreeNavigator cursor) {
        this.cursor = cursor;
    }
    public int getIntProperty(int i) {
        updateResult(i);
        return result.getInt();
    }
    public int getIntProperty(String name) {
        updateResult(name);
        return result.getInt();
    }
    public short getShortProperty(int i) {
        updateResult(i);
        return result.getShort();
    }
    public short getShortProperty(String name) {
        updateResult(name);
        return result.getShort();
    }
    public long getLongProperty(int i) {
        updateResult(i);
        return result.getLong();
    }
    public long getLongProperty(String name) {
        updateResult(name);
        return result.getLong();
    }
    public float getFloatProperty(int i) {
        updateResult(i);
        return result.getFloat();
    }
    public float getFloatProperty(String name) {
        updateResult(name);
        return result.getFloat();
    }    
    public double getDoubleProperty(int i) {
        updateResult(i);
        return result.getDouble();
    }
    public double getDoubleProperty(String name) {
        updateResult(name);
        return result.getDouble();
    }
    public String getStringProperty(int i) {
        updateResult(i);
        return result.getString();
    }
    public String getStringProperty(String name) {
        updateResult(name);
        return result.getString();
    }
    public Date getDateProperty(int i) {
        updateResult(i);
        return result.getDate();
    }
    public Date getDateProperty(String name) {
        updateResult(name);
        return result.getDate();
    }
    public char getCharProperty(int i) {
        updateResult(i);
        return result.getChar();
    }
    public char getCharProperty(String name) {
        updateResult(name);
        return result.getChar();
    }
    public int getByteProperty(int i) {
        updateResult(i);
        return result.getByte();
    }
    public int getByteProperty(String name) {
        updateResult(name);
        return result.getByte();
    }
    public boolean getBooleanProperty(int i) {
        updateResult(i);
        return result.getBoolean();
    }
    public boolean getBooleanProperty(String name) {
        updateResult(name);
        return result.getBoolean();
    }
    public Object getObjectProperty(int i) {
        updateResult(i);
        return result.getObject();
    }
    public Object getObjectProperty(String name) {
        if (name.equals("null")) return null;
        updateResult(name);
        return result.getObject();
    }

    private void updateResult( int i ) {
        int index = indeces[i];
        if ( simpleCursor == null || mainCursor != cursor ) {
            simpleCursor =  cursor.cursorForPath( tupleTreePaths[i] );
            mainCursor = cursor;
        }
        tuples[i].columnValue( index, simpleCursor, result );
    }
    
    private void updateResult( String name ) {
        if ( simpleCursor == null || mainCursor != cursor ) {
            simpleCursor =  cursor.cursorForPath( defaultTreePath );
            mainCursor = cursor;
        }
        tuple.columnValue( tuple.columnIndexByName(name), simpleCursor, result );
    }
    
}
