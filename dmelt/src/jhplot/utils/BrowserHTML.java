package jhplot.utils;

import java.awt.*;
import java.net.*;
import java.awt.Desktop;
import java.net.URI;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Simple Web browser" using Swing. Supply a URL on the command line.
 * @author S.Chekanov 
 * 
 */

public class BrowserHTML { 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		if (args.length == 0)
			try {
				new BrowserHTML(new URL("http://jwork.org/scavis/api/doc.php/jhplot/package-summary"));
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		else
			try {
				new BrowserHTML(new URL(args[0]));
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public BrowserHTML(final URL url) {


            if(Desktop.isDesktopSupported()){
            Desktop desktop = Desktop.getDesktop();
            try {
                desktop.browse(new URI(url.toString()));
            } catch (IOException | URISyntaxException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec("/usr/bin/firefox -new-window " + url.toString()); 
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }



}
