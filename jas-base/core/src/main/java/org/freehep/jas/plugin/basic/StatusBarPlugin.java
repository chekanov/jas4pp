package org.freehep.jas.plugin.basic;

import javax.swing.BorderFactory;
import org.freehep.application.ProgressMeter;
import org.freehep.application.StatusBar;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.jas.services.ProgressMeterProvider;

/**
 *
 * @author tonyj
 */
public class StatusBarPlugin extends Plugin implements ProgressMeterProvider
{
   protected void init() throws org.xml.sax.SAXException, java.io.IOException
   {
      final Studio app = getApplication();
      MemoryButton mb = new MemoryButton();
      mb.setAlignmentX(0.95f);
      mb.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
      app.getStatusBar().add(mb);
      app.getLookup().add(this);
   }

   public void freeProgressMeter(ProgressMeter meter)
   {
      final StatusBar bar = getApplication().getStatusBar();
      bar.remove(meter);
      bar.revalidate();
   }
   
   public ProgressMeter getProgressMeter()
   {
      final StatusBar bar = getApplication().getStatusBar();
      ProgressMeter meter = new ProgressMeter();
      bar.add(meter);
      bar.revalidate();
      return meter;
   }
}