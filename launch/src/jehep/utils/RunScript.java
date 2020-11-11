package jehep.utils;


import java.io.*;
import java.net.*;
import javax.script.*;


/**
 * Run a DMelt file. It can be Ruby, Groovy, Jython, BeanShell.  File type is calculated from the file extension.
 * If file ends with *.py - Jython, if with *.bsh - bean shell, with *.rb - Ruby 
 * @author S.Chekanov 
 */


// input: file to run
 
public class RunScript { 
	public static void main(String [] args)
	{

   if (args.length > 0) {

    String fin = args[0].trim();

    String option="";
    if (args.length > 1) option=" "+args[1].trim();

    if (fin.endsWith(".py") || fin.endsWith(".Py") || fin.endsWith(".PY"))  {
              System.out.println("Executing:" + fin);
              org.python.util.jython.run(args);
              return;


    } if (fin.endsWith(".java") || fin.endsWith(".Java") || fin.endsWith(".JAVA"))  {

   
     try { 

      System.out.println("Java compiling & executing:" + fin);
      String classpath=System.getProperty("java.class.path");
      //System.out.println(System.getProperty("java.class.path"));

      int k =  runProcess("javac -classpath "+classpath+" "+fin);
      if (k==0) { 
        String ss=fin.substring(0, fin.length() - 5);
        k=runProcess("java -classpath "+classpath+" "+ss+option);
      }
    } catch (Exception e) {
        System.err.println("Error evaluating code: " + e.getMessage());
    }


     } else if (fin.endsWith(".groovy") || fin.endsWith(".gvy") || fin.endsWith(".gy") || fin.endsWith(".GROOVY")) {
              System.out.println("Executing:" + fin);
              try {
                 File fopen = new File(fin);
                 groovy.lang.GroovyShell shell = new groovy.lang.GroovyShell();
                 shell.evaluate(fopen);
               } catch(IOException  e) {
                                 System.err.println("No file found ="+fin);
               }
             return;

      } else {
                
               System.err.println("Can only run Jython (*.py), Groovy (*.groovy, *.gvy, *.gy), JRuby (*.rb), BeanShell (*.bsh) script!");
       }



   } else {
     System.err.println("Argument" + args[0] + " must be a script file or other arguments.");
     System.exit(1);
     }


	}




   
  private static void printLines(String name, InputStream ins) throws Exception {
    String line = null;
    BufferedReader in = new BufferedReader(
        new InputStreamReader(ins));
    while ((line = in.readLine()) != null) {
        System.out.println(name + " " + line);
    }
  }

  private static int runProcess(String command) throws Exception {
    Process pro = Runtime.getRuntime().exec(command);
    printLines(command + " stdout:", pro.getInputStream());
    printLines(command + " stderr:", pro.getErrorStream());
    pro.waitFor();
   // System.out.println(command + " exitValue() " + pro.exitValue());
    return pro.exitValue();
  }




}
