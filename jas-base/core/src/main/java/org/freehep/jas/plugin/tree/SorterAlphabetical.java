package org.freehep.jas.plugin.tree;

/**
 *
 * @author The FreeHEP team @ SLAC.
 */
public class SorterAlphabetical implements FTreeNodeSorter {
    
    public String algorithmName() {
        return "Alphabetical";
    }
    
    public String description() {
        return "Sorts nodes alphabetically";
    }
    
    public int sort(FTreeNode node1, FTreeNode node2) {
        DefaultFTreeNode n1 = (DefaultFTreeNode)node1;
        DefaultFTreeNode n2 = (DefaultFTreeNode)node2;
        String name1 = n1.path().getLastPathComponent();
        String name2 = n2.path().getLastPathComponent();
        return name1.compareTo( name2 );
    }
    
}
