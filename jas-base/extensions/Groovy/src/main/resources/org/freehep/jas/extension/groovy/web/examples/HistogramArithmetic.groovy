import hep.aida.*;
import java.util.Random;

      af = IAnalysisFactory.create();
      hf = af.createHistogramFactory(af.createTreeFactory().create());
      
      h1 = hf.createHistogram1D("test 1d",50,-3,6);
      h2 = hf.createHistogram1D("test 2d",50,-3,6);
      
      r = new Random(12345);
      for (int i=0; i<10000; i++) 
      {
         h1.fill(r.nextGaussian());
         h2.fill(3+r.nextGaussian());
      }
      plus = hf.add("h1+h2",h1,h2);
      minus = hf.subtract("h1-h2",h1,h2);
      mul = hf.multiply("h1*h2",h1,h2);
      div = hf.divide("h1 over h2",h1,h2);
      
      plotter = af.createPlotterFactory().create("HistogramArithmetic.java plot");
      plotter.createRegions(2,2,0);
      plotter.region(0).plot(plus);
      plotter.region(1).plot(minus);
      plotter.region(2).plot(mul);
      plotter.region(3).plot(div);
      plotter.show();
