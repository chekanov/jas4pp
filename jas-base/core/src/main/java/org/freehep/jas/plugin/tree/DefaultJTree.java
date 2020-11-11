package org.freehep.jas.plugin.tree;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.StringTokenizer;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.tree.TreePath;
import org.freehep.swing.popup.HasPopupItems;
import org.freehep.util.commanddispatcher.CommandProcessor;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
class DefaultJTree extends JTree implements HasPopupItems {
    
    private DefaultFTree model;
    private TreePath rootTreePath;
    private CommandProcessor commandProcessor;
    private DefaultFTreeNode[] selectedNodes = null;
    private FTreeSelectionManager selectionManager;

    DefaultJTree(DefaultFTree model) {
        super(model);
        this.model = model;
        model.setJTree( this );
        this.selectionManager = new FTreeSelectionManager(this);
        ToolTipManager.sharedInstance().registerComponent(this);
        rootTreePath = new TreePath(model.getRoot());
        setEditable(true);
        setShowsRootHandles(true);    
        commandProcessor = new FTreeNodeCommands( this );
    }
    
    public CommandProcessor commandProcessor() {
        return commandProcessor;
    }
    
    DefaultFTreeNode[] selectedNodes() {
        return selectedNodes;
    }
        
    void setSelectedNodes( DefaultFTreeNode[] selectedNodes ) {
        this.selectedNodes = selectedNodes;
    }
    
    FTreeSelectionManager selectionManager() {
        return selectionManager;
    }
    
    public String getToolTipText(java.awt.event.MouseEvent event)   {
        TreePath path = getClosestPathForLocation(event.getX(),event.getY());
        Rectangle r = getPathBounds(path);
        if ( r != null )
            if ( r.contains(event.getPoint()) ) 
                return ((DefaultFTreeNode)path.getLastPathComponent()).toolTipMessage();
        return null;
    }
    public JPopupMenu modifyPopupMenu(JPopupMenu menu, Component source, Point p) {
        TreePath path = getClosestPathForLocation(p.x, p.y);
        if ( getPathBounds(path) == null ) return menu;
        if (getPathBounds(path).contains(p)) {
            
            //This is to reprocude the Windows behaviour: if right click is on a selected node
            //the selection does not change, otherwise the selected path is the only one being
            //selected.
            if ( ! isPathSelected(path) ) {
                removeSelectionPaths( getSelectionPaths() );
                addSelectionPath(path);
            }
            
            menu = model().adapterManager().modifyPopupMenu(selectedNodes(), menu);
        }
        return menu;
    }
    
    void setExpandedState( String path ) {
        setExpandedState(path, false);
    }
    
    void setExpandedState( String path, boolean select ) {
        StringTokenizer st = new StringTokenizer(path,"/");
        TreePath treePath = new TreePath( model().getRoot() );
        FTreePath mTreePath = null;
        DefaultFTreeNode node = null;
        while( st.hasMoreTokens() ) {
            if ( mTreePath == null )
                mTreePath = new FTreePath(st.nextToken());
            else
                mTreePath = mTreePath.pathByAddingChild( st.nextToken() );
            node = (DefaultFTreeNode)model().findNode( mTreePath );
            if ( node != null ) {
                node.getChildCount();
                treePath = treePath.pathByAddingChild( node );
            }
            if ( st.hasMoreTokens() )
                expandRow( getRowForPath(treePath) );
        }
        int row = getRowForPath(treePath);
        if ( select ) setSelectionRow( row );
        else expandRow( row );
    }
    
    DefaultFTree model() {
        return model;
    }
    
}
