package org.freehep.jas.services;

import java.net.URL;
import java.io.IOException;

/**
 * An interface to be implemented by services which can open URLs.
 * 
 * @author tonyj
 * @version $Id: URLHandler.java 16113 2014-08-21 21:20:15Z onoprien $
 */
public interface URLHandler
{
   /**
    * Returns true if this URL handler is able to open the specified URL.
    * @param url The URL to test.
    * @return true if URL can be opened.
    */
   boolean accept(URL url) throws IOException;

   /**
    * Opens a given url.
    * @param url The URL to open
    */
   void openURL(URL url) throws IOException;

}
