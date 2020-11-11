package org.freehep.jas.extensions.jconsole;

import java.io.IOException;
import org.freehep.application.Application;
import org.freehep.swing.popup.HasPopupItems;

/**
 * Provider that creates {@link JLineConsole} instances.
 *
 * @author onoprien
 */
public class JLineConsoleProvider implements JConsoleProvider {

// -- Private parts : ----------------------------------------------------------

// -- Construction and initialization : ----------------------------------------

  @Override
  public JConsole createConsole(String name, HasPopupItems popupItems) {
    try {
      return new JLineConsole(name);
    } catch (IOException x) {
      Application.getApplication().error("Unable to create console", x);
      return null;
    }
  }

}
