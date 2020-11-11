package org.freehep.jas.plugin.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;


/**
 * This class is used to order the the adapers by priority.
 *
 * @author The FreeHEP team @ SLAC
 *
 */
class DefaultFTreeNodeAdapterLookup {
    
    private ArrayList adapterProviders = new ArrayList();
    private Map       cachedNodeAdapters = new HashMap();
    private EventListenerList all = new EventListenerList();
    private ChangeEvent event = new ChangeEvent(this);
    private FTree tree;
    private AdapterComparator comparator;
        
    DefaultFTreeNodeAdapterLookup(FTree tree) {
        this.tree = tree;
        comparator = new AdapterComparator(tree);
    }
    
    public void registerFTreeNodeAdapterProvider( FTreeNodeAdapterProvider provider ) {
        adapterProviders.add(provider);
        cachedNodeAdapters.clear();
//        notifyAllListeners();
    }

     /**
     * Returns a list of the adapters for Class c, with the highest
     * priority item first
     */
    protected List getFTreeNodeAdaptersForClass(Class c) {
        List result = (List) cachedNodeAdapters.get(c);
        if (result == null) {
            result = new ArrayList();
            for ( int i = 0; i < adapterProviders.size(); i++ ) {
                FTreeNodeAdapter[] adapters = ( (FTreeNodeAdapterProvider) adapterProviders.get(i) ).treeNodeAdaptersForClass(c);
                if ( adapters != null ) {
                    for ( int j = 0; j < adapters.length; j++ ) {
                        if ( ! result.contains(adapters[j]) ) {
                            result.add( adapters[j] );
                        }
                    }
                }
            }

            Collections.sort(result,comparator);
            
            cachedNodeAdapters.put(c, result);

        }
        return result;
    }       

    private class AdapterComparator implements Comparator {
        
        private FTree tree;
        
        AdapterComparator(FTree tree) {
            this.tree = tree;
        }
        
        public int compare(Object obj1, Object obj2) {
            FTreeNodeAdapter a1 = (FTreeNodeAdapter)obj1;
            FTreeNodeAdapter a2 = (FTreeNodeAdapter)obj2;

            if ( a1.priority(tree) > a2.priority(tree) )
                return -1;
            else if ( a1.priority(tree) < a2.priority(tree)) 
                return 1;
            return 0;
        }
    }
    
    protected void addListener(ChangeListener l) {
        all.add(ChangeListener.class, l);
    }
    
    protected void removeListener(ChangeListener l) {
        all.remove(ChangeListener.class, l);
    }
    
    private void notifyAllListeners() {
        ChangeListener[] l = (ChangeListener[]) all.getListeners(ChangeListener.class);
        for (int i = 0; i < l.length; i++)
            l[i].stateChanged(event);
    }    
}