package org.freehep.jas.plugin.xmlio;

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeSet;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;
import org.freehep.application.ProgressMeter;
import org.freehep.application.PropertyUtilities;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.mdi.PageManager;
import org.freehep.jas.plugin.xmlio.XMLIOAdapter;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.PluginInfo;
import org.freehep.application.studio.Studio;
import org.freehep.application.studio.pluginmanager.PluginManager;
import org.freehep.jas.plugin.plotter.PlotterPlugin;
import org.freehep.jas.plugin.tree.FTreePlugin;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.jas.services.ProgressMeterProvider;
import org.freehep.swing.ExtensionFileFilter;
import org.freehep.util.FreeHEPLookup;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.xml.io.DefaultXMLIORegistry;
import org.freehep.xml.io.XMLIO;
import org.freehep.xml.io.XMLIOFactory;
import org.freehep.xml.io.XMLIOFileManager;
import org.freehep.xml.io.XMLIOManager;
import org.freehep.xml.io.XMLIOProxy;
import org.freehep.xml.io.XMLIORegistry;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.openide.util.Lookup;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class XMLIOPlugin extends Plugin implements PreferencesTopic {
    
    private Studio app;
    private XMLIOManager xmlioManager;
    private FreeHEPLookup lookup;
    private ArrayList xmlioPlugins = new ArrayList();
    private ArrayList xmlioHelpers = new ArrayList();
    private ArrayList adapters = new ArrayList();
    private AdapterComparator comparator = new AdapterComparator();
    private ProgressMeterProvider progressMeterProvider;
    private ProgressMeter meter;
    private Properties userProperties;
    private FileFilter fileFilter = new ExtensionFileFilter("xml");
    private XMLIOCommands xmlioCommands;
    private boolean wasSuccessful = true;
    
    protected void init()  throws org.xml.sax.SAXException, java.io.IOException {
        app = getApplication();
        
        userProperties = app.getUserProperties();
        
        lookup = app.getLookup();
        lookup.add(this);
        
        // Build the menus
        XMLMenuBuilder builder = app.getXMLMenuBuilder();
        URL xml = getClass().getResource("XMLIOPlugin.menus");
        builder.build(xml);
        
        // Add the commands on the toolbar
        xmlioCommands = new XMLIOCommands(this);
        getApplication().getCommandTargetManager().add( xmlioCommands );
        
        lookup.add( new ObjectFactoryAndProxy(this) );
        
        progressMeterProvider = (ProgressMeterProvider)lookup.lookup( ProgressMeterProvider.class );
        
    }
    
    
    public void restoreAtStart() {
        if ( getRestoreAtStart() )
            xmlioCommands.onXMLIORestoreConfiguration();
    }
    
    public boolean saveAtExit() {
        if ( getSaveAtExit() )
            xmlioCommands.onXMLIOSaveConfiguration();
        return wasSuccessful;
    }
    
    private void loadXMLIOPlugins() {
        Lookup.Result result = lookup.lookup( new Lookup.Template( XMLPluginIO.class ) );
        Iterator plugins = result.allInstances().iterator();
        while( plugins.hasNext() ) {
            XMLPluginIO xmlPluginIO = (XMLPluginIO) plugins.next();
            XMLIOAdapter adapter = new XMLIOAdapter( xmlPluginIO );
            xmlioPlugins.add( xmlPluginIO );
            adapters.add(adapter);
            xmlioManager.getXMLIORegistry().register( adapter );
        }
        
        Lookup.Result fResult = lookup.lookup( new Lookup.Template( XMLIOFactory.class ) );
        Iterator factories = fResult.allInstances().iterator();
        while( factories.hasNext() )
            xmlioHelpers.add(factories.next() );
        
        Lookup.Result pResult = lookup.lookup( new Lookup.Template( XMLIOProxy.class ) );
        Iterator proxyies = pResult.allInstances().iterator();
        while( proxyies.hasNext() ) {
            Object obj = proxyies.next();
            if ( ! xmlioHelpers.contains(obj) )
                xmlioHelpers.add( obj );
        }
        
        for ( int i = 0; i < xmlioHelpers.size(); i++ ) {
            xmlioManager.getXMLIORegistry().register( xmlioHelpers.get(i) );
        }
    }
    
    private void clear() {
        xmlioPlugins.clear();
        adapters.clear();
        xmlioHelpers.clear();
        xmlioManager = null;
        wasSuccessful = true;
    }
    
    private class AdapterComparator implements Comparator {
        
        public int compare(Object o1, Object o2) {
            XMLIOAdapter adapter1 = (XMLIOAdapter)o1;
            XMLIOAdapter adapter2 = (XMLIOAdapter)o2;
            if ( adapter1.level() > adapter2.level() ) return 1;
            if ( adapter1.level() == adapter2.level() ) return 0;
            return -1;
        }
        
        public boolean equals(Object o) {
            return false;
        }
    }
    
    private class EmptyRun implements Runnable {
        private XMLIOAdapter adapter;
        
        public EmptyRun(XMLIOAdapter adapter) {
            this.adapter = adapter;
        }
        
        public void run() {
            adapter.restoreNextLevel();
        }
    }
    
    private void startShowProgress() {
        meter = progressMeterProvider.getProgressMeter();
        meter.setShowStopButton(false);
        meter.setVisible(true);
        meter.setModel(new MyBoundedRangeModel());
    }
    
    private class MyBoundedRangeModel extends DefaultBoundedRangeModel {
        public void setValue(int value) {
            super.setValue(value);
            if ( value == 100 ) {
                meter.setVisible(false);
                progressMeterProvider.freeProgressMeter(meter);
            }
        }
    }
    
    private boolean restoreNextLevel() {
        Collections.sort(adapters,comparator);
        meter.getModel().setValue( (int) (100*(1-(1.*adapters.size())/(1.*xmlioPlugins.size()))) );
        if ( adapters.size() == 0 ) {
            if ( ! wasSuccessful )
                SwingUtilities.invokeLater( new Runnable() {
                    public void run() {
                        JOptionPane.showMessageDialog(app,"There was a problem restoring the configuration. The restored state might be incomplete.","Error",JOptionPane.ERROR_MESSAGE);
                    }
                });
                
                return false;
        }
        final XMLIOAdapter adapter = (XMLIOAdapter)adapters.get(0);
        int level = adapter.level();
        if ( level != XMLPluginIO.RESTORE_DONE ) {
            EmptyRun run = new EmptyRun( adapter );
            try {
                SwingUtilities.invokeAndWait( run );
            }  catch (Throwable e) {
                e.printStackTrace();
                wasSuccessful = false;
                adapters.remove(adapter);
            }
        } else
            adapters.remove(adapter);
        return true;
    }
    
    protected String chooseFile(File oldFile, String buttonText) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory( oldFile );
        fileChooser.setSelectedFile( oldFile );
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setApproveButtonText(buttonText);
        // Use Open / Save Dialog because MacOS X will not allow you to select a non-existing file using open.
        int result = buttonText.equals("Restore") ? fileChooser.showOpenDialog(app) : fileChooser.showSaveDialog(app);
        if (result == JFileChooser.APPROVE_OPTION ) {
            File file = fileChooser.getSelectedFile();
            if ( file != null )
                return file.getAbsolutePath();
        }
        return null;
    }
    
    public class XMLIOCommands extends CommandProcessor {
        
        private XMLIOPlugin thePlugin;
        
        public XMLIOCommands(XMLIOPlugin thePlugin) {
            this.thePlugin = thePlugin;
        }
        
        public void onXMLIOSaveConfiguration() {
            saveConfiguration( getDefaultFile() );
        }
        
        public void onXMLIOSaveConfigurationAs() {
            String fileName = thePlugin.chooseFile( new File( getLastFile() ), "Save" );
            if ( fileName != null ) {
                setLastFile( fileName );
                saveConfiguration( fileName );
            }
        }
        
        public void onXMLIORestoreConfiguration() {
            restoreConfiguration( getDefaultFile() );
        }
        
        public void onXMLIORestoreConfigurationFrom() {
            String fileName = thePlugin.chooseFile( new File( getLastFile() ), "Restore" );
            if ( fileName != null ) {
                setLastFile( fileName );
                restoreConfiguration( fileName );
            }
        }
        
        public void enableXMLIOSaveConfiguration(CommandState state) {
            state.setEnabled( true );
        }
        public void enableXMLIOSaveConfigurationAs(CommandState state) {
            state.setEnabled( true );
        }
        
        public void enableXMLIORestoreConfiguration(CommandState state) {
            state.setEnabled( true );
        }
        
        public void enableXMLIORestoreConfigurationFrom(CommandState state) {
            state.setEnabled( true );
        }
        
        private void saveConfiguration( String outputFileName ) {
            clear();
            xmlioManager = new XMLIOManager(outputFileName);
            loadXMLIOPlugins();
            Element rootEl = new Element("JAS3Configuration");
            rootEl.setAttribute("version",app.getAppProperties().getProperty("version"));
            try {
                for ( int i = 0; i < xmlioPlugins.size(); i++ ) {
                    Object obj = xmlioPlugins.get(i);
                    rootEl.addContent( xmlioManager.save(xmlioPlugins.get(i) ) );
                }
                
                Element pageManagerEl = new Element("PageManager");
                rootEl.addContent( xmlioManager.saveAs( app.getPageManager(), PageManager.class ) );
            } catch ( Throwable t ) {
                t.printStackTrace();
                wasSuccessful = false;
                return;
            }
            try {
                xmlioManager.getXMLIOStreamManager().saveRootElement(rootEl);
            } catch ( IOException ioe ) {
                ioe.printStackTrace();
                wasSuccessful = false;
            }
            if ( ! wasSuccessful ) {
                JOptionPane.showMessageDialog(app,"There was an error saving the configuration.","Error",JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void restoreConfiguration( String outputFileName ) {
            clear();
            xmlioManager = new XMLIOManager(outputFileName);
            startShowProgress();
            Thread waitThread = new Thread() {
                public void run() {
                    loadXMLIOPlugins();
                    try {
                        SwingUtilities.invokeAndWait( new Runnable() {
                            public void run() {
                                xmlioManager.restoreFromXML();
                            }
                        });
                    }  catch (Throwable e) {
                        e.printStackTrace();
                        wasSuccessful = false;
                    }
                    while( restoreNextLevel() ) {}
                }
            };
            waitThread.start();
        }
    }
    
    
    private class ObjectFactoryAndProxy implements XMLIOFactory, XMLIOProxy {
        
        private XMLIOPlugin thePlugin;
        private Class[] classes = {PageManager.class};
        
        public ObjectFactoryAndProxy( XMLIOPlugin thePlugin ) {
            this.thePlugin = thePlugin;
        }
        
        public Class[] XMLIOFactoryClasses() {
            return classes;
        }
        
        public Class[] XMLIOProxyClasses() {
            return classes;
        }
        
        public Object createObject(Class objClass) throws IllegalArgumentException {
            if ( objClass == PageManager.class )
                return thePlugin.getApplication().getPageManager();
            throw new IllegalArgumentException();
        }
        
        public void restore(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        }
        
        public void save(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
            List pages = thePlugin.getApplication().getPageManager().pages();
            for ( int i = 0; i < pages.size(); i++ ) {
                PageContext page = (PageContext) pages.get(i);
                Element pageEl = new Element("Page");
                pageEl.setAttribute("name",page.getTitle());
                nodeEl.addContent(pageEl);
            }
        }
    }
    
    
    // Preferences
    public boolean apply(JComponent panel) {
        ((XMLIOPluginPrefsDialog) panel).apply(this);
        return true;
    }
    
    public JComponent component() {
        return new XMLIOPluginPrefsDialog(this);
    }
    
    public String[] path() {
        return new String[]{"Save/Restore"};
    }
    
    private final static String XMLIOPLUGIN_DEFAULT_FILE     = "org.freehep.jas.extension.xmlioplugin.DefaultFile";
    private final static String XMLIOPLUGIN_LAST_FILE        = "org.freehep.jas.extension.xmlioplugin.LastFile";
    private final static String XMLIOPLUGIN_RESTORE_AT_START = "org.freehep.jas.extension.xmlioplugin.RestoreAtStart";
    private final static String XMLIOPLUGIN_SAVE_AT_EXIT     = "org.freehep.jas.extension.xmlioplugin.SaveAtExit";
    
    protected String getDefaultFile() {
        return PropertyUtilities.getString(userProperties, XMLIOPLUGIN_DEFAULT_FILE, System.getProperty("user.home")+"/.JAS3/JAS3Configuration.xml");
    }
    protected void setDefaultFile(String defaultFile) {
        userProperties.setProperty(XMLIOPLUGIN_DEFAULT_FILE, defaultFile);
    }
    
    protected String getLastFile() {
        return PropertyUtilities.getString(userProperties, XMLIOPLUGIN_LAST_FILE, System.getProperty("user.home")+"/.JAS3/JAS3Configuration.xml");
    }
    protected void setLastFile(String lastFile) {
        userProperties.setProperty(XMLIOPLUGIN_LAST_FILE, lastFile);
    }
    
    protected boolean getRestoreAtStart() {
        return PropertyUtilities.getBoolean(userProperties, XMLIOPLUGIN_RESTORE_AT_START, false);
    }
    protected void setRestoreAtStart(boolean restoreAtStart) {
        userProperties.setProperty(XMLIOPLUGIN_RESTORE_AT_START, Boolean.toString(restoreAtStart));
    }
    
    protected boolean getSaveAtExit() {
        return PropertyUtilities.getBoolean(userProperties, XMLIOPLUGIN_SAVE_AT_EXIT, false);
    }
    protected void setSaveAtExit(boolean saveAtExit) {
        userProperties.setProperty(XMLIOPLUGIN_SAVE_AT_EXIT, Boolean.toString(saveAtExit));
    }
}