package org.freehep.jas.extensions.jconsole;

import org.freehep.swing.popup.HasPopupItems;

/**
 * Interface to be implemented by classes that create consoles.
 *
 * @author onoprien
 */
public interface JConsoleProvider {

  /**
   * Creates a new console.
   *
   * @param name The name of the newly created console
   * @param popupItems Pop-up items to be attached to the console, or <tt>null</tt> for no none.
   * @return The newly created console.
   */
  JConsole createConsole(String name, HasPopupItems popupItems);
  
}
