package org.freehep.jas.plugin.datasource;

import java.io.IOException;
import org.freehep.application.studio.Plugin;
import org.xml.sax.SAXException;

/**
 *
 * @author tonyj
 */
public class DataSourcePlugin extends Plugin
{
  protected void init() throws SAXException, IOException {
  }

  protected void postInit() {
    super.postInit();
    DataSourceMenu menu = new DataSourceMenu(getApplication());
    menu.setEnabled(menu.getItemCount() > 0);
    addMenu(menu, 100110505); 
  }
}
