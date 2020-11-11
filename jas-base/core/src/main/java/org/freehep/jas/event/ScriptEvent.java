package org.freehep.jas.event;
import org.freehep.jas.services.ScriptEngine;

/**
 *
 * @author tonyj
 */
public class ScriptEvent extends java.util.EventObject
{
   public ScriptEvent(ScriptEngine engine)
   {
      super(engine);
   }
   public ScriptEngine getScriptEngine()
   {
      return (ScriptEngine) getSource();
   }
}
