package rootio;


import hep.io.root.RootClassNotFound;
import hep.io.root.RootFileReader;
import hep.io.root.interfaces.*;
import java.util.List;
import java.util.ArrayList;
import hep.io.root.core.RootInput;


/**
 * Get arrays of values from Delphes branches. You can use static methods "get" to return double arrays, where one index is the event number while the second is particle position. 
 * S.Chekanov (ANL). 
 */
public class  Delphes 
{

	/**
	 *  Extract array with float values. 
	 *  @param mainbranch main branch (Particle, Track etc)
	 *  @param name   name of the branch. 
	 *  @return Double list with float values.
	 **/
     public static ArrayList<ArrayList<Float>>  getFloat(TBranch mainbranch, String name)  throws java.io.IOException, RootClassNotFound   {

       ArrayList<ArrayList<Float>>  values= new ArrayList< ArrayList<Float> >();
       TBranch branch = mainbranch.getBranchForName(name);
       TLeaf leaf = (TLeaf)branch.getLeaves().get(0);
       //long n = particle.getEntries();
       //System.out.println("nEntries="+n);
       long[] startingEntries = branch.getBasketEntry();
       int event=0;
       for (int i = 0;  i < startingEntries.length-1;  i++) {
                //System.out.println(String.format("BASKET %d", i));
                long endEntry = startingEntries[i + 1];
                event++;
                // all but the last one
                for (long entry = startingEntries[i];  entry < endEntry - 1;  entry++) {
                    //System.out.println(String.format("entry %d endEntry %d", entry, endEntry));
                    event++;
                    ArrayList<Float> newevent = new ArrayList<Float>();
                    //System.out.println(event++);
                    RootInput in = branch.setPosition(leaf, entry + 1);
                    long endPosition = in.getPosition();
                    in = branch.setPosition(leaf, entry);
                    while (in.getPosition() < endPosition) {
                          float pt=in.readFloat(); // PT value  
                          //System.out.print(pt);
                          //System.out.print(" ");
                          newevent.add(pt); 
                    }
                     values.add(newevent);
                    // System.out.println();
                }

                ArrayList<Float> newevent = new ArrayList<Float>();
                // the last one
                RootInput in = branch.setPosition(leaf, endEntry - 1);
                long endPosition = in.getLast();
                while (in.getPosition() < endPosition) {
                     float pt=in.readFloat(); // PT value  
                     //total += in.readFloat();
                    // System.out.print(in.readFloat());
                    // System.out.print(" ");
                     newevent.add(pt);
                }
                // System.out.println();
                values.add(newevent);
     }

   return values;
  }
  
  	/**
	 *  Extract arrays with double values (event, particle index). 
	 *  @param mainbranch main branch (Particle, Track etc)
	 *  @param name   name of the branch. 
	 *  @return  List with double values.
         **/	
     public static ArrayList<ArrayList<Double>>  getDouble(TBranch mainbranch, String name)  throws java.io.IOException, RootClassNotFound   {

       ArrayList<ArrayList<Double>>  values= new ArrayList< ArrayList<Double> >();
       TBranch branch = mainbranch.getBranchForName(name);
       TLeaf leaf = (TLeaf)branch.getLeaves().get(0);
       //long n = particle.getEntries();
       //System.out.println("nEntries="+n);
       long[] startingEntries = branch.getBasketEntry();
       int event=0;
       for (int i = 0;  i < startingEntries.length-1;  i++) {
                //System.out.println(String.format("BASKET %d", i));
                long endEntry = startingEntries[i + 1];
                event++;
                // all but the last one
                for (long entry = startingEntries[i];  entry < endEntry - 1;  entry++) {
                    //System.out.println(String.format("entry %d endEntry %d", entry, endEntry));
                    event++;
                    ArrayList<Double> newevent = new ArrayList<Double>();
                    //System.out.println(event++);
                    RootInput in = branch.setPosition(leaf, entry + 1);
                    long endPosition = in.getPosition();
                    in = branch.setPosition(leaf, entry);
                    while (in.getPosition() < endPosition) {
                          double  pt=in.readDouble(); // PT value  
                          //System.out.print(pt);
                          //System.out.print(" ");
                          newevent.add(pt); 
                    }
                     values.add(newevent);
                    // System.out.println();
                }

                ArrayList<Double> newevent = new ArrayList<Double>();
                // the last one
                RootInput in = branch.setPosition(leaf, endEntry - 1);
                long endPosition = in.getLast();
                while (in.getPosition() < endPosition) {
                     double pt=in.readDouble(); // PT value  
                    // System.out.print(in.readFloat());
                    // System.out.print(" ");
                     newevent.add(pt);
                }
                // System.out.println();
                values.add(newevent);
     }

   return values;
  }

