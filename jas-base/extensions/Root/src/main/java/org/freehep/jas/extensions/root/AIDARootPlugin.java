package org.freehep.jas.extensions.root;

import hep.aida.IAnalysisFactory;
import hep.aida.ITreeFactory;

import hep.io.root.daemon.RootAuthenticator;
import hep.io.root.daemon.RootStreamHandler;

import java.io.File;
import java.io.IOException;
import javax.swing.JComponent;

import javax.swing.filechooser.FileFilter;
import org.freehep.application.PropertyUtilities;

import org.freehep.application.studio.Plugin;
import org.freehep.jas.plugin.datasource.FileHandlerDataSource;
import org.freehep.jas.services.DataSource;
import org.freehep.jas.services.FileHandler;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.swing.ExtensionFileFilter;
import org.freehep.util.FreeHEPLookup;


/**
 * A JAS3 Plugin for AIDA.
 * @author tonyj
 * @version $Id: AIDARootPlugin.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class AIDARootPlugin extends Plugin implements FileHandler, PreferencesTopic, DataSource
{
   //*************************************//
   // Methods for the FileHandler service //
   //*************************************//
   private static String[] preferencesPath = { "AIDA", "Root" };
   
   /** Holds value of property showAllCycles. */
   private boolean showAllCycles;
   
   public FileFilter getFileFilter()
   {
      return new ExtensionFileFilter("root", "Root File");
   }

   public boolean accept(File file) throws IOException
   {
      return file.getName().endsWith(".root");
   }

   public void openFile(File file) throws IOException
   {
      IAnalysisFactory factory = IAnalysisFactory.create();
      ITreeFactory tf = factory.createTreeFactory();
      String options = isShowAllCycles() ? "showAllCycles" : "";
      tf.create(file.getAbsolutePath(), "root", true, false, options);
   }

   //****************************************//
   protected void init() throws IOException
   {
      FreeHEPLookup lookup = getApplication().getLookup();
      lookup.add(this);
      
      lookup.add(new RootAuthenticator(getApplication()),"root");
      lookup.add(new RootStreamHandler(),"root");
      lookup.add(new FileHandlerDataSource(this));
   }
   
   public boolean apply(JComponent panel)
   {
      return ((AIDARootPreferences) panel).apply();
   }
   
   public JComponent component()
   {
      return new AIDARootPreferences(this);
   }
   
   public String[] path()
   {
      return preferencesPath;
   }
   boolean isShowAllCycles()
   {
      return PropertyUtilities.getBoolean(getApplication().getUserProperties(), "Root.isShowAllCycles", false);
   }
   void setShowAllCycles(boolean showAllCycles)
   {
      PropertyUtilities.setBoolean(getApplication().getUserProperties(),"Root.isShowAllCycles", showAllCycles);
   }  
   
   public String getName()
   {
      return "Root Daemon";
   }
   
   public org.freehep.swing.wizard.WizardPage getWizardPage()
   {
      return new RootWizardPage();
   }
   
}
