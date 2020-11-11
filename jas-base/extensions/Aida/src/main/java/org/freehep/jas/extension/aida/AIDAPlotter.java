package org.freehep.jas.extension.aida;

import hep.aida.IPlotterRegion;
import hep.aida.IPlotter;
import hep.aida.ref.plotter.DummyPlotter;
import java.awt.Dimension;
import java.io.IOException;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.freehep.application.PropertyUtilities;
import org.freehep.jas.services.PlotPage;
import org.freehep.jas.services.PlotRegion;
import org.freehep.jas.services.PlotFactory;

/**
 *
 * @author tonyj
 */
public class AIDAPlotter extends DummyPlotter implements Runnable
{
   private PlotFactory factory;
   private PlotPage page;
   private boolean show = false;
   private boolean isShowing = false;

   public AIDAPlotter(PlotFactory factory, PlotPage page)
   {
      this.factory = factory;
      this.page = page;
      destroyRegions();
      createRegion();
   }
   public void show()
   {
      show = true;
      //SwingUtilities.invokeLater(this);
      invokeOnSwingThread(this);
   }
   public void hide()
   {
      show=false;
      SwingUtilities.invokeLater(this);
   }
   public void run()
   {
      if (show != isShowing)
      {
         if (show)
         {
            page.showPage();
         }
         else
         {
            page.hidePage();
         }
         isShowing = show;
      }
      else if (show) page.showPage();
   }
   protected IPlotterRegion justCreateRegion(double x, double y, double width, double height)
   {
      CreateRegion cr = new CreateRegion(x,y,width,height,this);
      invokeOnSwingThread(cr);
      return cr.getRegion();
   }
   public void destroyRegions()
   {
      invokeOnSwingThread(new DestroyRegions());
      super.destroyRegions();
   }

   public void writeToFile(String file, String type) throws IOException {
       writeToFile(file, type, System.getProperties());
   }

   public boolean isShowing() {
        return isShowing;
    }
    
    public JPanel panel() {
        return (JPanel)page;
    }

    private class CreateRegion implements Runnable
   {
      private double x;
      private double y;
      private double width;
      private double height;
      private IPlotterRegion region;
      private IPlotter plotter;
      
      CreateRegion(double x, double y, double width, double height, IPlotter plotter)
      {
         this.x = x;
         this.y = y;
         this.width = width;
         this.height = height;
         this.plotter = plotter;
      }
      public void run()
      {
         PlotRegion pg = page.createRegion(x,y,width,height);
         region = new AIDARegion(factory,pg,plotter);
      }
      IPlotterRegion getRegion()
      {
         return region;
      }
   }
   private class DestroyRegions implements Runnable
   {
      public void run()
      {
         page.clearRegions();
      }
   }
   
}