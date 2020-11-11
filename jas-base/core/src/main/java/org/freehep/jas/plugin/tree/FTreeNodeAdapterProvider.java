package org.freehep.jas.plugin.tree;

/**
 * This interface is to be implemented by classes that provide FTreeNodeAdapters
 * for a given class.
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public interface FTreeNodeAdapterProvider {
    
    /**
     * Get the array of FTreeNodeAdapter that provide the behavior for the 
     * given class.
     * @param clazz The class of the FTreeNode for which the adapters are being requested.
     * @return       The array of FTreeNodeAdapter compatible with the given
     *              class. If no adapters are available, null should be returned.
     */
    FTreeNodeAdapter[] treeNodeAdaptersForClass( Class clazz );
    
}
