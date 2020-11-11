package org.freehep.jas.services;
import java.net.URL;
/**
 *
 * @author tonyj
 * @version $Id: WebBrowser.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public interface WebBrowser
{
   /**
    * Shows a URL in the internal browser.
    * @param url The URL to show
    */
   void showURL(URL url);
   /**
    * Shows a URL.
    * @param url The URL to show
    * @param external If <code>true</code> display in OS specific browser, else use internal browser
    */
   void showURL(URL url, boolean external);
}
