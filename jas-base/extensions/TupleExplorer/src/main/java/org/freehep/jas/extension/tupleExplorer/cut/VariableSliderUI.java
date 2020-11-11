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
public class VariableSliderUI extends BasicSliderUI {
  
    VariableSlider  mSlider;
    Rectangle[]   thumbRects;
    int           thumbNum;
    Icon thumbRenderer;
    
    private transient boolean isDragging;


    ChangeHandler changeHandler;
    TrackListener trackList;
    
    
    public VariableSliderUI( VariableSlider vs )   {
	super( vs );
    }
    
    
    public void installUI(JComponent c)   {
	super.installUI( c );
	mSlider    = (VariableSlider)c;
	slider = (JSlider) mSlider;

	thumbNum   = mSlider.getThumbNum();
	thumbRects = new Rectangle[thumbNum];
	changeHandler = new ChangeHandler();
	for (int i=0; i<thumbNum; i++) {
	    thumbRects[i] = new Rectangle();
	    mSlider.getModelAt(i).addChangeListener( changeHandler );
	}
	isDragging = false;
	trackList = new VariableSliderUI.TrackListener(mSlider);
	insetCache = slider.getInsets();

	slider.addMouseListener(trackList);
	slider.addMouseMotionListener(trackList);

    }
    
    public void uninstallUI(JComponent c) {

	slider.removeMouseListener(trackList);
	slider.removeMouseMotionListener(trackList);
	trackList = null;
	thumbRects = null;
	changeHandler = null;
	insetCache = null;
	super.uninstallUI( c );
    }
 

    public void paint( Graphics g, JComponent c )   {
        recalculateIfInsetsChanged();
	recalculateIfOrientationChanged();
	Rectangle clip = g.getClipBounds();

	BasicSliderUI basicUi = ((BasicSliderUI)mSlider.getSliderAt(0).getUI());

	//	if ( slider.getPaintTrack() && clip.intersects( trackRect ) ) {
	if ( slider.getPaintTrack() ) {
	    basicUi.paintTrack( g );
	}
        if ( slider.getPaintTicks() && clip.intersects( tickRect ) ) {
            basicUi.paintTicks( g );
        }
        if ( slider.getPaintLabels() && clip.intersects( labelRect ) ) {
            basicUi.paintLabels( g );
        }
	if ( slider.hasFocus() && clip.intersects( focusRect ) ) {
	    basicUi.paintFocus( g );      
	}

	for (int i=thumbNum-1; 0<=i; i--) {
	    if ( clip.intersects( thumbRects[i] ) ) {
		thumbRect = thumbRects[i];
		((BasicSliderUI)mSlider.getSliderAt(i).getUI()).paintThumb( g );
	    }
	}
    }
    

     
    protected void calculateThumbSize() {
	Dimension size = getThumbSize();
	for (int i=0; i<thumbNum; i++) {
	    thumbRects[i].setSize( size.width, size.height );
	}
    }
  
    
  protected void calculateThumbLocation() {
      //      System.out.print("Calculating now the thumb location "+this+"\n");
    for (int i=0; i<thumbNum; i++) {
      if ( mSlider.getSnapToTicks() ) {
        int tickSpacing = mSlider.getMinorTickSpacing();	    
        if (tickSpacing == 0) {
          tickSpacing = mSlider.getMajorTickSpacing();
        }
        if (tickSpacing != 0) {      
          int sliderValue  = mSlider.getValueAt(i);           
          int snappedValue = sliderValue; 
          //int min = mSlider.getMinimumAt(i);                           
          int min = mSlider.getMinimum();                          
          if ( (sliderValue - min) % tickSpacing != 0 ) {
            float temp = (float)(sliderValue - min) / (float)tickSpacing;
            int whichTick = Math.round( temp );
            snappedValue = min + (whichTick * tickSpacing);            
            mSlider.setValueAt( snappedValue , i);           
          }
        }
      }	
      if ( mSlider.getOrientation() == JSlider.HORIZONTAL ) {
        int valuePosition = xPositionForValue( mSlider.getValueAt(i) );
	thumbRects[i].x = valuePosition - (thumbRects[i].width / 2);
	//        thumbRects[i].x = valuePosition - ( 1 - mSlider.getThumbTypeAt( i ) )*(thumbRects[i].width / 2);
        thumbRects[i].y = trackRect.y;
        
      } else {
        int valuePosition = yPositionForValue(mSlider.getValueAt(i));     // need
        thumbRects[i].x = trackRect.x;
        thumbRects[i].y = valuePosition - (thumbRects[i].height / 2);
      }
    }
  }

    
  public int getThumbNum() {
    return thumbNum;
  }
  
