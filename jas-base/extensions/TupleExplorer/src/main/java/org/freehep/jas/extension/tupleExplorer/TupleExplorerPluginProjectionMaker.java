package org.freehep.jas.extension.tupleExplorer;

import java.util.Date;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.project.Profile;
import org.freehep.jas.extension.tupleExplorer.project.Project1D;
import org.freehep.jas.extension.tupleExplorer.project.Project2D;
import org.freehep.jas.extension.tupleExplorer.project.ProjectObject1D;
import org.freehep.jas.extension.tupleExplorer.project.ProjectXY;
import org.freehep.jas.extension.tupleExplorer.project.Scatter2D;
import org.freehep.jas.plugin.tree.*;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class TupleExplorerPluginProjectionMaker {
    
    public final static int HISTOGRAM    = 0;
    public final static int SCATTER_2D   = 1;
    public final static int PROJECT_XY   = 2;
    public final static int PROFILE      = 3;
    
    public static AbstractProjection projection1D(MutableTupleColumn column) {
        Class colType = column.type();
        if (colType.isPrimitive())
            if ( colType == Boolean.TYPE || colType == Character.TYPE )
                return new ProjectObject1D(column);
            else
                return new Project1D(column);
        else if (Date.class.isAssignableFrom(colType)) return new Project1D(column);
        else return new ProjectObject1D(column);
    }
    
    public static AbstractProjection projection2D(MutableTupleColumn columnx, MutableTupleColumn columny, int type) {
        switch ( type ) {
            case HISTOGRAM:
                return new Project2D(columnx,columny);
            case SCATTER_2D:
                return new Scatter2D(columnx,columny);
            case PROJECT_XY:
                return new ProjectXY(columnx,columny);
            case PROFILE:
                return new Profile(columnx,columny);
            default:
                throw new IllegalArgumentException("** Unknown type "+type);
        }
    }
}
