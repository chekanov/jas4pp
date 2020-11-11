package org.freehep.jas.extension.tupleExplorer.mutableTuple;

import java.util.ArrayList;
import java.util.Hashtable;
import org.freehep.jas.extension.tupleExplorer.cut.Cut;
import org.freehep.jas.extension.tupleExplorer.cut.CutSet;
import org.freehep.jas.plugin.tree.FTreePath;
import hep.aida.ref.tuple.FTupleCursor;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class MutableTupleTreeCutSet extends CutSet {

    private FTreePath path;
    
    public MutableTupleTreeCutSet(String name) {
        super(name);
    }
        
    
    
    void setPath(FTreePath path) {
        this.path = path;
    }
    
    public boolean accept( FTupleCursor cutDataCursor ) {
        int size = getNCuts();
        if ( size == 0 )
            return true;
        for ( int i = 0; i < size; i++ ) {
            Cut cut = getCut(i);
            if ( cut instanceof CutSet ) {
                (( MutableTupleTreeCutSet ) cut ).setPath(path);
            }
            else {
                (( MutableTupleTreeCut ) cut ).setPath(path);
            }
        }
        return super.accept(cutDataCursor);
    }
    
    
    /**
     * @param cursor The MutableTupleTreeNavigator with the current position on the tree.
     * @param path   The path to which the cut should be applied.
     *
     */
    public boolean accept(MutableTupleTreeNavigator cursor, FTreePath path) {
        setPath(path);
        return accept(cursor);
    }
    
}