  public Rectangle[] getThumbRects() {
    return thumbRects;
  }
  
  
 
    private static Rectangle unionRect = new Rectangle();
    
    public void setThumbLocationAt(int x, int y, int index)  { 

	Rectangle rect = thumbRects[index];  
	unionRect.setBounds( rect );
	
	rect.setLocation( x, y );
	SwingUtilities.computeUnion( rect.x, rect.y, rect.width, rect.height, unionRect ); 
	mSlider.repaint( unionRect.x, unionRect.y, unionRect.width, unionRect.height );
    }
  
  
  
  public class ChangeHandler implements ChangeListener {
    public void stateChanged(ChangeEvent e) {
        calculateThumbLocation();
	mSlider.repaint();
    }
  }
  
  
  
  public class TrackListener extends MouseInputAdapter {
    protected transient int offset;
    protected transient int currentMouseX, currentMouseY;
    protected Rectangle adjustingThumbRect = null;
    protected int adjustingThumbIndex;
    protected VariableSlider   slider;
      //    protected Rectangle trackRect;
    
    TrackListener(VariableSlider slider) {
      this.slider = slider;
    }
      
      public void mousePressed(MouseEvent e) {
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
      }
      
      public void mouseDragged( MouseEvent e ) {                    
	  if ( !slider.isEnabled() 
	       || !isDragging 
	       || !slider.getValueIsAdjusting()
	       || adjustingThumbRect == null
	       || !slider.getModelAt(adjustingThumbIndex).isEnabled() ) {
	      return;
	  }
	  	  
	  int thumbMiddle = 0;
	  currentMouseX = e.getX();
	  currentMouseY = e.getY();
	  
	  Rectangle rect = thumbRects[adjustingThumbIndex];
	  switch ( slider.getOrientation() ) {
	  case JSlider.VERTICAL:      
	      int halfThumbHeight = rect.height / 2;
	      int thumbTop    = e.getY() - offset;
	      int trackTop    = trackRect.y;
	      int trackBottom = trackRect.y + (trackRect.height - 1);
	  
	      thumbTop = Math.max( thumbTop, trackTop    - halfThumbHeight );
	      thumbTop = Math.min( thumbTop, trackBottom - halfThumbHeight );
	      
	      setThumbLocationAt(rect.x, thumbTop, adjustingThumbIndex);
	      
	      thumbMiddle = thumbTop + halfThumbHeight;
	      mSlider.setValueAt( valueForYPosition( thumbMiddle ) , adjustingThumbIndex);
	      break;
	      
	  case JSlider.HORIZONTAL:
	      int halfThumbWidth = rect.width / 2;
	      
	      int thumbLeft  = e.getX() - offset;
	      int trackLeft  = trackRect.x;
	      int trackRight = trackRect.x + (trackRect.width - 1);
	      
	      int thumbType = slider.getThumbTypeAt( adjustingThumbIndex );
	      
	      //	      thumbLeft = Math.max( thumbLeft, trackLeft  - ( 1 - thumbType )*halfThumbWidth );
	      //	      thumbLeft = Math.min( thumbLeft, trackRight - ( 1 - thumbType )*halfThumbWidth );

	      
	      thumbLeft = Math.max( thumbLeft, trackLeft  - halfThumbWidth );
	      thumbLeft = Math.min( thumbLeft, trackRight - halfThumbWidth );

	      
	      setThumbLocationAt( thumbLeft, rect.y, adjustingThumbIndex);
	      
	      //	      thumbMiddle = thumbLeft + ( 1 - thumbType )*halfThumbWidth;
	      thumbMiddle = thumbLeft + halfThumbWidth;
	      mSlider.setValueAt( valueForXPosition( thumbMiddle ), adjustingThumbIndex );          
	      break;
	  }
      }
      
      public void mouseReleased(MouseEvent e) {
	  if ( !slider.isEnabled() ) {
	      return;
	  }
	  offset = 0;
	  isDragging = false;
	  mSlider.setValueIsAdjusting(false);
	  mSlider.repaint();
      }
      
      public boolean shouldScroll(int direction) {
	  return false;
      }
    
  }
  
}


