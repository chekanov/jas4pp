package org.freehep.jas.plugin.tree;

/**
 *
 * @author The FreeHEP team @ SLAC.
 */
public class SorterFolderFirst implements FTreeNodeSorter {
    
    public String algorithmName() {
        return "Folders first";
    }
    
    public String description() {
        return "Sorts folders before leaf nodes";
    }
    
    public int sort(FTreeNode node1, FTreeNode node2) {
        DefaultFTreeNode n1 = (DefaultFTreeNode)node1;
        DefaultFTreeNode n2 = (DefaultFTreeNode)node2;
        boolean allowsChildren1 = n1.getAllowsChildren();
        boolean allowsChildren2 = n2.getAllowsChildren();
        if ( allowsChildren1 == allowsChildren2 ) 
            return 0;
        else if ( allowsChildren1 && ( ! allowsChildren2 ) )
            return -1;
        else
            return 1;
    }
    
}
