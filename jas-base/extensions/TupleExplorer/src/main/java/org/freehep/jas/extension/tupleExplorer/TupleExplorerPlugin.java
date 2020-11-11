package org.freehep.jas.extension.tupleExplorer;

import org.freehep.jas.extension.tupleExplorer.adapter.*;
import org.freehep.util.*;
import org.freehep.util.commanddispatcher.*;
import org.freehep.application.*;
import org.freehep.application.mdi.*;
import org.freehep.application.studio.*;
import org.freehep.xml.menus.*;
import org.freehep.jas.plugin.tree.*;
import org.freehep.jas.services.*;
import org.freehep.jas.services.PreferencesTopic;

import hep.aida.ref.tuple.*;

import java.util.*;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import org.freehep.application.PropertyUtilities;

import org.freehep.jas.extension.tupleExplorer.cut.CutSet;
import org.freehep.jas.extension.tupleExplorer.cut.CutColumn;
import org.freehep.jas.extension.tupleExplorer.cut.CutFactoryAndProxy;
import org.freehep.jas.extension.tupleExplorer.plot.Plot;
import org.freehep.jas.extension.tupleExplorer.project.AbstractProjection;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleListener;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleEvent;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeCutSet;
import org.freehep.jas.extension.tupleExplorer.plot.PlotFactoryAndProxy;
import org.freehep.jas.extension.tupleExplorer.project.ProjectionFactoryAndProxy;
import org.freehep.jas.plugin.tree.FTreeNodeRepaintNotification;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.plugin.xmlio.XMLPluginIO;
import org.freehep.util.template.Template;
import org.freehep.xml.io.XMLIOFactory;
import org.freehep.xml.io.XMLIOManager;
import org.freehep.xml.io.XMLIOProxy;
import org.jdom.Element;

/**
 * A JAS3 plugin for the TupleExplorer.
 * @author The FreeHEP team @ SLAC.
 *
 */
public class TupleExplorerPlugin extends Plugin implements MutableTupleListener, PreferencesTopic, XMLPluginIO {
    
    private static TupleExplorerPlugin thePlugin;
    private Hashtable pathMutableTupleTreeMap  = new Hashtable();
    private FTreeProvider treeProvider;
    public Studio app;
    
    private int nPages = 0;
    private PlotPage plotPage;
    private org.freehep.jas.services.PlotFactory plotFactory;
    private Properties userProperties;
    private TupleExplorerPluginCommands commands;
    
    protected void init()  throws org.xml.sax.SAXException, java.io.IOException {
        thePlugin = this;
        
        app = getApplication();
        FreeHEPLookup lookup = app.getLookup();
        lookup.add(this);
        userProperties = app.getUserProperties();
        
        // Add the Tuple Explorer menus
        XMLMenuBuilder builder = app.getXMLMenuBuilder();
        URL xml = getClass().getResource("TupleExplorerPlugin.menus");
        builder.build(xml);
        commands = new TupleExplorerPluginCommands(this);
        app.getCommandTargetManager().add( commands );
        
        // Get the FTree
        treeProvider = ( (FTreeProvider) getApplication().getLookup().lookup(FTreeProvider.class) );
        
        // Register the standard adapters
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter( new TupleAdapter(this,commands), FTuple.class );
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter( new TupleColumnAdapter(this,commands), FTupleColumn.class );
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter( new CutAdapter(this,commands), CutColumn.class );
        
        plotFactory = (org.freehep.jas.services.PlotFactory) getApplication().getLookup().lookup(org.freehep.jas.services.PlotFactory.class);
        plotPage = plotFactory.currentPage();
        
        app.getLookup().add( new PlotFactoryAndProxy() );
        app.getLookup().add( new ProjectionFactoryAndProxy() );
        app.getLookup().add( new CutFactoryAndProxy() );
        
        // Add the web documentation
        Template map = new Template();
        map.set("title", "Tuple Explorer");
        map.set("url", "classpath:/org/freehep/jas/extension/tupleExplorer/web/tuple.html");
        map.set("description", "A plugin to perfom a GUI based inspection of n-Tuples, add new columns, apply cuts and create plots");
        app.getLookup().add(map, "extension-plugins");
        
    }
    
    public static TupleExplorerPlugin thePlugin() {
        return thePlugin;
    }
    
