import hep.aida.*;
import java.util.Random;

public class Histogram 
{
   public static void main(String[] argv)
   {
      IAnalysisFactory af = IAnalysisFactory.create();
      IHistogramFactory hf = af.createHistogramFactory(af.createTreeFactory().create());
      
      IHistogram1D h1d = hf.createHistogram1D("test 1d",50,-3,3);
      IHistogram2D h2d = hf.createHistogram2D("test 2d",50,-3,3,50,-3,3);
      
      Random r = new Random();
      for (int i=0; i<10000; i++) 
      {
         h1d.fill(r.nextGaussian());
         h2d.fill(r.nextGaussian(),r.nextGaussian());
      }
      
      IPlotter plotter = af.createPlotterFactory().create("Plot");
      plotter.createRegions(1,2,0);
      plotter.region(0).plot(h1d);
      plotter.region(1).plot(h2d);
      plotter.show();
   }
}