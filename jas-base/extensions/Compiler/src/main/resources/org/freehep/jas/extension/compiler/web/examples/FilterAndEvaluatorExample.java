import hep.aida.*;
import java.util.Random;

public class FilterAndEvaluatorExample
{
   public static void main(String[] argv)
   {
        IAnalysisFactory af = IAnalysisFactory.create();
        ITree tree = af.createTreeFactory().create();
        ITupleFactory tf = af.createTupleFactory( tree );
        IHistogramFactory hf = af.createHistogramFactory(tree);

        java.util.Random r = new java.util.Random();

        ITuple tuple = tf.create("TupleTreeName","Title: Test Tuple", "int n, double x, double y, double z");

        for ( int i = 0; i < 30; i++ ) {
            double v1 = r.nextDouble()*10.;
            double v2 = r.nextDouble()*10.;
            double v3 = 5.3*v1+2.1*v1*v2 + 10.7;
            tuple.fill(0,i);
            tuple.fill(1,v1);
            tuple.fill(2,v2);
            tuple.fill(3,v3);
            tuple.addRow();
        }

        // Create IFilter and initialize it to this ITuple
        IFilter filter = tf.createFilter("5.3*x+2.1*y*x + 10.7 > 50.");
        filter.initialize(tuple);

        // Create IEvaluator and initialize it to this ITuple
        IEvaluator evaluator = tf.createEvaluator("(1.5*x*x-5.2*y*x + 4*sin(y))/85");
        evaluator.initialize(tuple);

        // Example 1: Filter ITuple Data
        tuple.start();
        while (tuple.next()) {
            if (filter.accept()) {
                System.out.println(tuple.getInt(0) + "\t Row passed, evaluate: " + evaluator.evaluateDouble());
            } else {
                System.out.println(tuple.getInt(0) + "\t Row failed, do not evaluate");
            }                            
        }

        // Use IHistogramFactory to create two empty histograms
        IHistogram1D h1 = hf.createHistogram1D("hist-1", "Use All Data", 50, -10, 10);
        IHistogram1D h2 = hf.createHistogram1D("hist-2", "Use Filtered Data", 50, -10, 10);
 
        // Example 2: Fill histograms from ITuple
        tuple.project(h1, evaluator);
        tuple.project(h2, evaluator, filter);

        // Plot histograms
        IPlotter plotter = af.createPlotterFactory().create("Filter and Evaluator Example");
        plotter.createRegions(2,1,0);

        plotter.region(0).plot(h1);
        plotter.region(1).plot(h2);
        plotter.show();
   }
}