   	/**
	 *  Extract arrays with integer values. 
	 *  @param mainbranch main branch (Particle, Track etc)
	 *  @param name   name of the branch. 
	 *  @return list with integer values.
	 **/
     public static ArrayList<ArrayList<Integer>>  getInt(TBranch mainbranch, String name)  throws java.io.IOException, RootClassNotFound   {

       ArrayList<ArrayList<Integer>>  values= new ArrayList< ArrayList<Integer> >();
       TBranch branch = mainbranch.getBranchForName(name);
       TLeaf leaf = (TLeaf)branch.getLeaves().get(0);
       //long n = particle.getEntries();
       //System.out.println("nEntries="+n);
       long[] startingEntries = branch.getBasketEntry();
       int event=0;
       for (int i = 0;  i < startingEntries.length-1;  i++) {
                //System.out.println(String.format("BASKET %d", i));
                long endEntry = startingEntries[i + 1];
                event++;
                // all but the last one
                for (long entry = startingEntries[i];  entry < endEntry - 1;  entry++) {
                    //System.out.println(String.format("entry %d endEntry %d", entry, endEntry));
                    event++;
                    ArrayList<Integer> newevent = new ArrayList<Integer>();
                    //System.out.println(event++);
                    RootInput in = branch.setPosition(leaf, entry + 1);
                    long endPosition = in.getPosition();
                    in = branch.setPosition(leaf, entry);
                    while (in.getPosition() < endPosition) {
                          int pt=in.readInt(); // PT value  
                          //System.out.print(pt);
                          //System.out.print(" ");
                          newevent.add(pt); 
                    }
                     values.add(newevent);
                    // System.out.println();
                }

                ArrayList<Integer> newevent = new ArrayList<Integer>();
                // the last one
                RootInput in = branch.setPosition(leaf, endEntry - 1);
                long endPosition = in.getLast();
                while (in.getPosition() < endPosition) {
                     int pt=in.readInt(); // PT value  
                    // System.out.print(in.readFloat());
                    // System.out.print(" ");
                     newevent.add(pt);
                }
                // System.out.println();
                values.add(newevent);
     }

   return values;
  }

     /**
	 * Extract arrays with boolean values
	 *  @param mainbranch Main branch (Particle, Track etc)
	 *  @param name   Name of the branch. 
	 *  @return list with integer values.
	 **/
     public static ArrayList<ArrayList<Boolean>>  getBool(TBranch mainbranch, String name)  throws java.io.IOException, RootClassNotFound   {

       ArrayList<ArrayList<Boolean>>  values= new ArrayList< ArrayList<Boolean> >();
       TBranch branch = mainbranch.getBranchForName(name);
       TLeaf leaf = (TLeaf)branch.getLeaves().get(0);
       //long n = particle.getEntries();
       //System.out.println("nEntries="+n);
       long[] startingEntries = branch.getBasketEntry();
       int event=0;
       for (int i = 0;  i < startingEntries.length-1;  i++) {
                //System.out.println(String.format("BASKET %d", i));
                long endEntry = startingEntries[i + 1];
                event++;
                // all but the last one
                for (long entry = startingEntries[i];  entry < endEntry - 1;  entry++) {
                    //System.out.println(String.format("entry %d endEntry %d", entry, endEntry));
                    event++;
                    ArrayList<Boolean> newevent = new ArrayList<Boolean>();
                    //System.out.println(event++);
                    RootInput in = branch.setPosition(leaf, entry + 1);
                    long endPosition = in.getPosition();
                    in = branch.setPosition(leaf, entry);
                    while (in.getPosition() < endPosition) {
                          boolean pt=in.readBoolean(); // PT value  
                          //System.out.print(pt);
                          //System.out.print(" ");
                          newevent.add(pt); 
                    }
                     values.add(newevent);
                    // System.out.println();
                }

                ArrayList<Boolean> newevent = new ArrayList<Boolean>();
                // the last one
                RootInput in = branch.setPosition(leaf, endEntry - 1);
                long endPosition = in.getLast();
                while (in.getPosition() < endPosition) {
                     boolean  pt=in.readBoolean(); // PT value  
                    // System.out.print(in.readFloat());
                    // System.out.print(" ");
                     newevent.add(pt);
                }
                // System.out.println();
                values.add(newevent);
     }

   return values;
  }
  
  
  

}

  
