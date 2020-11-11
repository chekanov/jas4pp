package org.freehep.jas.extension.tupleExplorer.cut;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 * @version $Id: CutVariableSlider.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */

public class CutVariableSlider extends JSlider implements CutVariableGUI {

    private CutVariable cutVar;
    private double cutVarMinVal, cutVarMaxVal, cutVarCurrentVal;
    private double range;
    private int bins = 100000;
    private CutVariableListenerAdapter cutVarListener;
    private boolean isChanging = false;
    private ChangeListener changeListener;
    private MouseAdapter mouseAdapter;
    
    /**
     * Create a new CutVariableSlider object.
     * @param cut the cut this panel is representing
     *
     */
    public CutVariableSlider( CutVariable cutVar ) {
        setMinimum( 1 );
        setMaximum( bins );
        //	putClientProperty( "JSlider.isFilled", Boolean.TRUE );
        setPaintTrack( true );
        
        this.cutVar = cutVar;
        cutVarMinVal = cutVar.getMin();
        cutVarMaxVal = cutVar.getMax();
        cutVarCurrentVal = cutVar.getValue();
        range = cutVarMaxVal - cutVarMinVal;
        setModelValue( cutVarCurrentVal );
        if ( cutVar.isLocked() ) setEnabled( false );
        
        
        cutVarListener = new CutVariableListenerAdapter( this ) {
            public void cutVarValueChanged( CutChangedEvent cutChangedEvent ) {
                isChanging = true;
                CutVariableSlider.this.setVarValue( cutChangedEvent.getVarValue() );
                isChanging = false;
            }
            public void cutVarStateChanged( CutChangedEvent cutChangedEvent ) {
                if ( cutChangedEvent.getVarState() == CutVariable.CUTVARIABLE_LOCKED ) {
                    setEnabled( false );
                } else {
                    setEnabled( true );
                }
            }
        };
        cutVar.addCutVariableListener( cutVarListener );
        
        changeListener = new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                if ( ! isChanging ) {
                    if ( ! CutVariableSlider.this.cutVar.isLocked() ) {
                        CutVariableSlider.this.cutVar.setValue( getVarValue() );
                    }
                }
            }
        };
        
        addChangeListener( changeListener );
        
        mouseAdapter = new MouseAdapter() {
            public void mouseReleased( MouseEvent e ) {
                isChanging = true;
                double value = CutVariableSlider.this.cutVar.getValue();
                setVarValue( 2*value );
                setVarValue( value );
                isChanging = false;
            }
        };
        addMouseListener(mouseAdapter);
        
        
    }
    
    /**
     * Get the CutVariable value corresponding
     * to the slider position.
     * @return the CutVariable value
     *
     */
    private double getVarValue() {
        return cutVarMinVal + getValue()*range/(double) bins;
    }
    
    /**
     * Set the slider position given the CutVariable's value
     * @param cutVarValue the CutVariable value
     *
     */
    private void setVarValue( double cutVarValue ) {
        if ( cutVar.isLocked() ) return;
        setModelValue( cutVarValue );
    }
    
    /**
     * Set the slider's model position given the CutVariable's value
     * @param cutVarValue the CutVariable value
     *
     */
    private void setModelValue( double cutVarValue ) {
        int value = (int) ( bins * ( cutVarValue - cutVarMinVal )/range );
        getModel().setValue( value );
    }
    
    /**
     * Get the Cut that this Panel is a representation of.
     * @return the Cut
     *
     */
    public CutVariable getCutVariable() {
        return cutVar;
    }
    
    /**
     * When closing the GUI it is important to
     * properly remove all the listeners associated
     * with the CutVariable.
     *
     */
    public void removeCutVariableListeners() {
        cutVar.removeCutVariableListener( cutVarListener );
    }
    
}
