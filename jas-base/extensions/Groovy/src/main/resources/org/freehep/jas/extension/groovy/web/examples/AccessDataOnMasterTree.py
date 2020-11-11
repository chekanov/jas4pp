from hep.aida import *
from java.util import Random
from java.lang import Boolean

true = Boolean("true")
false = Boolean("false")

af   = IAnalysisFactory.create();

# Create a folder with two histograms in it. This is meant to emulate the
# opening of a file.

tree = af.createTreeFactory().create("","",false,false,"mountpoint=/Python Folder")
hf = af.createHistogramFactory(tree)

h1 = hf.createHistogram1D("hist1D","Example of Histogram1D",100, -5, 5)
h2 = hf.createHistogram2D("hist2D","Example of Histogram2D",100, -5, 5,100, -5, 5)

r =  Random()
r1 = Random()

for i in range(10000):
      h1.fill( r.nextGaussian() );
      h2.fill( r.nextGaussian(), r1.nextGaussian() );

# We will access now the above histograms using the 
# JAS3 internal AIDA tree called "aidaMasterTree"

hist1D = aidaMasterTree.find("/Python Folder/hist1D");
hist2D = aidaMasterTree.find("/Python Folder/hist2D");

plotter = af.createPlotterFactory().create("AccessDataOnMasterTree.py plot");
plotter.createRegions(1,2,0);
plotter.region(0).plot(hist1D);
plotter.region(1).plot(hist2D);
plotter.show();