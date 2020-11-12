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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;

import com.artenum.tk.ui.api.BooleanListener;
import com.artenum.tk.ui.api.BooleanValue;

public class BooleanInput implements BooleanValue, ActionListener {
    private JCheckBox checkBox;
    private ArrayList<BooleanListener> listeners;

    public BooleanInput(String name) {
	checkBox = new JCheckBox(name);
	checkBox.addActionListener(this);
	listeners = new ArrayList<BooleanListener>();
    }

    public void addBooleanListener(BooleanListener l) {
	listeners.add(l);
    }

    public JComponent getUI() {
	return checkBox;
    }

    public boolean getValue() {
	return checkBox.isSelected();
    }

    public void notifyListener() {
	for (BooleanListener l : listeners)
	    l.valueChanged(checkBox.isSelected());
    }

    public void removeBooleanListener(BooleanListener l) {
	listeners.remove(l);
    }

    public void setValue(boolean value, boolean notify) {
	checkBox.setSelected(value);
	if (notify)
	    notifyListener();
    }

    public void actionPerformed(ActionEvent e) {
	notifyListener();
    }
}
