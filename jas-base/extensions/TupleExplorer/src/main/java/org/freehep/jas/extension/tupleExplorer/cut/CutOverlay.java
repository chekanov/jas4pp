package org.freehep.jas.extension.tupleExplorer.cut;

import jas.hist.CustomOverlay;
import jas.hist.DataSource;
import jas.hist.OverlayWithHandles;
import jas.plot.DoubleCoordinateTransformation;

/**
 *
 * @author tonyj
 * @version $Id: CutOverlay.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public class CutOverlay extends OverlayWithHandles implements CustomOverlay 
{
   /** Creates new CutOverlay */
    public CutOverlay(CutDataSource ds) 
    {
       super(ds);
       this.ds = ds;
    }
    public void containerNotify(jas.plot.OverlayContainer overlayContainer)
    {
    }
    
    public void setDataSource(jas.hist.DataSource dataSource)
    {
    }
    
    public void paint(jas.plot.PlotGraphics g, boolean isPrinting)
    {
		DoubleCoordinateTransformation xt = (DoubleCoordinateTransformation) container.getXTransformation();
		DoubleCoordinateTransformation yt = (DoubleCoordinateTransformation) container.getYTransformation();
		Numeric1DCut cut = ds.getCut();
      
		g.setTransformation(xt,yt);
		g.setColor(java.awt.Color.yellow);
		
 		if ( cut.getNCutVariables() == 2) 
 		    g.drawRect(cut.getCutVariable(0).getValue(),yt.getPlotMin(),cut.getCutVariable(1).getValue(),yt.getPlotMax()); 
    }
    private CutDataSource ds;
}
class CutDataSource implements DataSource
{
   CutDataSource(Numeric1DCut cut)
   {
   }
   public String getTitle()
   {
      return "Cut";
   }
   Numeric1DCut getCut()
   {
      return cut;
   }
   private Numeric1DCut cut;
}
