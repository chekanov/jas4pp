package org.freehep.jas.services;

import org.freehep.swing.wizard.WizardPage;

/**
 * An interface to be implemented by Plugins that want to create an entry
 * in the Wizard create by the Open DataSource menu.
 * @author tonyj
 * @version $Id: DataSource.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public interface DataSource
{
   /**
    * @return The wizard page to display if the user selects this data source
    */
   WizardPage getWizardPage();
   /**
    * @return The name of the data source (as it should appear in the wizard)
    */
   String getName();
}
