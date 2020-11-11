package org.freehep.jas.extension.tupleExplorer.mutableTuple;

import hep.aida.ref.tuple.FTuple;
import hep.aida.ref.tuple.FTupleCursor;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.util.Value;

/**
 * A MutableTupleTreeNavigator is used to loop over all the MutableTupleColumns
 * registered with it in one single pass.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class MutableTupleTreeNavigator implements FTupleCursor {
    
    private MutableTupleTree mutableTupleTree;
    private MutableTuple tuple;
    private FTupleCursor cursor;
    private Hashtable pathCursorHash;
    private Hashtable pathChildCursorHash = new Hashtable();
    private Hashtable childCursorTupleIndexHash = new Hashtable();
    private ArrayList childList = new ArrayList();
    private Value value = new Value();
    private String cursorName;
    
    private MutableTupleTreeNavigator[] childCursors;
    private int[] childCursorIndeces;
    private ArrayList activeChildCursors = new ArrayList();
    private int nChild;
    
    private boolean canAdvance = true;
    private boolean advanceCursor = false;
    private boolean advanced;
    private int row = -1;
    
    private boolean isEnabled = true;
    
    protected MutableTupleTreeNavigator(MutableTupleTree tupleTree) {
        this( tupleTree, tupleTree.treePathForMutableTuple( tupleTree.rootMutableTuple() ), new Hashtable() );
    }
    
    private MutableTupleTreeNavigator(MutableTupleTree tupleTree, FTreePath path, Hashtable cursorHash) {
        this.mutableTupleTree = tupleTree;
        this.pathCursorHash = cursorHash;
        this.cursorName = path.toString();
        this.tuple = tupleTree.mutableTupleForPath(path);
        updateCursor();
        addCursor( tupleTree, path );
        
        nChild = childList.size();
        childCursors = new MutableTupleTreeNavigator[ nChild ];
        childCursorIndeces = new int[ nChild ];
        
        for ( int i = 0; i < nChild; i++ ) {
            MutableTupleTreeNavigator cursor = (MutableTupleTreeNavigator) childList.get(i);
            childCursors[i] = cursor;
            childCursorIndeces[i] = ( (Integer) childCursorTupleIndexHash.get(cursor) ).intValue();
        }
        setAdvanced(false);
    }
    
    private void updateCursor() {
        this.cursor = tuple.cursor();
        start();
    }
    
    private void updateTuple(FTuple fTuple) {
        this.tuple.setTuple(fTuple);
        updateCursor();
    }
    
    protected int nCursors() {
        return pathCursorHash.size();
    }

    public MutableTupleTreeNavigator cursorForPath( String pathName ) {
        return (MutableTupleTreeNavigator) pathCursorHash.get(pathName);
    }
    
    protected int nChild() {
        return nChild;
    }
    
    protected MutableTupleTreeNavigator childCursorForPath( FTreePath path ) {
        return (MutableTupleTreeNavigator) pathChildCursorHash.get(path.toString());
    }
    
    protected void printCursor() {
        printCursor("");
    }
    
    protected void printCursor(String indent) {
        System.out.println(indent+"*** *** *** "+this+" has "+nChild()+" child and "+nCursors()+" cursors in all ");
        for ( int i = 0; i < nChild(); i++ )
            childCursors[i].printCursor(indent+"---");
    }
    
    public String toString() {
        return "MutableTupleTreeNavigator : "+cursorName+" "+super.toString();
    }
    
    private void addCursor( MutableTupleTree tupleTree, FTreePath path ) {
        MutableTuple tuple = tupleTree.mutableTupleForPath(path);
        pathCursorHash.put( path.toString(), this );
        for ( int i = 0; i < tuple.columns(); i++ )
            if ( ((MutableTupleColumn)tuple.column(i)).isFolder() ) {
                MutableTupleTreeNavigator cursor = new MutableTupleTreeNavigator( tupleTree,  path.pathByAddingChild( tuple.column(i).name() ), pathCursorHash );
                pathChildCursorHash.put(path.pathByAddingChild( tuple.column(i).name() ).toString(), cursor );
                childList.add(cursor);
                childCursorTupleIndexHash.put(cursor, new Integer(i));
            }
    }
    
    public void printStatus() {
        printStatus("");
    }
    
    public void printStatus(String indent) {
        System.out.println(indent+"--> "+internalRow()+" "+advanced()+" "+canAdvance());
        for ( int i = 0; i < nChild; i++ ) {
            //            if ( childCursors[i].advanced() ) {
            childCursors[i].printStatus(indent+"---");
            //            }
        }
    }
    
    public boolean next() {
        setAdvanced(false);
        if ( advanceCursor ) {
            advanceCursor = false;
            canAdvance = cursor.next();
            setAdvanced( canAdvance );
            row++;
            if ( canAdvance )
                updateChildTuples();
        }
        else {
            int nActiveChildCursors = activeChildCursors.size();
            MutableTupleTreeNavigator[] MutableTupleTreeNavigators = new MutableTupleTreeNavigator[nActiveChildCursors];
            for ( int i = 0; i < nActiveChildCursors; i++ )
                MutableTupleTreeNavigators[i] = (MutableTupleTreeNavigator) activeChildCursors.get(i);
            
            for ( int i = 0; i < nActiveChildCursors; i++ ) {
                MutableTupleTreeNavigator activeChildCursor = MutableTupleTreeNavigators[i];
                activeChildCursor.next();
                if ( ! activeChildCursor.canAdvance() ) {
                    activeChildCursor.start();
                    activeChildCursors.remove(i);
                }
            }
        }
        if ( activeChildCursors.size() == 0 ) advanceCursor = true;
        return canAdvance();
    }
    
    public int row() {
        return cursor.row();
    }
    
    private int internalRow() {
        return row;
    }
    
    public void start() {
        cursor.start();
        advanceCursor = true;
        setAdvanced(false);
        row = -1;
    }
    
    private void updateChildTuples() {
        activeChildCursors.clear();
        for ( int i = 0; i < nChild; i++ ) {
            MutableTupleTreeNavigator childCursor = childCursors[i];
            if ( childCursor.isEnabled() ) {
                int columnIndex = childCursorIndeces[i];
                tuple.columnValue( columnIndex, cursor, value );
                FTuple childTuple = (FTuple) value.getObject();
                childCursor.updateTuple( childTuple );
                if ( childCursor.next() ) {
                    activeChildCursors.add(childCursor);
                }
            }
        }
    }
    
    public FTupleCursor cursor() {
        return this;
    }
    
    public FTupleCursor cursor(FTreePath path) {
        return cursorForPath(path.toString()).cursor();
    }
    
    public FTuple tuple() {
        return tuple;
    }
    
    public FTuple tuple(FTreePath path) {
        return cursorForPath(path.toString()).tuple();
    }
    
    public boolean advanced() {
        return advanced;
    }
    
    public boolean advanced(FTreePath path) {
        return cursorForPath(path.toString()).advanced();
    }
    
    public void disableAllChild() {
        for ( int i = 0; i < nChild; i++ ) {
            MutableTupleTreeNavigator childCursor = childCursors[i];
            childCursor.setEnabled(false);
            childCursor.disableAllChild();
        }
    }
    
    public void enablePath( FTreePath path ) {
        FTreePath parent = path;
        MutableTupleTreeNavigator cursor = cursorForPath(parent.toString());
        while( parent != null && cursor != null ) {
            cursor.setEnabled(true);
            parent = parent.getParentPath();
            if ( parent == null ) break;
            cursor = cursorForPath(parent.toString());
        }
    }
    
    private void setAdvanced( boolean advanced ) {
        this.advanced = advanced;
        if ( ! advanced )
            for ( int i = 0; i < nChild; i++ )
                childCursors[i].setAdvanced(advanced);
    }
    
    protected boolean canAdvance() {
        if ( tuple.rows() == 0 ) return false;
        if ( (! canAdvance ) && activeChildCursors.size() == 0 ) return false;
        return true;
    }
    
    public void setRow(int row) {
        cursor.setRow(row);
    }
    
    public void skip(int param) {
        throw new UnsupportedOperationException("Please report this problem");
    }
    
    private boolean isEnabled() {
        return isEnabled;
    }
    
    private void setEnabled( boolean isEnabled ) {
        this.isEnabled = isEnabled;
    }
    
}
