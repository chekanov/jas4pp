package org.freehep.jas.extension.tupleExplorer.cut;

import java.awt.BorderLayout;
import java.awt.event.*;
import java.awt.Font;
import javax.swing.JTextField;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutVariableValuePanel.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */

public class CutVariableValuePanel extends JTextField implements CutVariableGUI
{
    
    private CutVariable cutVar;
    private CutVariableListenerAdapter cutVariableListener;
    private int valLength = 6;

    /**
     * Create a new CutVariableValuePanel object.
     * @param cutVar the cutVar this panel is representing
     *
     */
    public CutVariableValuePanel( CutVariable cutVar )
    {
	this.cutVar = cutVar;
	cutVariableListener = new CutVariableListenerAdapter( this )
	    {
		public void cutVarValueChanged( CutChangedEvent cutChangedEvent )
		{
		    CutVariableValuePanel.this.setStringValue( cutChangedEvent.getVarValue() );
		    CutVariableValuePanel.this.doLayout();
		}
		public void cutVarStateChanged( CutChangedEvent cutChangedEvent )
		{
		    if ( cutChangedEvent.getVarState() == CutVariable.CUTVARIABLE_LOCKED )
			CutVariableValuePanel.this.setEditable( false );
		}
	    }; 
	cutVar.addCutVariableListener( cutVariableListener );
	initCutVariableValuePanel();
    }

    /**
     * Set the textField text converting the CutVariable value to String
     * @param value the value of the CutVariable
     *
     */
    private void setStringValue( double value )
    {
	String sValue = String.valueOf( value );
	sValue = sValue.substring( 0, Math.min( valLength, sValue.length() ) );
	setText( sValue );
	setColumns( Math.max( valLength, sValue.length() ) );
    }

    /**
     * Initialization of a CutVariableValuePanel
     *
     */
    private void initCutVariableValuePanel() {

	/* The TextField for the cut name */
	setHorizontalAlignment( JTextField.CENTER );
	setFont( new Font( "Serif", Font.BOLD, 12 ) );
	setStringValue( cutVar.getValue() );
	setEditable( false );
	addActionListener( new ActionListener()
	    {
		public void actionPerformed( ActionEvent e )
		{
		    String newValue = getText();
		    CutVariableValuePanel.this.setStringValue( Double.parseDouble( newValue ) );
		    cutVar.setValue( Double.parseDouble( newValue ) );
		    CutVariableValuePanel.this.setColumns( Math.max( valLength, newValue.length() ) );
		    CutVariableValuePanel.this.doLayout();
		    CutVariableValuePanel.this.setEditable( false );
		} 
	    } );	
	addMouseListener( new MouseAdapter()
	    {
		public void mouseClicked( MouseEvent e )
		{ 
		    if ( ! cutVar.isLocked() )
			CutVariableValuePanel.this.setEditable( true );
		}
	    } );	

	addFocusListener( new FocusAdapter()
	    {
		public void focusLost( FocusEvent e )
		{
		    if ( CutVariableValuePanel.this.isEditable() )
			CutVariableValuePanel.this.setEditable( false );
		}
	    } );
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
	cutVar.removeCutVariableListener( cutVariableListener );
    }

}
