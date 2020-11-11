package org.freehep.jas.plugin.datasource;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.freehep.application.studio.Studio;
import org.freehep.jas.services.DataSource;
import org.freehep.jas.services.DataSourceWithoutWizard;
import org.freehep.swing.wizard.WizardDialog;
import org.freehep.swing.wizard.WizardPage;
import org.openide.util.Lookup.Result;
import org.openide.util.Lookup.Template;

/**
 *
 * @author onoprien
 */
class DataSourceMenu extends JMenu {
  
// -- Private parts : ----------------------------------------------------------

  private final Studio _app;

// -- Construction : -----------------------------------------------------------  
  
  DataSourceMenu(Studio app) {
    super("Open Data Source");
    _app = app;
    Template template = new Template(DataSource.class);
    Result result = app.getLookup().lookup(template);
    ArrayList<DataSourceMenuItem> dataSources = new ArrayList<DataSourceMenuItem>();
    for (Object o : result.allInstances()) {
      dataSources.add(new DataSourceMenuItem((DataSource)o));
    }
    template = new Template(DataSourceWithoutWizard.class);
    result = app.getLookup().lookup(template);
    for (Object o : result.allInstances()) {
      dataSources.add(new DataSourceMenuItem((DataSourceWithoutWizard)o));
    }
    Collections.sort(dataSources);
    for (DataSourceMenuItem item : dataSources) {
      add(item);
    }
  }

  
// -- DataSourceMenuItem class : -----------------------------------------------
  
  private class DataSourceMenuItem extends JMenuItem implements Comparable<JMenuItem> {
    
    private final Object _dataSource;
    
    DataSourceMenuItem(DataSource dataSource) {
      _dataSource = dataSource;
      setText(dataSource.getName() +" ...");
      addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
         Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class,_app);
         WizardDialog wizard = new AppWizardDialog(frame,"Open Data Source", ((DataSource)_dataSource).getWizardPage());
         wizard.pack();
         wizard.setLocationRelativeTo(_app);
         wizard.setVisible(true);
        }
      });
    }
    
    DataSourceMenuItem(DataSourceWithoutWizard dataSource) {
      _dataSource = dataSource;
      setText(dataSource.getName());
      addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
         ((DataSourceWithoutWizard)_dataSource).openDataSource();
        }
      });
    }

    public int compareTo(JMenuItem other) {
      return getText().compareTo(other.getText());
    }
    
  }
  
  private class AppWizardDialog extends WizardDialog {

    AppWizardDialog(Frame frame, String title, WizardPage firstPage) {
      super(frame, title, firstPage);
    }

    protected void handleError(String message, Throwable t) {
      _app.error(message, t);
    }
  }

}