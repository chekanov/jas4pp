package org.freehep.jas.extension.aida.flavor;

import java.awt.datatransfer.DataFlavor;

/**
 *
 * @author tonyj
 * @version $Id: HistogramDataFlavor.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class HistogramDataFlavor extends DataFlavor
{
   public HistogramDataFlavor()
   {
      super(DataFlavor.javaJVMLocalObjectMimeType + 
                  "; class=hep.aida.IBaseHistogram", 
                  "Histogram");
   }
}
