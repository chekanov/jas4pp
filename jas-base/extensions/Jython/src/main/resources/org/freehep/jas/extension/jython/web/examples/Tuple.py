from hep.aida import *
from java.util import Random
from java.lang import *

af   = IAnalysisFactory.create();
tree = af.createTreeFactory().create();
hf   = af.createHistogramFactory(tree);
tf   = af.createTupleFactory(tree);

r = Random()
      
columnNames  = [ "iFlat = 0", " fGauss = 3.", " fFlat =-2." ]
columnClasses = [ Integer.TYPE, Float.TYPE, Float.TYPE ]
      
tuple = tf.create( "tuple", "tupleLabel", columnNames, columnClasses, "");
      
for i in range(10000):
    tuple.fill(0, Integer( r.nextInt(20) ) );
    tuple.fill(1, Float( r.nextGaussian() ) );
    tuple.fill(2, Float( r.nextFloat() ) );
    tuple.addRow();
      
colG = tuple.findColumn("fGauss");
colF = tuple.findColumn("fFlat");
colI = tuple.findColumn("iFlat");
      
h1dI = hf.createHistogram1D("h1dI",50,tuple.columnMin(colI),tuple.columnMax(colI));
h1dF = hf.createHistogram1D("h1dF",50,tuple.columnMin(colF),tuple.columnMax(colF));
h1dG = hf.createHistogram1D("h1dG",50,tuple.columnMin(colG),tuple.columnMax(colG));
h2d = hf.createHistogram2D("h2d",50,tuple.columnMin(colG),tuple.columnMax(colG),
      50,tuple.columnMin(colF),tuple.columnMax(colF));
      
tuple.start();
while tuple.next() :
   h1dI.fill( tuple.getInt(colI) );
   h1dF.fill( tuple.getFloat(colF) );
   h1dG.fill( tuple.getFloat(colG) );
   h2d.fill( tuple.getFloat(colG), tuple.getFloat(colF) );

      
plotter = af.createPlotterFactory().create("Tuple.pnut plot");
plotter.createRegions(2,2,0);
plotter.region(0).plot(h1dI);
plotter.region(1).plot(h1dF);
plotter.region(2).plot(h1dG);
plotter.region(3).plot(h2d);
plotter.show();