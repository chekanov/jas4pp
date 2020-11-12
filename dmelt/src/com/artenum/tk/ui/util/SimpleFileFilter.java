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

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * Simple file filter that could be used in any JFileChooser
 */
public class SimpleFileFilter extends FileFilter {
	private int id;
	private String fileExtension;
	private String description;

	public SimpleFileFilter( int id, String fileExtension, String description ) {
		this.id = id;
		this.fileExtension = fileExtension.toLowerCase();
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public boolean accept( File f ) {
		return f.isDirectory() || f.getName().toLowerCase().endsWith(fileExtension);
	}

	public String getDescription() {
		return description;
	}

}
