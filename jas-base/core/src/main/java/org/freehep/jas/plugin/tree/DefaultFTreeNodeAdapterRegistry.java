package org.freehep.jas.plugin.tree;

import java.util.ArrayList;
import org.freehep.jas.plugin.tree.FTree;
import org.freehep.jas.plugin.tree.FTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreeNodeAdapterProvider;
import org.freehep.jas.plugin.tree.FTreeNodeAdapterRegistry;
import org.freehep.jas.plugin.tree.FTreeNodeObjectProvider;
import org.freehep.jas.plugin.tree.FTreeProvider;

/**
 * The default implementation of the FTreeNodeAdapterRegistry interface.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class DefaultFTreeNodeAdapterRegistry implements FTreeNodeAdapterRegistry {
    
    private FTreeProvider treeProvider;
    private ArrayList adaptersList = new ArrayList();
    private ArrayList adaptersClassList = new ArrayList();
    private ArrayList adapterProvidersList = new ArrayList();
    private ArrayList objectProvidersList = new ArrayList();
    private ArrayList objectProvidersClassList = new ArrayList();
    private ArrayList objectProvidersPriorityList = new ArrayList();
    
    public DefaultFTreeNodeAdapterRegistry(FTreeProvider treeProvider) {
        this.treeProvider = treeProvider;
    }
    
    public void registerNodeAdapter(FTreeNodeAdapter adapter, Class clazz) {
        adaptersList.add( adapter );
        adaptersClassList.add( clazz );
        String[] names = treeProvider.namesOfExistingTrees();
        for ( int i = 0; i < names.length; i++ )
            ((DefaultFTree) treeProvider.tree(names[i])).adapterManager().registerNodeAdapter(adapter, clazz);
    }
    
    public void registerNodeAdapter(FTree tree, FTreeNodeAdapter adapter, Class clazz) {
        ((DefaultFTree) tree).adapterManager().registerNodeAdapter(adapter, clazz);
    }
    
    public void registerNodeAdapterProvider(FTreeNodeAdapterProvider adapterProvider) {
        adapterProvidersList.add( adapterProvider );
        String[] names = treeProvider.namesOfExistingTrees();
        for ( int i = 0; i < names.length; i++ )
            ((DefaultFTree) treeProvider.tree(names[i])).adapterManager().registerNodeAdapterProvider(adapterProvider);
    }    
    
    public void registerNodeAdapterProvider(FTree tree, FTreeNodeAdapterProvider adapterProvider) {
        ((DefaultFTree) tree).adapterManager().registerNodeAdapterProvider(adapterProvider);
    }
    
    public void registerNodeObjectProvider(FTreeNodeObjectProvider objectProvider, Class clazz, int priority) {
        objectProvidersList.add( objectProvider );
        objectProvidersClassList.add( clazz );
        objectProvidersPriorityList.add( new Integer( priority ) );
        String[] names = treeProvider.namesOfExistingTrees();
        for ( int i = 0; i < names.length; i++ )
            ((DefaultFTree) treeProvider.tree(names[i])).adapterManager().registerObjectProvider(objectProvider, clazz, priority);
    }
    
    public void registerNodeObjectProvider(FTree tree, FTreeNodeObjectProvider objectProvider, Class clazz, int priority) {
        ((DefaultFTree) tree).adapterManager().registerObjectProvider(objectProvider, clazz, priority);
    }
    
    void addCommonAdaptersToTree( FTree tree ) {
        DefaultFTree t = (DefaultFTree) tree;        
        for ( int i = 0; i < adaptersList.size(); i++ )
            t.adapterManager().registerNodeAdapter((FTreeNodeAdapter) adaptersList.get(i), (Class) adaptersClassList.get(i));
        for ( int i = 0; i < adapterProvidersList.size(); i++ )
            t.adapterManager().registerNodeAdapterProvider((FTreeNodeAdapterProvider) adapterProvidersList.get(i));
        for ( int i = 0; i < objectProvidersList.size(); i++ )
            t.adapterManager().registerObjectProvider((FTreeNodeObjectProvider) objectProvidersList.get(i), (Class) objectProvidersClassList.get(i), ((Integer) objectProvidersPriorityList.get(i)).intValue());
    }
    
}
