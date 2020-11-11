package org.freehep.jas.services;

import java.io.File;
import javax.swing.Icon;
/** An interface to be implemented by all TextEditors
 * @author tonyj
 * @version $Id: TextEditor.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public interface TextEditor
{
   /** Gett the file being editor
    * @return The file, or <CODE>null</CODE> if no file is associated with the editor
    */   
   File getFile();
   /** Test if the text in the editor has been modified
    * @return <CODE>true</CODE> if the text has been modified
    */   
   boolean isModified();
   /** Get the associated mime-type
    * @return The mime-type
    */   
   String getMimeType();
   /** Get the title of the editor
    * @return The title
    */   
   String getTitle();
   /** Get the text from the editor, as a String.
    * @return The text
    */   
   String getText();
   
   /**
    * Save the text being edited.
    *
    */
   void saveText();
}