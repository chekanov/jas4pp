package jhepsim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;


/**
 *  Static singleton class to provide and persistently store program options.
 */
public class Options
{

// this is absolute defaults
   private static Properties  defaults = new Properties();

// keep here only those which are different from the default
   private static Properties  properties = new Properties( defaults ); // can be modified 

   // this sets the default values for user selectable options
   static {

        defaults.setProperty( "frame.x", "0" );
        defaults.setProperty( "frame.y", "0" );
        defaults.setProperty( "frame.width", "600" );
        defaults.setProperty( "frame.height", "500" );
        defaults.setProperty( "article.font.family", "Arial" );
        defaults.setProperty( "article.font.face","bold"); 
        defaults.setProperty( "article.font.name", "Arial" );  
        defaults.setProperty( "article.font.style", "normal" );
        defaults.setProperty( "article.font.size", "14" );
        
  
   }
   
   /** Initializes this service with attempting to load the parameter file.
    *  If the parameter file cannot be opened, a standard file is assumed
    *  instead.
    * 
    * @param file specifies the JPWS options file to load;
    *        may  be <b>null</b>
    */
   public static void init ( )
   {
      InputStream in;
      File f;
      int i;

//
      
      f  = new File ( SetEnv.INIFILE );

      // try to load option values from application file
        try {
//               System.out.println( "- attempting OPTION FILE: " + f.getAbsolutePath() );
               in = new FileInputStream( f );
               properties.load( in ); // this can be changed 
               in.close();
            }
         catch ( IOException e )
         {
 //         System.err.println("*** CANNOT LOAD PROGRAM OPTIONS:" + f.getAbsolutePath() ); 
       //  System.err.println( e );
         }
 

  }  // init
   
   /** Saves the actual content of the options db to persistent file.
    */
   public static void save()
   {
      OutputStream out;
      File stddFile;
      
 //      properties.put( "user-defined", "this is my options" );
      stddFile = new File( SetEnv.INIFILE);
      
      // try to write option values to application file
         try {
         //       System.out.println("***Write:"+Global.INIFILE);
               out = new FileOutputStream( stddFile  );
               properties.store( out, "--> jhepsim preferences");
               out.close();
         }
         catch ( IOException e )
         {
            System.out.println("*** CANNOT WRITE OPTIONS:" + SetEnv.INIFILE );
         }


   }  // save
   
   /** Elementary property function. */
   public static String getProperty ( String token )
   {
      return properties.getProperty( token );
   }


   /** Get default property. **/   
   public static String getDefault( String token )
   {
      return defaults.getProperty( token );
   }



   /** Elementary property function. */
   public static void setProperty ( String token, String value )
   {
      properties.setProperty( token, value );
   }

   /** Whether the specified (boolean) option is set to "true". */
   public static boolean isOptionSet ( String token )
   {
      String p = getProperty( token );
      return p != null && p.equals( "true" );
   }

   /** Sets a boolean value for the parameter option name.
    * 
    * @param token the option name
    * @param value assigned boolean value
    */
   public static void setOption ( String token, boolean value )
   {
      String hstr;
      
      hstr = value ? "true" : "false";
      if ( hstr.equals( defaults.getProperty( token )) )
         properties.remove( token );
      else
         setProperty( token, hstr );
   }

   /** Sets a string value for the parameter option name.
    * 
    * @param token the option name
    * @param value assigned string value, may be <b>null</b> to clear
    */
   public static void setOption ( String token, String value )
   {
      if ( value == null || value.equals( defaults.getProperty( token )) )
         properties.remove( token );
      else
         setProperty( token, value );
   }

   /** Returns the mapped option string value or empty string if the option is
    *  undefined.
    * @param token the option name
    * @return
    */
   public static String getOption ( String token )
   {
      String hstr;
      
      if ( (hstr = getProperty( token )) == null )
         hstr = "";
      return hstr;
   }
   
   /** Returns the mapped option integer value or 0 if the option is
    *  undefined.
    * @param token the option name
    * @return
    */
   public static int getIntOption ( String token )
   {
      String p;
      int i;

      i = 0;
      p = getProperty( token );
      if ( p != null )
      try {
         i = Integer.parseInt( p );
      }
      catch ( Exception e )
      {
         p = defaults.getProperty( token );
         try {
            i = Integer.parseInt( p );
         }
         catch ( Exception e2 )
         {}
      }
      return i;
   }  // getIntOption




   /**
    * Reset preferences.
    */
 public static void reset ()
   {
    File f = new File( SetEnv.INIFILE);
    if (f.exists() ) f.delete();
 
   }  






   /** Sets an integer value for the parameter option name.
    * 
    * @param token the option name
    * @param value assigned int value
    */
   public static void setIntOption ( String token, int value )
   {
      setOption( token, String.valueOf( value ) );
   }


   public static boolean checkOption ()
   {

    boolean tmp = true;


    java.util.Set states = properties.keySet();
    Iterator itr = states.iterator();

    while (itr.hasNext() ) {
           String str = (String)itr.next();
//          System.out.println( str + " " + properties.getProperty(str));
//          if (!defaults.getProperty(str).equals(properties.getProperty(str)) ) {
//               System.out.println( str + " default=" + defaults.getProperty(str)
//               + "  set=" + properties.getProperty(str));
                tmp = false;
     }


     return tmp;

   }





}
