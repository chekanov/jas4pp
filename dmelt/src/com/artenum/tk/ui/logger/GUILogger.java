/*
 * (c) Copyright: Artenum SARL, 101-103 Boulevard Mac Donald, 
 *                75019, Paris, France 2005.
 *                http://www.artenum.com
 *
 * License:
 *
 *  This program is free software; you can redistribute it 
 *  and/or modify it under the terms of the Q Public License; 
 *  either version 1 of the License.
 * 
 *  This program is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 *  PURPOSE. See the Q Public License for more details.
 * 
 *  You should have received a copy of the Q Public License 
 *  License along with this program;
 *  if not, write to:
 *    Artenum SARL, 101-103 Boulevard Mac Donald,
 *    75019, PARIS, FRANCE, e-mail: contact@artenum.com
 *
 * Initial Author:
 *    - Sebastien Jourdain (Artenum sarl)
 *    - Julien Forest (Artenum sarl)
 */

package com.artenum.tk.ui.logger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.Hashtable;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.artenum.tk.ui.xmlgui.GUILoader;

/**
 * <pre>
 * &lt;b&gt;Project ref           :&lt;/b&gt; Artenum Tool Kit
 * &lt;b&gt;Copyright and license :&lt;/b&gt; Copyright Artenum under the term of QPL
 * &lt;b&gt;Status                :&lt;/b&gt; Stable
 * &lt;b&gt;Creation              :&lt;/b&gt; 04/03/2005
 * &lt;b&gt;Modification          :&lt;/b&gt;
 * &lt;b&gt;Description  :&lt;/b&gt; Graphical component that can be use for printing logs.
 * 
 * </pre>
 * <table cellpadding="3" cellspacing="0" border="1" width="100%">
 * <tr BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 * <td><b>Version number</b></td>
 * <td><b>Author (name, e-mail)</b></td>
 * <td><b>Corrections/Modifications</b></td>
 * </tr>
 * <tr>
 * <td>0.1</td>
 * <td>Sebastien Jourdain, jourdain@artenum.com</td>
 * <td>Creation</td>
 * </tr>
 * </table>
 * 
 * @author Sebastien Jourdain
 * @version 0.2
 */
