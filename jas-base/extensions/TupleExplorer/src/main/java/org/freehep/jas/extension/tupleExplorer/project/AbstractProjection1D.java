package org.freehep.jas.extension.tupleExplorer.project;

import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.plugin.tree.FTreePath;


/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
abstract class AbstractProjection1D extends AbstractProjection {
    
    private MutableTupleColumn col;
    private FTreePath path;
    
    protected  void setColumn( MutableTupleColumn col ) {
        this.col = col;
        initProjection1D();
    }
    
    protected MutableTupleColumn column() {
        return col;
    }
    
    protected abstract void initProjection1D();
    
    public FTreePath getLeadingPath() {
        FTreePath path = column().treePath().getParentPath();
        return path;
    }
        
    public FTreePath path() {
        path = column().treePath();
        return path;
    }

    public String[] axisLabels() {
//        return new String[] {column().name()};
        return null;
    }

}
