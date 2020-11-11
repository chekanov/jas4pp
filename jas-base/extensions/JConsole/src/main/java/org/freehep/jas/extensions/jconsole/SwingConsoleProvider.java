package org.freehep.jas.extensions.jconsole;

import javax.swing.Icon;
import org.freehep.swing.popup.HasPopupItems;

/**
 *
 * @author onoprien
 */
public class SwingConsoleProvider implements JConsoleProvider {

// -- Private parts : ----------------------------------------------------------

// -- Construction and initialization : ----------------------------------------

  

  @Override
  public JConsole createConsole(String name, HasPopupItems popupItems) {
    return new SwingConsole(name);
  }
  
}
