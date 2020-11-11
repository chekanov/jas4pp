package org.freehep.jas.services;

import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileFilter;

/**
 * An interface to be implemented by services which can open files.
 * 
 * @author tonyj
 * @version $Id: FileHandler.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public interface FileHandler
{
   /**
    * Returns true if this file handler is able to open the file
    * @param file The file to test
    * @return true if file can be opened.
    */
   boolean accept(File file) throws IOException;
   /**
    * Returns a file filter suitable for using in a FileOpen dialog.
    */
   FileFilter getFileFilter();
   /**
    * Opens a given file.
    * @param file The file to open
    */
   void openFile(File file) throws IOException;

}
