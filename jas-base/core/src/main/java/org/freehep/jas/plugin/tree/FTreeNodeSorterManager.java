package org.freehep.jas.plugin.tree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.freehep.application.studio.Studio;
import org.freehep.jas.plugin.tree.FTreeNodeSorter;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.freehep.util.FreeHEPLookup;

/**
 * Manages internally the FTreeNodeSorters.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
abstract class FTreeNodeSorterManager {
    
    private static Hashtable sortersHash = new Hashtable();
    private static String stringSeparator = ":-:";
    private static Lookup.Result result;
    private static SorterListener listener;
    
    private static FTreeNodeSorter sorter(String sorterName) {
        Collection availableSorters = availableSorters();
        Iterator iter = availableSorters.iterator();
        while ( iter.hasNext() ) {
            FTreeNodeSorter sorter = (FTreeNodeSorter) iter.next();
            if ( sorter.algorithmName().equals(sorterName) )
                return sorter;
        }
        throw new IllegalArgumentException("Sorter \""+sorterName+"\" does not exist");
    }
        
    
    static Collection availableSorters() {
        result = FreeHEPLookup.instance().lookup( new Lookup.Template( FTreeNodeSorter.class ) );
        return result.allInstances();
    }
    
    static synchronized String sortingString(Collection sorters) {
        int size = sorters.size();
        if ( size == 0 ) return null;
        String sortingString = new String();
        Iterator iter = sorters.iterator();
        while ( iter.hasNext() ) {
            if ( ! sortingString.equals("") ) sortingString += stringSeparator;
            sortingString += ( (FTreeNodeSorter) iter.next() ).algorithmName();
        }
        if ( ! sortersHash.contains( sortingString ) ) 
            sortersHash.put( sortingString, new SorterComparator( sorters ) );
        return sortingString;
    }

    static synchronized SorterComparator sortingComparator( String sortingString ) {
        Object obj = sortersHash.get(sortingString);
        if ( obj == null ) {
            ArrayList sorters = new ArrayList();
            while ( true ) {
                int index = sortingString.indexOf( stringSeparator );
                if ( index == -1 )
                    index = sortingString.length();
                String sorterName = sortingString.substring(0, index);                
                sorters.add( sorter( sorterName ) );
                
                sortingString = sortingString.substring(index);
                if ( sortingString.length() > 0 )
                    sortingString = sortingString.substring(3);
                else
                    break;                
            }
            obj = new SorterComparator(sorters);
            sortersHash.put(sortingString, obj);
        }
        return (SorterComparator) obj;
    }
    
    synchronized static void startListerning() {
        if (listener != null) return;
        doStartListerning();
    }
    
    synchronized static void stopListerning() {
        if (listener == null) return;
        doStopListerning();
    }
    
    private static void doStartListerning() {
        listener = new SorterListener();
        if (result == null)  result = FreeHEPLookup.instance().lookup( new Lookup.Template( FTreeNodeSorter.class ) );
        result.addLookupListener(listener);
    }
    
    private static void doStopListerning() {
        if (result == null)  return;
        result.removeLookupListener(listener);
        result = null;
        listener = null;
    }
    private synchronized static void refillHash() {
        sortersHash.clear();
        //doStopListerning();
        //doStartListerning();
    }
        
    static class SorterListener implements LookupListener {
        public void resultChanged(LookupEvent ev) {
            refillHash();
        }        
    }
        
    static class SorterComparator implements Comparator {
        
        private Collection sorters;
        
        SorterComparator() {
            this( new ArrayList() );
        }

        SorterComparator( Collection sorters ) {
            this.sorters = sorters;
        }

        public int compare(Object p1, Object p2) {
            int result = 0;
            DefaultFTreeNode node1 = (DefaultFTreeNode) p1;
            DefaultFTreeNode node2 = (DefaultFTreeNode) p2;
            Iterator iter = sorters.iterator();
            while ( iter.hasNext() ) {
                result = ( (FTreeNodeSorter) iter.next() ).sort(node1, node2);
                if ( result != 0 ) break;
            }
            return result;
        }
        
        Collection sorters() {
            return sorters;
        }
    }
}
