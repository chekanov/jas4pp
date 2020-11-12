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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 * Generic ComboBoxModel based on HashMap<K, ArrayList<V>> data structure.
 */
public class MapComboBoxModel<K, V> extends AbstractListModel implements ComboBoxModel {
	private static final long serialVersionUID = 1L;
	private Map<K, LinkedList<V>> data;
	private V selectedValue;
	private K selectedKey;

	public MapComboBoxModel( Map<K, LinkedList<V>> data ) {
		this.data = data;
		if (data.size() > 0) {
			selectedKey = data.keySet().iterator().next();
			if (data.get(selectedKey).size() > 0)
				selectedValue = data.get(selectedKey).get(0);
		}
	}

	public Object getSelectedItem() {
		return selectedValue;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedItem( Object anItem ) {
		selectedValue = (V) anItem;
	}

	public Object getElementAt( int index ) {
		return data.get(selectedKey).get(index);
	}

	public int getSize() {
		if (selectedKey == null)
			return 0;
		return data.get(selectedKey).size();
	}

	public void addItem( V item ) {
		if (item == null || selectedKey == null)
			return;
		data.get(selectedKey).add(item);
		update();
	}

	public void removeItem( V item ) {
		data.get(selectedKey).remove(item);
		update();
	}

	public void update() {
		fireContentsChanged(this, 0, getSize());
	}

	public List<V> getSelectedDataList() {
		return data.get(selectedKey);
	}

	public V getSelectedValue() {
		return selectedValue;
	}

	public void clear() {
		data.get(selectedKey).clear();
		update();
	}

	public void clearAll() {
		selectedKey = null;
		data.clear();
		update();
	}

	public void setSelectedKey( K newSelectedKey ) {
		this.selectedKey = newSelectedKey;
		if (data.get(selectedKey) == null) {
			data.put(selectedKey, new LinkedList<V>());
		}
		update();
	}

	public K getSelectedKey() {
		return selectedKey;
	}

	public Map<K, ? extends List<V>> getDataModel() {
		return data;
	}
}
