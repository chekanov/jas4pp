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

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * GUIHelper used for repetitive setting on UI components.
 * 
 */
public class GuiHelper {
	public static JPanel buildLine( String label , int labelSize , JComponent... ui ) {
		JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.X_AXIS));
		JLabel labelUI = new JLabel(label, JLabel.RIGHT);
		Dimension labelDimension = labelUI.getPreferredSize();
		if (labelSize != -1)
			labelDimension.width = labelSize;
		labelUI.setMinimumSize(labelDimension);
		labelUI.setMaximumSize(labelDimension);
		labelUI.setPreferredSize(labelDimension);
		//
		result.add(labelUI);
		for (JComponent c : ui)
			result.add(c);
		return result;
	}

	public static void fixHeight( Component... components ) {
		for (Component c : components) {
			Dimension d = c.getPreferredSize();
			int height = d.height;
			d = c.getMaximumSize();
			d.height = height;
			c.setMaximumSize(d);
			d = c.getMinimumSize();
			d.height = height;
			c.setMinimumSize(d);
		}
	}

	public static void fixWidth( Component component , int width ) {
		Dimension d = component.getPreferredSize();
		d.width = width;
		component.setPreferredSize(d);
		d = component.getMaximumSize();
		d.width = width;
		component.setMaximumSize(d);
		d = component.getMinimumSize();
		d.width = width;
		component.setMinimumSize(d);
	}
}
