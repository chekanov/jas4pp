from hep.aida import *
from java.util import Random

anFactory = IAnalysisFactory.create()
tree = anFactory.createTreeFactory().create()
histFactory = anFactory.createHistogramFactory( tree );
tuplFactory = anFactory.createTupleFactory( tree );
funcFactory = anFactory.createFunctionFactory( tree );
fitFactory  = anFactory.createFitFactory();
fitter      = fitFactory.createFitter("chi2","jminuit");
    
hist  = histFactory.createHistogram1D("hist","Test Histogram",100,-5,5);

r = Random(1234);

for i in range(10000):
  x = r.nextGaussian();
  hist.fill(x);

fitData = fitFactory.createFitData();
fitData.create1DConnection(hist);

pars = [hist.maxBinHeight(),hist.mean(),hist.rms()];
fitResult = fitter.fit(fitData,"g",pars);

meanIndex = fitResult.fittedFunction().indexOfParameter("mean");
meanVal = fitResult.fittedParameters()[meanIndex];
meanErr = fitResult.errors()[meanIndex];
meanScan = fitter.createScan1D(fitData, fitResult.fittedFunction(),"mean",20, meanVal-3*meanErr, meanVal+3*meanErr);

sigmaIndex = fitResult.fittedFunction().indexOfParameter("sigma");
sigmaVal = fitResult.fittedParameters()[sigmaIndex];
sigmaErr = fitResult.errors()[sigmaIndex];
oneSigmaContour = fitter.createContour(fitData, fitResult, "mean", "sigma", 10, 1);
twoSigmaContour = fitter.createContour(fitData, fitResult, "mean", "sigma", 10, 2);

plotter = anFactory.createPlotterFactory().create("ScansAndContour.py plot");
plotter.destroyRegions();
plotter.createRegion(0,0,.66,1).plot(hist);
plotter.region(0).plot(fitResult.fittedFunction());
plotter.createRegion(.66,0,.33,.5).plot( meanScan );
plotter.createRegion(.66,.5,.33,.5).plot( twoSigmaContour );
plotter.region(2).plot( oneSigmaContour );
plotter.show();
