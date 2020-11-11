package org.freehep.jas.extension.tupleExplorer.cut;

import javax.swing.event.EventListenerList;

/**
 * AbstractCutVariable the abstract implementation of CutVariable
 * @author The FreeHEP team @ SLAC.
 * @version $Id: AbstractCutVariable.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */
public abstract class AbstractCutVariable implements CutVariable {

    private String cutVarName;
    private int cutVarState;
    private double lowerBound, upperBound, cutVarValue, cutVarValueOld;
    private EventListenerList cutVarListeners = new EventListenerList();
    private CutDataSet cutDataSet;
    private CutVariableListener cutVariableGUIListener = new CutVariableGUIListener();
    
    /**
     * Create a new AbstractCutVariable object
     * @param cutVarName the name of the cut variable
     *
     */
    public AbstractCutVariable( String cutVarName ) {
        this.cutVarName  = cutVarName;
        this.cutVarState = CUTVARIABLE_UNLOCKED;
    }
    
    /**
     * Create a new AbstractCutVariable object
     * @param cutVarName the name of the cut variable
     * @param cutDataSet    the CutDataSet data set
     *
     */
    public AbstractCutVariable( String cutVarName, CutDataSet cutDataSet ) {
        this.cutVarName  = cutVarName;
        this.cutVarState = CUTVARIABLE_UNLOCKED;
        setCutDataSet( cutDataSet );
    }
    
    /**
     * Set the name of the CutVariable
     * @param cutVarName the name of the CutVariable
     *
     */
    public void setName( String cutVarName ) {
        this.cutVarName = cutVarName;
    }
    
    /**
     * Get the name of the CutVariable
     * @return the name of the CutVariable
     *
     */
    public String getName() {
        return cutVarName;
    }
    
    /**
     * Set the CutVariable range.
     * @param lowerBound the lower bound of the range
     * @param upperBound  the upper bound of the range
     *
     */
    public void setRange( double lowerBound, double upperBound ) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
    /**
     * Get the lower bound of the range
     * @return the lower bound of the range
     *
     */
    public double getMin() {
        return lowerBound - 0.05*Math.abs(lowerBound);
    }
    
    /**
     * Get the upper bound of the range
     * @return the upper bound of the range
     *
     */
    public double getMax() {
        return upperBound +0.05*Math.abs(upperBound);
    }
    
    /**
     * Set the lower bound of the range
     * @param lowerBound the lower bound of the range
     *
     */
    public void setMin( double lowerBound ) {
        this.lowerBound = lowerBound;
    }
    
    /**
     * Set the upper bound of the range
     * @param upperBound the upper bound of the range
     *
     */
    public void setMax( double upperBound ) {
        this.upperBound = upperBound;
    }
    
    /**
     * Set the state of the CutVariable
     * @param cutVarState the state of the CutVariable
     *
     */
    public void setState( int cutVarState ) {
        this.cutVarState = cutVarState;
        fireCutVarStateChanged();
    }
    
    /**
     * Get the state of the CutVariable
     * @return the state of the CutVariable
     *
     */
    public int getState() {
        return cutVarState;
    }
    
    /**
     * Check if the CutVariable is locked
     * @return <code>true<\code> if the variable is locked
     *         <code>false<\code> otherwise
     *
     */
    public boolean isLocked() {
        if ( cutVarState == CUTVARIABLE_LOCKED )
            return true;
        return false;
    }
    
    /**
     * Set the current value of the AbstrctCutVariable.
     * @param cutVarValue the current value of the CutVariable
     *
     */
    public void setValue( double cutVarValue ) {
        if ( ! isLocked() ) {
            if ( cutVarValue < getMin() || cutVarValue > getMax() ) {
                if ( cutVarValue < getMin() ) cutVarValue = getMin();
                if ( cutVarValue > getMin() ) cutVarValue = getMax();
            }
            this.cutVarValueOld = this.cutVarValue;
            this.cutVarValue = cutVarValue;
            fireCutVarValueChanged();
        }
    }
    
    /**
     * Get the current value of the CutVariable.
     * @return the current value of the CutVariable
     *
     */
    public double getValue() {
        return cutVarValue;
    }
    
    /**
     * Get the CutVariable change in its value
     * @return the change in the CutVariable's value
     *
     */
    public double getValueChange() {
        return cutVarValueOld - cutVarValue;
    }
    
    /**
     * Assign to this CutVariable a CutDataSet. The CutDataSet data set is used
     * to set the CutVariable's range.
     * @param cutDataSet the CutDataSet data set
     *
     */
    public void setCutDataSet( CutDataSet cutDataSet ) {
        this.cutDataSet = cutDataSet;
        setRange( cutDataSet.getDataMinValue(), cutDataSet.getDataMaxValue() );
    }
    
    /**
     * Get the CutDataSet corresponding to this CutVariable
     * @return the CutDataSet
     *
     */
    public CutDataSet getCutDataSet() {
        return cutDataSet;
    }
    
