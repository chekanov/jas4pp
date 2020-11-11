package org.freehep.jas.extensions.text.core;

/**
 *
 * @author Tony Johnson
 */
public interface Tokenizer
{
   void setLine(CharSequence in);
   String nextToken();
}
