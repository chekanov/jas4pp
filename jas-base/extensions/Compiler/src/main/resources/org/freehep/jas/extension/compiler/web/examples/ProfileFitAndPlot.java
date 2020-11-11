import hep.aida.*;
import java.util.*;

public class ProfileFitAndPlot
{
   public static void main(String[] argv)
   {    
      IAnalysisFactory af = IAnalysisFactory.create();
      ITree tree = af.createTreeFactory().create();
      IHistogramFactory hf = af.createHistogramFactory(tree);
      
      // Create 1D and 2D IProfile with fixed bin width
      IProfile1D prof1DFixedBinWidth = hf.createProfile1D("prof1DFixedBinWidth","Fixed bin width 1D", 10, 0, 1);
      IProfile2D prof2DFixedBinWidth = hf.createProfile2D("prof2DFixedBinWidth","Fixed bin width 2D", 10, 0, 1, 10, -5, 5);

      double[] xBinEdges = {0,0.1,0.21,0.35,0.48,0.52,0.65,0.75,0.83,0.94,1.0};
      double[] yBinEdges = {-5.0,-4.1,-3.2,-2.0,-1.1,-0.4,1.2,2.3,3.5,4.2,5.0};

      // Create 1D and 2D IProfile with variable bin width
      IProfile1D prof1DVariableBinWidth = hf.createProfile1D("prof1DVariableBinWidth", "Variable bin width 1D", xBinEdges);
      IProfile2D prof2DVariableBinWidth = hf.createProfile2D("prof2DVariableBinWidth", "Variable bin width 2D", xBinEdges, yBinEdges);

      Random r = new Random();

      for ( int i = 0; i < 100; i++ ) {
        double x = r.nextDouble();
        double y = x + 0.1*r.nextGaussian();

        // Fill the IProfiles with default weight.
        prof1DFixedBinWidth.fill(x,y);
        prof2DFixedBinWidth.fill(x,y, r.nextDouble());

        prof1DVariableBinWidth.fill(x,y);
        prof2DVariableBinWidth.fill(x,y, r.nextDouble());
      }

      // Create the Fitter and fit the profiles
      IFitFactory  fitFactory = af.createFitFactory();
      IFitter      fitter     = fitFactory.createFitter("Chi2","jminuit");

      // Perform the fits
      IFitResult resProf1DFix = fitter.fit(prof1DFixedBinWidth,"p1");

      IFitResult resProf1DVar = fitter.fit(prof1DVariableBinWidth,"p1");

      // Display the results
      IPlotter plotter = af.createPlotterFactory().create("Fit and Plot an IProfile");
      plotter.createRegions(2,1);
      plotter.region(0).plot( prof1DFixedBinWidth );
      plotter.region(0).plot( resProf1DFix.fittedFunction() );
      plotter.region(1).plot( prof1DVariableBinWidth );
      plotter.region(1).plot( resProf1DVar.fittedFunction() );
      plotter.show();

   }
}