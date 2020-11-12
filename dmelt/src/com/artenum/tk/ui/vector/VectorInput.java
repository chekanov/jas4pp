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
package com.artenum.tk.ui.vector;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.artenum.tk.ui.api.ConstantArrayIndex;
import com.artenum.tk.ui.api.FloatListener;
import com.artenum.tk.ui.api.Tuple3Listener;
import com.artenum.tk.ui.api.Tuple3Value;
import com.artenum.tk.ui.basic.FloatSliderInput;
import com.artenum.tk.ui.basic.FloatTextInput;

public class VectorInput implements Tuple3Value, Tuple3Listener, FloatListener,
		ConstantArrayIndex {
	private static final long serialVersionUID = 1L;
	private JPanel ui;
	private FloatTextInput[] txtValue;
	private FloatSliderInput[] sliderValue;
	private ArrayList<Tuple3Listener> listeners;
	//private DragRotation rotation;

	public VectorInput() {
		listeners = new ArrayList<Tuple3Listener>();
		//rotation = new DragRotation(80);
		txtValue = new FloatTextInput[3];
		sliderValue = new FloatSliderInput[3];
		for (int i = 0; i < sliderValue.length; i++) {
			sliderValue[i] = new FloatSliderInput();
		}
		for (int i = 0; i < txtValue.length; i++) {
			txtValue[i] = new FloatTextInput();
		}
		for (int i = 0; i < txtValue.length; i++) {
			txtValue[i].addFloatListener(sliderValue[i]);
			sliderValue[i].addFloatListener(txtValue[i]);
			txtValue[i].addFloatListener(this);
			sliderValue[i].addFloatListener(this);
		}
		//rotation.addTuple3Listener(this);
		//addTuple3Listener(rotation);

		// init UI
		ui = new JPanel();
		ui.setLayout(new BoxLayout(ui, BoxLayout.Y_AXIS));
		JPanel line = new JPanel();
		line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
		line.add(new JLabel("x:"));
		line.add(txtValue[X].getUI());
		line.add(new JLabel("y:"));
		line.add(txtValue[Y].getUI());
		line.add(new JLabel("z:"));
		line.add(txtValue[Z].getUI());
		line.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
		ui.add(line);
		JPanel subPart = new JPanel(new BorderLayout());
		JPanel subPartLeft = new JPanel(new BorderLayout());
		//subPartLeft.add(rotation, BorderLayout.CENTER);
		subPartLeft.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		JPanel subPartRight = new JPanel();
		subPartRight.setLayout(new BoxLayout(subPartRight, BoxLayout.Y_AXIS));
		line = new JPanel();
		line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
		line.add(new JLabel("x:"));
		line.add(sliderValue[X].getUI());
		line.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 5));
		subPartRight.add(line);
		line = new JPanel();
		line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
		line.add(new JLabel("y:"));
		line.add(sliderValue[Y].getUI());
		line.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 5));
		subPartRight.add(line);
		line = new JPanel();
		line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
		line.add(new JLabel("z:"));
		line.add(sliderValue[Z].getUI());
		line.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 5));
		subPartRight.add(line);
		subPart.add(subPartRight, BorderLayout.CENTER);
		subPart.add(subPartLeft, BorderLayout.WEST);
		ui.add(subPart);
	}

	public void addTuple3Listener(Tuple3Listener l) {
		listeners.add(l);
	}

	public float[] getBoundingBox() {
		return new float[] { txtValue[X].getRange()[MIN],
				txtValue[Y].getRange()[MIN], txtValue[Z].getRange()[MIN],
				txtValue[0].getRange()[X], txtValue[Y].getRange()[MAX],
				txtValue[0].getRange()[MAX], txtValue[Z].getRange()[MAX] };
	}

	public JComponent getUI() {
		return ui;
	}

	public float[] getValue() {
		return new float[] { txtValue[X].getValue(), txtValue[Y].getValue(),
				txtValue[Z].getValue() };
	}

	public void notifyListener() {
		float[] value = getValue();
		for (Tuple3Listener l : listeners) {
			l.valueChanged(value[X], value[Y], value[Z]);
		}
	}

	public void removeTuple3Listener(Tuple3Listener l) {
		listeners.remove(l);
	}

	public void setBoundingBox(float minCoordX, float minCoordY,
			float minCoordZ, float maxCoordX, float maxCoordY, float maxCoordZ) {
		txtValue[X].setRange(minCoordX, maxCoordX);
		txtValue[Y].setRange(minCoordY, maxCoordY);
		txtValue[Z].setRange(minCoordZ, maxCoordZ);
		sliderValue[X].setRange(minCoordX, maxCoordX);
		sliderValue[Y].setRange(minCoordY, maxCoordY);
		sliderValue[Z].setRange(minCoordZ, maxCoordZ);
	}

	public void setValue(float xCoord, float yCoord, float zCoord,
			boolean notify) {
		txtValue[X].setValue(xCoord, true);
		txtValue[Y].setValue(yCoord, true);
		txtValue[Z].setValue(zCoord, true);
		sliderValue[X].setValue(xCoord, true);
		sliderValue[Y].setValue(yCoord, true);
		sliderValue[Z].setValue(zCoord, true);
		if (notify)
			notifyListener();
	}

	public void valueChanged(float xCoord, float yCoord, float zCoord) {
		setValue(xCoord, yCoord, zCoord, false);
	}

	public void valueChanged(float newValue) {
		notifyListener();
	}
}
