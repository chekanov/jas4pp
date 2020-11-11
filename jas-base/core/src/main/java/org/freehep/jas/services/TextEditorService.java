package org.freehep.jas.services;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import javax.swing.Icon;

/** An interface to be implemented by all TextEditor systems
 * @author tonyj
 * @version $Id: TextEditorService.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public interface TextEditorService
{
   /** Show the given text in a new editor window
    * @param text The text to display
    * @param mimeType The mime-type to be associated with the editor
    * @param title The title for the editor
    */   
   void show(String text, String mimeType, String title);
   /** Show a file in an editor window
    * @param f The file to show
    * @param mimeType The mime-type for the file
    * @throws IOException If there is a problem reading the file.
    */   
   void show(File f, String mimeType) throws IOException;

   /** Show a file from an URL in an editor window
    * @param url The URL of the file
    * @param mimeType The mime-type for the file
    * @throws IOException If there is a problem reading the file.
    */   
   void show(URL url, String mimeType) throws IOException;

   /** Show text read from a Reader in an editor
    * @param reader The reader to read from.
    * @param mimeType The mime-type to associate with the read text
    * @param title The title for the editor window
    * @throws IOException If there is a problem reading from the Reader.
    */   
   void show(Reader reader, String mimeType, String title) throws IOException;
  
   /** Associates a given icon with a mime-type.
    * @param mimeType The mime-type
    * @param icon The associated icon
    */   
   void addMimeType(String mimeType, Icon icon);
   /** Retrieve the icon for a mime-type
    * @param mimeType The mime-type to search for
    * @return The icon, or <CODE>null</CODE> if none exists.
    */   
   Icon getIconForMimeType(String mimeType);
   
   /** Get the "current" text editor.
    * @return The currently selected text editor, or <CODE>null</CODE> if none is selected
    */   
   TextEditor getCurrentEditor();
   /** Get a list of all active editors.
    * @return The List of TextEditors
    * @see TextEditor
    */   
   List editors();
}
