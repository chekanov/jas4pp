package org.freehep.jas.extension.tupleExplorer.cut;

import hep.aida.ref.tuple.FTupleCursor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeCut;

/**
 *
 * @author tonyj
 * @version $Id: CutSet.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class CutSet extends AbstractCut {

    private List cuts= new ArrayList();
    private Map map = new HashMap();
    private int lastRow = -1;
    private boolean lastResult = false;
    
    private CutListener cutListener;
    
    /**
     * Creates a new CutSet
     * @param name the name of the CutSet
     *
     */
    public CutSet(String name) {
        super( name );
        cutListener = new CutListenerAdapter() {
            public void cutChanged( CutChangedEvent cutChangedEvent ) {
                CutState cs = ( CutState ) map.get( cutChangedEvent.getSource() );
                if ( cs != null && cs.state != Cut.CUT_DISABLED ) fireCutChanged();
            }
        };
    }
    
    /**
     * Apply the cut to the CutData.
     * @param cutDataCursor the CutDataCursor to access the CutDataSet current value
     * @return <code>true<\code> if the current value of the CutDataSet is accepted by the cut
     *         <code>false<\code> otherwise
     *
     */
    public boolean accept( FTupleCursor cutDataCursor ) {

        int size = cuts.size();
        if ( size == 0 ) return true;

        
        
        //The logic below was to save time evaluating the cut.
        //It does not work for tuples with folders as the cursor is no longer
        //a flat structure but is a tree of cursors. Checking on the row
        //does not work anymore. The whole tree structure should be checked.
        //Is it worth it?
        /*
        int row = cutDataCursor.row();
        if (row == lastRow) return lastResult;
        lastRow = row;
        */
        for (int i = size; i>0; ) {
            CutState cs = (CutState) cuts.get(--i);
            int state = cs.state;
            if ( state == Cut.CUT_DISABLED ) continue;
            if ( cs.cut.accept( cutDataCursor) != ( state == CUT_ENABLED ) ) return lastResult = false;
        }
        return lastResult = true;
    }
    
    /**
     * Add a new cut to this CutSet
     * @param cut the cut to add
     *
     */
    public void addCut( Cut cut ) {
        CutState cs = new CutState( cut, cut.getState() );
        int index = cuts.size();
        cuts.add( cs );
        map.put( cut, cs );
        if ( cut instanceof MutableTupleTreeCut )
            map.put( ((MutableTupleTreeCut) cut).cut(), cs);
        fireCutAdded( index );
        fireCutChanged();
        cut.addCutListener( cutListener );
    }
    
    /**
     * Remove a cut from this CutSet
     * @param cut the cut to be removed
     *
     */
    public void removeCut( Cut cut ) {
        cut.removeCutListener( cutListener );
        CutState cs = ( CutState ) map.remove( cut );
        if ( cut instanceof MutableTupleTreeCut )
            map.remove( ((MutableTupleTreeCut) cut).cut() );
        int index = cuts.indexOf( cs );
        cuts.remove( cs );
        fireCutRemoved( index );
        fireCutChanged();
    }
    
    /**
     * Change the state of one of the cuts belonging to the CutSet
     * @param cut the cut whose state has to be changed
     * @param state the new cut state
     *
     */
    public void setCutState( Cut cut, int state ) {
        CutState cs = ( CutState ) map.get( cut );
        if ( cs.state != state ) {
            cs.state = state;
            fireCutChanged();
        }
    }
    
    /**
     * Get the state of one of the cuts belonging to the CutSet
     * @param cut the cut whose state is returned
     * @return the cut's state
     *
     */
    public int getCutState( Cut cut ) {
        return ( ( CutState ) map.get( cut ) ).state;
    }
    
    /**
     * Get the number of cuts belonging to the CutSet
     * @return the number of cuts
     *
     */
    public int getNCuts() {
        return cuts.size();
    }
    
    /**
     * Get the i-th cut belonging to the CutSet
     * @param index the cut's index in the CutSet
     * @return the i-th cut
     *
     */
    public Cut getCut( int index ) {
        return ( ( CutState ) cuts.get( index ) ).cut;
    }
    
    /**
     * Check if a Cut belongs to the CutSet
     * @param cut the Cut to check
     * @param recurse if <code>true<\code> it recursevly check inside
     *                CutSets belonging to the CutSet
     * @return <code>true<\code> if the cut belongs
     *         <code>false<\code> otherwise
     *
     */
    boolean contains( Cut cut, boolean recurse ) {
        for ( int i = cuts.size(); i > 0; ) {
            CutState cs = ( CutState ) cuts.get( --i );
            if ( cs.cut == cut ) return true;
            else if ( recurse && cs.cut instanceof CutSet ) {
                if ( ( ( CutSet ) cs.cut ).contains( cut, true ) ) return true;
            }
        }
        return false;
    }
    
    /**
     * Add a ListDataListener
     * @param listDataListener the ListDataListener to add
     *
     */
    public void addListDataListener( ListDataListener listDataListener ) {
        cutListeners.add( ListDataListener.class, listDataListener );
    }
    
    /**
     * Remove a ListDataListener
     * @param listDataListener the ListDataListener to remove
     *
     */
    public void removeListDataListener( ListDataListener listDataListener ) {
        cutListeners.remove( ListDataListener.class, listDataListener );
    }
    
    /**
     * Fire an event to the ListDataListeners when a cut is added to the CutSet
     * @param index the index of the added cut
     *
     */
    private void fireCutAdded(int index) {
        if ( cutListeners.getListenerCount( ListDataListener.class ) == 0 ) return;
        ListDataEvent e = new ListDataEvent( this, ListDataEvent.INTERVAL_ADDED, index, index );
        ListDataListener[] l = ( ListDataListener[] ) cutListeners.getListeners( ListDataListener.class );
        for ( int i = 0; i < l.length; i++) {
            l[i].intervalAdded( e );
        }
    }
    
    /**
     * Fire an event to the ListDataListeners when a cut is removed from the CutSet
     * @param index the index of the cut that has been removed
     *
     */
    private void fireCutRemoved(int index) {
        if ( cutListeners.getListenerCount( ListDataListener.class ) == 0 ) return;
        ListDataEvent e = new ListDataEvent( this, ListDataEvent.INTERVAL_REMOVED, index, index );
        ListDataListener[] l = ( ListDataListener[] ) cutListeners.getListeners( ListDataListener.class );
        for ( int i = 0; i < l.length; i++ ) {
            l[i].intervalRemoved( e );
        }
    }
    
    /**
     * CutState class. For each Cut belonging to the CutSet
     * it stores the information on its internal state
     *
     */
    private class CutState {
        int state;
        Cut cut;
        CutState( Cut cut, int state ) {
            this.state = state;
            this.cut = cut;
        }
    }
    
}
