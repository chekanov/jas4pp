package org.freehep.jas.extension.aida;

import hep.aida.*;

import hep.aida.ref.*;
import hep.aida.ref.tree.*;

import hep.aida.ref.event.*;
import hep.aida.ref.plotter.DummyPlotterFactory;
import hep.aida.ref.tree.Folder;
import hep.aida.ref.tree.MountPoint;
import hep.aida.ref.tree.Tree;
import hep.aida.ref.tuple.FTuple;
import jas.hist.FunctionRegistry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.freehep.application.PropertyUtilities;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.application.studio.StudioListener;

import org.freehep.jas.event.*;
import org.freehep.jas.extension.aida.adapter.DataPointSetAdapter;
import org.freehep.jas.extension.aida.adapter.FolderAdapter;
import org.freehep.jas.extension.aida.adapter.FunctionAdapter;
import org.freehep.jas.extension.aida.adapter.HistogramAdapter;
import org.freehep.jas.extension.aida.adapter.ManagedObjectAdapter;
import org.freehep.jas.extension.aida.adapter.MountPointAdapter;
import org.freehep.jas.extension.aida.adapter.PlottableAdapter;
import org.freehep.jas.extension.aida.fitter.FitterFactoryAdapter;
import org.freehep.jas.extension.aida.fitter.JAS3FitterFactory;
import org.freehep.jas.extension.aida.function.ConstantFunctionFactory;
import org.freehep.jas.extension.aida.function.ExponentialFunctionFactory;
import org.freehep.jas.extension.aida.function.GaussianFunctionFactory;
import org.freehep.jas.extension.aida.function.MoyalFunctionFactory;
import org.freehep.jas.extension.aida.function.LorentzianFunctionFactory;
import org.freehep.jas.extension.aida.function.JAS3FunctionFactory;
import org.freehep.jas.extension.aida.function.LineFunctionFactory;
import org.freehep.jas.extension.aida.function.ParabolaFunctionFactory;
import org.freehep.jas.extension.aida.function.SumFunctionFactory;
import org.freehep.jas.plugin.plotter.JAS3DataSource;
import org.freehep.jas.plugin.tree.FTree;
import org.freehep.jas.plugin.tree.FTreeNodeAddedNotification;
import org.freehep.jas.plugin.tree.FTreeNodeMovedNotification;
import org.freehep.jas.plugin.tree.FTreeNodeRemovedNotification;
import org.freehep.jas.plugin.tree.FTreeNodeRepaintNotification;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.plugin.tree.FTreeProvider;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapterProxy;
import org.freehep.jas.plugin.tree.utils.linkNode.LinkNode;
import org.freehep.jas.plugin.xmlio.XMLPluginIO;
import org.freehep.jas.plugin.datasource.FileHandlerDataSource;
import org.freehep.jas.services.FileHandler;
import org.freehep.jas.services.PlotFactory;
import org.freehep.jas.services.PlotPage;
import org.freehep.jas.services.Plotter;
import org.freehep.jas.services.PlotterAdapter;
import org.freehep.jas.services.PlotterProvider;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.jas.services.ScriptEngine;
import org.freehep.jas.services.URLHandler;

import org.freehep.swing.ExtensionFileFilter;
import org.freehep.util.FreeHEPLookup;
import org.freehep.xml.io.XMLIOFactory;
import org.freehep.xml.io.XMLIOManager;
import org.jdom.Element;

import org.xml.sax.SAXException;

/** A JAS3 Plugin for AIDA.
 * <p>
 * This plugin implements {@link PlotterProvider} that creates plotters (instances of
 * {@link AIDAPlot}) for plotting objects of types <tt>IBaseHistogram</tt>,
 * <tt>IDataPointSet</tt>, and <tt>IFunction</tt>. It also registers an instance of
 * {@link AIDAPlotAdapter} as a {@link PlotterAdapter} for converting objects of these 
 * types into instances of {@link JAS3DataSource}.
 * 
 * @author tonyj
 * @version $Id: AIDAPlugin.java 16235 2015-02-20 04:37:31Z onoprien $
 */
