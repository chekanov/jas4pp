package org.freehep.jas.extension.aida.adapter;

import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;

import hep.aida.IBaseHistogram;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IManagedObject;
import hep.aida.ITuple;

import hep.aida.ref.event.Connectable;
import hep.aida.ref.tree.Tree;
import org.freehep.application.studio.Studio;
import org.freehep.jas.extension.aida.AIDAObjectProvider;
import org.freehep.jas.extension.aida.AIDAPlugin;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreeNodeTextChangeEvent;
import org.freehep.jas.plugin.tree.FTreePath;

/**
 * The FTreeNodeAdapter for an IManagedObject
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class ManagedObjectAdapter extends DefaultFTreeNodeAdapter {
    
    private AIDAPlugin plugin;
    private Studio app;
    private Tree aidaMasterTree;
    
    public ManagedObjectAdapter(AIDAPlugin plugin, Studio app) {
        super(50,new AIDAObjectProvider(plugin));
        this.plugin = plugin;
        this.app = app;
        aidaMasterTree = plugin.aidaMasterTree();
    }
    
    public String text( FTreeNode node, String oldText ) {
        if ( plugin.isShowNamesAndTitles() ) {
            Object o = node.objectForClass(IManagedObject.class);
            if ( o != null ) {
                IManagedObject obj = (IManagedObject) o;
                String name = obj.name();
                String title = name;
                if ( obj instanceof IBaseHistogram )
                    title = ((IBaseHistogram)obj).title();
                else if ( obj instanceof IDataPointSet )
                    title = ((IDataPointSet)obj).title();
                else if ( obj instanceof ITuple )
                    title = ((ITuple)obj).title();
                else if ( obj instanceof IFunction )
                    title = ((IFunction)obj).title();
                if ( ! title.equals(name) )
                    name += " ["+title+"]";
                return name;
            }
        }
        return null;
    }
    
    public Component treeCellRendererComponent(Component component, FTreeNode node, boolean sel, boolean expanded, boolean leaf, boolean hasFocus) {
        try {
            Object obj = getObjectForPath(plugin.fullPath( node.path() ));
            Font font = ((JLabel) component).getFont();
            boolean connected = true;
            if (obj instanceof Connectable) {
                connected = ((Connectable) obj).isConnected();
                
                boolean normal = !font.isItalic();
                if (connected != normal) {
                    int size = font.getSize();
                    int style = connected ? Font.PLAIN : Font.ITALIC;
                    String name = font.getName();
                    Font newFont = new Font(name, style, size);
                    ((JLabel) component).setFont(newFont);
                    
                }
                if (connected) {
                    ((JLabel) component).setForeground(Color.black);
                } else {
                    ((JLabel) component).setForeground(Color.red);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return component;
    }
    public boolean canTextBeChanged(FTreeNodeTextChangeEvent evt, boolean oldCanBeRenamed) {
        return true;
    }
    
    public boolean acceptNewText(FTreeNodeTextChangeEvent evt, boolean oldAcceptNewName) {
        IManagedObject obj = null;
        try {
            obj = aidaMasterTree.find( newFullPath(evt) );
        } catch (Exception e) {}
        if ( obj != null ) return false;
        return true;
    }
    
    public void nodeTextChanged(FTreeNodeTextChangeEvent evt) {
        aidaMasterTree.mv( oldFullPath(evt), newFullPath(evt) );
        evt.consume();
    }
    
    private String newFullPath(FTreeNodeTextChangeEvent evt) {
        String newName = evt.newText();
        FTreeNode node = evt.node();
        FTreePath parentPath = node.path().getParentPath();
        FTreePath newPath;
        if ( parentPath == null )
            newPath = new FTreePath(newName);
        else
            newPath = parentPath.pathByAddingChild(newName);
        return plugin.fullPath( newPath );
    }
    
    private String oldFullPath(FTreeNodeTextChangeEvent evt) {
        FTreeNode node = evt.node();
        FTreePath path = node.path();
        return plugin.fullPath( path );
    }
    
    private IManagedObject getObjectForPath(String pathString) {
        IManagedObject mo = null;
        mo = ((hep.aida.ref.tree.Tree) aidaMasterTree).findObject(pathString);
        return mo;
    }
}
