/**
*    This program is free software; you can redistribute it and/or modify it under the terms
*    of the GNU General Public License as published by the Free Software Foundation; either
*    version 3 of the License, or any later version.
*
*    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
*    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*    See the GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License along with this program;
*    if not, see <http://www.gnu.org/licenses>.
*
*    Additional permission under GNU GPL version 3 section 7:
*    If you have received this program as a library with written permission from the DataMelt team,
*    you can link or combine this library with your non-GPL project to convey the resulting work.
*    In this case, this library should be considered as released under the terms of
*    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
*    provided you include this license notice and a URL through which recipients can access the
*    Corresponding Source.
**/

package rootio;


import hep.io.root.RootClassNotFound;
import hep.io.root.RootFileReader;
import hep.io.root.daemon.xrootd.XrootdURLStreamFactory;
import hep.io.root.daemon.RootAuthenticator;
import hep.io.root.daemon.RootURLStreamFactory;
import hep.io.root.interfaces.*;
import jas.hist.DataSource;
import jas.hist.JASHist;
import java.io.*;
import java.util.List;
import hep.io.root.util.*;
import javax.swing.*;
import java.awt.event.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.Authenticator;

/**
 * ROOT object browser. Improved vesrions of RootObjectBrowser adopted for Java scripting.  
 * @author S.Chekanov
 */
public class Browser
{

     private JFrame frame;


     /**
     *  Open a ROOT file and show GUI for browsing ROOT objecst.
     *  @param path to ROOT file (or URL). If path starts from "root:", XROOTD is assumed. 
     */

     public  Browser(String path)  throws java.io.IOException  {
 


      URL.setURLStreamHandlerFactory(new XrootdURLStreamFactory());

      frame = new JFrame("Root Object Browser");
      RootObjectBrowser browser = new RootObjectBrowser();
      Authenticator.setDefault(new RootAuthenticator(browser));
      URLConnection.setDefaultAllowUserInteraction(true);

      if (path.startsWith("root:")) browser.setRootFile(new URL(path));
      else browser.setRootFile(new File(path));
      frame.setContentPane(browser);
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);

      }

}


  
