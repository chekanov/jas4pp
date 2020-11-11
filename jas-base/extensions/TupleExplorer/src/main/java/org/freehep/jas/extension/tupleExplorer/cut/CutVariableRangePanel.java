package org.freehep.jas.extension.tupleExplorer.cut;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutVariableRangePanel.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */

public class CutVariableRangePanel extends JPanel implements CutVariableGUI
{
    private CutVariable cutVar;
    private int varState;
    private JButton leftButton;
    private JButton rightButton;
    private CutVariableListenerAdapter cutVarListener;

    /**
     * Create a new CutVariableRangePanel object.
     * @param cut the cut this panel is representing
     *
     */
    public CutVariableRangePanel( CutVariable cVar )
    {
	super( new BorderLayout() );

	cutVar = cVar;
	cutVarListener = new CutVariableListenerAdapter( this )
	    {
		public void cutVarRangeChanged( CutChangedEvent cutChangedEvent )
		{

		}
	    };
	cutVar.addCutVariableListener( cutVarListener );

	varState = cutVar.getState();
	initRangeButtons();
    }

    /**
     * Initialization of the range buttons
     *
     */
    private void initRangeButtons()
    {
	/* The left range button */
	leftButton = new JButton();
	leftButton.setIcon( leftIcon );
	leftButton.setMargin( new java.awt.Insets(0,0,0,0) );
	leftButton.setBorderPainted( false );
	leftButton.setContentAreaFilled( false );

	leftButton.addActionListener( new ActionListener() 
	    {
		public void actionPerformed( ActionEvent e )
		{
		    //		    System.out.print("Change the left margin\n");
		}
	    });
	add( leftButton, BorderLayout.WEST );

	/* The left range button */
	rightButton = new JButton();
	rightButton.setIcon( rightIcon );
	rightButton.setMargin( new java.awt.Insets(0,0,0,0) );
	rightButton.setBorderPainted( false );
	rightButton.setContentAreaFilled( false );

	rightButton.addActionListener( new ActionListener() 
	    {
		public void actionPerformed( ActionEvent e )
		{
		    //		    System.out.print("Change the right margin\n");
		}
	    });
	add( rightButton, BorderLayout.EAST );


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
     * The buttons icons
     *
     */
    private final static Icon leftIcon = org.freehep.util.images.ImageHandler.getIcon( "left.gif", CutPanel.class );
    private final static Icon rightIcon = org.freehep.util.images.ImageHandler.getIcon( "right.gif", CutPanel.class );

}
