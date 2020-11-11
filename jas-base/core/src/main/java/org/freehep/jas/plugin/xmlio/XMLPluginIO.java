package org.freehep.jas.plugin.xmlio;

import org.freehep.xml.io.XMLIOManager;
import org.jdom.Element;

/**
 * This interface is to be implemented by all plugins that want their configuration
 * to be saved and restored to XML.
 * Each plugin is responsible to save any item that it created in the MasterTree, 
 * in the window manager, in plot pages etc etc. 
 * The reloading of the configuration is done in steps to ensure that all the 
 * object dependencies are properly handled. The first step of the reloading process
 * is to ask all the plugins to open the data structures on which all the other
 * objects (like plots, pages, editors) depend on. Each plugin is called for the 
 * initialization phase and should tell the XMLIOManager at which restore level 
 * it should be called back.
 *
 *
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public interface XMLPluginIO {
    
    public final int RESTORE_DONE = -1;
    public final int RESTORE_DATA = 0;
    public final int RESTORE_TREE_OBJECTS = 20;
    public final int RESTORE_PLOT_DATA_SOURCES = 40;
    public final int RESTORE_PAGES = 60;
    public final int RESTORE_PLOTS_IN_PAGES = 80;
    public final int RESTORE_TREE_STRUCTURE = 100;
    public final int RESTORE_FINALIZE = 120;
    
    public void save( XMLIOManager manager, Element el );
    
    /**
     * level is the level at which the restore should be performed.
     * Levels:
     *        0 - the initial level. At this level all the data should
     *            be restored. This level MUST be present!!!
     *
     *        more then 100 - final state restoring: finishing touches on the tree,
     *                order of the panels on the plot area and console area.
     */
    public int restore( int level, XMLIOManager manager, Element el ); 
}
