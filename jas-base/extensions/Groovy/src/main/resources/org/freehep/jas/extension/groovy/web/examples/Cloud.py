from hep.aida import *
from java.util import Random

af   = IAnalysisFactory.create();
tree = af.createTreeFactory().create();
hf   = af.createHistogramFactory(tree);

r = Random()
      
cl1D = hf.createCloud1D( "cl1D", "1-Dimensional Cloud", 1500, "" );
cl2D = hf.createCloud2D( "cl2D", "2-Dimensional Cloud", 1500, "" );
      
entries = 20000;
      
for i in range(20000):
   xval = r.nextGaussian();
   yval = r.nextGaussian();
   w    = r.nextDouble();
   cl1D.fill( xval, w );
   cl2D.fill( xval, yval, w );

      
plotter = af.createPlotterFactory().create("Cloud.py plot");
plotter.createRegions(1,2,0);
plotter.region(0).plot(cl1D);
plotter.region(1).plot(cl2D);
plotter.show();