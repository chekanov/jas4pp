package org.freehep.jas.extension.tupleExplorer.cut;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JPanel;


/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutStatePanel.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */

public class CutStatePanel extends JPanel implements CutGUI {
    
    private Cut cut;
    private int    cutState;
    private JCheckBox invertCut;
    private JCheckBox disableCut;
    private CutListenerAdapter cutListener;

    /**
     * Create a new CutStatePanel object.
     * @param cut the cut this panel is representing
     *
     */
    public CutStatePanel( Cut c )
    {
	super( new BorderLayout() );
	cut = c;

	cutListener = new CutListenerAdapter()
	    {
		public void cutStateChanged( CutChangedEvent cutChangedEvent )
		{
		    cutState = cutChangedEvent.getCutState();
		    setCheckBoxState();
		}
	    };

	cut.addCutListener( cutListener );
	
	cutState  = c.getState();
	
	initCutStatePanel();
    }

    /**
     * Initialization of a CutStatePanel
     *
     */
    private void initCutStatePanel() {

	/* The  CheckBox to invert the cut and to enable/disable it */
	Box box = Box.createVerticalBox();
	
	invertCut = new JCheckBox("Inverted");
	invertCut.getInsets().bottom = 0;
	invertCut.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
		    if ( invertCut.isSelected() ) {
			cut.setState( Cut.CUT_INVERTED );
		    } else {
			cut.setState( Cut.CUT_ENABLED );
		    }
		}
	    });
	
        disableCut = new JCheckBox("Disabled");
	disableCut.getInsets().top = 0;
	disableCut.addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e ) {
		    if ( disableCut.isSelected() ) {
			if ( invertCut.isSelected() ) {
			    cut.setState( Cut.CUT_DISABLED_INVERTED );
			} else {
			    cut.setState( Cut.CUT_DISABLED );
			}
			invertCut.setEnabled( false );
		    } else {
			invertCut.setEnabled( true );
			if ( invertCut.isSelected() ) {
			    cut.setState( Cut.CUT_INVERTED );
			} else {
			    cut.setState( Cut.CUT_ENABLED );
			}
		    }
		}
	    });
	setCheckBoxState();

	box.add( box.createVerticalGlue() );
	box.add( disableCut );
	box.add( invertCut );
	box.add( box.createVerticalGlue() );
	add( box, BorderLayout.EAST );
    }
        
    /**
     * Check the status of the cut and set the
     * corresponding configuration of the check boxes
     *
     */
    private void setCheckBoxState()
    { 
	invertCut.setSelected( ( cutState == Cut.CUT_INVERTED || cutState == Cut.CUT_DISABLED_INVERTED ) );

	boolean enabled =  ( cutState == Cut.CUT_DISABLED || cutState == Cut.CUT_DISABLED_INVERTED );
	invertCut.setEnabled  ( !enabled );
	disableCut.setSelected( enabled );
    }

    /**
     * Get the Cut that this Panel is a representation of.
     * @return the Cut
     *
     */
    public Cut getCut()
    {
	return cut;
    }

    /**
     * When closing the GUI it is important to
     * properly remove all the listeners associated
     * with the Cut.
     *
     */
    public void removeCutListeners()
    {
	cut.removeCutListener( cutListener );
    }

}
