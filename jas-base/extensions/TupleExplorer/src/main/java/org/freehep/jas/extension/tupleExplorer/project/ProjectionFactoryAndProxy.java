package org.freehep.jas.extension.tupleExplorer.project;

import org.freehep.jas.extension.tupleExplorer.TupleExplorerPlugin;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection1D;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection2D;
import org.freehep.jas.extension.tupleExplorer.project.Profile;
import org.freehep.jas.extension.tupleExplorer.project.Project1D;
import org.freehep.jas.extension.tupleExplorer.project.Project2D;
import org.freehep.jas.extension.tupleExplorer.project.ProjectObject1D;
import org.freehep.jas.extension.tupleExplorer.project.ProjectXY;
import org.freehep.jas.extension.tupleExplorer.project.Scatter2D;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.xml.io.XMLIOFactory;
import org.freehep.xml.io.XMLIOManager;
import org.freehep.xml.io.XMLIOProxy;
import org.jdom.Element;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class ProjectionFactoryAndProxy implements XMLIOFactory, XMLIOProxy {
    
    private Class[] classes = {Profile.class, Project1D.class, Project2D.class, ProjectObject1D.class, ProjectXY.class, Scatter2D.class};

    public Class[] XMLIOProxyClasses() {
        return classes;
    }
    
    public void restore(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        MutableTupleTree tupleTree = (MutableTupleTree) xmlioManager.restore( (Element) nodeEl.getChildren().get(0) );
        if ( obj instanceof AbstractProjection1D ) {
            AbstractProjection1D proj = (AbstractProjection1D) obj;
            MutableTupleColumn col = tupleTree.mutableTupleColumnForPath( new FTreePath(nodeEl.getAttributeValue("path")) );
            proj.setColumn(col);
        } else if ( obj instanceof AbstractProjection2D ) {
            AbstractProjection2D proj = (AbstractProjection2D) obj;
            MutableTupleColumn colx = tupleTree.mutableTupleColumnForPath( new FTreePath(nodeEl.getAttributeValue("pathX")) );
            MutableTupleColumn coly = tupleTree.mutableTupleColumnForPath( new FTreePath(nodeEl.getAttributeValue("pathY")) );
            proj.setColumns(colx,coly);
        } else
            throw new IllegalArgumentException("Cannot restore object of type "+obj.getClass());
    }
    
    public void save(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        if ( obj instanceof AbstractProjection1D ) {
            AbstractProjection1D proj = (AbstractProjection1D) obj;
            nodeEl.addContent( xmlioManager.save( proj.column().parent().mutableTupleTree() ));
            nodeEl.setAttribute("path",proj.column().treePath().toString());
        } else if ( obj instanceof AbstractProjection2D ) {
            AbstractProjection2D proj = (AbstractProjection2D) obj;
            nodeEl.addContent( xmlioManager.save( proj.columnX().parent().mutableTupleTree() ));
            nodeEl.setAttribute("pathX",proj.columnX().treePath().toString());
            nodeEl.setAttribute("pathY",proj.columnY().treePath().toString());
        } else
            throw new IllegalArgumentException("Cannot save object of type "+obj.getClass());
    }
    
    public Class[] XMLIOFactoryClasses() {
        return classes;
    }
    
    public Object createObject(Class objClass) throws IllegalArgumentException {
        if ( objClass == Profile.class ) 
            return new Profile();
        else if ( objClass == Project1D.class )
            return new Project1D();
        else if ( objClass == Project2D.class )
            return new Project2D();
        else if ( objClass == ProjectObject1D.class )
            return new ProjectObject1D();
        else if ( objClass == ProjectXY.class )
            return new ProjectXY();
        else if ( objClass == Scatter2D.class )
            return new Scatter2D();
        else
            throw new IllegalArgumentException("Cannot create object of type "+objClass);
    }
    
}
