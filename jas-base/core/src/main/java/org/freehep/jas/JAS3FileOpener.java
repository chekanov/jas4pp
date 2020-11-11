package org.freehep.jas;

import java.io.IOException;
import org.freehep.jas.plugin.basic.FileHelper;

/**
 *
 * @author tonyj
 * @version $Id: JAS3FileOpener.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public class JAS3FileOpener
{  
   private static String[] chained;
   public static void main(String[] args) throws IOException
   {
      FileHelper fh = new FileHelper(JAS3FileOpener.class);
      try
      {
         fh.send(args[0]);
      }
      catch (Throwable x)
      {
         String home = System.getProperty("application.home");
         chained = new String[]{home+"jas3w.exe","jas3w","\""+args[0]+"\""};
      }
   } 
   public static String[] chain()
   {
      return chained;
   }
}