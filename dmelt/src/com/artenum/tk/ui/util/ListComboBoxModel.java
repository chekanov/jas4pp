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

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 * Generic ComboBoxModel based on List data structure.
 * 
 */
public class ListComboBoxModel<T> extends AbstractListModel implements ComboBoxModel {
	private static final long serialVersionUID = 1L;
	private List<T> data;
	private T selectedObject;

	public ListComboBoxModel( List<T> data ) {
		this.data = data;
		if (data.size() > 0)
			selectedObject = data.get(0);
	}

	public Object getSelectedItem() {
		return selectedObject;
	}

	@SuppressWarnings("unchecked")
	public void setSelectedItem( Object anItem ) {
		selectedObject = (T) anItem;
	}

	public Object getElementAt( int index ) {
		return data.get(index);
	}

	public int getSize() {
		return data.size();
	}

	public void addItem( T item ) {
		if (item == null)
			return;
		data.add(item);
		fireContentsChanged(this, 0, getSize());
	}

	public void removeItem( T item ) {
		data.remove(item);
		fireContentsChanged(this, 0, getSize());
	}

	public void update() {
		fireContentsChanged(this, 0, getSize());
	}

	public List<T> getDataList() {
		return data;
	}
	
	public void clear(){
		data.clear();
		update();
	}

}
