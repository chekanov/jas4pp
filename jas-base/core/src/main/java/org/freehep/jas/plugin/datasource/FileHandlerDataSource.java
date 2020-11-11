package org.freehep.jas.plugin.datasource;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.IOException;
import javax.swing.JPanel;
import org.freehep.application.Application;
import org.freehep.jas.services.DataSource;
import org.freehep.jas.services.FileHandler;
import org.freehep.jas.util.OpenLocalFilePanel;
import org.freehep.swing.wizard.Finishable;
import org.freehep.swing.wizard.WizardPage;

/**
 * Makes a DataSource from a FileHandler
 * @author Tony Johnson
 */
public class FileHandlerDataSource implements DataSource
{
   private String name;
   private FileHandler handler;

   public FileHandlerDataSource(FileHandler handler)
   {
      this(handler,null);
   }
   public FileHandlerDataSource(FileHandler handler, String name)
   {
      this.name = name;
      this.handler = handler;
   }   
   public String getName()
   {
      return name == null ? handler.getFileFilter().getDescription() : name;
   }
   
   public WizardPage getWizardPage()
   {
      return new FHWizardPage();
   }
   protected void openFile(File file) throws IOException
   {
      handler.openFile(file);
   }
   protected Component addOptions()
   {
      return null;
   }
   
   private class FHWizardPage extends WizardPage implements Finishable
   {
      private OpenLocalFilePanel panel = new OpenLocalFilePanel(handler.getClass().getName()+".files",handler.getFileFilter(),false,false);
      FHWizardPage()
      {
         setLayout(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.gridwidth = GridBagConstraints.REMAINDER;
         gbc.fill = GridBagConstraints.HORIZONTAL;
         gbc.anchor = GridBagConstraints.NORTH;
         gbc.weightx = 1.0;
         add(panel,gbc);
         Component c = addOptions();
         gbc.weighty = 1.0;
         add(c == null ? new JPanel() : c,gbc);
      }
      public void onFinish()
      {
         try
         {
            openFile(new File(panel.getText()));
            panel.saveState();
            dispose();
         }
         catch (IOException x)
         {
            Application.getApplication().error("Error opening file",x);
         }
      }     
   }
}
