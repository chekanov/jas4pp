from hep.aida import *
from java.util import Random

factory = IAnalysisFactory.create();
tree = factory.createTreeFactory().create();
hf = factory.createHistogramFactory(tree);

tree.mkdir("/Histograms");
tree.cd("/Histograms");

h1 = hf.createHistogram1D("Histogram 1D",50,-3,3);
h2 = hf.createHistogram2D("Histogram 2D",40,-3,3,40,-3,3);

tree.mkdir("/Clouds");
tree.cd("/Clouds");

c1 = hf.createCloud1D("Cloud 1D");
c2 = hf.createCloud2D("Cloud 2D");

plotter = factory.createPlotterFactory().create("CreateAndPlotHistograms.py plot");

plotter.show();
plotter.createRegions(2,2);

plotter.region(0).plot(h1);
plotter.region(1).plot(h2);
plotter.region(2).plot(c1);
plotter.region(3).plot(c2);

r = Random()

for i in range(100000):
    h1.fill(r.nextGaussian())
    h2.fill(r.nextGaussian(),r.nextGaussian())
    c1.fill(r.nextGaussian())
    c2.fill(r.nextGaussian(),r.nextGaussian())
