package org.freehep.jas.event;
import javax.swing.*;
import org.freehep.jas.services.TextEditor;

/**
 * An event fired when an editor is about to popup an a popup menu.
 * The menu can be modified by the receiver.
 * @author tonyj
 * @version $Id: EditorPopupEvent.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public class EditorPopupEvent extends java.util.EventObject
{
   private JPopupMenu menu;
   
   public EditorPopupEvent(TextEditor source, JPopupMenu menu)
   {
      super(source);
      this.menu = menu;
   }
   public JPopupMenu getMenu()
   {
      return menu;
   }
   public TextEditor getEditor()
   {
      return (TextEditor) getSource();
   }
}
