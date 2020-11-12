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
package com.artenum.tk.ui.util;

import java.util.LinkedList;

import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * SpinnerModel that impose Double value
 * 
 */
public class DoubleSpinnerModel implements SpinnerModel {
	private double value;
	private LinkedList<ChangeListener> listeners;
	private double step;

	public DoubleSpinnerModel() {
		value = 0;
		step = 1;
		listeners = new LinkedList<ChangeListener>();
	}

	public void addChangeListener( ChangeListener l ) {
		listeners.add(l);
	}

	public Object getNextValue() {
		return value + step;
	}

	public Object getPreviousValue() {
		return value - step;
	}

	public Object getValue() {
		return value;
	}

	public double getDoubleValue() {
		return value;
	}

	public void removeChangeListener( ChangeListener l ) {
		listeners.remove(l);
	}

	public void setValue( Object value ) {
		this.value = ((Number) value).doubleValue();
		notifyListeners();
	}

	public double getStep() {
		return step;
	}

	public void setStep( double step ) {
		this.step = step;
	}

	private void notifyListeners() {
		ChangeEvent e = new ChangeEvent(this);
		for (ChangeListener l : listeners) {
			l.stateChanged(e);
		}
	}
}
