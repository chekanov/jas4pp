from hep.aida import *
from java.util import Random
from java.lang import Boolean

true = Boolean("true")

anFactory   = IAnalysisFactory.create();
tree = anFactory.createTreeFactory().create();
histFactory = anFactory.createHistogramFactory(tree);
tuplFactory = anFactory.createTupleFactory( tree );
funcFactory = anFactory.createFunctionFactory( tree );
fitFactory  = anFactory.createFitFactory();
fitter      = fitFactory.createFitter("chi2","jminuit");
    
hist  = histFactory.createHistogram2D("hist","Test Histogram",100,-5,15,50,-5,5);

r = Random();

for i in range(10000):
  x = r.nextGaussian()+5;
  if  r.nextDouble() > 0.8:
	  x = 2.5*r.nextGaussian()+5
  y = r.nextGaussian();
  hist.fill(x,y);


fitData = fitFactory.createFitData();
fitData.create2DConnection(hist);

func = funcFactory.createFunctionFromScript("twoDdistr",2,"N*(a*exp( -(x[0]-mu0)*(x[0]-mu0)/(2*s0*s0) )+(1-a)*exp( -(x[0]-mu1)*(x[0]-mu1)/(2*s1*s1) ))*exp( -(x[1]-mu2)*(x[1]-mu2)/(2*s2*s2) )","N,a,mu0,s0,mu1,s1,mu2,s2","",None);

initialPars = [ 1, 0.8, 5, 1, 5, 2, 0, 1];
func.setParameters( initialPars );

fitter.fitParameterSettings("mu2").setFixed(true);
fitter.fitParameterSettings("a").setBounds(0.5,0.9);
fitter.fitParameterSettings("a").setStepSize(0.001);
fitter.fitParameterSettings("s1").setBounds(2,4);
fitter.fitParameterSettings("s1").setStepSize(0.1);
fitter.setConstraint("s0 = s2");
fitter.setConstraint("mu0 = mu1");


fitResult = fitter.fit(fitData,func);

print "Chi2 = ",fitResult.quality()

fPars = fitResult.fittedParameters();
fParErrs = fitResult.errors();
fParNames = fitResult.fittedParameterNames();

for i in range(fitResult.fittedFunction().numberOfParameters()):
	print fParNames[i], " : ", fPars[i], " +- ", fParErrs[i]

plotter = anFactory.createPlotterFactory().create("ComplexFit.py plot");
plotter.destroyRegions();
plotter.createRegion(0,0,.66,1).plot(hist);
plotter.createRegion(.66,0,.33,.5).plot( histFactory.projectionX("projX",hist) );
plotter.createRegion(.66,.5,.33,.5).plot( histFactory.projectionY("projY",hist) );
plotter.show();


