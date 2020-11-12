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

public interface FloatValue {
	public void addFloatListener(FloatListener l);
	public void removeFloatListener(FloatListener l);
	public void notifyListener();
	public float getValue();
	public JComponent getUI();
	public void setValue(float value, boolean notify);
	public float[] getRange();
	public void setRange(float minValue, float maxValue);
}
