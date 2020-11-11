package org.freehep.jas.services;

/**
 *
 * @author tonyj
 * @version $Id: ScriptEngine.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public interface ScriptEngine
{
   void registerVariable(String name, Object value);
   void runScript(String text);
   boolean canAccept(String mimeType);
}