public class AIDAPlugin extends Plugin implements StudioListener, FileHandler, URLHandler, PlotterProvider, XMLPluginIO, PreferencesTopic {
    
    private static AIDAPlugin thePlugin;
    private IAnalysisFactory factory;
    private Tree aidaMasterTree;
    private Hashtable openFiles = new Hashtable();
    private Hashtable treeNameHash = new Hashtable();
    private PlotFactory plotFactory;
    private String defaultTreeName = "tree";
    private static String[] preferencesPath = { "AIDA", "General" };
    private Properties userProperties;
    private FTree masterTree;
    private FreeHEPLookup lookup;
    private Studio app;
    
// -- Plugin life cycle : ------------------------------------------------------
    
    @Override
    protected void init() throws SAXException, IOException {
        thePlugin = this;
        app = getApplication();
        lookup = app.getLookup();
        lookup.add(this);
        lookup.add(new FileHandlerDataSource(this));
    }
    
    @Override
    protected void postInit() {
 
        // Create a JAS3AnalysisFactory, and register it as "the analysis factory"
        System.setProperty("hep.aida.IAnalysisFactory", JASAnalysisFactory.class.getName());
        factory = new JASAnalysisFactory();
        lookup.add(factory);
        
        // Register HTMLComponentFactory for AIDA objects
        AIDAHTMLComponentFactory htmlFactory = new AIDAHTMLComponentFactory();
        htmlFactory.init(app);
        
        // Create a default tree
        aidaMasterTree = (Tree) factory.createTreeFactory().create();
        lookup.add(aidaMasterTree);
        
        // Create Aida utility
//        lookup.add(new Aida(aidaMasterTree, factory.createHistogramFactory(aidaMasterTree)));
        
        FTreeProvider treeProvider = ( (FTreeProvider) getApplication().getLookup().lookup(FTreeProvider.class) );
        masterTree = treeProvider.tree();
        FTreeAIDAListenerAdapter materTreeAidaAdapter =  new FTreeAIDAListenerAdapter(masterTree,aidaMasterTree);
        
        // Register the standard adapters
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new ManagedObjectAdapter(this,app), IManagedObject.class);
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new HistogramAdapter(this,app), IBaseHistogram.class);
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new FunctionAdapter(this,app), IFunction.class);
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new DataPointSetAdapter(this,app), IDataPointSet.class);
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new MountPointAdapter(this,app), MountPoint.class);
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new PlottableAdapter(this, app), IPlottable.class);
        
        FolderAdapter folderAdapter = new FolderAdapter(this,app);
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter(folderAdapter, Folder.class);

        // add FolderAdapter to the lookup to be able to retrieve it 
        // and set "showEmptyFolderAsLeaf" later, if needed
        lookup.add(folderAdapter);
        
        // Register an adapter to convert ITuple to FTuple
        treeProvider.treeNodeAdapterRegistry().registerNodeObjectProvider( new ITupleFTupleObjectProvider(), ITuple.class,  150 );
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter( new DefaultFTreeNodeAdapterProxy( FTuple.class ), ITuple.class );
        
        // Register to receive scriptEvents
        app.getEventSender().addEventListener(this, ScriptEvent.class);
        
        // Register an adapter for mounting AIDA Tree in FTreee directly, not in aidaMasterTree
        //treeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new FTreeMountPointAdapter(this, app), ITree.class);
        
        plotFactory = (PlotFactory) getApplication().getLookup().lookup(PlotFactory.class);
        PlotterAdapter adapter = new AIDAPlotAdapter(this,app);
        plotFactory.registerAdapter( adapter, IBaseHistogram.class, JAS3DataSource.class );
        plotFactory.registerAdapter( adapter, IFunction.class, JAS3DataSource.class );
        plotFactory.registerAdapter( adapter, IDataPointSet.class, JAS3DataSource.class );
        
        app.getLookup().add( new AIDAPlotFactory(this) );
        
        //Get the user properties
        userProperties = app.getUserProperties();
        
        FunctionRegistry.instance().registerFunction( new GaussianFunctionFactory() );
        FunctionRegistry.instance().registerFunction( new ExponentialFunctionFactory() );
        FunctionRegistry.instance().registerFunction( new ConstantFunctionFactory() );
        FunctionRegistry.instance().registerFunction( new LineFunctionFactory() );
        FunctionRegistry.instance().registerFunction( new ParabolaFunctionFactory() );
        FunctionRegistry.instance().registerFunction( new MoyalFunctionFactory() );
        FunctionRegistry.instance().registerFunction( new LorentzianFunctionFactory() );
        FunctionRegistry.instance().registerFunction( new SumFunctionFactory() );
                
        FitterFactoryAdapter.registerFitters(getApplication().getLookup());
    }
    
    
