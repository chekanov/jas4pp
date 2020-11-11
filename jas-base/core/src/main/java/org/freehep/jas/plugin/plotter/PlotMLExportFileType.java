package org.freehep.jas.plugin.plotter;

import jas.hist.JASHist;
import java.awt.Component;
import java.awt.GridLayout;
import java.io.*;
import java.util.Properties;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import org.freehep.application.PropertyUtilities;
import org.freehep.graphicsbase.util.export.ExportFileType;


/**
 *
 * @author Tony Johnson
 * @version $Id: PlotMLExportFileType.java 14049 2012-10-24 00:55:06Z onoprien $
 */
class PlotMLExportFileType extends ExportFileType
{
   private static final String SNAPSHOT = "snapshot";
   
   public void exportToFile(File file, Component[] target, Component parent, Properties properties, String creator) throws IOException
   {
      Writer writer = new FileWriter(file);
      ((JASHist) target[0]).writeXML(writer,PropertyUtilities.getBoolean(properties,SNAPSHOT,true));
      writer.close();
   }
   
   public void exportToFile(OutputStream os, Component[] target, Component parent, Properties properties, String creator) throws IOException
   {
      Writer writer = new OutputStreamWriter(os);
      ((JASHist) target[0]).writeXML(writer,true);
      writer.close();
   }
   
   public String getDescription()
   {
      return "XML Plot File";
   }
   
   public String[] getExtensions()
   {
      return new String[]{"plotml"};
   }
   
   public String[] getMIMETypes()
   {
      return new String[]{"application/x-plotml"};
   }
   
   public JPanel createOptionPanel(Properties options)
   {
      return new OptionPanel(PropertyUtilities.getBoolean(options,SNAPSHOT,true));
   }
   
   public boolean hasOptionPanel()
   {
      return true;
   }

   public boolean applyChangedOptions(JPanel custom, Properties options)
   {
      PropertyUtilities.setBoolean(options,SNAPSHOT,((OptionPanel) custom).isSnapshot());
      return true;
   }
   
   private class OptionPanel extends JPanel
   {
      private JRadioButton button1;
      private JRadioButton button2;
      
      OptionPanel(boolean snapshot)
      {  
         super(new GridLayout(0,1));
         button1 = new JRadioButton("Save current snapshot of data",snapshot);
         button2 = new JRadioButton("Save reference to live data",!snapshot);
         add(button1);
         add(button2);
         ButtonGroup bg = new ButtonGroup();
         bg.add(button1);
         bg.add(button2);
      }
      boolean isSnapshot()
      {
         return button1.isSelected();
      }
   }
   
}
