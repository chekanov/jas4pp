package org.freehep.jas.extension.compiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tonyj
 * @version $Id: ExternalCompiler.java 13884 2011-09-20 23:10:19Z tonyj $
 */
class ExternalCompiler implements CompilerInterface
{
   ExternalCompiler(String command)
   {
      compilerCommand = command;
   }
   public boolean compile(File file)
   {
      PrintWriter pw = new PrintWriter(out);
      
      String[] envP = new String[1];
      envP[0] = "CLASSPATH="+classpath;
      
      List command = new ArrayList(); 
      command.add(compilerCommand);
      if (outDir != null)
      { 
         command.add("-d");
         command.add(outDir.getAbsolutePath());
      }
      command.add(file.getAbsolutePath());
      try
      {
         String[] commands = new String[command.size()];
         command.toArray(commands);
         Process p = Runtime.getRuntime().exec(commands,envP);
         Thread t1 = new CompileOutputProcessor(p.getInputStream(),out);
         Thread t2 = new CompileOutputProcessor(p.getErrorStream(),out);
         int rc = p.waitFor();
         t1.join();
         t2.join();
         return (rc == 0);
      }
      catch (InterruptedException x) { x.printStackTrace(pw); }
      catch (IOException x) { x.printStackTrace(pw); }
      finally { pw.flush(); }
      return false;
   }
   
   public void setClassPath(String classpath)
   {
      this.classpath = classpath;
   }
   
   public void setOutputDir(File out)
   {
      this.outDir = out;
   }
   
   public void setOutputStream(OutputStream out)
   {
      this.out = out;
   }
   private OutputStream out;
   private String classpath;
   private File outDir;
   private String compilerCommand = "javac";
   
   private class CompileOutputProcessor extends Thread
   {
      CompileOutputProcessor(InputStream in, OutputStream out)
      {
         this.in = in;
         this.out = out;
         start();
      }
      public void run()
      {
         try
         {
            byte[] buffer = new byte[4096];
            for (int n = in.read(buffer); n >= 0 ; n = in.read(buffer))
            {
               out.write(buffer,0,n);
            }
         }
         catch (IOException x)
         {}
      }
      private InputStream in;
      private OutputStream out;
   }//CompileOutputProcessor

}
