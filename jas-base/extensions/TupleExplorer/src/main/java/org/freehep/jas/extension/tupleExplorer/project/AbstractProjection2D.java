package org.freehep.jas.extension.tupleExplorer.project;

import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.plugin.tree.FTreePath;


/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
abstract class AbstractProjection2D extends AbstractProjection {
    
    private MutableTupleColumn colx;
    private MutableTupleColumn coly;
    private FTreePath path;
    
    protected  void setColumns( MutableTupleColumn colx, MutableTupleColumn coly ) {
        this.colx = colx;
        this.coly = coly;
        initProjection2D();
    }
    
    protected MutableTupleColumn columnX() {
        return colx;
    }
    
    protected MutableTupleColumn columnY() {
        return coly;
    }
    
    protected abstract void initProjection2D();
        
    public FTreePath getLeadingPath() {
        FTreePath pathx = columnX().treePath().getParentPath();
        FTreePath pathy = columnY().treePath().getParentPath();
        if ( pathx.isDescendant( pathy ) )
            return pathy;
        return pathx;
    }   
    
    public FTreePath path() {
        path = columnX().treePath().getParentPath();
        path = path.pathByAddingChild( columnY().name() + " vs "+columnX().name() );
        return path;
    }    
    
    public String[] axisLabels() {
        return new String[] {columnX().name(), columnY().name()};
    }
    
}
