from hep.aida import *
from java.util import Random

af   = IAnalysisFactory.create();
tree = af.createTreeFactory().create();
hf   = af.createHistogramFactory(tree);

h1 = hf.createHistogram1D("Histogram 1D",50,-3,3)

r = Random()


for i in range(100000):
    h1.fill(r.nextGaussian())
    h1.fill(r.nextDouble()*10-5)

functionfact = af.createFunctionFactory(tree)
gauss = functionfact.createFunctionFromScript("gauss",1,"background+a*exp(-(x[0]-mean)*(x[0]-mean)/sigma/sigma)","a,mean,sigma,background","A Gaussian")
gauss.setParameter("a",h1.maxBinHeight())
gauss.setParameter("mean",h1.mean())
gauss.setParameter("sigma",h1.rms())

plotter = af.createPlotterFactory().create("Fit.py Plot")
plotter.region(0).plot(h1)
plotter.region(0).plot(gauss)


ff = af.createFitFactory()
jminuit = ff.createFitter("Chi2","jminuit")
jminuitResult = jminuit.fit(h1,gauss)
plotter.region(0).plot(jminuitResult.fittedFunction())
plotter.show()

functionfact.cloneFunction("fitted gauss (jminuit)",jminuitResult.fittedFunction())


print "jminuit Chi2 = ", jminuitResult.quality()
