package org.freehep.jas.plugin.xmlio;

import java.io.File;
import org.freehep.xml.io.XMLIOFactory;
import org.freehep.xml.io.XMLIOManager;
import org.freehep.xml.io.XMLIOProxy;
import org.jdom.Element;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class XMLIOFileProxyAndFactory implements XMLIOProxy, XMLIOFactory {
    
    private Class[] proxyClasses;
    private Class[] factoryClasses;

    public XMLIOFileProxyAndFactory() {
        proxyClasses = new Class[1];
        proxyClasses[0] = File.class;
        factoryClasses = new Class[1];
        factoryClasses[0] = File.class; 
    }
    
    public Class[] XMLIOProxyClasses() {
        return proxyClasses;
    }
        
    public Class[] XMLIOFactoryClasses() {
        return factoryClasses;
    }

    public void restore(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        obj = new File( nodeEl.getAttributeValue( "pathName" ) );
    }
    
    public void save(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        nodeEl.setAttribute( "pathName", String.valueOf( ((File)obj).getPath() ) );
    }
    
    public Object createObject(Class objClass) {
        if ( objClass == File.class ) return new File("");
        else throw new IllegalArgumentException("Cannot create object of class "+objClass);
    }
    
    
}