public class GUILogger extends JPanel implements ActionListener, ComponentListener, ChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1367411009432805437L;
	public final static String STYLE_NORMAL = "regular";
	public final static String STYLE_ERROR = "error";
	public final static String STYLE_WARNING = "warning";
	public final static String STYLE_DEBUG = "debug";

	public final static String STYLE_INFO_ICON = "regularIcon";
	public final static String STYLE_ERROR_ICON = "errorIcon";
	public final static String STYLE_WARNING_ICON = "warningIcon";
	public final static String STYLE_DEBUG_ICON = "debugIcon";

	public final static String STYLE_SYSTEM = "system";
	public final static String STYLE_LARGE = "large";
	public final static String STYLE_SMALL = "small";
	public final static String STYLE_BOLD = "bold";

	private StyledDocument document;
	private JTextPane log;
	private JButton clear;
	private JScrollPane scroll;
	private JCheckBox autoScroll;
	private int logSize = 50000;
	private JSlider slider;

	/** Error icon. */
	private ImageIcon errorIcon;

	/** Warning icon. */
	private Icon warningIcon;

	/** Debug icon. */
	private Icon debugIcon;

	/** Info icon. */
	private ImageIcon infoIcon;

	public GUILogger() {
		super(new BorderLayout());

		document = new DefaultStyledDocument();
		log = new JTextPane(document);
		log.setEditable(false);
		// Buffer size setting
		slider = new JSlider(1, 5);
		Hashtable tabLabel = new Hashtable();
		tabLabel.put(new Integer(5), new JLabel("Inf"));
		tabLabel.put(new Integer(1), new JLabel("Min"));
		slider.setLabelTable(tabLabel);
		slider.setPaintLabels(true);
		slider.addChangeListener(this);
		slider.setValue(5);

		scroll = new JScrollPane(log);
		clear = new JButton("Clear");
		clear.setActionCommand("CLEAR");
		clear.addActionListener(this);
		autoScroll = new JCheckBox("Auto scroll", true);
		JPanel cmdLine = new JPanel();
		cmdLine.setLayout(new BoxLayout(cmdLine, BoxLayout.LINE_AXIS));
		cmdLine.add(autoScroll);
		cmdLine.add(Box.createHorizontalGlue());
		cmdLine.add(slider);
		cmdLine.add(Box.createHorizontalGlue());
		cmdLine.add(clear);
		//
		add(scroll, BorderLayout.CENTER);
		add(cmdLine, BorderLayout.SOUTH);
		// 
		log.addComponentListener(this);

		// Init Style
		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

		// Normal
		Style regular = document.addStyle(STYLE_NORMAL, def);
		StyleConstants.setFontFamily(def, "SansSerif");
		// Bold
		Style s = document.addStyle(STYLE_BOLD, regular);
		StyleConstants.setBold(s, true);
		// Small
		s = document.addStyle(STYLE_SMALL, regular);
		StyleConstants.setFontSize(s, 10);
		// Large
		s = document.addStyle(STYLE_LARGE, regular);
		StyleConstants.setFontSize(s, 16);

		// Error
		s = document.addStyle(STYLE_ERROR, regular);
		StyleConstants.setForeground(s, Color.RED);
		// Warning
		s = document.addStyle(STYLE_WARNING, regular);
		StyleConstants.setForeground(s, new Color(255, 82, 0));
		// System
		s = document.addStyle(STYLE_SYSTEM, regular);
		StyleConstants.setForeground(s, Color.BLUE);
		// Debug
		s = document.addStyle(STYLE_DEBUG, regular);
		StyleConstants.setForeground(s, new Color(240, 0, 240));

		// Styles with icon
		// debug icon
		// debugIcon = new ImageIcon("icons/debug_icon.png");
		debugIcon = getImageIconFromClassPath("/debug_icon.png");
		s = document.addStyle(STYLE_DEBUG_ICON, regular);
		StyleConstants.setIcon(s, debugIcon);

		// infoIcon = new ImageIcon("icons/normal_icon.png");
		infoIcon = getImageIconFromClassPath("/normal_icon.png");
		s = document.addStyle(STYLE_INFO_ICON, regular);
		StyleConstants.setIcon(s, infoIcon);

		// warningIcon = new ImageIcon("icons/warning_icon.png");
		warningIcon = getImageIconFromClassPath("/warning_icon.png");
		s = document.addStyle(STYLE_WARNING_ICON, regular);
		StyleConstants.setIcon(s, warningIcon);

		// errorIcon = new ImageIcon("icons/error_icon.png");
		errorIcon = getImageIconFromClassPath("/error_icon.png");
		s = document.addStyle(STYLE_ERROR_ICON, regular);
		StyleConstants.setIcon(s, errorIcon);
	}

	/**
	 * Load an icon from the path to the file.
	 * 
	 * @param iconPath
	 * @return
	 */
	public ImageIcon getImageIconFromClassPath(String iconPath) {
		ImageIcon icon = null;

		if (iconPath != null) {
			URL imgURL = GUILoader.class.getResource(iconPath);
			if (imgURL != null) {
				icon = new ImageIcon(imgURL);
			} else {
				System.err.println("Couldn't find file: " + iconPath);
			}
		}
		return (icon);
	}

	StyledDocument getDocument() {
		return (document);
	}

	public void setBufferSize(int nbCharMax) {
		logSize = nbCharMax;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("CLEAR")) {
			try {
				document.remove(0, document.getLength());
			} catch (BadLocationException e1) {
				e1.printStackTrace();
			}
		}
	}

	public StyledDocument getStyledDocument() {
		return document;
	}

	public PrintStream getStreamedLogged(String styleName) {
		return new PrintStream(new LoggerOutputStream(styleName));
	}

	public void updateScrollPosition() {
		scroll.getViewport().setViewPosition(new Point(0, log.getHeight()));
	}

	public void componentHidden(ComponentEvent e) {
	}

	public void componentMoved(ComponentEvent e) {
	}

	public void componentResized(ComponentEvent e) {
		if (autoScroll.isSelected()) {
			updateScrollPosition();
		}
	}

	public void componentShown(ComponentEvent e) {
	}

	public void stateChanged(ChangeEvent e) {
		switch (slider.getValue()) {
		case 1:
			setBufferSize(5000);
			break;
		case 2:
			setBufferSize(10000);
			break;
		case 3:
			setBufferSize(50000);
			break;
		case 4:
			setBufferSize(100000);
			break;
		case 5:
			setBufferSize(-1);
			break;
		}
	}

	/**
	 * 
	 * @author juju
	 * 
	 */
	public class LoggerOutputStream extends OutputStream {
		private String styleName;

		public LoggerOutputStream(String styleName) {
			this.styleName = styleName;
		}

		public void setStyle(String style) {
			this.styleName = style;
		}

		public void checkBufferSize() {
			if ((logSize > 0) && ((document.getLength() - logSize) > 0)) {
				try {
					document.remove(0, document.getLength() - logSize);
				} catch (BadLocationException e1) {
					e1.printStackTrace();
				}
			}
		}

		public void write(int b) throws IOException {
			try {
				document.insertString(document.getLength(), String.valueOf((char) b), document.getStyle(styleName));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			checkBufferSize();
		}

		public void write(byte[] b) throws IOException {
			try {

				document.insertString(document.getLength(), new String(b), document.getStyle(styleName));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			checkBufferSize();
		}

		public void write(byte[] b, String style) throws IOException {
			styleName = style;
			write(b);
		}

		public void write(byte[] b, int off, int len) throws IOException {
			try {
				document.insertString(document.getLength(), new String(b, off, len), document.getStyle(styleName));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}

			checkBufferSize();
		}
	}
}
