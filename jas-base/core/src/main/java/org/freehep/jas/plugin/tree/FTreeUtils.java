package org.freehep.jas.plugin.tree;

import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.tree.TreePath;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreeUtils {
    
    static FTreePath createTreePath( TreePath path ) {
        if ( path == null ) return null;
        int pathLength = path.getPathCount();
        if ( pathLength < 2 ) throw new IllegalArgumentException("This is the root element. Something is wrong. Please report this problem");
        DefaultFTreeNode node = (DefaultFTreeNode)path.getPathComponent(1);
        FTreePath treePath = new FTreePath( node.realName() );
        for ( int i = 2; i < pathLength; i++ )
            treePath = treePath.pathByAddingChild( ( (DefaultFTreeNode)path.getPathComponent(i) ).realName() );
        return treePath;
    }
        
    static TreePath treePathForNode( FTreeNode node ) {
        DefaultFTreeNode parent = (DefaultFTreeNode) node;
        ArrayList list = new ArrayList();
        while( parent != null ) {
            list.add(0,parent);
            parent = (DefaultFTreeNode)parent.parent();
        }
        
        TreePath path = null;
        if (list.size() != 0) {
            path = new TreePath(list.get(0));
            for ( int i = 1; i < list.size(); i++ )
                path = path.pathByAddingChild(list.get(i));
        }
        return path;
    }
    
}
