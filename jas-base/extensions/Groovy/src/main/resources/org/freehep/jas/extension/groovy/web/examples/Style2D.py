from hep.aida import *
from java.util import Random
from java.lang import Boolean

true = Boolean("true")
false = Boolean("false")

factory = IAnalysisFactory.create();
tree = factory.createTreeFactory().create();
hf = factory.createHistogramFactory(tree);

r = Random();

plotter = factory.createPlotterFactory().create("Style2D.py Plot");
plotter.createRegions(2,2);
plotter.show();

h2 = hf.createHistogram2D("Histogram 2D",40,-3,3,40,-3,3);
c2 = hf.createCloud2D("Cloud 2D");

plotter.region(0).setTitle("Histogram 2D");
plotter.region(0).style().regionBoxStyle().backgroundStyle().setColor("yellow");
plotter.region(0).style().regionBoxStyle().foregroundStyle().setColor("green");
plotter.region(0).style().setParameter("hist2DStyle","ellipse");
plotter.region(0).style().dataStyle().markerStyle().setColor("blue");
plotter.region(0).plot(h2);

plotter.region(1).setTitle("Color Map");
plotter.region(1).style().statisticsBoxStyle().setVisible(false);
plotter.region(1).style().setParameter("hist2DStyle","colorMap");
plotter.region(1).style().dataStyle().fillStyle().setParameter("colorMapScheme","rainbow");
plotter.region(1).plot(h2);


plotter.region(2).setTitle("Cloud 2D");
plotter.region(2).style().dataStyle().markerStyle().setColor("red");
plotter.region(2).style().dataStyle().markerStyle().setShape("1");
plotter.region(2).style().dataStyle().markerStyle().setParameter("size","7");
plotter.region(2).plot(c2);

plotter.region(3).setTitle("Binned Cloud 2D");
plotter.region(3).style().setParameter("showAsScatterPlot","false");
plotter.region(3).plot(c2);

for i in range(10000) :
	h2.fill(r.nextGaussian(),r.nextGaussian());
	c2.fill(r.nextGaussian(),r.nextGaussian());


