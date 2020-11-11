from hep.aida import *
from java.util import Random
from java.lang import Boolean

true = Boolean("true")
false = Boolean("false")

factory = IAnalysisFactory.create();
tree = factory.createTreeFactory().create();
hf = factory.createHistogramFactory(tree);

hf = factory.createHistogramFactory(tree);
h1 = hf.createHistogram1D("Histogram 1D",50,-3,3);

plotter = factory.createPlotterFactory().create("Style1D.py Plot");
plotter.region(0).plot(h1);
plotter.show();

style = plotter.region(0).style();

style.regionBoxStyle().backgroundStyle().setColor("255,255,104");
style.dataBoxStyle().backgroundStyle().setColor("204,255,255");
style.dataBoxStyle().borderStyle().setBorderType("shadow");
style.dataStyle().lineStyle().setVisible(false);
style.dataStyle().fillStyle().setVisible(false);
style.dataStyle().markerStyle().setVisible(true);
style.dataStyle().markerStyle().setShape("circle");

r = Random();
for i in range(100000) :
	h1.fill(r.nextGaussian());