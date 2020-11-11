package org.freehep.jas.plugin.tree;

import java.util.ArrayList;
import java.util.List;
import org.freehep.jas.plugin.tree.FTreeNodeSorter;
import org.freehep.jas.plugin.tree.FTreeNodeStructureProvider;

/**
 * The default implementation of FTreeNodeStructureProvider.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class DefaultFTreeNodeStructureProvider implements FTreeNodeStructureProvider {
    
    private ArrayList children = new ArrayList();
    
    public boolean addNode(FTreeNode node) {
        children.add(node);
        return true;
    }
    
    public boolean removeNode(FTreeNode node) {
        children.remove(node);
        return true;
    }
    
    public List nodes() {
        return children;
    }
    
}
