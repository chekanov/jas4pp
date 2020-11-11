package org.freehep.jas.extension.tupleExplorer.cut;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutVariableLockPanel.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */

public class CutVariableLockPanel extends JPanel implements CutVariableGUI
{
    private CutVariable cutVar;
    private int varState;
    private JToggleButton lockButton;
    private CutVariableListenerAdapter cutVarListener;

    /**
     * Create a new CutVariableLockPanel object.
     * @param cut the cut this panel is representing
     *
     */
    public CutVariableLockPanel( CutVariable cVar )
    {
	super( new BorderLayout() );

	cutVar = cVar;
	cutVarListener = new CutVariableListenerAdapter( this )
	    {
		public void cutVarStateChanged( CutChangedEvent cutChangedEvent )
		{
		    varState = cutChangedEvent.getVarState();
		    setLockState();
		}
	    };
	cutVar.addCutVariableListener( cutVarListener );

	varState = cutVar.getState();
	initLockButton();
    }

    /**
     * Initialization of the lock/unlock button
     *
     */
    private void initLockButton()
    {
	/* The Button to lock/unlock the cut */
	lockButton = new JToggleButton();
	lockButton.setSelectedIcon( lockedIcon );
	lockButton.setIcon( unlockIcon );
	lockButton.setMargin( new java.awt.Insets(0,0,0,0) );
	lockButton.setBorderPainted( false );
	lockButton.setContentAreaFilled( false );
	setLockState();
	lockButton.addActionListener( new ActionListener() 
	    {
		public void actionPerformed( ActionEvent e )
		{
		    if ( lockButton.isSelected() ) {
			cutVar.setState( CutVariable.CUTVARIABLE_LOCKED );
			varState = CutVariable.CUTVARIABLE_LOCKED;
		    } else {
			int cutVarState = cutVar.getState();
			if ( cutVarState == CutVariable.CUTVARIABLE_LOCKED ) {
			    cutVar.setState( CutVariable.CUTVARIABLE_UNLOCKED );
			} else {
			    cutVar.setState( cutVarState );
			}
		    }
		}
	    });
	add( lockButton, BorderLayout.EAST );
    }
        
    /**
     * Set the state of the lock button
     *
     */
    private void setLockState()
    {	
	if ( varState == CutVariable.CUTVARIABLE_LOCKED ) {
	    lockButton.setSelected( true );
	} else {
	    lockButton.setSelected( false );
	} 
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

    /*
     * The lock icons
     *
     */
    private final static Icon lockedIcon = org.freehep.util.images.ImageHandler.getIcon( "lock.gif", CutPanel.class );
    private final static Icon unlockIcon = org.freehep.util.images.ImageHandler.getIcon( "unlock.gif", CutPanel.class );

}
