package org.freehep.jas.extension.tupleExplorer.cut;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import java.lang.reflect.Field;



/**
 * @version 1.0 09/08/99
 */
public class SliderTrackUI extends BasicSliderUI {
  
    private SliderTrack  sliderTrack;
    private int           thumbNum;
    private ChangeHandler changeHandler;
    private TrackListener trackList;
    
    
    public SliderTrackUI( SliderTrack sliderTrack )   {
	super( sliderTrack );
	this.sliderTrack = sliderTrack;
    }
    
    
    public void installUI(JComponent c)   {
	super.installUI( c );
	sliderTrack    = (SliderTrack)c;
	thumbNum   = sliderTrack.getThumbNum();
	changeHandler = new ChangeHandler();
	for (int i=0; i<thumbNum; i++) {
	    sliderTrack.getModelAt(i).addChangeListener( changeHandler );
	}
	trackList = new SliderTrackUI.TrackListener(sliderTrack);
	sliderTrack.addMouseListener(trackList);

    }
    
    public void uninstallUI(JComponent c) {
	sliderTrack.removeMouseListener(trackList);
	trackList = null;
	changeHandler = null;
	super.uninstallUI( c );
    }
 

    public void paint( Graphics g, JComponent c )   {
	paintTrack( g );
    }
    
    public class ChangeHandler implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    sliderTrack.repaint();
	}
    }
  
  
  
    public class TrackListener extends MouseInputAdapter {
	protected transient int offset;
	protected transient int currentMouseX, currentMouseY;
	protected Rectangle adjustingThumbRect = null;
	protected int adjustingThumbIndex;
	protected SliderTrack   slider;
    
	TrackListener(SliderTrack slider) {
	    this.slider = slider;
	}
      
	public void mousePressed(MouseEvent e) {
	    System.out.print("MousePressed\n");
	    /*
	    if ( !slider.isEnabled() ) {
		return; 
	    }
	    currentMouseX = e.getX();
	    currentMouseY = e.getY();
	    
	    for (int i=0; i<thumbNum; i++) {
		Rectangle rect = thumbRects[i];
		
		if ( rect.contains(currentMouseX, currentMouseY) ) {
		    
		    switch ( slider.getOrientation() ) {
		  case JSlider.VERTICAL:
		      offset = currentMouseY - rect.y;
		      break;
		  case JSlider.HORIZONTAL:
		      offset = currentMouseX - rect.x;
		      break;
		  }
		  isDragging = true;
		  slider.setValueIsAdjusting(true);
		  adjustingThumbRect = rect;
		  adjustingThumbIndex = i;
		  
		  return;
	      }
	  }
	    */
      }
      
      public void mouseReleased(MouseEvent e) {
	    System.out.print("MouseReleased\n");
	  /*
	  if ( !slider.isEnabled() ) {
	      return;
	  }
	  offset = 0;
	  isDragging = false;
	  sliderTrack.setValueIsAdjusting(false);
	  sliderTrack.repaint();
	  */
      }
      
  }
  
}