    public void registerMutableTupleTree(MutableTupleTree mutableTupleTree, FTreeNode node) {
        MutableTuple rootMutableTuple = mutableTupleTree.rootMutableTuple();
        if ( ! rootMutableTuple.isInMemory() )
            if ( getAutoLoadTuple() )
                if ( rootMutableTuple.estimatedSize() < Double.valueOf( getLoadTupleMemorySize() ).doubleValue() )
                    try {
                        rootMutableTuple.loadTupleInMemory();
                    } catch (RuntimeException re) {
                        getApplication().error("Failed to load tuple "+rootMutableTuple.name()+" in memory. No more attempts will be made to load it in memory.", re);
                    }
        mutableTupleTree.addMutableTupleListener(this);
        pathMutableTupleTreeMap.put( node.path().toString(), mutableTupleTree );
    }

    protected PlotPage currentPage() {
        return plotPage;
    }
    
    
    public Plot createPlot(final MutableTupleTree tuple, AbstractProjection proj) {
        MutableTupleTreeCutSet cs = new MutableTupleTreeCutSet("plotCutSet "+proj.dataSource().getTitle());
        cs.addCut(tuple.defaultCuts());
        Plot plot = new Plot(proj.dataSource().getTitle(),proj,cs,tuple);
        tuple.plots().add(plot);
        tuple.run();
        return plot;
    }
    
    public void plotInPage( Plot plot, boolean newPage, boolean overlay, boolean newPlot ) {
        
        plotPage = newPage ? null : plotFactory.currentPage();
        boolean justCreated = false;
        if (plotPage == null) {
            plotPage = plotFactory.createPage(null);
            plotPage.showPage();
            justCreated = true;
        }
        
        PlotRegion region = plotPage.currentRegion();
        if (region == null) region = plotPage.createRegion(0,0,1,1);
        else if (newPlot && ! justCreated )
            region = plotPage.addRegion();
        
        Plotter plotter = region.currentPlot();
        
        if (plotter == null) {
            plotter = plotFactory.createPlotterFor(plot.getProjection().jas3DataSource().getClass());
            region.showPlot(plotter);
        }
        plotter.plot(plot.getProjection().jas3DataSource(),overlay ? plotter.OVERLAY : plotter.NORMAL);
    }
    
    
    public void columnsAdded(MutableTupleEvent event) {
        MutableTuple tuple = (MutableTuple) event.getSource();
        MutableTupleTree mutableTuple = tuple.mutableTupleTree();
        FTreePath treePath = mutableTuple.treePathForMutableTuple( tuple );
        FTupleColumn col = tuple.column(event.getFirstIndex());
        treePath = treePath.pathByAddingChild(tuple.columnName( event.getFirstIndex() ));
        if ( col.type() == MutableTuple.class )
            mutableTuple.tree().treeChanged( new FTreeNodeAddedNotification(this, treePath, MutableTuple.class));
        else
            mutableTuple.tree().treeChanged( new FTreeNodeAddedNotification(this, treePath, col));
    }
    
    public void columnsRemoved(MutableTupleEvent event) {
        MutableTuple tuple = (MutableTuple) event.getSource();
        MutableTupleTree mutableTuple = tuple.mutableTupleTree();
        FTreePath treePath = mutableTuple.treePathForMutableTuple( tuple );
        FTupleColumn col = tuple.column(event.getFirstIndex());
        treePath = treePath.pathByAddingChild(tuple.columnName( event.getFirstIndex() ));
        mutableTuple.tree().treeChanged( new FTreeNodeRemovedNotification(this, treePath));
    }
    
    public void columnsChanged(MutableTupleEvent e) {
        throw new UnsupportedOperationException("This operation is not supported jet. Please report this problem");
    }
    
    public boolean apply(JComponent panel) {
        ((TupleExplorerPrefsDialog) panel).apply(this);
        return true;
    }
    
    public JComponent component() {
        return new TupleExplorerPrefsDialog(this);
    }
    
    public String[] path() {
        return new String[]{"TupleExplorer"};
    }
    
