package org.freehep.jas.extensions.text;
import org.freehep.application.studio.Plugin;
import org.freehep.jas.extensions.text.aida.TextStoreFactory;
import org.freehep.jas.extensions.text.gui.StartPage;
import org.freehep.jas.services.DataSource;
import org.freehep.swing.wizard.WizardPage;
import org.freehep.util.FreeHEPLookup;

/**
 *
 * @author Tony Johnson
 */
public class TextPlugin extends Plugin implements DataSource
{
   public WizardPage getWizardPage()
   {
      return new StartPage();
   }
   
   public String getName()
   {
      return "Data from text file";
   }
   
   protected void init()
   {
      FreeHEPLookup lookup = getApplication().getLookup();
      lookup.add(this);
      lookup.add(new TextStoreFactory());
   }
   
}
