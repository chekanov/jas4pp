import hep.aida.*;
import java.util.Random;

      af     = IAnalysisFactory.create();
      tree   = af.createTreeFactory().create();
      dpsf   = af.createDataPointSetFactory(tree);
      funcF  = af.createFunctionFactory(tree);
      fitF   = af.createFitFactory();
      fitter = fitF.createFitter("Chi2","jminuit");

      // Create a two dimensional IDataPointSet.
      dataPointSet = dpsf.create("dataPointSet","two dimensional IDataPointSet",2);
      
      r = new Random();
      
      for (int i=0; i<20; i++) { 
          dataPointSet.addPoint();
          dp = dataPointSet.point(i);
          dp.coordinate(0).setValue(i);
          dp.coordinate(1).setValue(i+r.nextDouble()-0.5);
          dp.coordinate(1).setErrorPlus(1);
          dp.coordinate(1).setErrorMinus(1);
      }

      plotter = af.createPlotterFactory().create("CreateAndFitDataPointSet.java plot");
      plotter.region(0).plot(dataPointSet);
      plotter.show();

      line = funcF.createFunctionByName("line","p1");

      result = fitter.fit(dataPointSet,line);
      plotter.region(0).plot(result.fittedFunction());

      System.out.println("Chi2="+result.quality());   
