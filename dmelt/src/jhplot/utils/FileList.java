package jhplot.utils;


import java.io.File;
import java.util.*;
import java.util.regex.*;

 /**
 * Get list of files in all directories (including subdirectories).
 * Path root director and a string in form of 
 * a regular expression to find matches in a string
 * S.Chekanov (ANL) 
 **/

public class FileList {

    static private String match="";
    static private ArrayList<String> myArr;
    static private boolean doMatch=false;
    static private Pattern pattern;
 
    private static void processFile(File dir) {
    	
        // only files 
        if (dir.isDirectory() == false) {
         String sdir=dir.toString();
         if (doMatch){
              // int index = sdir.indexOf(match); 
              Matcher matcher = pattern.matcher(sdir);
              boolean matchFound = matcher.find();
              if (matchFound) 
                     myArr.add(sdir); 
         } else {  
                   myArr.add(sdir); 
                }

        } 

    }

    private static void traverse(File dir) {
        processFile(dir);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                traverse(new File(dir, children[i]));
            }
        }

    }


   /**
   * Get list of files in all directories (including subdirectories).
   * @param input directory
   * @param m a string for regular expression to find matches in file name           
   **/
    public static ArrayList  get(String dir, String m) {
       match = m.trim(); 
       if (match.length()>0) {
                     doMatch=true;
                     pattern = Pattern.compile( m );
                     }

       myArr = new ArrayList<String>();
       traverse(new File(dir));
       return myArr; 

    }



    /**
     * Sole entry point to the class and application.
     * @param args Array of String arguments.
     */
    public static void main(String[] args) {
        // traverse(new File("/home/sergei/"));
        ArrayList arraylistA = get("/home/sergei/Documents", ".pdf");
        Iterator i1 = arraylistA.iterator();
		System.out.print("ArrayList arraylistA --> ");
		while (i1.hasNext()) {
			System.out.print(i1.next() + "\n");
		}


    }

}
