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
package com.artenum.tk.ui.file;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.artenum.tk.ui.api.FileListener;
import com.artenum.tk.ui.api.FileValue;
import com.artenum.tk.ui.util.ObjectConvertor;

public class SingleFileInput implements FileValue, ActionListener {
	private final static Dimension MIN_SIZE = new Dimension(100, 20);
	private final static Dimension MAX_SIZE = new Dimension(1024, 20);
	private ArrayList<FileListener> listeners;
	private JPanel ui;
	private JTextField filePath;
	private JLabel fileSize;
	private JButton browse;
	private JFileChooser chooser;

	public SingleFileInput() {
		listeners = new ArrayList<FileListener>();
		filePath = new JTextField();
		filePath.addActionListener(this);
		fileSize = new JLabel();
		browse = new JButton("Parcourir");
		browse.addActionListener(this);
		ui = new JPanel();
		ui.setOpaque(false);
		ui.setLayout(new BoxLayout(ui, BoxLayout.X_AXIS));
		ui.add(filePath);
		ui.add(Box.createHorizontalStrut(5));
		ui.add(browse);
		ui.add(Box.createHorizontalStrut(5));
		filePath.setMaximumSize(MAX_SIZE);
		filePath.setMinimumSize(MIN_SIZE);
		Dimension size = new Dimension(100, 20);
		fileSize.setMaximumSize(size);
		fileSize.setMinimumSize(size);
		fileSize.setPreferredSize(size);
		fileSize.setSize(size);
		ui.add(fileSize);
		//
		chooser = new JFileChooser();
	}

	public void addFileListener( FileListener l ) {
		listeners.add(l);
	}

	public JComponent getUI() {
		return ui;
	}

	public String getValue() {
		return filePath.getText();
	}

	public void notifyListener() {
		String value = filePath.getText();
		for (FileListener listener : listeners) {
			listener.valueChanged(value);
		}
	}

	public void removeFileListener( FileListener l ) {
		listeners.remove(l);
	}

	public void setValue( String value , boolean notify ) {
		filePath.setText(value);
		if (notify)
			notifyListener();
		updateFileInfo();
	}

	public void actionPerformed( ActionEvent actionEvent ) {
		if (actionEvent.getSource().equals(filePath)) {
			updateFileInfo();
		} else if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(SwingUtilities.getWindowAncestor(ui))) {
			filePath.setText(chooser.getSelectedFile().getAbsolutePath());
		}
		updateFileInfo();
	}

	private void updateFileInfo() {
		checkFileSize();
		// Notify listener
		notifyListener();
	}

	private void checkFileSize() {
		String fileSizeTxt = ObjectConvertor.convertToFileSizeRepresentation(new File(filePath.getText()).length());
		filePath.setToolTipText(fileSizeTxt);
		fileSize.setText("(".concat(fileSizeTxt).concat(")"));
		ui.validate();
		ui.repaint();
	}

}
