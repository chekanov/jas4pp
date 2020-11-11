import hep.aida.*;
import java.util.Random;

public class CreateAndPlotHistograms
{
   public static void main(String[] argv)
   {
      IAnalysisFactory af = IAnalysisFactory.create();
      ITree tree = af.createTreeFactory().create();
      IHistogramFactory hf = af.createHistogramFactory(tree);
      
      tree.mkdir("/Histograms");
      tree.cd("/Histograms");

      IHistogram1D h1 = hf.createHistogram1D("Histogram 1D",50,-3,3);
      IHistogram2D h2 = hf.createHistogram2D("Histogram 2D",40,-3,3,40,-3,3);

      tree.mkdir("/Clouds");
      tree.cd("/Clouds");

      ICloud1D c1 = hf.createCloud1D("Cloud 1D");
      ICloud2D c2 = hf.createCloud2D("Cloud 2D");

      IPlotter plotter = af.createPlotterFactory().create("CreateAndPlotHistograms.java plot");

      plotter.show();
      plotter.createRegions(2,2);

      plotter.region(0).plot(h1);
      plotter.region(1).plot(h2);
      plotter.region(2).plot(c1);
      plotter.region(3).plot(c2);

      Random r = new Random();

      for (int i = 0; i < 100000; i++ ) {
          h1.fill(r.nextGaussian());
          h2.fill(r.nextGaussian(),r.nextGaussian());
          c1.fill(r.nextGaussian());
          c2.fill(r.nextGaussian(),r.nextGaussian());
      }
   }
}