    /**
     * Add a CutVariableListener to the CutVariable
     * @param cutVarListener the CutVariableListenere to add
     *
     */
    public void addCutVariableListener( CutVariableListener cutVarListener ) {
        cutVarListeners.add( CutVariableListener.class, cutVarListener );
    }
    
    /**
     * Remove a CutVariableListener from the CutVariable
     * @param cutVarListener the CutVariableListenere to remove
     *
     */
    public void removeCutVariableListener( CutVariableListener cutVarListener ) {
        cutVarListeners.remove( CutVariableListener.class, cutVarListener );
    }
    
    /**
     * Fire a CutChangedEvent when the cutVariable's range is changed.
     * Invoke the <code>cutVarRangeChanged<\code> method of all the
     * listeners but the one responsible for the change, if any.
     * @param cutChangedEvent the event containing the change
     * @see CutVariableListener#cutVarRangeChanged( CutChangedEvent )
     *
     */
    private void fireCutVarRangeChanged() {
        CutVariableListener[] listeners = ( CutVariableListener[] ) cutVarListeners.getListeners( CutVariableListener.class );
        if ( listeners.length > 0 ) {
            CutChangedEvent rangeChangedEvent = new CutChangedEvent( this );
            for (int i=0; i<listeners.length; i++) {
                CutVariableListener listener = listeners[i];
                listener.cutVarRangeChanged( rangeChangedEvent );
            }
        }
    }
    
    /**
     * Fire a CutChangedEvent when the cutVariable's state is changed.
     * Invoke the <code>cutVarStateChanged<\code> method of all the
     * listeners but the one responsible for the change, if any.
     * @param cutChangedEvent the event containing the change
     * @see CutVariableListener#cutVarStateChanged( CutChangedEvent )
     *
     */
    private void fireCutVarStateChanged() {
        CutVariableListener[] listeners = ( CutVariableListener[] ) cutVarListeners.getListeners( CutVariableListener.class );
        if ( listeners.length > 0 ) {
            CutChangedEvent stateChangedEvent = new CutChangedEvent( this );
            for (int i=0; i<listeners.length; i++) {
                CutVariableListener listener = listeners[i];
                listener.cutVarStateChanged( stateChangedEvent );
            }
        }
    }
    
    /**
     * Fire a CutChangedEvent when the cutVariable's value is changed.
     * Invoke the <code>cutVarValueChanged<\code> method of all the
     * listeners but the one responsible for the change, if any.
     * @param cutChangedEvent the event containing the change
     * @see CutVariableListener#cutVarValueChanged( CutChangedEvent )
     *
     */
    private void fireCutVarValueChanged() {
        CutVariableListener[] listeners = ( CutVariableListener[] ) cutVarListeners.getListeners( CutVariableListener.class );
        if ( listeners.length > 0 ) {
            CutChangedEvent valueChangedEvent = new CutChangedEvent( this );
            for (int i=0; i<listeners.length; i++) {
                CutVariableListener listener = listeners[i];
                
                listener.cutVarValueChanged( valueChangedEvent );
            }
        }
    }
    
    /**
     * Get the CutVariableGUIListener that listens to the GUI representations
     * of the CutVariable.
     * @return the CutVariableGUIListener
     *
     */
    public CutVariableListener getCutVariableGUIListener() {
        return cutVariableGUIListener;
    }
    
    /**
     * Internal class that implements the behavior of CutVariableListener.
     * This class should be registered with the GUI representations of the
     * CutVariable to listen to their CutChangedEvents.
     *
     */
    class CutVariableGUIListener implements CutVariableListener {
        
        /**
         * Invoked when the CutVariable has changed the current value
         * @param cutChangedEvent the event describing the change
         *
         */
        public void cutVarValueChanged( CutChangedEvent cutChangedEvent ) {
            double newValue = cutChangedEvent.getVarValue();
            setValue( newValue );
        }
        
        
        /**
         * Invoked when the CutVariable range has changed
         * @param cutChangedEvent the event describing the change
         *
         */
        public void cutVarRangeChanged( CutChangedEvent cutChangedEvent ) {
            double minValue = cutChangedEvent.getVarMin();
            double maxValue = cutChangedEvent.getVarMax();
            
            setRange( minValue, maxValue );
            if ( getCutDataSet() != null ) {
                cutDataSet.setDataMinValue( minValue );
                cutDataSet.setDataMaxValue( maxValue );
            }
        }
        
        /**
         * Invoked when the CutVariable state has changed
         * @param cutChangedEvent the event describing the change
         *
         */
        public void cutVarStateChanged( CutChangedEvent cutChangedEvent ) {
            int newVarState = cutChangedEvent.getVarState();
            if ( getState() != newVarState ) {
                setState( newVarState );
            }
        }
    }            
}
