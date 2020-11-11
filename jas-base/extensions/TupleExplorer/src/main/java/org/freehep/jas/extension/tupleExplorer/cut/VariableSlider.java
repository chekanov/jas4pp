package org.freehep.jas.extension.tupleExplorer.cut;


import java.awt.Color;
import javax.swing.Icon;
import javax.swing.JSlider;

public class VariableSlider extends JSlider {

    protected int thumbNum;
    protected CutVariableSliderModel[] sliderModels;
    protected JSlider[] sliders;
    protected Icon[] thumbRenderers;
    protected int[] thumbType;
    protected Color[] fillColors;
    protected Color trackFillColor;

    boolean uiUpdated = false;

    private static final String uiClassID = "VariableSliderUI";

    public VariableSlider( CutVariableSliderModel slider1 ) {
	super();
	thumbNum = 1;
	sliderModels   = new CutVariableSliderModel[1];    
	sliderModels[0] = slider1;

	sliders    = new JSlider[1];    
	sliders[0] = new JSlider( slider1 );

	sliders[0].setSize( getPreferredSize() );

	thumbType = new int[1];
	thumbType[0] = slider1.getType();
	createThumbs(1);    
	updateUI();
	uiUpdated = true;
    }

    public VariableSlider( CutVariableSliderModel slider1, CutVariableSliderModel slider2 ) {
	super();
	thumbNum = 2;
	sliderModels   = new CutVariableSliderModel[2];    
	sliderModels[0] = slider1;
	sliderModels[1] = slider2;

	sliders    = new JSlider[2];    
	sliders[0] = new JSlider( slider1 );
	sliders[1] = new JSlider( slider2 );

	sliders[0].setSize( getPreferredSize() );
	sliders[1].setSize( getPreferredSize() );

	thumbType = new int[2];
	thumbType[0] = slider1.getType();
	thumbType[1] = slider2.getType();
	createThumbs(2);    
	updateUI();
	uiUpdated = true;
     }


    public String getUIClassID() {
        return uiClassID;
    }


    protected void createThumbs(int n) {
	thumbRenderers = new Icon[n];
	fillColors = new Color[n];
	for (int i=0;i<n;i++) {
	    thumbRenderers[i] = null;
	    fillColors[i] = null;
	}
    }

    public void updateUI() {
	if ( !uiUpdated ) {
	    VariableSliderUI ui = new VariableSliderUI( this );
	    setUI( ui );
	}
	if ( sliders != null ) {
	    for ( int i = 0; i < thumbNum; i++ )
		sliders[i].updateUI();
	}
    }
    
    public int getThumbNum() {
	return thumbNum;
    }
    
    public int getValueAt(int index) {
	return getModelAt(index).getValue(); 
    }
    
    public void setValueAt(int n, int index) {
	getModelAt(index).setValue(n); 
    }
    

    public int getMinimum() {
	if ( sliderModels == null ) 
	    return super.getMinimum();
	return getModelAt(0).getMinimum(); 
    }
    
    public int getMaximum() {
	if ( sliderModels == null ) 
	    return super.getMaximum();
	return getModelAt(0).getMaximum(); 
    }

    public CutVariableSliderModel getModelAt(int index) {
	return sliderModels[index];
    }

    public JSlider getSliderAt(int index) {
	if ( sliders != null ) 
	    return sliders[index];
	return null;
    }
    
    public Icon getThumbRendererAt(int index) {
	return thumbRenderers[index];
    }
    
    public void setThumbRendererAt(Icon icon, int index) {
	thumbRenderers[index] = icon;
    }
    
    public Color getFillColorAt(int index) {
	return fillColors[index];
    }
    
    public void setFillColorAt(Color color, int index) {
	fillColors[index] = color;
    }
    
    public Color getTrackFillColor() {
	return trackFillColor;
    }
    
    public void setTrackFillColor(Color color) {
	trackFillColor = color;
    }

    public int getThumbTypeAt( int index )
    {
	return thumbType[ index ];
    }
}


