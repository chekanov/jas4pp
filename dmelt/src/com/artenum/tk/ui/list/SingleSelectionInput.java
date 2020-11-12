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
package com.artenum.tk.ui.list;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.artenum.tk.ui.api.ListItem;
import com.artenum.tk.ui.api.SingleSelectionListener;
import com.artenum.tk.ui.api.SingleSelectionValue;

public class SingleSelectionInput extends DefaultComboBoxModel implements SingleSelectionValue, ActionListener {
	private static final long serialVersionUID = 1L;
	private ArrayList<SingleSelectionListener> listeners;
	private ArrayList<ListItem> data;
	private JComboBox selector;
	private String defaultValue;
	private JPanel ui;

	public SingleSelectionInput( String name ) {
		listeners = new ArrayList<SingleSelectionListener>();
		data = new ArrayList<ListItem>();
		selector = new JComboBox(this);
		selector.addActionListener(this);
		ui = new JPanel();
		ui.setLayout(new BoxLayout(ui, BoxLayout.X_AXIS));
		ui.add(new JLabel(name));
		ui.add(Box.createHorizontalStrut(5));
		ui.add(selector);
	}

	public void addSingleSelectionListener( SingleSelectionListener l ) {
		listeners.add(l);
	}

	public String getSelectedValue() {
		return ((ListItem) selector.getSelectedItem()).getValue();
	}

	public JComponent getUI() {
		return ui;
	}

	public void removeSingleSelectionListener( SingleSelectionListener l ) {
		listeners.remove(l);
	}

	public void setDefaultValue( String defaultValue ) {
		this.defaultValue = defaultValue;
		//
		if (defaultValue != null) {
			int index = 0;
			for (ListItem item : data) {
				if (item.getValue().equals(defaultValue))
					break;
				index++;
			}
			if (data.size() > 0 && data.size() > index)
				selector.setSelectedIndex(index);
			else
				selector.setSelectedIndex(0);
		}

	}

	public void setValues( List<ListItem> items ) {
		data.clear();
		data.addAll(items);
		update();
	}

	public void update() {
		fireContentsChanged(this, 0, getSize());
		if (getSize() > 0 && defaultValue != null)
			setDefaultValue(defaultValue);
		else
			setSelectedItem("");
	}

	public Object getElementAt( int indice ) {
		return data.get(indice);
	}

	public int getSize() {
		return data.size();
	}

	public void actionPerformed( ActionEvent e ) {
		for (SingleSelectionListener l : listeners) {
			l.singleSelectionChanged(getSelectedValue());
		}
	}

}
