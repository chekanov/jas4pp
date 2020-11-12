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
package com.artenum.tk.ui.plane;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;

import com.artenum.tk.ui.api.PlaneValue;
import com.artenum.tk.ui.api.Tuple3Listener;
import com.artenum.tk.ui.point.PointInput;
import com.artenum.tk.ui.vector.VectorInput;

public class PlaneInput implements PlaneValue {
	private JPanel ui;
	private PointInput point;
	private VectorInput normal;

	public PlaneInput() {
		ui = new JPanel();
		ui.setLayout(new BoxLayout(ui, BoxLayout.Y_AXIS));
		JPanel pointPanel = new JPanel(new BorderLayout());
		point = new PointInput();
		pointPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.BLACK), "Plane origine"));
		pointPanel.add(point.getUI(), BorderLayout.CENTER);
		ui.add(pointPanel);
		JPanel normalPanel = new JPanel(new BorderLayout());
		normal = new VectorInput();
		normalPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(Color.BLACK), "Plane normal"));
		normalPanel.add(normal.getUI(), BorderLayout.CENTER);
		ui.add(normalPanel);
		ui.add(Box.createVerticalGlue());
	}

	public void addNormalTuple3Listener(Tuple3Listener l) {
		normal.addTuple3Listener(l);
	}

	public void addPointTuple3Listener(Tuple3Listener l) {
		point.addTuple3Listener(l);
	}

	public float[] getNormal() {
		return normal.getValue();
	}

	public float[] getPoint() {
		return point.getValue();
	}

	public JComponent getUI() {
		return ui;
	}

	public void removeNormalTuple3Listener(Tuple3Listener l) {
		normal.removeTuple3Listener(l);
	}

	public void removePointTuple3Listener(Tuple3Listener l) {
		point.removeTuple3Listener(l);
	}

	public void setNormal(float xCoord, float yCoord, float zCoord) {
		normal.setValue(xCoord, yCoord, zCoord, true);
	}

	public void setPoint(float xCoord, float yCoord, float zCoord) {
		point.setValue(xCoord, yCoord, zCoord, true);
	}

	public void setPointBound(float minCoordX, float minCoordY,
			float minCoordZ, float maxCoordX, float maxCoordY, float maxCoordZ) {
		point.setBoundingBox(minCoordX, minCoordY, minCoordZ, maxCoordX,
				maxCoordY, maxCoordZ);
	}

}
