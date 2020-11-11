import hep.aida.*;
import java.util.Random;

public class CreateAndFitDataPointSet {

   public static void main(String[] argv) {    

      IAnalysisFactory     af     = IAnalysisFactory.create();
      ITree                tree   = af.createTreeFactory().create();
      IDataPointSetFactory dpsf   = af.createDataPointSetFactory(tree);
      IFunctionFactory     funcF  = af.createFunctionFactory(tree);
      IFitFactory          fitF   = af.createFitFactory();
      IFitter              fitter = fitF.createFitter("Chi2","jminuit");

      // Create a two dimensional IDataPointSet.
      IDataPointSet dataPointSet = dpsf.create("dataPointSet","two dimensional IDataPointSet",2);
      
      Random r = new Random();
      
      for (int i=0; i<20; i++) { 
          dataPointSet.addPoint();
          IDataPoint dp = dataPointSet.point(i);
          dp.coordinate(0).setValue(i);
          dp.coordinate(1).setValue(i+r.nextDouble()-0.5);
          dp.coordinate(1).setErrorPlus(1);
          dp.coordinate(1).setErrorMinus(1);
      }

      IPlotter plotter = af.createPlotterFactory().create("CreateAndFitDataPointSet.java plot");
      plotter.region(0).plot(dataPointSet);
      plotter.show();

      IFunction line = funcF.createFunctionByName("line","p1");

      IFitResult result = fitter.fit(dataPointSet,line);
      plotter.region(0).plot(result.fittedFunction());

      System.out.println("Chi2="+result.quality());   
   }
}