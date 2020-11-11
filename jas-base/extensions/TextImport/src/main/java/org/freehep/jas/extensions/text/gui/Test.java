package org.freehep.jas.extensions.text.gui;

import javax.swing.UIManager;
import org.freehep.swing.wizard.WizardDialog;

/**
 *
 * @author Tony Johnson
 */
public class Test
{
   public static void main(String[] args) throws Exception
   {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

      WizardDialog dlg = new WizardDialog("test", new StartPage());
      dlg.setDefaultCloseOperation(dlg.EXIT_ON_CLOSE);
      dlg.pack();
      dlg.show();
   }
}