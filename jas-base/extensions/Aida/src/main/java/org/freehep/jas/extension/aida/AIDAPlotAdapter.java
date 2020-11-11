package org.freehep.jas.extension.aida;

import hep.aida.ICloud;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IHistogram;
import hep.aida.IManagedObject;
import hep.aida.IProfile;
import hep.aida.ref.plotter.adapter.AIDAAdapter;
import jas.hist.DataSource;
import java.awt.Component;
import javax.swing.JPopupMenu;
import org.freehep.application.studio.Studio;
import org.freehep.jas.plugin.plotter.JAS3DataSource;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.services.PlotterAdapter;
import org.freehep.xml.io.XMLIO;
import org.freehep.xml.io.XMLIOFactory;
import org.freehep.xml.io.XMLIOManager;
import org.jdom.Element;
import hep.aida.ref.plotter.adapter.AIDADataPointSetAdapter;
import hep.aida.ref.plotter.adapter.AIDACloudAdapter1D;
import hep.aida.ref.plotter.adapter.AIDAHistogramAdapter1D;
import hep.aida.ref.plotter.adapter.AIDAProfileAdapter1D;
import org.freehep.swing.popup.HasPopupItems;

/**
 * Adapter that wraps AIDA classes to make them plottable as {@link JAS3DataSource}.
 */
public class AIDAPlotAdapter implements PlotterAdapter<JAS3DataSource,Object> {
    
    private AIDAPlugin thePlugin;
    private static boolean factoryRegistered = false;
    
    protected AIDAPlotAdapter(AIDAPlugin thePlugin, Studio app) {
        this.thePlugin = thePlugin;
        
        // FIX TO JAS-258
        if ( ! factoryRegistered ) {
            factoryRegistered = true;
            app.getLookup().add( new JAS3DataSourceConverterFactory(thePlugin) );
        }
    }
    
    @Override
    public JAS3DataSource adapt(Object obj) {
        return new JAS3DataSourceConverter(thePlugin,obj);
    }
        
    protected class JAS3DataSourceConverter implements JAS3DataSource, XMLIO {
        
        private DataSource data;
        private Object obj;
        private AIDAPlugin thePlugin;
        
        JAS3DataSourceConverter( AIDAPlugin thePlugin, Object obj ) {
            setObject(obj);
            this.thePlugin = thePlugin;
        }
        
        protected void loadDataSource( Object obj ) {
            if ( obj instanceof DataSource )
                data = (DataSource) obj;
            else if (obj instanceof IProfile)
                this.data = AIDAAdapter.create((IProfile) obj);
            else if (obj instanceof ICloud)
                this.data =  AIDAAdapter.create((ICloud) obj);
            else if (obj instanceof IHistogram)
                this.data =  AIDAAdapter.create((IHistogram) obj);
            else if (obj instanceof IDataPointSet)
                this.data =  AIDAAdapter.create((IDataPointSet) obj);
            else if (obj instanceof IFunction)
                this.data =  AIDAAdapter.create((IFunction) obj);
            else throw new IllegalArgumentException("Invalid object "+obj.getClass());
        }
        
        protected Object object() {
            return obj;
        }
        
        protected void setObject( Object obj ) {
            if ( obj != null ) {
                this.obj = obj;
                loadDataSource(obj);
            }
        }
        
        public DataSource dataSource() {
            return data;
        }
        
        public void destroy() {
        }
        
        @Override
        public void modifyPopupMenu(JPopupMenu jPopupMenu, Component component) {
            try {
                HasPopupItems plottedObject = (HasPopupItems) obj;
                plottedObject.modifyPopupMenu(jPopupMenu, component, null);
            } catch (ClassCastException x) {
            }
        }
        
        public void restore(XMLIOManager xmlioManager, Element nodeEl) {
            String path = nodeEl.getAttributeValue("path");
            if ( path != null )
                setObject( thePlugin.aidaMasterTree().find( path ) );
        }
        
        public void save(XMLIOManager xmlioManager, Element nodeEl) {
            try {
                if ( obj instanceof IManagedObject ) {
                String path = thePlugin.aidaMasterTree().findPath((IManagedObject)obj);
                nodeEl.setAttribute("path",path);
                }
            } catch ( Exception e ) {}
        }
        
        /** Get the paths to the data, if available. */
        public FTreePath path() {
            if ( obj instanceof IManagedObject ) {
                return thePlugin.pathForManagedObject( (IManagedObject)object() );
            }
            return new FTreePath("");
        }
        
        public String[] axisLabels() {
            return null;
        }
        
        /** This method was added to fulfill BaBar's needs to have
         * DataPointSets with Dates on the axis.
         */
        public void setAxisType(int type) {
            if ( data instanceof AIDADataPointSetAdapter ) 
                ( (AIDADataPointSetAdapter) data ).setAxisType(type);
            else if ( data instanceof AIDAHistogramAdapter1D ) 
                ( (AIDAHistogramAdapter1D) data ).setAxisType(type);
            else if ( data instanceof AIDACloudAdapter1D ) 
                ( (AIDACloudAdapter1D) data ).setAxisType(type);
            else if ( data instanceof AIDAProfileAdapter1D ) 
                ( (AIDAProfileAdapter1D) data ).setAxisType(type);
        }
        
    }

    private class JAS3DataSourceConverterFactory implements XMLIOFactory {
        
        private AIDAPlugin thePlugin;
        
        JAS3DataSourceConverterFactory(AIDAPlugin thePlugin) {
            this.thePlugin = thePlugin;
        }
        
        private Class[] classes = {JAS3DataSourceConverter.class};
        
        public Class[] XMLIOFactoryClasses() {
            return classes;
        }
        
        public Object createObject(Class objClass) throws IllegalArgumentException {
            if ( objClass == JAS3DataSourceConverter.class )
                return new JAS3DataSourceConverter(thePlugin, null);
            throw new IllegalArgumentException("Cannot create class "+objClass);
        }
    }
}