// -- Implementing FileHandler and URLHandler services : -----------------------
    
    @Override
    public FileFilter getFileFilter() {
        return new ExtensionFileFilter("aida", "AIDA File");
    }
    
    @Override
    public boolean accept(File file) throws IOException {
        return file.getName().endsWith(".aida");
    }
    
    @Override
    public void openFile(File file) throws IOException {
        boolean createNew = !file.exists();
        boolean readOnly = createNew ? false : !file.canWrite();
        openFile(file.getAbsolutePath(), "xml", readOnly, createNew,"");
    }
    
    private void openFile( String fileName, String fileType, boolean readOnly, boolean createNew, String options ) throws IOException {
        ITreeFactory tf = factory.createTreeFactory();
        Tree tree = (Tree)tf.create(fileName, fileType, readOnly, createNew, options);
    }
    
    @Override
    public boolean accept(URL url) throws IOException
    {
        return url.getFile().endsWith(".aida");
    }

    @Override
    public void openURL(URL url) throws IOException
    {
       //ToDo: This could be time consuming, should show a status window
       File temp = File.createTempFile("tmp","aida");
       temp.deleteOnExit();
       OutputStream out = new FileOutputStream(temp);
       InputStream input = url.openStream();
       try
       {

          byte[] buffer = new byte[8096];
          for (;;)
          {
             int l = input.read(buffer);
             if (l<0) break;
             out.write(buffer,0,l);
          }
       }
       finally
       {
          input.close();
          out.close();
       }
       int pos = url.getFile().lastIndexOf("/");
       String name = pos>0 ? url.getFile().substring(pos+1) : url.getFile();
       openFile(temp.getAbsolutePath(), "xml", true, false,"mountpoint="+name);
    }
    
    
// -- Implementing PlotterProvider : -------------------------------------------
    
    @Override
    public Plotter create() {
        return new AIDAPlot(plotFactory,this,getApplication());
    }
    
    @Override
    public boolean supports(Class klass) {
        return IBaseHistogram.class.isAssignableFrom(klass) ||
                IDataPointSet.class.isAssignableFrom(klass) ||
                IFunction.class.isAssignableFrom(klass); // ||
                //IPlottable.class.isAssignableFrom(klass);
    }
    

// -- Implementing PreferencesTopic : ------------------------------------------
    
    @Override
    public boolean apply(JComponent panel) {
        return ((AidaPreferencePanel) panel).apply();
    }
    
    @Override
    public JComponent component() {
        return new AidaPreferencePanel(this);
    }
    
    @Override
    public String[] path() {
        return preferencesPath;
    }
    
    
