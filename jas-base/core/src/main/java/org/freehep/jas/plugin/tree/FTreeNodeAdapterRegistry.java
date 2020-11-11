package org.freehep.jas.plugin.tree;

import org.freehep.jas.plugin.tree.FTree;
import org.freehep.jas.plugin.tree.FTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreeNodeAdapterProvider;
import org.freehep.jas.plugin.tree.FTreeNodeObjectProvider;

/**
 * With the FTreeNodeAdapterRegistry it is possible to register FTreeNodeAdapterProviders,
 * FTreeNodeAdapters and FTreeNodeObjectProviders for all the FTrees or for individual
 * ones.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface FTreeNodeAdapterRegistry {
    
    /**
     * Register an FTreeNodeAdapterProvider for all the FTrees (currently
     * existing or that will be crated).
     * @param provider The FTreeNodeAdapterProvider to be registered
     *
     */
    public void registerNodeAdapterProvider( FTreeNodeAdapterProvider provider );
    
    /**
     * Register an FTreeNodeAdapterProvider for a given FTree.
     * @param tree     The FTree with which the FTreeNodeAdapterProvider is registered.
     * @param provider The FTreeNodeAdapterProvider to be registered.
     *
     */
    public void registerNodeAdapterProvider( FTree tree, FTreeNodeAdapterProvider provider );
    
    /**
     * Register an FTreeNodeAdapter for a given class for all the FTrees (currently
     * existing or that will be crated).
     * @param adapter The FTreeNodeAdapter to be registered.
     * @param clazz   The class for which the adapter is registered.
     *
     */
    public void registerNodeAdapter( FTreeNodeAdapter adapter, Class clazz );
    
    /**
     * Register an FTreeNodeAdapter for a given class for the given FTree.
     * @param tree    The FTree with which the FTreeNodeAdapter is registered.
     * @param adapter The FTreeNodeAdapter to be registered.
     * @param clazz   The class for which the adapter is registered.
     *
     */
    public void registerNodeAdapter( FTree tree, FTreeNodeAdapter adapter, Class clazz );

    /**
     * Register an FTreeNodeObjectProvider for all the FTrees (currently
     * existing or that will be crated).
     * @param provider The FTreeObjectProvider to be registered.
     * @param clazz    The class for which the object provider is registered.
     * @param priority The priority for the object provider.
     *
     */
    public void registerNodeObjectProvider( FTreeNodeObjectProvider provider, Class clazz, int priority );
        
    /**
     * Register an FTreeNodeObjectProvider for the given FTree.
     * @param tree     The FTree with which the FTreeNodeObjectProvider is registered.
     * @param provider The FTreeObjectProvider to be registered.
     * @param clazz    The class for which the object provider is registered.
     * @param priority The priority for the object provider.
     *
     */
    public void registerNodeObjectProvider( FTree tree, FTreeNodeObjectProvider provider, Class clazz, int priority );
}
