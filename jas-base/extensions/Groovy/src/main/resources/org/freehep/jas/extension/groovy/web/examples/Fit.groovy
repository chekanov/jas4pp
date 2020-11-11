import hep.aida.*;
import java.util.Random;

      // Create factories
      analysisFactory = IAnalysisFactory.create();
      treeFactory = analysisFactory.createTreeFactory();
      tree = treeFactory.create();
      plotter = analysisFactory.createPlotterFactory().create("Fit.java Plot");
      histogramFactory = analysisFactory.createHistogramFactory(tree);
      functionFactory = analysisFactory.createFunctionFactory(tree);
      fitFactory = analysisFactory.createFitFactory();
    
      h1 = histogramFactory.createHistogram1D("Histogram 1D",50,-3,3);

      r = new Random();

      for (int i=0; i<100000; i++) {
          h1.fill(r.nextGaussian());
          h1.fill(r.nextDouble()*10-5);
      }

      gauss = functionFactory.createFunctionFromScript("gauss",1,"background+a*exp(-(x[0]-mean)*(x[0]-mean)/sigma/sigma)","a,mean,sigma,background","A Gaussian");

      gauss.setParameter("a",h1.maxBinHeight());
      gauss.setParameter("mean",h1.mean());
      gauss.setParameter("sigma",h1.rms());

      plotter.region(0).plot(h1);
      plotter.region(0).plot(gauss);


      jminuit = fitFactory.createFitter("Chi2","jminuit");

      jminuitResult = jminuit.fit(h1,gauss);

      plotter.region(0).plot(jminuitResult.fittedFunction());
      plotter.show();

      functionFactory.cloneFunction("fitted gauss (jminuit)",jminuitResult.fittedFunction());
      
      System.out.println("jminuit Chi2="+jminuitResult.quality());
      
