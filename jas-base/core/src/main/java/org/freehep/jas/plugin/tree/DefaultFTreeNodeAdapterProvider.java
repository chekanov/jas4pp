package org.freehep.jas.plugin.tree;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class DefaultFTreeNodeAdapterProvider implements FTreeNodeAdapterProvider {
    
    private Class clazz;
    private FTreeNodeAdapter adapter;
    
    public DefaultFTreeNodeAdapterProvider(Class clazz, FTreeNodeAdapter adapter) {
        this.clazz  = clazz;
        this.adapter = adapter;
    }
    
    /**
     * Get the array of FTreeNodeAdapter that provide the behavior for the
     * given class.
     * @param c The class of the FTreeNode for which the adapters are being requested.
     * @return       The array of FTreeNodeAdapter compatible with the given
     *              class. If no adapters are available, null should be returned.
     *
     */
    public FTreeNodeAdapter[] treeNodeAdaptersForClass(Class c) {
        if ( clazz.isAssignableFrom( c ) )
            return new FTreeNodeAdapter[]{adapter};
        return null;
    }    
}
