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

public interface Tuple3Value {
	public void addTuple3Listener(Tuple3Listener l);
	public void removeTuple3Listener(Tuple3Listener l);
	public void notifyListener();
	public float[] getValue();
	public JComponent getUI();
	public void setValue(float xCoord, float yCoord, float zCoord, boolean notify);
	public float[] getBoundingBox();
	public void setBoundingBox(float xMinCoord, float yMinCoord, float zMinCoord,float xMaxCoord, float yMaxCoord, float zMaxCoord);
}
