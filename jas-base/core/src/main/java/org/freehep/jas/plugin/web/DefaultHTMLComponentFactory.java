package org.freehep.jas.plugin.web;

import jas.hist.JASHist;
import jas.hist.JASHistData;
//import jas.hist.test.MemoryDataSource;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import org.freehep.application.studio.Studio;
import org.freehep.jas.services.HTMLComponentFactory;
import org.freehep.jas.util.IgnoreCase;
import org.freehep.util.FreeHEPLookup;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;
import org.freehep.util.images.ImageHandler;

/**
 * A factory for a default set of "built-in" components.
 * @author serbo
 * @version $Id: DefaultHTMLComponentFactory.java 13876 2011-09-20 00:52:21Z tonyj $
 */
class DefaultHTMLComponentFactory implements HTMLComponentFactory
{
   private static final String[] classes = 
   {
      "MemoryPlot",
      "CommandButton",
      "CheckBoxButton",
      "TestPlot"
   };
   
   void init(Studio app)
   {
      FreeHEPLookup lookup = app.getLookup();
      for (int i=0; i<classes.length; i++)
      {
         lookup.add(this, classes[i]);
      }
   }
   
   public JComponent getComponent(String name, Map attributes)
   {      
      if (name.equals("MemoryPlot"))
      {
         JASHist plot = new JASHist();
         //JASHistData data = plot.addData(new MemoryDataSource());       
         plot.setTitle("Java Memory Usage");
         plot.setDataAreaBorderType(plot.ETCHED);
         plot.getYAxis().setLabel("MBytes");
         plot.getXAxis().setLabel("Time (seconds)");
         plot.setAllowUserInteraction(false);
         if (IgnoreCase.containsIgnoreCase(attributes, "WIDTH") || IgnoreCase.containsIgnoreCase(attributes, "HEIGHT")) {
             try{
                 String wStr = (String) IgnoreCase.getIgnoreCase(attributes, "WIDTH");
                 int w = Integer.parseInt(wStr);
                 
                 String hStr = (String) IgnoreCase.getIgnoreCase(attributes, "HEIGHT");
                int h = Integer.parseInt(hStr);
                plot.setSize(w, h);
             }catch (NumberFormatException e) { e.printStackTrace(); }
         }
         //data.show(true);
         return plot;  
      } 
     else if (name.equals("TestPlot"))
      {
         JASHist plot = new JASHist();
         JASHistData data = plot.addData(new TestDataSource());       
         plot.setTitle("Test XY Plot");
         plot.setDataAreaBorderType(plot.ETCHED);
         plot.getYAxis().setLabel("Y");
         plot.getXAxis().setLabel("X");
         plot.setAllowUserInteraction(true);
         data.show(true);
         return plot;  
      } 
      else if (name.equals("CommandButton"))
      {
         return new CommandButton(attributes);
      }
      else if (name.equals("CheckBoxButton"))
      {
         return new CheckBoxButton(attributes);
      }
      else
      {
         throw new RuntimeException("DefaultHTMLComponentFactory does not know about class: "+name);
      }
   }
   private static void setButtonProperties(AbstractButton b, Map attributes)
   {
      URL base = (URL) IgnoreCase.getIgnoreCase(attributes, "BASEURL");
      
      String text = (String) IgnoreCase.getIgnoreCase(attributes, "TEXT");
      if (text != null) b.setText(text);
      String tooltip = (String) IgnoreCase.getIgnoreCase(attributes,"TOOLTIPTEXT");
      if (tooltip != null) b.setToolTipText(tooltip);
      String icon = (String) IgnoreCase.getIgnoreCase(attributes, "ICON");
      try
      {
         if (icon != null) b.setIcon(ImageHandler.getIcon(new URL(base,icon)));
      }
      catch (MalformedURLException xx)
      {
         IllegalArgumentException x = new IllegalArgumentException("Bad URL: "+icon);
         x.initCause(xx);
         throw x;
      }
      String action = (String) IgnoreCase.getIgnoreCase(attributes, "ACTIONCOMMAND");
      if (action != null) b.setActionCommand(action);
   }
   private static class CommandButton extends JButton
   {
      private Studio app;
      CommandButton(Map attributes)
      {
         app = (Studio) IgnoreCase.getIgnoreCase(attributes, "STUDIO");
         setButtonProperties(this, attributes);
      }
      public void addNotify()
      {
         app.getCommandTargetManager().add(new CommandSourceAdapter(this));
         super.addNotify();
      }
      public void removeNotify()
      {
         app.getCommandTargetManager().remove(new CommandSourceAdapter(this));
         super.removeNotify();
      }
   }
   private static class CheckBoxButton extends JCheckBox
   {
      private Studio app;
      CheckBoxButton(Map attributes)
      {
         app = (Studio) IgnoreCase.getIgnoreCase(attributes, "STUDIO");
         setButtonProperties(this, attributes);
      }
      public void addNotify()
      {
         app.getCommandTargetManager().add(new CommandSourceAdapter(this));
         super.addNotify();
      }
      public void removeNotify()
      {
         app.getCommandTargetManager().remove(new CommandSourceAdapter(this));
         super.removeNotify();
      }
   }
   
   private static class TestDataSource implements jas.hist.XYDataSource {
       private double[] x = new double[] {1, 2, 3, 4, 5};
       private double[] y = new double[] {10, 8, 6, 8, 10};
       
       public int getAxisType() {
           return jas.hist.DataSource.DOUBLE;
       }
       
       public double getMinusError(int index) {
           return 0;
       }
       
       public int getNPoints() {
           return x.length;
       }
       
       public double getPlusError(int index) {
           return 0;
       }
       
       public String getTitle() {
           return "TestPlot Title";
       }
       
       public double getX(int index) {
           return x[index];
       }
       
       public double getY(int index) {
           return y[index];
       }
       
   }
}

