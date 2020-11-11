import hep.aida.*;
import java.util.Random;
import org.freehep.application.*;
import org.freehep.application.studio.*;

       af = IAnalysisFactory.create();

       // Create a folder with two histograms in it. This is meant to emulate the
       // opening of a file.
       tree = af.createTreeFactory().create("","",false,false,"mountpoint=/Java Folder");
       hf = af.createHistogramFactory(tree);

       h1 = hf.createHistogram1D("hist1D","Example of Histogram1D",100, -5, 5);
       h2 = hf.createHistogram2D("hist2D","Example of Histogram2D",100, -5, 5,100, -5, 5);

       r = new Random();
       r1 = new Random();

       for ( int i = 0; i < 10000; i++ ) {
           h1.fill( r.nextGaussian() );
           h2.fill( r.nextGaussian(), r1.nextGaussian() );
       }

       // We will access now the above histograms using the 
       // JAS3 internal AIDA tree called "aidaMasterTree"

       aidaMasterTree = (ITree) ((Studio) Application.getApplication()).getLookup().lookup(ITree.class);

       hist1D = (IHistogram1D) aidaMasterTree.find("/Java Folder/hist1D");
       hist2D = (IHistogram2D) aidaMasterTree.find("/Java Folder/hist2D");

       plotter = af.createPlotterFactory().create("AccessDataOnMasterTree.java plot");
       plotter.createRegions(1,2,0);
       plotter.region(0).plot(hist1D);
       plotter.region(1).plot(hist2D);
       plotter.show();
