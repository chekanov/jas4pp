import hep.aida.*;
import java.util.Random;

public class Fit
{
   public static void main(String[] args)
   {
      // Create factories
      IAnalysisFactory  analysisFactory = IAnalysisFactory.create();
      ITreeFactory      treeFactory = analysisFactory.createTreeFactory();
      ITree             tree = treeFactory.create();
      IPlotter          plotter = analysisFactory.createPlotterFactory().create("Fit.java Plot");
      IHistogramFactory histogramFactory = analysisFactory.createHistogramFactory(tree);
      IFunctionFactory  functionFactory = analysisFactory.createFunctionFactory(tree);
      IFitFactory       fitFactory = analysisFactory.createFitFactory();
    
      IHistogram1D h1 = histogramFactory.createHistogram1D("Histogram 1D",50,-3,3);

      Random r = new Random();

      for (int i=0; i<100000; i++) {
          h1.fill(r.nextGaussian());
          h1.fill(r.nextDouble()*10-5);
      }

      IFunction gauss = functionFactory.createFunctionFromScript("gauss",1,"background+a*exp(-(x[0]-mean)*(x[0]-mean)/sigma/sigma)","a,mean,sigma,background","A Gaussian");

      gauss.setParameter("a",h1.maxBinHeight());
      gauss.setParameter("mean",h1.mean());
      gauss.setParameter("sigma",h1.rms());

      plotter.region(0).plot(h1);
      plotter.region(0).plot(gauss);


      IFitter jminuit = fitFactory.createFitter("Chi2","jminuit");

      IFitResult jminuitResult = jminuit.fit(h1,gauss);

      plotter.region(0).plot(jminuitResult.fittedFunction());
      plotter.show();

      functionFactory.cloneFunction("fitted gauss (jminuit)",jminuitResult.fittedFunction());
      
      System.out.println("jminuit Chi2="+jminuitResult.quality());
      
   }
}