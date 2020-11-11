package org.freehep.jas.extension.tupleExplorer.cut;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 * @version $Id: CutVariableSliderModel.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */

public class CutVariableSliderModel extends DefaultBoundedRangeModel implements CutVariableGUI
{
    private CutVariable cutVar;
    private double cutVarMinVal, cutVarMaxVal, cutVarCurrentVal;
    private double range;
    private int bins = 100000;
    private CutVariableListenerAdapter cutVarListener;
    private boolean isChanging = false;
    private ChangeListener changeListener;
    private int type;

    /**
     * There are three types of sliders
     *
     */
    public static final int LEFT_SLIDER   = -1;
    public static final int CENTER_SLIDER = 0;
    public static final int RIGHT_SLIDER  = 1;

    /**
     * Create a new CutVariableSliderModel object.
     * @param cutVar the cutVariable the model is representing
     * @param type the type of this model
     *
     */
    public CutVariableSliderModel( CutVariable cutVar, int type )
    {
	setMinimum( 0 );
	setMaximum( bins );

	this.cutVar = cutVar;
	this.type = type;

	cutVarListener = new CutVariableListenerAdapter( this ) {
		public void cutVarValueChanged( CutChangedEvent cutChangedEvent )
		{
		    isChanging = true;
		    CutVariableSliderModel.this.setVarValue( cutChangedEvent.getVarValue() );   
		    isChanging = false;
		}
	    };
	cutVar.addCutVariableListener( cutVarListener );

	changeListener = new ChangeListener() 
	    {
		public void stateChanged( ChangeEvent e )
		{
		    if ( ! isChanging ) {
			if ( ! CutVariableSliderModel.this.cutVar.isLocked() ) {
			    CutVariableSliderModel.this.cutVar.setValue( getVarValue() );
			}
		    }
		}
	    };
	
	addChangeListener( changeListener );

	cutVarMinVal = cutVar.getMin();
	cutVarMaxVal = cutVar.getMax();
	cutVarCurrentVal = cutVar.getValue();
	range = cutVarMaxVal - cutVarMinVal;
	setVarValue( cutVarCurrentVal );
    }

    /**
     * Create a new CutVariableSliderModel object.
     * @param cutVar the cutVariable the model is representing
     *
     */
    public CutVariableSliderModel( CutVariable cutVar )
    {
	this( cutVar, CENTER_SLIDER );
    }

    /**
     * Get the CutVariable value corresponding
     * to the slider position.
     * @return the CutVariable value
     *
     */
    private double getVarValue()
    {
	return cutVarMinVal + getValue()*range/(double) bins;
    }

    /**
     * Set the slider position given the CutVariable's value
     * @param cutVarValue the CutVariable value
     *
     */
    private void setVarValue( double cutVarValue )
    {
	int value = (int) ( bins * ( cutVarValue - cutVarMinVal )/range );
	setValue( value );
    }

    /**
     * Get the Cut that this Panel is a representation of.
     * @return the Cut
     *
     */
    public CutVariable getCutVariable()
    {
	return cutVar;
    }

    /**
     * When closing the GUI it is important to
     * properly remove all the listeners associated
     * with the CutVariable.
     *
     */
    public void removeCutVariableListeners()
    {
	cutVar.removeCutVariableListener( cutVarListener );
    }

    /**
     * Check if the cutVariable is enabled
     * @return <code>true<\code> if the variable is enabled
     *         <code>false<\code> otherwise
     *
     */
    public boolean isEnabled()
    {
	return ( cutVar.getState() == CutVariable.CUTVARIABLE_LOCKED ) ? false : true;
    }

    /**
     * Set the type of this model.
     * @param type the type of this model
     *
     */
    public void setType( int type )
    {
	this.type = type;
    }

    /**
     * Get the type of this model.
     * @return the type of this model
     *
     */
    public int getType()
    {
        return type;
    }

}
