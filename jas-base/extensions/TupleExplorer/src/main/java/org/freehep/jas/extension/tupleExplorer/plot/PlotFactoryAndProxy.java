package org.freehep.jas.extension.tupleExplorer.plot;

import java.util.List;
import org.freehep.jas.extension.tupleExplorer.TupleExplorerPlugin;
import org.freehep.jas.extension.tupleExplorer.cut.CutSet;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeCutSet;
import org.freehep.jas.extension.tupleExplorer.plot.Plot;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection;
import org.freehep.xml.io.XMLIOFactory;
import org.freehep.xml.io.XMLIOManager;
import org.freehep.xml.io.XMLIOProxy;
import org.jdom.Element;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class PlotFactoryAndProxy implements XMLIOFactory, XMLIOProxy {

    private Class[] classes = {Plot.class};
    
    public Class[] XMLIOProxyClasses() {
        return classes;
    }
    
    public void restore(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        Plot plot = (Plot)obj;
        String plotName = nodeEl.getAttributeValue("name");
        plot.setName(plotName);
        List plotComponents = nodeEl.getChildren();
        AbstractProjection projection = (AbstractProjection) xmlioManager.restore( (Element) plotComponents.get(0) );
        plot.setProjection(projection);
        MutableTupleTreeCutSet cutSet = (MutableTupleTreeCutSet) xmlioManager.restore( (Element) plotComponents.get(1) );
        MutableTupleTree tupleTree = (MutableTupleTree) xmlioManager.restore( (Element) plotComponents.get(2) );
        plot.setTupleAndCuts( cutSet, tupleTree );
    }
    
    public void save(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        Plot plot = (Plot)obj;
        nodeEl.setAttribute( "name", plot.getName() );
        nodeEl.addContent( xmlioManager.save(plot.getProjection()) );
        nodeEl.addContent( xmlioManager.save(plot.getCuts()));
        nodeEl.addContent( xmlioManager.save(plot.getTuple()));
    }
    
    public Class[] XMLIOFactoryClasses() {
        return classes;
    }
    
    public Object createObject(Class objClass) throws IllegalArgumentException {
        if ( objClass == Plot.class ) 
            return new Plot();
        throw new IllegalArgumentException("Cannot create object of type "+objClass);
    }
    
}
