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
package com.artenum.tk.ui.model;

import java.util.ArrayList;
import java.util.Properties;

import com.artenum.tk.ui.api.PropertiesListener;
import com.artenum.tk.ui.api.Tuple3Listener;

public class Tuple3ListenerPropertyModel implements Tuple3Listener {
	private Properties props;
	private String keyA, keyB, keyC;
	private ArrayList<PropertiesListener> listeners;

	public Tuple3ListenerPropertyModel(String keyA, String keyB, String keyC) {
		this(keyA, keyB, keyC, new Properties());
	}

	public Tuple3ListenerPropertyModel(String keyA, String keyB, String keyC,
			Properties propertyToUse) {
		props = propertyToUse;
		this.keyA = keyA;
		this.keyB = keyB;
		this.keyC = keyC;
		listeners = new ArrayList<PropertiesListener>();
	}

	public Properties getProperties() {
		return props;
	}

	public void valueChanged(float coordA, float coordB, float coordC) {
		props.setProperty(keyA, Float.toString(coordA));
		props.setProperty(keyB, Float.toString(coordB));
		props.setProperty(keyC, Float.toString(coordC));
		notifyListeners();
	}

	public void addPropertiesListener(PropertiesListener l) {
		listeners.add(l);
	}

	public void removePropertiesListener(PropertiesListener l) {
		listeners.remove(l);
	}

	public void notifyListeners() {
		for (PropertiesListener l : listeners)
			l.valueChanged(props);
	}
}
