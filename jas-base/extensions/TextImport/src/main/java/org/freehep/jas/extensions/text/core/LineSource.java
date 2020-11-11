package org.freehep.jas.extensions.text.core;

import java.io.IOException;

/**
 * An interface to be implemented by any line source.
 * @author Tony Johnson
 */
public interface LineSource
{
   public final static int UNKNOWN = -1;
   /**
    * Get the number of rows in this LineSource
    * @param forceCalculation If false this method may return UNKNOWN, 
    * if true it must calculate the actual number of rows (may be slow)
    * @return The number of rows, or UNKNOWN
    */
   int rows(boolean forceCalculation);
   boolean setRow(int row);
   String getLine();
   void close() throws IOException;
   void setLineComment(String prefix);
   void setStartLine(int offset);
}
