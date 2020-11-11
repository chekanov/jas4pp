from hep.aida import *
from java.util import Random

af   = IAnalysisFactory.create();
tree = af.createTreeFactory().create();
hf   = af.createHistogramFactory(tree);

r = Random(12345)
      
h1 = hf.createHistogram1D("test 1d",50,-3,6);
h2 = hf.createHistogram1D("test 2d",50,-3,6);
      
for i in range(10000):
   h1.fill(r.nextGaussian());
   h2.fill(3+r.nextGaussian());

plus = hf.add("h1+h2",h1,h2);
minus = hf.subtract("h1-h2",h1,h2);
mul = hf.multiply("h1*h2",h1,h2);
div = hf.divide("h1 divded by h2",h1,h2);
      
plotter = af.createPlotterFactory().create("HistogramArithmetic.py plot");
plotter.createRegions(2,2,0);
plotter.region(0).plot(plus);
plotter.region(1).plot(minus);
plotter.region(2).plot(mul);
plotter.region(3).plot(div);
plotter.show();
