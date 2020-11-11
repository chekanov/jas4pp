// Part of the FreeHEP library for Java.  Copyright 1999-2004 SLAC. All Rights Reserved.
package org.freehep.jas.extensions.text.aida;

import hep.aida.IAnalysisFactory;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.ITuple;
import java.io.IOException;
import java.util.Arrays;
import org.freehep.util.FreeHEPLookup;

/**
 *
 * @author Tony Johnson
 * @version $Id: Test.java,v 1.1.1.1 2004/05/21 00:12:31 tonyj Exp $
 */
public class Test
{   
   /**
    * @param args the command line arguments
    */
   public static void main(String[] args) throws IOException
   {
      FreeHEPLookup.instance().add(new TextStoreFactory());
      
      IAnalysisFactory af = IAnalysisFactory.create();
      ITreeFactory tf = af.createTreeFactory();
      String file = "C:\\pawdemo.txt";
      ITree tree = tf.create(file,"text");
      ITuple tuple = (ITuple) tree.find("tuple");
      Class[] types = tuple.columnTypes();
      System.out.println(Arrays.asList(types));
      System.out.println(Arrays.asList(tuple.columnNames()));
      
      tuple.start();
      while (tuple.next())
      {
         System.out.println(tuple.getString(0));
      }
      
   }  
}