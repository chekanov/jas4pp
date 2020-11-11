import hep.aida.*;
import java.util.Random;

public class Tuple
{
   public static void main(String[] argv)
   {
      
      IAnalysisFactory af = IAnalysisFactory.create();
      ITree tree = af.createTreeFactory().create();
      ITupleFactory tf = af.createTupleFactory(tree);
      
      String[] columnNames  = { "iFlat = 0", " fGauss = 3.", " fFlat =-2." };
      Class[] columnClasses = { Integer.TYPE, Float.TYPE, Float.TYPE };
      
      ITuple tuple = tf.create( "tuple", "tupleLabel", columnNames, columnClasses, "");

      Random r = new Random();
      for (int i=0; i<10000; i++)
      {
         tuple.fill(0, r.nextInt(20) );
         tuple.fill(1, (float)r.nextGaussian() );
         tuple.fill(2, r.nextFloat() );
         tuple.addRow();
      }
      
      int colG = tuple.findColumn("fGauss");
      int colF = tuple.findColumn("fFlat");
      int colI = tuple.findColumn("iFlat");
      
      IHistogramFactory hf = af.createHistogramFactory(tree);
      IHistogram1D h1dI = hf.createHistogram1D("h1dI",50,tuple.columnMin(colI),tuple.columnMax(colI));
      IHistogram1D h1dF = hf.createHistogram1D("h1dF",50,tuple.columnMin(colF),tuple.columnMax(colF));
      IHistogram1D h1dG = hf.createHistogram1D("h1dG",50,tuple.columnMin(colG),tuple.columnMax(colG));
      IHistogram2D h2d = hf.createHistogram2D("h2d",50,tuple.columnMin(colG),tuple.columnMax(colG),
      50,tuple.columnMin(colF),tuple.columnMax(colF));
      
      tuple.start();
      while ( tuple.next() )
      {
         h1dI.fill( tuple.getInt(colI) );
         h1dF.fill( tuple.getFloat(colF) );
         h1dG.fill( tuple.getFloat(colG) );
         h2d.fill( tuple.getFloat(colG), tuple.getFloat(colF) );
      }
      
      IPlotter plotter = af.createPlotterFactory().create("Tuple.java plot");
      plotter.createRegions(2,2,0);
      plotter.region(0).plot(h1dI);
      plotter.region(1).plot(h1dF);
      plotter.region(2).plot(h1dG);
      plotter.region(3).plot(h2d);
      plotter.show();
   }
}
