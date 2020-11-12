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
package com.artenum.tk.ui.vector;

import java.io.PrintStream;

import com.artenum.tk.ui.api.Tuple3Listener;

public class PrintVectorListener implements Tuple3Listener {
	private PrintStream printer;

	public PrintVectorListener(PrintStream printer) {
		this.printer = printer;
	}

	public void valueChanged(float x, float y, float z) {
		printer.println("Normal(" + x + ", " + y + ", " + z + ")");
	}
}
