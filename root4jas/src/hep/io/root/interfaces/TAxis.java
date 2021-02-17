/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Apr 21 13:58:49 PDT 2003
 */
package hep.io.root.interfaces;

import java.util.List;

public interface TAxis extends hep.io.root.RootObject, hep.io.root.interfaces.TNamed, hep.io.root.interfaces.TAttAxis,hep.io.root.interfaces.TList 
{
   public final static int rootIOVersion = 10;
   public final static int rootCheckSum = 18741940;


   /** first bin to display */
   int getFirst();

   /** last bin to display */
   int getLast();

   /** Number of bins */
   int getNbins();

   /** on/off displaying time values instead of numerics */
   boolean getTimeDisplay();

   /** Date&time format, ex: 09/12/99 12:34:00 */
   //hep.io.root.interfaces.TString getTimeFormat();
   String getTimeFormat();

   /** Bin edges array in X */
   //hep.io.root.interfaces.TArrayD getXbins();
   double[] getXbins();

   /** upper edge of last bin */
   double getXmax();

   /** low edge of first bin */
   double getXmin();

   // ROOT 6
   enum test 
    { 
        kAlphanumeric,  kCanExtend, kNotAlpha; 
    } 
 
  boolean   hasBinWithoutLabel(); 

  hep.io.root.interfaces.TList  GetModifiedLabels(); 
  //List GetModifiedLabels(); 
  
  int  findBin(double x);
  int  findFixBin(String label); 

  void  setNoAlphanumeric(boolean noalpha); 

  /** List of modified labels */
   hep.io.root.interfaces.TList getModLabs();
   //  List getModLabs();


   void centerLabels(boolean  center);

   void centerTitle(boolean  center);

  void rotateTitle(boolean  rotate);

  void setDecimals(boolean  dot);

  void setMoreLogLabels(boolean more);

  void setNoExponent(boolean noExponent);

}
