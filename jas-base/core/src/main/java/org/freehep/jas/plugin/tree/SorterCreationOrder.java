package org.freehep.jas.plugin.tree;

import java.util.List;
import org.freehep.jas.plugin.tree.FTreeNodeStructureProvider;

/**
 *
 * @author The FreeHEP team @ SLAC.
 */
public class SorterCreationOrder implements FTreeNodeSorter {
    
    public String algorithmName() {
        return "Default order";
    }
    
    public String description() {
        return "Sorts the nodes as specified by the structure provider. By default this is the creation order.";
    }
    
    public int sort(FTreeNode node1, FTreeNode node2) {
        FTreeNode parent = node1.parent();
        FTreeNodeStructureProvider structureProvider = parent.tree().adapterForClass(  parent.type() ).nodeStructureProvider(parent);
        List nodes = structureProvider.nodes();        
        return nodes.indexOf(node1) > nodes.indexOf(node2) ? 1 : -1;
    }
    
}
