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

//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Graphics;
//import java.awt.event.MouseEvent;
//import java.awt.event.MouseListener;
//import java.awt.event.MouseMotionListener;
//import java.util.ArrayList;
//
//import javax.swing.JComponent;
//import javax.vecmath.AxisAngle4f;
//import javax.vecmath.Matrix3f;
//import javax.vecmath.Vector3f;
//
//import com.artenum.ui.tk.api.Tuple3Listener;
//import com.artenum.ui.tk.api.Tuple3Value;

public class DragRotation {//extends JComponent implements Tuple3Value,
		//Tuple3Listener, MouseListener, MouseMotionListener {
//	private static final long serialVersionUID = 1L;
//	private Vector3f normal;
//	private Vector3f axeX;
//	private Vector3f axeY;
//	private Vector3f axeZ;
//	private AxisAngle4f rotationQuaternion;
//	private Matrix3f rotationMatrix;
//	private int oldX, oldY;
//	private float dx, dy;
//	private char[] axeLabel = new char[] { 'x', 'y', 'z' };
//	private ArrayList<Tuple3Listener> listeners;
//	private float[] boundingBox = { -1, -1, -1, 1, 1, 1 };
//	private Dimension uiSize;
//
//	public DragRotation(int size) {
//		uiSize = new Dimension(size, size);
//		normal = new Vector3f(0, 0, 1);
//		axeX = new Vector3f(1, 0, 0);
//		axeY = new Vector3f(0, 1, 0);
//		axeZ = new Vector3f(0, 0, -1);
//		rotationQuaternion = new AxisAngle4f();
//		rotationMatrix = new Matrix3f();
//		addMouseListener(this);
//		addMouseMotionListener(this);
//		listeners = new ArrayList<Tuple3Listener>();
//	}
//
//	@Override
//	public void paint(Graphics g) {
//		Dimension size = getSize();
//		int centerX = size.width / 2;
//		int centerY = size.height / 2;
//		int scale = Math.min(centerX, centerY);
//
//		g.setColor(Color.BLACK);
//		g.fillRect(0, 0, size.width, size.height);
//
//		g.setColor(Color.RED);
//		g.drawLine(centerX, centerY, (int) (centerX + (scale * axeX.x)),
//				(int) (centerY + (scale * axeX.y)));
//		g.drawChars(axeLabel, 0, 1, (int) (centerX + (scale * axeX.x)),
//				(int) (centerY + (scale * axeX.y)));
//		g.setColor(Color.GREEN);
//		g.drawLine(centerX, centerY, (int) (centerX + (scale * axeY.x)),
//				(int) (centerY + (scale * axeY.y)));
//		g.drawChars(axeLabel, 1, 1, (int) (centerX + (scale * axeY.x)),
//				(int) (centerY + (scale * axeY.y)));
//		g.setColor(Color.BLUE);
//		g.drawLine(centerX, centerY, (int) (centerX + (scale * axeZ.x)),
//				(int) (centerY + (scale * axeZ.y)));
//		g.drawChars(axeLabel, 2, 1, (int) (centerX + (scale * axeZ.x)),
//				(int) (centerY + (scale * axeZ.y)));
//
//	}
//
//	public void mouseClicked(MouseEvent e) {
//
//	}
//
//	public void mouseEntered(MouseEvent e) {
//
//	}
//
//	public void mouseExited(MouseEvent e) {
//
//	}
//
//	public void mousePressed(MouseEvent e) {
//		oldX = e.getX();
//		oldY = e.getY();
//	}
//
//	public void mouseReleased(MouseEvent e) {
//		normal.normalize();
//		axeX.normalize();
//		axeY.normalize();
//		axeZ.normalize();
//	}
//
//	public void mouseDragged(MouseEvent e) {
//		dx = e.getX() - oldX;
//		dy = e.getY() - oldY;
//		oldX = e.getX();
//		oldY = e.getY();
//		// compute the rotation
//		rotationQuaternion.set(-dy, dx, 0f, 0.09f);
//		rotationMatrix.set(rotationQuaternion);
//		// 
//		rotationMatrix.transform(axeX);
//		rotationMatrix.transform(axeY);
//		rotationMatrix.transform(axeZ);
//		rotationMatrix.invert();
//		rotationMatrix.transform(normal);
//		//
//		repaint();
//		// 
//		notifyListener();
//	}
//
//	public float[] getBoundingBox() {
//		return boundingBox;
//	}
//
//	public JComponent getUI() {
//		return this;
//	}
//
//	public float[] getValue() {
//		return new float[] { normal.x, normal.y, normal.z };
//	}
//
//	public void notifyListener() {
//		normal.normalize();
//		for (Tuple3Listener l : listeners) {
//			l.valueChanged(normal.x, normal.y, normal.z);
//		}
//	}
//
//	public void setBoundingBox(float minCoord, float minCoord2,
//			float minCoord3, float maxCoord, float maxCoord2, float maxCoord3) {
//		// Can't change it
//	}
//
//	public void setValue(float coord, float coord2, float coord3, boolean notify) {
//		// Compute the needed rotation
//		Vector3f newNormal = new Vector3f(coord, coord2, coord3);
//		newNormal.normalize();
//		normal.normalize();
//		double angle = normal.angle(newNormal);
//		if (normal.epsilonEquals(newNormal, 0.01f))
//			return;
//		Vector3f axeOfRotation = new Vector3f();
//		axeOfRotation.cross(normal, newNormal);
//		Matrix3f rotationMatrix = new Matrix3f();
//		AxisAngle4f rotationQuat = new AxisAngle4f(axeOfRotation, (float) angle);
//		rotationMatrix.set(rotationQuat);
//		// check ok
//		//System.out.println("Angle: " + angle);
//		//System.out.println("Ask to be: " + newNormal);
//		//System.out.println("Finally be: " + normal);
//		// Apply rotation
//		rotationMatrix.transform(normal);
//		rotationMatrix.invert();
//		rotationMatrix.transform(axeX);
//		rotationMatrix.transform(axeY);
//		rotationMatrix.transform(axeZ);
//		//
//		if (notify)
//			notifyListener();
//		//
//		repaint();
//	}
//
//	public void valueChanged(float coord, float coord2, float coord3) {
//		setValue(coord, coord2, coord3, false);
//	}
//
//	public void mouseMoved(MouseEvent e) {
//
//	}
//
//	public void addTuple3Listener(Tuple3Listener l) {
//		listeners.add(l);
//	}
//
//	public void removeTuple3Listener(Tuple3Listener l) {
//		listeners.remove(l);
//	}
//
//	@Override
//	public Dimension getPreferredSize() {
//		return uiSize;
//	}
//
//	@Override
//	public Dimension getMinimumSize() {
//		return uiSize;
//	}
//
//	@Override
//	public Dimension getMaximumSize() {
//		return uiSize;
//	}
//
//	@Override
//	public Dimension getSize() {
//		return uiSize;
//	}

}
