import hep.aida.*;
import java.util.Random;
import org.freehep.application.*;
import org.freehep.application.studio.*;

public class AccessDataOnMasterTree
{
   public static void main(String[] argv) throws java.io.IOException
   {

       IAnalysisFactory af = IAnalysisFactory.create();

       // Create a folder with two histograms in it. This is meant to emulate the
       // opening of a file.

       ITree tree = af.createTreeFactory().create("","",false,false,"mountpoint=/Java Folder");
       IHistogramFactory hf = af.createHistogramFactory(tree);

       IHistogram1D h1 = hf.createHistogram1D("hist1D","Example of Histogram1D",100, -5, 5);
       IHistogram2D h2 = hf.createHistogram2D("hist2D","Example of Histogram2D",100, -5, 5,100, -5, 5);

       Random r = new Random();
       Random r1 = new Random();

       for ( int i = 0; i < 10000; i++ ) {
           h1.fill( r.nextGaussian() );
           h2.fill( r.nextGaussian(), r1.nextGaussian() );
       }

       // We will access now the above histograms using the 
       // JAS3 internal AIDA tree called "aidaMasterTree"

       ITree aidaMasterTree = (ITree) ((Studio) Application.getApplication()).getLookup().lookup(ITree.class);

       IHistogram1D hist1D = (IHistogram1D) aidaMasterTree.find("/Java Folder/hist1D");
       IHistogram2D hist2D = (IHistogram2D) aidaMasterTree.find("/Java Folder/hist2D");

       IPlotter plotter = af.createPlotterFactory().create("AccessDataOnMasterTree.java plot");
       plotter.createRegions(1,2,0);
       plotter.region(0).plot(hist1D);
       plotter.region(1).plot(hist2D);
       plotter.show();
   }
}