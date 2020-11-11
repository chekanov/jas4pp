/*
 * PreferencesTopic.java
 *
 * Created on December 9, 2002, 4:10 PM
 */

package org.freehep.jas.services;

import javax.swing.JComponent;

/**
 * An interface to be implemented by anything that wants to appear in the
 * preferences dialog.
 * @author tonyj
 */
public interface PreferencesTopic
{
   /** Specifies where in the preferences tree this item should appear.
    * @return The path under which this topic should be displayed in the preferences dialog.
    */   
   String[] path();
   /** Get the component to display in the preferences dialog
    * @return The component to be used.
    */   
   JComponent component();
   /** Called when the user pushes the apply button in the preferences dialog.
    * This is also called if the user changes to another preferences topic, or
    * if the user hits OK to dismiss the preferences dialog.
    * @param panel The component currently being displayed
    * @return true if success, false if an error occured (invalid input)
    * @see #component()
    */   
   boolean apply(JComponent panel);
}
