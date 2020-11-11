package org.freehep.jas.extension.tupleExplorer.cut;

import javax.swing.event.EventListenerList;

/**
 * AbstractCut abstract implementation of Cut
 * @author The FreeHEP team @ SLAC.
 * @version $Id: AbstractCut.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public abstract class AbstractCut implements Cut {
    
    private String cutName;
    private int cutState;
    private CutListener cutGUIListener = new CutGUIListener();
    protected EventListenerList cutListeners = new EventListenerList();
    private CutDataSet cutDataSet;
    
    /**
     * The default constructor
     * @param cutName the name of the cut
     *
     */
    public AbstractCut( String cutName ) {
        this.cutName = cutName;
        this.cutState = CUT_ENABLED;
    }
    
    /**
     * Get the name of the AbstractCut
     * @return the name of the AbstractCut
     *
     */
    public String getName() {
        return cutName;
    }
    
    /**
     * Set the name of the AbstractCut
     * @param cutName the name of the AbstractCut
     *
     */
    public void setName( String cutName ) {
        this.cutName = cutName;
        fireCutNameChanged();
    }
    
    /**
     * Get the state of the AbstractCut.
     * @return the state of the AbstractCut
     *
     */
    public int getState() {
        return cutState;
    }
    
    /**
     * Set the state of the AbstractCut.
     * @param cutState the state of the AbstractCut
     *
     */
    public void setState( int cutState ) {
        this.cutState = cutState;
        fireCutStateChanged();
        fireCutChanged();
    }
    
    /**
     * Check if the Cut is enabled
     * @return <code>true<\code> if the cut is enabled
     *         <code>false<\code> otherwise
     *
     */
    public boolean isEnabled() {
        if ( cutState == CUT_ENABLED || cutState == CUT_INVERTED )
            return true;
        return false;
    }
    
    /**
     * Check if the Cut is inverted
     * @return <code>true<\code> if the cut is inverted
     *         <code>false<\code> otherwise
     *
     */
    public boolean isInverted() {
        if ( cutState == CUT_INVERTED || cutState == CUT_DISABLED_INVERTED )
            return true;
        return false;
    }
    
    /**
     * Get the CutDataSet on which the cut is applied
     * @return the CutDataSet.
     *
     */
    public CutDataSet getCutDataSet() {
        return cutDataSet;
    }
    
    /**
     * Set the CutDataSet on which the cut is applied
     * @param cutDataSet the CutDataSet.
     *
     */
    public void setCutDataSet( CutDataSet cutDataSet ) {
        this.cutDataSet = cutDataSet;
    }
    
    /**
     * Add a CutListener to the AbstractCut
     * @param cutListener the Listenere to add
     *
     */
    public void addCutListener( CutListener cutListener ) {
        cutListeners.add( CutListener.class, cutListener );
    }
    
    /**
     * Remove a CutListener from the AbstractCut
     * @param cutListener the CutListener to remove
     *
     */
    public void removeCutListener( CutListener cutListener ) {
        cutListeners.remove( CutListener.class, cutListener );
    }
    
    /**
     * Fire a CutChangedEvent when the cut's name is changed.
     * Invoke the <code>cutNameChanged<\code> method of all the
     * listeners
     * @see CutListener#cutNameChanged( CutChangedEvent )
     *
     */
    private void fireCutNameChanged() {
        CutListener[] listeners = ( CutListener[] ) cutListeners.getListeners( CutListener.class );
        if ( listeners.length > 0 ) {
            CutChangedEvent nameChangedEvent = new CutChangedEvent( this );
            for (int i=0; i<listeners.length; i++) {
                CutListener listener = listeners[i];
                listener.cutNameChanged( nameChangedEvent );
            }
        }
    }
    
    /**
     * Fire a CutChangedEvent when the cut's state is changed.
     * Invoke the <code>cutStateChanged<\code> method of all the
     * listeners
     * @see CutListener#cutStateChanged( CutChangedEvent )
     *
     */
    private void fireCutStateChanged() {
        CutListener[] listeners = ( CutListener[] ) cutListeners.getListeners( CutListener.class );
        if ( listeners.length > 0 ) {
            CutChangedEvent stateChangedEvent = new CutChangedEvent( this );
            for (int i=0; i<listeners.length; i++) {
                CutListener listener = listeners[i];
                listener.cutStateChanged( stateChangedEvent );
            }
        }
    }
    
    /**
     * Fire a CutChangedEvent when the cut's <code>accept<\code> condition is changed.
     * Invoke the <code>cutStateChanged<\code> method of all the
     * listeners
     * @see CutListener#cutChanged( CutChangedEvent )
     *
     */
    protected void fireCutChanged() {
        CutListener[] listeners = ( CutListener[] ) cutListeners.getListeners( CutListener.class );
        if ( listeners.length > 0 ) {
            CutChangedEvent changedEvent = new CutChangedEvent( this );
            for (int i=0; i<listeners.length; i++) {
                CutListener listener = listeners[i];
                listener.cutChanged( changedEvent );
            }
        }
    }
    
    /**
     * Get the list of CutListeners listening to this Cut
     * @return the list of CutListeners
     *
     */
    public EventListenerList getCutListeners() {
        return cutListeners;
    }
    
    /**
     * Get the CutListener internal component of this Cut.
     * @return the CutListener
     *
     */
    public CutListener getCutGUIListener() {
        return cutGUIListener;
    }
        
    /** 
     * Invert the cut.
     *
     */
    public void invert() {
        if ( cutState == Cut.CUT_ENABLED )
            setState( Cut.CUT_INVERTED );
        else if ( cutState == Cut.CUT_INVERTED )
            setState( Cut.CUT_ENABLED );
    }
    
    /** 
     * Disable the cut.
     *
     */
    public void setDisabled(boolean isDisabled) {
        if ( cutState == Cut.CUT_DISABLED ) {
            if ( ! isDisabled )
                setState( Cut.CUT_ENABLED );
        } else if ( cutState == Cut.CUT_DISABLED_INVERTED ) {
            if ( ! isDisabled )
                setState( Cut.CUT_INVERTED );
        } else if ( cutState == Cut.CUT_ENABLED ) {
            if ( isDisabled )
                setState( Cut.CUT_DISABLED );
        } else if ( cutState == Cut.CUT_INVERTED ) {
            if ( isDisabled )
                setState( Cut.CUT_DISABLED_INVERTED );
        }            
    }
    
    /**
     * Internal class that implements the behavior of CutListener.
     *
     */
    class CutGUIListener implements CutListener {
        
        /**
         * Invoked when the Cut name has changed
         * @param cutChangedEvent the event describing the change
         * @see CutListener#cutNameChanged( CutChangedEvent )
         *
         */
        public void cutNameChanged( CutChangedEvent cutChangedEvent ) {
            if ( getName() != cutChangedEvent.getCutName() )
                setName( cutChangedEvent.getCutName() );
        }
        
        /**
         * Invoked when the Cut state has changed
         * @param cutChangedEvent the event describing the change
         * @see CutListener#cutStateChanged( CutChangedEvent )
         *
         */
        public void cutStateChanged( CutChangedEvent cutChangedEvent ) {
            if ( getState() != cutChangedEvent.getCutState() )
                setState( cutChangedEvent.getCutState() );
        }
        
        /**
         * Invoked when the condition in the <code>accept<\code> method has changed
         * @param cutChangedEvent the event describing the change
         * @see CutListener#cutChanged( CutChangedEvent )
         *
         */
        public void cutChanged( CutChangedEvent cutChangedEvent ) {
            fireCutChanged();
        }
    }    
    
    
    
    
}
