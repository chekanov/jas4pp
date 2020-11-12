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
package com.artenum.tk.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

public class PropertyLoader {
    public static void loadProperties(String fileToLoad)
        throws IOException, FileNotFoundException {
        loadProperties(new File(fileToLoad));
    }

    public static void loadProperties(File fileToLoad)
        throws IOException, FileNotFoundException {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            fis = new FileInputStream(fileToLoad);
            props.load(fis);
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        for (Enumeration<Object> e = props.keys(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            System.setProperty(key, props.getProperty(key));
        }
    }
}
