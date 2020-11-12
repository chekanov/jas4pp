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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JTextField;

import com.artenum.tk.ui.api.FloatListener;
import com.artenum.tk.ui.api.FloatValue;

public class FloatTextInput implements ActionListener, FocusListener,
		FloatValue, FloatListener {
	private static final long serialVersionUID = 1L;
	private float value, min, max;
	private JTextField valueUI;
	private ArrayList<FloatListener> listeners;

	public FloatTextInput() {
		this(0);
	}

	public FloatTextInput(float value) {
		this(0, -1, 1);
	}

	public FloatTextInput(float value, float min, float max) {
		// UI
		valueUI = new JTextField(Float.toString(value));
		valueUI.addActionListener(this);
		valueUI.addFocusListener(this);
		Dimension size = valueUI.getPreferredSize();
		size.height = valueUI.getMinimumSize().height;
		valueUI.setPreferredSize(size);
		size = valueUI.getMaximumSize();
		size.height = valueUI.getMinimumSize().height;
		valueUI.setMaximumSize(size);
		//
		listeners = new ArrayList<FloatListener>();
		this.value = value;
		this.min = min;
		this.max = max;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float newValue, boolean notify) {
		value = newValue;
		value = Math.min(value, max);
		value = Math.max(value, min);

		valueUI.setText(Float.toString(value));
		if (notify)
			notifyListener();
	}

	public JComponent getUI() {
		return valueUI;
	}

	public void setRange(float min, float max) {
		this.min = min;
		this.max = max;
	}

	public float[] getRange() {
		return new float[] { min, max };
	}

	public void actionPerformed(ActionEvent e) {
		try {
			setValue(Float.parseFloat(valueUI.getText()),true);
		} catch (Exception pb) {
		}
		valueUI.setText(Float.toString(value));
	}

	public void focusGained(FocusEvent e) {
	}

	public void focusLost(FocusEvent e) {
		try {
			setValue(Float.parseFloat(valueUI.getText()),true);
		} catch (Exception pb) {
		}
		valueUI.setText(Float.toString(value));
	}

	public void addFloatListener(FloatListener l) {
		listeners.add(l);
	}

	public void removeFloatListener(FloatListener l) {
		listeners.remove(l);
	}

	public void notifyListener() {
		for (FloatListener l : listeners) {
			l.valueChanged(value);
		}
	}

	public void valueChanged(float newValue) {
		setValue(newValue,false);
	}
}
