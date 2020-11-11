package org.freehep.jas.plugin.xmlio;

import org.freehep.xml.io.XMLIOManager;
import org.freehep.jas.plugin.xmlio.XMLPluginIO;
import org.freehep.xml.io.XMLIOFactory;
import org.freehep.xml.io.XMLIOProxy;
import org.jdom.Element;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
class XMLIOAdapter implements XMLIOProxy, XMLIOFactory {
    
    private XMLPluginIO xmlPluginIO;
    private XMLIOManager xmlioManager;
    private int level = 0;
    private Element pluginEl = null;
    private Class[] classes = new Class[1];
    
    protected XMLIOAdapter(XMLPluginIO xmlPluginIO) {
        this.xmlPluginIO = xmlPluginIO;
        classes[0] = xmlPluginIO.getClass();
    }
    
    protected Object plugin() {
        return xmlPluginIO;
    }
    
    protected int level() {
        return level;
    }
    
    protected void restoreNextLevel() {
        restore( level() );
    }
    
    private void restore( int level ) {
        this.level = xmlPluginIO.restore( level, xmlioManager, pluginEl );
    }
        
    public Class[] XMLIOProxyClasses() {
        return classes;
    }
    
    public void restore(Object obj, XMLIOManager xmlioManager, Element pluginEl) throws IllegalArgumentException {
        this.level = xmlPluginIO.restore( XMLPluginIO.RESTORE_DATA, xmlioManager, pluginEl );
        this.pluginEl = pluginEl;
        this.xmlioManager = xmlioManager;
    }
    
    public void save(Object obj, XMLIOManager xmlioManager, Element pluginEl) throws IllegalArgumentException {
        xmlPluginIO.save(xmlioManager, pluginEl);
    }
    
    public Class[] XMLIOFactoryClasses() {
        return classes;
    }
    
    public Object createObject(Class objClass) throws IllegalArgumentException {
        if ( objClass != classes[0] ) throw new IllegalArgumentException("Illegal class "+objClass+" while restoring. Please report this problem.");
        return xmlPluginIO;
    }
        
}
