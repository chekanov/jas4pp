package org.freehep.jas.extension.tupleExplorer.cut;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: Numeric1DCutPanel.java 13893 2011-09-28 23:42:34Z tonyj $
 *
 */

public class Numeric1DCutPanel extends CutPanel {
    
    private Numeric1DCut cut;
    //    private SliderTrack sliderTrack;
    private Color acceptedColor = Color.green;
    private Color rejectedColor = Color.gray;
    private JSlider slider1;
    private JSlider slider2;
    private int nCutVar;
    
    /**
     * Create a new Numeric1DCutPanel object.
     *
     */
    public Numeric1DCutPanel() {
    }
    
    /**
     * Create a new Numeric1DCutPanel object.
     * @param cut the cut this panel is representing
     *
     */
    public Numeric1DCutPanel( Numeric1DCut cut ) {
        this.cut = cut;
        setCut( cut );
        initNumeric1DCutPanel();
    }
    
    /**
     * Initialize the CutPanel
     *
     */
    private void initNumeric1DCutPanel() {
        nCutVar = cut.getNCutVariables();
        if ( nCutVar == 0 ) return;
        
        JPanel varPanel = new JPanel( new BorderLayout() );
        
        
        if ( nCutVar == 2 ) {
            CutVariable cutVar1 = cut.getCutVariable( 0 );
            CutVariable cutVar2 = cut.getCutVariable( 1 );
            CutVariableSliderModel cutVarModel2 = new CutVariableSliderModel( cutVar2, CutVariableSliderModel.RIGHT_SLIDER );
            CutVariableSliderModel cutVarModel1 = new CutVariableSliderModel( cutVar1, CutVariableSliderModel.LEFT_SLIDER );
            
            slider1 = new CutVariableSlider( cutVar1 );
            slider2 = new CutVariableSlider( cutVar2 );
            
            JPanel varPanel1 = new JPanel( new BorderLayout() );
            varPanel1.add( new CutVariableLockPanel( cutVar1 ), BorderLayout.EAST );
            varPanel1.add( new CutVariableValuePanel( cutVar1 ), BorderLayout.CENTER );
            varPanel1.add( slider1, BorderLayout.WEST );
            
            JPanel varPanel2 = new JPanel( new BorderLayout() );
            varPanel2.add( new CutVariableLockPanel( cutVar2 ), BorderLayout.EAST );
            varPanel2.add( new CutVariableValuePanel( cutVar2 ), BorderLayout.CENTER );
            varPanel2.add( slider2, BorderLayout.WEST );
            
            JPanel varPanel12 = new JPanel( new BorderLayout() );
            varPanel12.add( varPanel1, BorderLayout.NORTH );
            varPanel12.add( varPanel2, BorderLayout.SOUTH );
            
            final JCheckBox synchronizeBox = new JCheckBox("Sync",false);
            synchronizeBox.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( synchronizeBox.isSelected() )
                        cut.synchronizeSliders();
                    else
                        cut.resetSynchronization();
                }
            });
            
            varPanel.add(synchronizeBox, BorderLayout.EAST);
            
            varPanel.add( varPanel12, BorderLayout.CENTER);
            
            
        } else if ( nCutVar == 1 ) {
            CutVariable cutVar1 = cut.getCutVariable( 0 );
            CutVariableSliderModel cutVarModel1 = new CutVariableSliderModel( cutVar1, CutVariableSliderModel.CENTER_SLIDER );
            slider1 = new CutVariableSlider( cutVar1 );
            JPanel varPanel1 = new JPanel( new BorderLayout() );
            varPanel1.add( new CutVariableLockPanel( cutVar1 ), BorderLayout.EAST );
            varPanel1.add( new CutVariableValuePanel( cutVar1 ), BorderLayout.CENTER );
            varPanel1.add( slider1, BorderLayout.WEST );
            varPanel.add( varPanel1, BorderLayout.CENTER);
        }
        
        add( varPanel, BorderLayout.CENTER );
        setSliderTrackColor();
        
    }
    
    /**
     * Set the enabled/disabled state of the sliders
     *
     */
    private void setSlidersEnabled( boolean state ) {
        slider1.setEnabled( state );
        if ( nCutVar == 2 )
            slider2.setEnabled( state );
    }
    
    /**
     * Set the colors of the slider tracks
     *
     */
    private void setSliderTrackColor() {
        
        if ( nCutVar == 1 ) {
            if ( cut instanceof Numeric1DCut ) {
                switch ( ( ( Numeric1DCut ) cut ).getCutType() ) {
                    case Numeric1DCut.NUMERIC1DCUT_GREATER_THAN:
                        break;
                    case Numeric1DCut.NUMERIC1DCUT_LESS_THAN:
                        break;
                }
                slider1.repaint();
            }
        } else if ( nCutVar == 2 ) {
            
            if ( cut instanceof Numeric1DCut ) {
                switch ( ( ( Numeric1DCut ) cut ).getCutType() ) {
                    case Numeric1DCut.NUMERIC1DCUT_INCLUSIVE:
                        
                        break;
                    case Numeric1DCut.NUMERIC1DCUT_EXCLUSIVE:
                        
                        break;
                }
                slider1.repaint();
                slider2.repaint();
            }
        }
        
        //	sliderTrack.putClientProperty( "JSlider.isFilled", Boolean.TRUE );
        //	sliderTrack.setAcceptColor(acceptedColor);
        //	sliderTrack.setRejectColor(rejectedColor);
        
        /*
         
         
        }
         */
    }
    
}






