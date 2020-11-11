package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTree;
import org.freehep.jas.plugin.tree.FTreeNodeAdapterRegistry;

/**
 * The FTreeProvider service provides access to existing or new FTrees and to
 * the FTreeNodeAdapterRegistry with wich FTreeNodeAdapters are to be registered.
 * 
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface FTreeProvider {

    /**
     * Get the provider's default FTree.
     * @return The default FTree for this provider
     *
     */
    public FTree tree();
    
    /**
     * Get an FTree by name. If the tree for the given name does not exist,
     * a new one is created.
     * @param name The name of the FTree.
     * @return     The corresponding FTree.
     *
     */
    public FTree tree( String name );
    
    /**
     * Get the names of the exising FTrees.
     * @return The array with the names of the existing FTrees.
     *
     */
    public String[] namesOfExistingTrees();
    
    /**
     * Get the FTreeNodeAdapterRegistry.
     * Via the FTreeNodeAdapterRegistry it is possible to register FTreeNodeAdapters and
     * FTreeNodeObjectProviders to shape the behavior of FTreeNodes.
     * @return The FTreeNodeAdapterRegistry.
     *
     */
    public FTreeNodeAdapterRegistry treeNodeAdapterRegistry();

}
