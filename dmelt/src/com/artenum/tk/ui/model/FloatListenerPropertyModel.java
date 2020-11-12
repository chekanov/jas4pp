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

import com.artenum.tk.ui.api.FloatListener;
import com.artenum.tk.ui.api.PropertiesListener;

public class FloatListenerPropertyModel implements FloatListener {
    private Properties props;
    private String key;
    private ArrayList<PropertiesListener> listeners;

    public FloatListenerPropertyModel(String key) {
	this(key, new Properties());
    }

    public FloatListenerPropertyModel(String key, Properties propertyToUse) {
	props = propertyToUse;
	this.key = key;
	listeners = new ArrayList<PropertiesListener>();
    }

    public Properties getProperties() {
	return props;
    }

    public void valueChanged(float newValue) {
	props.setProperty(key, Float.toString(newValue));
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
