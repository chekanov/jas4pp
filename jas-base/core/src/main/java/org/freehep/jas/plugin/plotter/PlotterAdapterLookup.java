package org.freehep.jas.plugin.plotter;

import java.util.ArrayList;
import org.freehep.jas.services.PlotterAdapter;

/**
 * Lookup for {@link PlotterAdapter} instances.
 * This lookup can also chain pairs of registered adapters.
 */
public class PlotterAdapterLookup {
    
    private ArrayList<AdapterEntry> adapters = new ArrayList<>();
    
    /**
     * Adds the specified adapter to this lookup.
     */
    public void registerAdapter(PlotterAdapter adapter, Class from, Class to) {
        adapters.add( new AdapterEntry(adapter,from,to) );
    }

    /**
     * Returns an adapter for conversion between the specified type, or <tt>null</tt> if none found.
     */
    public PlotterAdapter adapter( Class from, Class to ) {
        ArrayList<AdapterEntry> fromAdapters = new ArrayList<>();
        ArrayList<AdapterEntry> toAdapters = new ArrayList<>();
        
        for (AdapterEntry entry : adapters) {
            if ( entry.isAdapter( from, to ) ) {
              return entry.adapter();
            }
            if ( entry.isAdapterFrom( from ) ) {
              fromAdapters.add( entry );
            }
            if ( entry.isAdapterTo(to) ) {
              toAdapters.add( entry );
            }
        }
        
        if ( fromAdapters.isEmpty() || toAdapters.isEmpty() ) return null;
        
        for ( AdapterEntry fromEntry : fromAdapters) {
            for (AdapterEntry toEntry : toAdapters) {
                if ( fromEntry.isAdapterTo( toEntry.from() ) ) {
                    return new CombinedAdapter( fromEntry.adapter(), toEntry.adapter() );
                } 
            }
        }
        return null;
    }
    
    private class AdapterEntry {
      
        private PlotterAdapter adapter;
        private Class from;
        private Class to;
        
        public AdapterEntry(PlotterAdapter adapter, Class from, Class to) {
            this.adapter = adapter;
            this.from = from;
            this.to = to;
        }

        public PlotterAdapter adapter() {
            return adapter;
        }
        
        public Class from() {
            return from;
        }
        
        public Class to() {
            return to;
        }
        
        public boolean isAdapter( Class from, Class to ) {
            return isAdapterFrom(from) && isAdapterTo(to);
        }
        
        public boolean isAdapterFrom( Class from ) {
            return from().isAssignableFrom( from );
        }
        
        public boolean isAdapterTo( Class to ) {
            return to.isAssignableFrom( to() );
        }
            
    }
    
    private class CombinedAdapter implements PlotterAdapter {
        
        private PlotterAdapter fromAdapter;
        private PlotterAdapter toAdapter;
        private PlotterAdapter inBetweenAdapter;
        
        public CombinedAdapter( PlotterAdapter fromAdapter, PlotterAdapter toAdapter ) {
            this.fromAdapter = fromAdapter;
            this.toAdapter = toAdapter;
        }

        public CombinedAdapter( PlotterAdapter fromAdapter, PlotterAdapter inBetweenAdapter, PlotterAdapter toAdapter ) {
            this.fromAdapter = fromAdapter;
            this.toAdapter = toAdapter;
            this.inBetweenAdapter = inBetweenAdapter;
        }
        
        public Object adapt( Object obj ) {
            if ( inBetweenAdapter == null ) 
                return toAdapter.adapt( fromAdapter.adapt( obj ) );
            
            return toAdapter.adapt( inBetweenAdapter.adapt( fromAdapter.adapt( obj ) ) );
        }
    }
    
}
