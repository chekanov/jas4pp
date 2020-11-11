package org.freehep.jas.services;

import org.freehep.swing.wizard.WizardPage;

/**
 * An interface to be implemented by Plugins that want to create an entry
 * in the Wizard created by the Open DataSource menu, but which do not 
 * themselves require any wizard pages.
 * @author tonyj
 * @version $Id: DataSourceWithoutWizard.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public interface DataSourceWithoutWizard
{
   /**
    * Called by the data source plugin when the user selects to open this data source 
    */
   void openDataSource();
   /**
    * @return The name of the data source (as it should appear in the wizard)
    */
   String getName();
}
