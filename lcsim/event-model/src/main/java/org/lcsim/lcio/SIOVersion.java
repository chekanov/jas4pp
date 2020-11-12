package org.lcsim.lcio;

/** Simple helper class to encode/decode the LCIO version number.
 *  @author gaede
 *  @version $Id: SIOVersion.java,v 1.1 2009/02/23 19:36:39 jeremy Exp $
 */
public class SIOVersion
{
   
   /** Returns the version encoded in one word.
    */
   public static int encode(int major, int minor)
   {
      
      return (major << 16) | ( 0xffff & minor ) ;
   }
}
