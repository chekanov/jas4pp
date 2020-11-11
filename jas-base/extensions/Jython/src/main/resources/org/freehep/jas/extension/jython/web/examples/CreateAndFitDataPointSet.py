from hep.aida import *
from java.util import Random

af   = IAnalysisFactory.create();
tree = af.createTreeFactory().create();
hf   = af.createHistogramFactory(tree);

r = Random()

dpf = af.createDataPointSetFactory(tree)
dataPointSet = dpf.create("dataPointSet","two dimensional IDataPointSet",2)

for i in range(20):
    dataPointSet.addPoint()
    dp = dataPointSet.point(i)
    dp.coordinate(0).setValue(i)
    dp.coordinate(1).setValue(i+r.nextDouble()-0.5)
    dp.coordinate(1).setErrorPlus(1)
    dp.coordinate(1).setErrorMinus(1)

plotter = af.createPlotterFactory().create("CreateAndFitDataPointSet.pnut plot")
plotter.region(0).plot(dataPointSet)
plotter.show()

functionfact = af.createFunctionFactory(tree)
line = functionfact.createFunctionByName("line","p1")

ff = af.createFitFactory()
fitter = ff.createFitter("Chi2","jminuit")
result = fitter.fit(dataPointSet,line)
plotter.region(0).plot(result.fittedFunction())

print "Chi2 = ", result.quality()