    private final static String TUPLEEXPLORER_AUTO_LOAD_TUPLE = "org.freehep.jas.extension.tupleexplorer.AutoLoadMemory";
    private final static String TUPLEEXPLORER_LOAD_TUPLE_MEMORY_SIZE = "org.freehep.jas.extension.tupleexplorer.LoadTupleMemorySize";
    private final static String TUPLEEXPLORER_DOUBLECLICK = "org.freehep.jas.extension.tupleexplorer.doubleClick";
    
    public String getLoadTupleMemorySize() {
        return PropertyUtilities.getString(userProperties, TUPLEEXPLORER_LOAD_TUPLE_MEMORY_SIZE, "5");
    }
    
    protected void setLoadTupleMemorySize(String loadTupleMemorySize) {
        userProperties.setProperty(TUPLEEXPLORER_LOAD_TUPLE_MEMORY_SIZE, loadTupleMemorySize);
    }
    
    public boolean getAutoLoadTuple() {
        return PropertyUtilities.getBoolean(userProperties, TUPLEEXPLORER_AUTO_LOAD_TUPLE, true);
    }
    
    protected void setAutoLoadTuple(boolean autoLoadTuple) {
        userProperties.setProperty(TUPLEEXPLORER_AUTO_LOAD_TUPLE, String.valueOf(autoLoadTuple));
    }
    
    public int getDoubleClick() {
        return PropertyUtilities.getInteger(userProperties, TUPLEEXPLORER_DOUBLECLICK, 0);
    }
    
    protected void setDoubleClick(int doubleClick) {
        userProperties.setProperty(TUPLEEXPLORER_DOUBLECLICK, String.valueOf(doubleClick));
    }
    
    public int restore(int level, XMLIOManager manager, Element el) {
        switch (level) {
            case RESTORE_DATA:
                return RESTORE_TREE_OBJECTS;
            case RESTORE_TREE_OBJECTS:
                Element tupleTreesEl = el.getChild("TupleTrees");
                List tupleTreesList = tupleTreesEl.getChildren();
                for ( int i = 0; i < tupleTreesList.size(); i++ ) {
                    Element tupleTreeEl = (Element) tupleTreesList.get(i);
                    String path = tupleTreeEl.getAttributeValue("path");
                    String treeName = tupleTreeEl.getAttributeValue("treeName");
                    FTree tree = treeProvider.tree(treeName);
                    FTreePath treePath = new FTreePath(path);
                    tree.treeChanged( new FTreeNodeRepaintNotification(this, treePath ) );
                    FTreeNode node = tree.findNode(treePath);
                    MutableTupleTree tuple = (MutableTupleTree)node.objectForClass(MutableTupleTree.class);
                    manager.restore( tuple, tupleTreeEl );
                }
                return RESTORE_PLOT_DATA_SOURCES;
            case RESTORE_PLOT_DATA_SOURCES:
                Element tTEl = el.getChild("TupleTrees");
                List tTList = tTEl.getChildren();
                for ( int i = 0; i < tTList.size(); i++ ) {
                    Element tupleTreeEl = (Element) tTList.get(i);
                    String path = tupleTreeEl.getAttributeValue("path");
                    String treeName = tupleTreeEl.getAttributeValue("treeName");
                    FTree tree = treeProvider.tree(treeName);
                    FTreePath treePath = new FTreePath(path);
                    FTreeNode node = tree.findNode(treePath);
                    MutableTupleTree tuple = (MutableTupleTree)node.objectForClass(MutableTupleTree.class);
                    tuple.run();
                }
                return RESTORE_DONE;
            default :
                throw new IllegalArgumentException("Unsupported restore level : "+level);
        }
    }
    
    public void save(XMLIOManager manager, Element el) {
        // Save the MutableTupleTrees currently open
        Element tupleTreesEl = new Element("TupleTrees");
        Enumeration pathsToTupleTrees = pathMutableTupleTreeMap.keys();
        while( pathsToTupleTrees.hasMoreElements() ) {
            String path = (String) pathsToTupleTrees.nextElement();
            MutableTupleTree tupleTree = (MutableTupleTree) pathMutableTupleTreeMap.get(path);
            Element ttEl = manager.save(tupleTree);
            ttEl.setAttribute("treeName",tupleTree.tree().name());
            ttEl.setAttribute("path",path);
            tupleTreesEl.addContent(ttEl);
        }
        el.addContent(tupleTreesEl);
    }
}