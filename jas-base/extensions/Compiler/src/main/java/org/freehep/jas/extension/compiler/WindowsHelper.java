package org.freehep.jas.extension.compiler;
import java.io.*;

class WindowsHelper
{
   
   private static final String REGQUERY_UTIL = "reg query ";
   private static final String REGSTR_TOKEN = "REG_SZ";
   
   private static final String JDK_VERSION_CMD = REGQUERY_UTIL +
           "\"HKLM\\Software\\JavaSoft\\Java Development Kit\" /v CurrentVersion";
   private static final String JDK_LOCATION_CMD = REGQUERY_UTIL +
           "\"HKLM\\Software\\JavaSoft\\Java Development Kit\\%s\" /v JavaHome";
   
   static String getJDKVersion()
   {
      try
      {
         Process process = Runtime.getRuntime().exec(JDK_VERSION_CMD);
         StreamReader reader = new StreamReader(process.getInputStream());
         
         reader.start();
         process.waitFor();
         reader.join();
         
         String result = reader.getResult();
         int p = result.indexOf(REGSTR_TOKEN);
         
         if (p == -1)
            return null;
         
         return result.substring(p + REGSTR_TOKEN.length()).trim();
      }
      catch (Exception e)
      {
         return null;
      }
   }
   static String getJDKLocation()
   {
      try
      {
         String query = JDK_LOCATION_CMD.replaceAll("\\%s",getJDKVersion());
         Process process = Runtime.getRuntime().exec(query);
         StreamReader reader = new StreamReader(process.getInputStream());
         
         reader.start();
         process.waitFor();
         reader.join();
         
         String result = reader.getResult();
         int p = result.indexOf(REGSTR_TOKEN);
         
         if (p == -1)
            return null;
         
         return result.substring(p + REGSTR_TOKEN.length()).trim();
      }
      catch (Exception e)
      {
         return null;
      }
   }
   static class StreamReader extends Thread
   {
      private InputStream is;
      private StringWriter sw;
      
      StreamReader(InputStream is)
      {
         this.is = is;
         sw = new StringWriter();
      }
      
      public void run()
      {
         try
         {
            int c;
            while ((c = is.read()) != -1)
               sw.write(c);
         }
         catch (IOException e)
         { ; }
      }
      
      String getResult()
      {
         return sw.toString();
      }
   }
}
