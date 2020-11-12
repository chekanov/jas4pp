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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class ObjectConvertor {
    private static final DecimalFormat decimalFormat = new DecimalFormat("###.##");
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private final static String[] FILE_UNITS = { " o", " Ko", " Mo", " Go", " To" };

    public static String convertToFileSizeRepresentation(long size) {
	StringBuffer result = new StringBuffer();
	int unitDepth = 0;
	double numberToPrint = size;
	while (numberToPrint > 1000.0) {
	    unitDepth++;
	    numberToPrint /= 1000.0;
	}

	result.append(decimalFormat.format(numberToPrint));
	result.append(FILE_UNITS[unitDepth]);

	return result.toString();
    }

}
