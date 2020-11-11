import hep.aida.*;
import java.util.Random;

public class ChainedAndFilteredTuplesExample
{
   public static void main(String[] argv)
   {
        IAnalysisFactory af = IAnalysisFactory.create();    
        ITree tree = af.createTreeFactory().create();
        ITupleFactory tf = af.createTupleFactory( tree );

        java.util.Random r = new java.util.Random();
        
        // Create and fill 4 different ITuples
        ITuple tup1 = tf.create("tup1","tup1","int n, double x");
        ITuple tup2 = tf.create("tup2","tup2","int n, double x");
        ITuple tup3 = tf.create("tup3","tup3","int n, double x");
        ITuple tup4 = tf.create("tup4","tup4","int n, double x");
        
        for ( int i = 0; i < 20; i++ ) {
            tup1.fill(0,i);
            tup2.fill(0,i+20);
            tup3.fill(0,i+40);
            tup4.fill(0,i+60);

            tup1.fill(1, r.nextDouble()*10.);
            tup2.fill(1, r.nextDouble()*10.);
            tup3.fill(1, r.nextDouble()*10.);
            tup4.fill(1, r.nextDouble()*10.);

            tup1.addRow();
            tup2.addRow();
            tup3.addRow();
            tup4.addRow();
        }

        // Create a chain
        ITuple[] set = new ITuple[] { tup1, tup2, tup3, tup4};
        ITuple chain = tf.createChained("ChainedTuple", "New Chained Tuple", set);

        chain.start();
        System.out.println("\n\nChained Tuple:");
        while (chain.next()) System.out.println(chain.getInt(0) + "\t" + chain.getDouble(1));


        // Create IFilter and filtered ITuple
        IFilter filter = tf.createFilter("n>14 && n<46");
        filter.initialize(chain);
        ITuple filteredTuple = tf.createFiltered("FilteredTuple", chain, filter);

        filteredTuple.start();
        System.out.println("\n\nFiltered Tuple:");
        while (filteredTuple.next()) System.out.println(filteredTuple.getInt(0) + 
                                                        "\t" + filteredTuple.getDouble(1));
   }
}