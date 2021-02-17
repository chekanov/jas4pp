package rootio;


import hep.io.root.RootClassNotFound;
import hep.io.root.RootFileReader;
import hep.io.root.daemon.RootAuthenticator;
import hep.io.root.daemon.RootURLStreamFactory;
import hep.io.root.interfaces.*;
import jas.hist.DataSource;
import jas.hist.JASHist;
import java.io.*;
import java.util.List;



/**
 * Read ROOt files 
 */
public class Main
{
  
/*	
   public static void main(String[] args) throws IOException 
   {

        System.setProperty("useNIO","true");
        System.setProperty("debugRoot","true"); 
        FileRoot reader = new FileRoot("Example.root");
        System.out.println(reader.toString());
 
  }

*/

 public static void main(String[] args)   throws java.io.IOException, RootClassNotFound
   {


       // System.setProperty("debugRoot", "true");
       // System.setProperty("jasminPath", "jasmin");
        RootFileReader reader = new RootFileReader("Example.root");

        //TH1F histo = (TH1F)reader.get("totalHistogram");
        //TH1F histo = (TH1F)reader.get("jjlmass");
        //System.out.println("Nr of entries="+Double.toString(histo.getEntries()));

         // histograms works for version 3. Need to fix for version 6 
        //TH1F histo = (TH1F)reader.get("totalHistogram");
        //TH1F histo = (TH1F)reader.get("h14");
        //System.out.println("Nr of entries="+Double.toString(histo.getEntries()));

        TTree tree = (TTree)reader.get("tree");
        List leaves = (List)tree.getLeaves();

        long maxevents=tree.getEntries(); 
        System.out.println(maxevents); 

        TLeafI leaf0 = (TLeafI)leaves.get(0);
        TLeafF leaf1 = (TLeafF)leaves.get(1);
        TLeafC leaf2 = (TLeafC)leaves.get(2);

        System.out.println(leaf0.getName()+" "+leaf1.getName()+" "+leaf2.getName());

        for (int i=0; i<tree.getEntries(); i++){
          System.out.println(leaf0.getValue(i)); 
        };
/*
        assertEquals(leaf0.getName(), "one");
        assertEquals(leaf1.getName(), "two");
        assertEquals(leaf2.getName(), "three");

        assertEquals(leaf0.getValue(0), 1);
        assertEquals(leaf1.getValue(0), 1.1, 1e-6);
        assertEquals(leaf2.getValue(0), "uno");

        assertEquals(leaf0.getValue(1), 2);
        assertEquals(leaf1.getValue(1), 2.2, 1e-6);
        assertEquals(leaf2.getValue(1), "dos");

        assertEquals(leaf0.getValue(2), 3);
        assertEquals(leaf1.getValue(2), 3.3, 1e-6);
        assertEquals(leaf2.getValue(2), "tres");
*/

}


}
  
