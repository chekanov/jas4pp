import hep.aida.*;
import java.util.Random;

public class CreateAndFillTupleWithComplexStructure {
  
  public static void main(String[] argv) throws java.io.IOException {
    
    String columnString = "int event =0,  tracks =0; ITuple momentums  = { double px = .2, py = 3.,"+
      "px = 0., ITuple hits = {int x,y,z} }; float ipx, ipy, ipz";
    
    IAnalysisFactory analysisFactory = IAnalysisFactory.create();
    ITreeFactory treeFactory = analysisFactory.createTreeFactory();
    ITree tree = treeFactory.create("testTupleWithComplexStructure.aida","type=xml;compress=no");
    ITupleFactory tupleFactory = analysisFactory.createTupleFactory(tree);
    ITuple tuple = tupleFactory.create("tuple", "label",columnString,"");
    
    Random r = new Random();
    int events = 100;
    
    for ( int i=0; i<events; i++ ) {
      tuple.fill(0, i);
      
      int tracks = r.nextInt(10);
      tuple.fill(1,tracks);
      
      ITuple momentum = tuple.getTuple( 2 );
      
      for ( int j = 0; j<tracks; j++ ) {
        momentum.fill(0,r.nextGaussian());
        momentum.fill(1,r.nextGaussian());
        momentum.fill(2,r.nextGaussian());
        
        int nHits = r.nextInt(20);
        
        ITuple hits = momentum.getTuple( 3 );
        for ( int k = 0; k<nHits; k++ ) {
          hits.fill(0,r.nextInt(40));
          hits.fill(1,r.nextInt(40));
          hits.fill(2,r.nextInt(40));
          hits.addRow();
        } // end of hits loop
        momentum.addRow();
      }// end of tracks loop
      
      tuple.fill(3,r.nextGaussian());
      tuple.fill(4,r.nextGaussian());
      tuple.fill(5,r.nextGaussian());
      tuple.addRow();
    }//end of loop over events
    
    IHistogramFactory hf = analysisFactory.createHistogramFactory(tree);
    IHistogram1D pxHist = hf.createHistogram1D("pxHist",100,tuple.getTuple(3).columnMin(0),tuple.getTuple(3).columnMax(0));
    
    tuple.start();
    while ( tuple.next() ) {
      ITuple momTuple = (ITuple) tuple.getObject(3);
      momTuple.start();
      
      while ( momTuple.next() ) 
        pxHist.fill( momTuple.getDouble(0) );
    }
    
    
    IPlotter plotter = analysisFactory.createPlotterFactory().create("Plot");
    plotter.createRegions(1,1,0);
    plotter.region(0).plot(pxHist);
    plotter.show();
    
  }
}
