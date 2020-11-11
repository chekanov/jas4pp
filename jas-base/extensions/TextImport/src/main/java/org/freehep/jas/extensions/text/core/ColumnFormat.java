package org.freehep.jas.extensions.text.core;

import org.freehep.util.Value;

/**
 *
 * @author  Tony Johnson
 */
public interface ColumnFormat
{
   String getName();
   boolean check(String token);
   void parse(Value value, String token);
   Class getJavaClass();
}
