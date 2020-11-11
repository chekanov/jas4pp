package org.freehep.jas.extension.compiler;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import org.freehep.application.Application;
import org.freehep.application.studio.Studio;
import org.freehep.swing.JDirectoryChooser;

class ClassPathPanel extends AddRemovePanel
{
   private Studio app;

   
   ClassPathPanel(Studio app, List cp)
   {
      super(cp, "Look for class files in these directories:", null, null, new ClassPathListCellRenderer());
      this.app = app;
   }

   protected Object add(String addText)
   {
      String dir = app.getUserProperties().getProperty("ClassPathPanel.dir");
      JDirectoryChooser choose = new JDirectoryChooser(dir);
      choose.setFileSelectionMode(choose.FILES_AND_DIRECTORIES);
      choose.setFileHidingEnabled(false);
      choose.setFileView(new ClassPathFileView());
      choose.setFileFilter(new ClassPathFilter());
      choose.setDialogTitle("Select Directory or Jar File");

      int rc = choose.showDialog(this);
      if (rc != choose.APPROVE_OPTION)
         return null;

      File f = choose.getSelectedFile();
      app.getUserProperties().put("ClassPathPanel.dir", f.getAbsolutePath());
      return f;
   }
}
