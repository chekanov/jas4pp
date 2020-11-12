/**
 * Project        : ArtTk
 * Copyright      : (c) Artenum SARL, 24 rue Louis Blanc
 *                  75010, Paris, France 2009-2010
 *                  http://www.artenum.com
 *                  All copyright and trademarks reserved.
 * Email          : contact@artenum.com
 * Licence        : cf. LICENSE.txt
 * Developed By   : Artenum SARL
 * Authors        : Sebastien Jourdain      (jourdain@artenum.com)
 *                  Benoit thiebault        (thiebault@artenum.com)
 *                  Jeremie Turbet (JeT)    (turbet@artenum.com)
 *                  Julien Forest           (j.forest@artenum.com)
 * Created        : 11 Nov. 2005
 * Modified       : 23 Aug. 2010
 */
package com.artenum.tk.ui.basic;

import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.artenum.tk.ui.api.FloatListener;
import com.artenum.tk.ui.api.FloatValue;

public class FloatSliderInput implements FloatValue, ChangeListener, FloatListener {
	private static final long serialVersionUID = 1L;
	private float value, min, max;
	private int nbStep;
	private JSlider valueUI;
	private ArrayList<FloatListener> listeners;

	public FloatSliderInput() {
		this(0);
	}

	public FloatSliderInput( float value ) {
		this(0, -1, 1);
	}

	public FloatSliderInput( float value, float min, float max ) {
		this(value, min, max, 1000);
	}

	public FloatSliderInput( float value, float min, float max, int nbStep ) {
		listeners = new ArrayList<FloatListener>();
		this.value = value;
		this.nbStep = nbStep;
		this.min = min;
		this.max = max;
		// UI
		valueUI = new JSlider(0, nbStep, convertToSliderValue(value));
		valueUI.addChangeListener(this);
	}

	public float getValue() {
		return value;
	}

	private int convertToSliderValue( float value ) {
		return (int) ((value - min) / (max - min) * nbStep);
	}

	public void setValue( float newValue , boolean notify ) {
		value = newValue;
		value = Math.min(value, max);
		value = Math.max(value, min);
		valueUI.setValue(convertToSliderValue(value));
		if (notify)
			notifyListener();
	}

	public JComponent getUI() {
		return valueUI;
	}

	public void setRange( float min , float max ) {
		this.min = min;
		this.max = max;
	}

	public float[] getRange() {
		return new float[] { min, max };
	}

	public void addFloatListener( FloatListener l ) {
		listeners.add(l);
	}

	public void removeFloatListener( FloatListener l ) {
		listeners.remove(l);
	}

	public void notifyListener() {
		for (FloatListener l : listeners) {
			l.valueChanged(value);
		}
	}

	public void stateChanged( ChangeEvent e ) {
		setValue(((float) valueUI.getValue()) / ((float) nbStep) * (max - min) + min, true);
	}

	public void valueChanged( float newValue ) {
		setValue(newValue, false);
	}
}
