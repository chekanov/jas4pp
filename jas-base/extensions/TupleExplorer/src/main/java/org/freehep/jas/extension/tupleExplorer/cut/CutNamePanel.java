package org.freehep.jas.extension.tupleExplorer.cut;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JTextField;


/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: CutNamePanel.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */

public class CutNamePanel extends JTextField implements CutGUI
{
    
    private Cut cut;
    private String panelName;
    private CutListenerAdapter cutListener;

    /**
     * Create a new CutNamePanel object.
     * @param cut the cut this panel is representing
     *
     */
    public CutNamePanel( Cut c )
    {
	cut = c;
	cutListener = new CutListenerAdapter()
	    {
		public void cutNameChanged( CutChangedEvent cutChangedEvent )
		{
		    panelName = cutChangedEvent.getCutName();
		    CutNamePanel.this.setText( panelName );
		    CutNamePanel.this.setColumns( panelName.length() );
		    CutNamePanel.this.doLayout();
		}
	    }; 
	cut.addCutListener( cutListener );

	panelName = cut.getName();	

	initCutNamePanel();
    }

    /**
     * Initialization of a CutNamePanel
     *
     */
    private void initCutNamePanel() {

	/* The TextField for the cut name */
	setColumns( panelName.length() );
	setHorizontalAlignment( JTextField.CENTER );
	setFont( new Font( "Serif", Font.BOLD, 12 ) );
	setText( panelName );
	setEditable( false );
	addActionListener( new ActionListener()
	    {
		public void actionPerformed( ActionEvent e )
		{
		    String newName = getText();
		    cut.setName( newName );
		    CutNamePanel.this.setColumns( newName.length() );
		    CutNamePanel.this.doLayout();
		    CutNamePanel.this.setEditable( false );
		} 
	    } );	
	addMouseListener( new MouseAdapter()
	    {
		public void mouseClicked( MouseEvent e )
		{ 
		    CutNamePanel.this.setEditable( true );
		}
	    } );	
	addFocusListener( new FocusAdapter()
	    {
		public void focusLost( FocusEvent e )
		{
		    if ( CutNamePanel.this.isEditable() )
			CutNamePanel.this.setEditable( false );
		}
	    } );
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




