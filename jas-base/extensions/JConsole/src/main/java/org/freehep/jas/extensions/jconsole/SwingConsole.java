package org.freehep.jas.extensions.jconsole;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author onoprien
 */
public class SwingConsole implements JConsole {

// -- Private parts : ----------------------------------------------------------
  
  private final Component _view;

// -- Construction and initialization : ----------------------------------------
  
  public SwingConsole(String name) {
    _view = new JPanel();
    _view.setName(name);
  }
  
  
// -- Getters : ----------------------------------------------------------------

  @Override
  public String getName() {
    return _view.getName();
  }

  @Override
  public Component getView() {
    return _view;
  }
  
  
// -- Life cycle : -------------------------------------------------------------

  @Override
  public void dispose() {
//    JOptionPane.showMessageDialog(null, "Disposing of "+ _name);
  }
  
  

}
