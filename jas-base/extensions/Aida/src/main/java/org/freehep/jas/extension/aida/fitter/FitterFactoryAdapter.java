package org.freehep.jas.extension.aida.fitter;

import hep.aida.ext.IOptimizerFactory;
import jas.hist.FitterFactory;
import jas.hist.FitterRegistry;
import java.util.Iterator;
import org.openide.util.Lookup;

/**
 *
 * @author tonyj
 */
public class FitterFactoryAdapter implements FitterFactory
{
   private String name;
   
   private FitterFactoryAdapter(String name)
   {
      this.name = name;
   }
   public jas.hist.Fitter createFitter()
   {
      return new FitterAdapter(name);
   }
   
   public String getFitterName()
   {
      return name;
   }
   public String toString()
   {
      return name;
   }
   public static void registerFitters(Lookup lookup)
   {
      Lookup.Template template = new Lookup.Template(IOptimizerFactory.class);
      Lookup.Result result = lookup.lookup(template);
      for (Iterator iter = result.allInstances().iterator(); iter.hasNext(); )
      {
         IOptimizerFactory opt = (IOptimizerFactory) iter.next();
         String[] names = opt.optimizerFactoryNames();
         FitterRegistry.instance().registerFitter(new FitterFactoryAdapter(names[0]));
      }
      // ToDo: We should listen for changes to the result, but this is not working reliably.
   }
}
