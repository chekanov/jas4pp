// * This code is licensed under:
// * jeHEP License, Version 1.0
// * - for license details see http://jehep.sourceforge.net/license.html 
// *
// * Copyright (c) 2005 by S.Chekanov (chekanov@mail.desy.de). 
// * All rights reserved.
package jhplot.utils;

import java.awt.*;
import javax.swing.*;
import java.net.*;


/**
 * A class to build a help for each canvas.
 * @author S.Chekanov
 *
 */
public class HelpDialog extends JDialog {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton closeButton;
	private JPanel panel1;
	private Component win;
	private String html_file;

	
	/**
	 * Show a help frame
	 * @param win  parent window
	 * @param html_file html file
	 */
	public HelpDialog(Component win, String html_file) {

		this.win = win;
		this.html_file = html_file;

		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setTitle("About");
		setModal(true);
		setResizable(true);

		// Get the system resolution
		Dimension res = Toolkit.getDefaultToolkit().getScreenSize();

		// make sure the dialog is not too big
		Dimension size = new Dimension(Math.min(400, res.width), Math.min(500,
				res.height));

		setSize(size);
		// setLocationRelativeTo(parent);

		JPanel topPanel = new JPanel();
		JPanel lowerPanel = new JPanel();
		lowerPanel.setPreferredSize(new Dimension(400, 35));

		closeButton = new JButton();
		closeButton.setText("Exit");
		closeButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				setVisible(false);
				dispose();
			}
		});

		lowerPanel.add(closeButton, null);
		topPanel.setLayout(new BorderLayout());
		getContentPane().add(topPanel, java.awt.BorderLayout.CENTER);
		getContentPane().add(lowerPanel, java.awt.BorderLayout.SOUTH);

		
		// Create the tab pages
		createPage1();
		topPanel.add(panel1, BorderLayout.CENTER);

		Util.centreWithin(win, this);

		// set visible and put on center
		this.setVisible(true);
	}

	// create about
	public void createPage1() {

		panel1 = new JPanel();
		panel1.setLayout(new BorderLayout());
		JEditorPane epane = new JEditorPane();
		epane.setOpaque(false);
		epane.setContentType("text/html;charset=ISO-8859-1");
		epane.setAutoscrolls(true);
		epane.setEditable(false);

		try {
			epane.setPage(getHTMLUrl(html_file));
		} catch (Exception e) {
			System.err.println("Couldn't create URL");
			epane.setContentType("text/plain");
		}

		JScrollPane jsp = new JScrollPane(epane);
		panel1.add(jsp);

	}

           /**
         * Returns a HTML page which is found in a valid image URL. The basis of the
         * url is where 'intro' is created, which can't be but the place where
         * JChess resides.
         *
         * @param name
         *            name of the HTML page
         * @return the URL to the page
         */
        public URL getHTMLUrl(String name) {
                URL url = null;
                try {
                        url = this.getClass().getResource("/html/" + name);
                } catch (Exception e) {
                }
                return url;
        }

}
