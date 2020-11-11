package org.freehep.jas.extension.compiler;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * A implementation of CompilerInterface that uses an in-process Java compiler.
 * This requires that tools.jar (or the relevant subset of classes) is in the
 * users CLASSPATH. Note: this uses a non-public com.sun API. 
 *
 * We use reflection to access the compiler class so that this routine will compile 
 * without tools.jar in the CLASSPATH.
 *
 * @author tonyj
 * @version $Id: InternalCompiler.java 13884 2011-09-20 23:10:19Z tonyj $
 */
class InternalCompiler implements CompilerInterface
{  
   private final static String compilerClassName = "com.sun.tools.javac.Main";
   private Method compileMethod;
   
   InternalCompiler() throws ClassNotFoundException, NoSuchMethodException
   {
      Class compilerClass = Class.forName(compilerClassName);
      Class[] argc = { String[].class, PrintWriter.class };
      compileMethod = compilerClass.getMethod("compile",argc);
   }
   
   public boolean compile(File file)
   {      
      PrintWriter pw = new PrintWriter(out);
      try
      {
         String[] args = new String[outDir == null ? 3 : 5];
         int k = 0;
         args[k++] = "-classpath";
         args[k++] = classpath;
         if (outDir != null)
         {
            args[k++] = "-d";
            args[k++] = outDir.getAbsolutePath();
         }
         args[k++] = file.getAbsolutePath();
         Object[] argv = { args, pw };
         int rc = ((Number) compileMethod.invoke(null,argv)).intValue();
         return (rc == 0);
      }
      catch (Throwable x)
      {
         x.printStackTrace(pw);
         return false;
      }
      finally
      {
         pw.flush();         
      }
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
}