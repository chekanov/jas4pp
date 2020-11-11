package org.freehep.jas.extensions.text.gui;

import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.freehep.application.Application;
import org.freehep.jas.util.OpenLocalFilePanel;
import org.freehep.swing.wizard.HasNextPages;
import org.freehep.swing.wizard.WizardPage;

/**
 *
 * @author  Tony Johnson
 */
public class StartPage extends WizardPage implements HasNextPages, ChangeListener
{
   private FirstPage next = new FirstPage();
   private OpenLocalFilePanel panel = new OpenLocalFilePanel("text", true, true);
   
   public StartPage()
   {
      add(panel);
      panel.addChangeListener(this);
      stateChanged(null);
   }
   
   public WizardPage getNext()
   {
      File file = new File(panel.getText());
      if (file.exists() && file.canRead())
      {
         try
         {
            GUIUtilities util = new GUIUtilities(file,panel.getGZIPed());
            next.setData(util);
            panel.saveState();
            return next;
         }
         catch (IOException x)
         {
            Application.error(this, "Error reading file", x);
            return null;
         }
      }
      else
      {
         JOptionPane.showMessageDialog(this, "Cannot read file", "Error...", JOptionPane.ERROR_MESSAGE);
         return null;
      }
   }
   
   public WizardPage[] getNextWizardPages()
   {
      return new WizardPage[] { next };
   }
   
   public void stateChanged(ChangeEvent e)
   {
      setNextEnabled(panel.getText().length() > 0);
   }
}