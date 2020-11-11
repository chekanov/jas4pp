package org.freehep.jas.extension.tupleExplorer.cut;

import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JSlider;

public class SliderTrack extends JSlider {

    protected int thumbNum;
    protected CutVariableSliderModel[] sliderModels;
    protected Color acceptColor;
    protected Color rejectColor;

    boolean uiUpdated = false;
    private static final String uiClassID = "SliderTrackUI";

    public SliderTrack( CutVariableSliderModel slider1 ) {
	super( slider1 );
	thumbNum = 1;

	sliderModels   = new CutVariableSliderModel[1];    
	sliderModels[0] = slider1;

	setDefaultColors();
	setDefaultSize();	
	updateUI();
	uiUpdated = true;
    }

    public SliderTrack( CutVariableSliderModel slider1, CutVariableSliderModel slider2 ) {
	super( slider1 );
	thumbNum = 2;

	sliderModels   = new CutVariableSliderModel[2];    
	sliderModels[0] = slider1;
	sliderModels[1] = slider2;

	setDefaultColors();	
	setDefaultSize();	
	updateUI();
	uiUpdated = true;
     }


    public String getUIClassID() {
	return uiClassID;
    }


    protected void setDefaultColors() {
	acceptColor = Color.green;
	rejectColor = Color.gray;
    }

    protected void setDefaultSize() {
	Dimension size = getPreferredSize();
	size.height = size.height/2;
	setPreferredSize( size );
    }

    public void updateUI() {
	if ( !uiUpdated ) {
	    SliderTrackUI ui = new SliderTrackUI( this );
	    setUI( ui );
	}
    }

    public int getThumbNum() {
	return thumbNum;
    }
    
    public CutVariableSliderModel getModelAt(int index) {
	return sliderModels[ index ]; 
    }

    public int getValueAt(int index) {
	return getModelAt(index).getValue(); 
    }
    
    public void setValueAt(int n, int index) {
	getModelAt(index).setValue(n); 
    }
    
    public Color getAcceptColor() {
	return acceptColor;
    }
    
    public void setAcceptColor(Color color) {
	acceptColor = color;
    }
    
    public Color getRejectColor() {
	return rejectColor;
    }
    
    public void setRejectColor(Color color) {
	rejectColor = color;
    }

}