// -- Implementing XMLPluginIO : -----------------------------------------------
    
    @Override
    public int restore(int level, XMLIOManager manager, Element el) {
        switch(level) {
            case RESTORE_DATA:
                if (el == null) return RESTORE_DONE;
                List files = el.getChildren("file");
                if (files == null) return RESTORE_DONE;
                for ( int i = 0; i < files.size(); i++ ) {
                    Element fileEl = (Element) files.get(i);
                    String fileName = fileEl.getAttributeValue("name");
                    String fileType = fileEl.getAttributeValue("type");
                    String options = "mountpoint="+fileEl.getAttributeValue("mountpoint");
                    String treeOptions = fileEl.getAttributeValue("options");
                    if (treeOptions != null && !treeOptions.trim().equals("")) options += ", "+treeOptions;
                    try {
                        openFile( fileName, fileType, true, false, options );
                    } catch (IOException ioe ) {
                        JOptionPane.showMessageDialog(getApplication(),"Could not restore file: "+fileName,"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
                return RESTORE_DONE;
            default :
                throw new IllegalArgumentException("Illegal level "+level+". Please report this problem");
        }
    }
    
    @Override
    public void save(XMLIOManager manager, Element el) {
        Enumeration trees = treeNameHash.keys();
        while( trees.hasMoreElements() ) {
            String mountPoint = (String) trees.nextElement();
            try {
                aidaMasterTree.listObjectNames("/"+mountPoint);
                String fileName = (String) treeNameHash.get(mountPoint);
                if ( fileName != null ) {
                    Element fileEl = new Element("file");
                    fileEl.setAttribute("name",fileName);
                    String fileType = (String)openFiles.get(fileName);
                    if ( fileType != null )
                        fileEl.setAttribute("type",fileType);
                    fileEl.setAttribute("mountpoint",mountPoint);
                    try {
                        ITree t = aidaMasterTree.findTree("/"+mountPoint);
                        Map optMap = ((Tree) t).getOptions();
                        if (optMap != null && optMap.size() > 0) {
                            String options = AidaUtils.createOptionsString(optMap);
                            fileEl.setAttribute("options", options);
                        }
                    } catch (Exception e) { e.printStackTrace(); }
                    el.addContent( fileEl );
                }
            } catch ( IllegalArgumentException iae ) {
            }
        }
    }
    
    
// -----------------------------------------------------------------------------
    
    public void handleEvent(EventObject event) {
        ScriptEngine engine = ((ScriptEvent) event).getScriptEngine();
        engine.registerVariable("aidaMasterTree", aidaMasterTree);
    }
    
    public Tree aidaMasterTree() {
        return aidaMasterTree;
    }
    
    public Object findInTree(FTreePath path, Class clazz) {
        Object obj = masterTree.findNode(path).objectForClass(clazz);
        return obj;
    }
    
    public FTree tree() {
        return masterTree;
    }
    
    private IPlotterFactory createPlotterFactory() {
        return new JASPlotterFactory();
    }
    
    private ITreeFactory createTreeFactory(IAnalysisFactory analysisFactory) {
        return new JAS3TreeFactory(analysisFactory);
    }
    
    public static class JASAnalysisFactory extends AnalysisFactory {
        private ITreeFactory treeFactory = null;
        
        public IPlotterFactory createPlotterFactory() {
            return thePlugin.createPlotterFactory();
        }
        
        public ITreeFactory createTreeFactory() {
            if ( treeFactory == null ) treeFactory = thePlugin.createTreeFactory(this);
            return treeFactory;
        }
        
        public IFunctionFactory createFunctionFactory(ITree tree) {
            return new JAS3FunctionFactory(tree);
        }
        
        public IFitFactory createFitFactory() {
            return new JAS3FitterFactory();
        }
        
    }
    
    private class JAS3TreeFactory extends TreeFactory {
        
        private IAnalysisFactory analysisFactory;
        
        public JAS3TreeFactory(IAnalysisFactory analysisFactory) {
            super( analysisFactory );
            this.analysisFactory = analysisFactory;
        }
        
        public ITree createNamedTree(String name, String storeName, String storeType, int mode, String options) throws IllegalArgumentException, IOException {
            Tree tree = (Tree) super.createNamedTree(name,storeName,storeType, mode, options);
            initTree(tree);
            return tree;
        }
                
        protected ITree createTree(String storeName, String storeType, boolean readOnly, boolean createNew, String options, boolean readOnlyUserDefined) throws IllegalArgumentException, IOException {
            Tree tree = (Tree) super.createTree(storeName, storeType, readOnly, createNew, options, readOnlyUserDefined);
            initTree(tree);
            return tree;
        }
        
        private void initTree(Tree tree) {
            if ( tree.storeName() == null )
                mountInMasterTree(null,tree);
            else {
                Map optionsMap = tree.getOptions();
                if ( optionsMap != null ) {
                    String autoMount = (String) optionsMap.get("automount");
                    String mountInFTree = (String) optionsMap.get("mountinftree");
                    if (autoMount == null || autoMount.equalsIgnoreCase("true")) {
                        String mountPoint = (String) optionsMap.get("mountpoint");
                        if (mountInFTree == null || mountInFTree.equalsIgnoreCase("false")) {
                            mountInMasterTree(mountPoint, tree);
                        } else {
                            mountInFTree(mountPoint, (Tree) tree);
                        }
                    }
                    if ( tree.storeType() != null )
                        openFiles.put(tree.storeName(), tree.storeType());
                }
            }            
        }
        
        private void mountInFTree(String mountPoint, Tree tree) {
            String name = mountPoint;
            if (name == null) {
                String storeName = tree.storeName();
                if (storeName != null) {
                    int pos = storeName.lastIndexOf(File.separatorChar,storeName.length()-1);
                    if (pos >= 0) storeName = storeName.substring(pos+1);
                    pos = storeName.lastIndexOf("/",storeName.length()-1);
                    if (pos >= 0) storeName = storeName.substring(pos+1);
                    name = storeName;
                }
            }
            FTreePath fTreePath = new FTreePath(name);
            
            masterTree.treeChanged( new FTreeNodeAddedNotification(getApplication(), fTreePath, tree) );
            FTreeAIDAListenerAdapter2 materTreeAidaAdapter =  new FTreeAIDAListenerAdapter2(masterTree, tree, fTreePath);
        }
        
        private void mountInMasterTree(String mountPoint, ITree tree) {
           if (aidaMasterTree != null) {
                String name = mountPoint;
                if (name == null) {
                    String storeName = tree.storeName();
                    if (storeName != null) {
                        int pos = storeName.lastIndexOf(File.separatorChar,storeName.length()-1);
                        if (pos >= 0) storeName = storeName.substring(pos+1);
                        pos = storeName.lastIndexOf("/",storeName.length()-1);
                        if (pos >= 0) storeName = storeName.substring(pos+1);
                        name = storeName;
                    }
                }
                name = getMountPointName( name );
                aidaMasterTree.mount("/"+name, tree, "/");
                
                String storeName = tree.storeName();
                if ( storeName != null )
                    treeNameHash.put(name, storeName);
            }
        }
    }
    
    public String getMountPointName(String name) {
        if (name == null) name = defaultTreeName;
        int index = 0;
        String tmpName = "/"+name;
        String[] names = aidaMasterTree.listObjectNames("/");
        String[] types = aidaMasterTree.listObjectTypes("/");
        for ( int i = 0; i < names.length; i++ ) {
            if ( types[i].equals("mnt") ) {
                names[i] = names[i].substring(0, names[i].length()-1);
                if ( names[i].startsWith( tmpName ) ) {
                    int nameIndex = 0;
                    if ( names[i].equals( tmpName ) )
                        nameIndex = 1;
                    else
                        try {
                            nameIndex = Integer.parseInt( names[i].substring(tmpName.length()+1) )+1;
                        } catch ( NumberFormatException nfe) {
                        }
                    if ( nameIndex > index ) index = nameIndex;
                }
            }
        }
        if ( index != 0 || name.equals(defaultTreeName) ) name+="-"+index;
        return name;
    }
    
    private class JASPlotterFactory extends DummyPlotterFactory {
        public IPlotter create() {
            return create("Plotter");
        }
        
        public IPlotter create(String title) {
            PlotPage page = plotFactory.createPage(title);
            return new AIDAPlotter(plotFactory,page);
        }
    }
    
    public FTreePath pathForManagedObject( IManagedObject obj ) {
        try {
            String path = aidaMasterTree.findPath(obj);
            return new FTreePath( path );
        } catch ( IllegalArgumentException iae ) {
            if ( obj.name() != null )
                return new FTreePath( obj.name() );
            return new FTreePath( "" );
        }
    }
    
    public void checkForChildrenForNode( FTreePath path ) {
        aidaMasterTree.checkForChildren( fullPath(path) );
    }
    
    public String fullPath( FTreePath path ) {
        String pathName = "";
        int nNodes = path.getPathCount();
        for ( int i = 0; i < nNodes-1; i++ )
            pathName += "/" + path.getPathComponent(i);
        pathName += "/" + AidaUtils.modifyName(path.getLastPathComponent());
        return pathName;
    }
    
    public boolean isShowNamesAndTitles() {
        return PropertyUtilities.getBoolean(getApplication().getUserProperties(), "AIDA.ShowNamesAndTitles", true);
    }
    
    void setShowNamesAndTitles(boolean showNamesAndTitles) {
        PropertyUtilities.setBoolean(getApplication().getUserProperties(),"AIDA.ShowNamesAndTitles", showNamesAndTitles);
        masterTree.treeChanged( new FTreeNodeRepaintNotification(this, masterTree.root().path(), true) );
    }
    
    private class FTreeAIDAListenerAdapter implements AIDAListener {
        
        private FTree masterTree;
        private FTreePath currentPath;
        private Tree tree;
        private HashMap map = new HashMap();
        
        public FTreeAIDAListenerAdapter( FTree masterTree, Tree tree ) {
            this.masterTree = masterTree;
            this.tree = tree;
            
            tree.checkForChildren("/");
            tree.addListener(this);
        }
        
        private String fullName( String[] path ) {
            if (path.length>0) {
                StringBuffer b = new StringBuffer();
                for (int i = 0; i < path.length; i++) {
                    b.append('/');
                    b.append(path[i]);
                }
                return b.toString();
            } else return "/";
        }
        
        private String parentFullName( String[] path ) {
            if ( path.length == 0 ) return "/";
            String[] newPath = new String[path.length-1];
            System.arraycopy(path, 0, newPath, 0, newPath.length);
            return fullName(newPath);
        }
        
        
        public void stateChanged(EventObject evt) {
            
            TreeEvent e = (TreeEvent) evt;
            int id = e.getID();
            Class clazz = e.getType();
            String[] eventPath = e.getPath();
            
            if (eventPath.length > 0) {
                String tmp = AidaUtils.parseName(eventPath[eventPath.length-1]);
                eventPath[eventPath.length-1] = tmp;
            }
            if (id == e.NODE_ADDED)
                masterTree.treeChanged( new FTreeNodeAddedNotification(this, new FTreePath( e.getPath() ), clazz) );
            else if ( id == e.LINK_ADDED )
                masterTree.treeChanged( new FTreeNodeAddedNotification(this, new FTreePath( e.getPath() ), new AidaLink(new FTreePath(e.getLinkPath() ), masterTree) ) );
            else if (id == e.NODE_DELETED)
                masterTree.treeChanged( new FTreeNodeRemovedNotification(this, new FTreePath( e.getPath() )) );
            if (id == e.NODE_MOVED)
                masterTree.treeChanged( new FTreeNodeMovedNotification(this, new FTreePath( e.getOldPath() ), new FTreePath( e.getPath() )) );
            else if (id == e.CHANGE_DIRECTORY) {
                //Don't have to do anything.
            } else if ( id == e.TREE_CLOSED )
                aidaMasterTree.unmount((new FTreePath( e.getPath())).toString());
            else if ( id == e.NODE_AVAILABLE || id == e.NODE_UNAVAILABLE ) {
                masterTree.treeChanged( new FTreeNodeRepaintNotification(this, new FTreePath( e.getPath() ), true) );
                //System.out.println("AIDAPlugin.stateChanged :: id="+id+", path="+(new FTreePath( e.getPath() )).toString()+", class="+clazz);
            }
        }
    }
    
    // This class is used for adding AIDA Tree to FTree directly, 
    // not to the aidaMasterTree
    private class FTreeAIDAListenerAdapter2 implements AIDAListener {
        
        private FTree masterTree;
        private FTreePath mountPath;
        private Tree tree;
        private HashMap map = new HashMap();
        
        public FTreeAIDAListenerAdapter2( FTree masterTree, Tree tree, FTreePath mountPath) {
            this.masterTree = masterTree;
            this.tree = tree;
            this.mountPath = mountPath;
            
            tree.checkForChildren("/");
            tree.addListener(this);
        }
        
        public void stateChanged(EventObject evt) {
            
            TreeEvent e = (TreeEvent) evt;
            int id = e.getID();
            Class clazz = e.getType();
            String[] eventPath = e.getPath();
            
            FTreePath fullPath = mountPath.pathByAddingPath(eventPath);
            
            if (eventPath.length > 0) {
                String tmp = AidaUtils.parseName(eventPath[eventPath.length-1]);
                eventPath[eventPath.length-1] = tmp;
            }
            if (id == e.NODE_ADDED)
                masterTree.treeChanged( new FTreeNodeAddedNotification(this, fullPath, clazz) );
            else if ( id == e.LINK_ADDED ) {
                FTreePath linkPath = mountPath.pathByAddingPath(e.getLinkPath());
                masterTree.treeChanged( new FTreeNodeAddedNotification(this, fullPath, new AidaLink(linkPath, masterTree) ) );
            } else if (id == e.NODE_DELETED)
                masterTree.treeChanged( new FTreeNodeRemovedNotification(this, fullPath));
            if (id == e.NODE_MOVED)
                masterTree.treeChanged( new FTreeNodeMovedNotification(this, mountPath.pathByAddingPath( e.getOldPath() ), mountPath.pathByAddingPath( e.getPath() )) );
            else if (id == e.CHANGE_DIRECTORY) {
                //Don't have to do anything.
            } else if ( id == e.TREE_CLOSED ) {
                //aidaMasterTree.unmount("/"+((IManagedObject)e.getSource()).name());
                masterTree.treeChanged( new FTreeNodeRemovedNotification(this, fullPath));
            }
        }
    }
    
    private class AidaLink implements LinkNode {
        
        private FTreePath path;
        private FTree tree;
        
        AidaLink(FTreePath path, FTree tree) {
            this.tree = tree;
            this.path = path;
        }
        
        public FTreePath linkedPath() {
            return path;
        }
        
        public FTree tree() {
            return tree;
        }
    }
    
    
    private class AIDAPlotFactory implements XMLIOFactory {
        
        private AIDAPlugin thePlugin;
        
        AIDAPlotFactory(AIDAPlugin thePlugin) {
            this.thePlugin = thePlugin;
        }
        
        private Class[] classes = {AIDAPlot.class};
        
        public Class[] XMLIOFactoryClasses() {
            return classes;
        }
        
        public Object createObject(Class objClass) throws IllegalArgumentException {
            if ( objClass == AIDAPlot.class )
                return thePlugin.create();
            throw new IllegalArgumentException("Cannot create class "+objClass);
        }
    }
    
    public File getLastDir() {
        String file = userProperties.getProperty("aidaPlugin.lastDir","{user.home}");
        return new File(file);
    }
    public void setLastDir(File dir) {
        userProperties.setProperty("aidaPlugin.lastDir", dir.getAbsolutePath());
    }
}
