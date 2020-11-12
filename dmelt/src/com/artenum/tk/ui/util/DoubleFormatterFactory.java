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

import java.text.ParseException;

import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;

/**
 * FormaterFactory used to impose double data type
 * 
 */
public class DoubleFormatterFactory extends JFormattedTextField.AbstractFormatterFactory {
	private static AbstractFormatter DOUBLE_FORMATTER = new AbstractFormatter() {
		private static final long serialVersionUID = 1L;

		public Object stringToValue( String text ) throws ParseException {
			return new Double(text);
		}

		public String valueToString( Object value ) throws ParseException {
			return Double.toString((Double) value);
		}
	};

	public AbstractFormatter getFormatter( JFormattedTextField tf ) {
		return DOUBLE_FORMATTER;
	}
}
