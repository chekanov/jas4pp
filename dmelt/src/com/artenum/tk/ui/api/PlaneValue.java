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
package com.artenum.tk.ui.api;

import javax.swing.JComponent;

public interface PlaneValue {
	public void addPointTuple3Listener(Tuple3Listener l);
	public void removePointTuple3Listener(Tuple3Listener l);
	public void addNormalTuple3Listener(Tuple3Listener l);
	public void removeNormalTuple3Listener(Tuple3Listener l);
	public JComponent getUI();
	public void setPoint(float xCoord, float yCoord, float zCoord);
	public float[] getPoint();
	public void setNormal(float xCoord, float yCoord, float zCoord);
	public float[] getNormal();
	public void setPointBound(float xMinCoord, float yMinCoord, float zMinCoord,float xMaxCoord, float yMaxCoord, float zMaxCoord);

}
