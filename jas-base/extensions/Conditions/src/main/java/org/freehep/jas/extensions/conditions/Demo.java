package org.freehep.jas.extensions.conditions;

import java.io.IOException;
import org.freehep.application.Application;
import org.freehep.application.studio.Studio;
import org.freehep.conditions.demo.ConditionsTest;
import org.freehep.record.loop.RecordEvent;
import org.freehep.record.loop.RecordListener;
import org.freehep.record.source.AbstractRecordSource;
import org.freehep.record.source.NoSuchRecordException;

/**
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class Demo extends AbstractRecordSource implements RecordListener {

// -- Private parts : ----------------------------------------------------------
  
  int _iEvent = -1;
  ConditionsTest _conTest;

// -- Construction and initialization : ----------------------------------------
  
  public Demo() {
    super("Conditions Viewer Test");
  }

  public void init() {
    Studio app = (Studio) Application.getApplication();
    app.getLookup().add(this);
    _conTest = new ConditionsTest();
    app.getLookup().add(_conTest.getConditionsManager());
  }
  
// -- Producing and processing records : ---------------------------------------
  
  @Override
  public Object getCurrentRecord() throws IOException {
    return new Object();
  }
  
  @Override
  public void next() throws IOException, NoSuchRecordException {
  }

  @Override
  public boolean hasNext() {
    return _conTest != null && _conTest.hasNext();
  }

  @Override
  public boolean supportsNext() {
    return true;
  }  
  
  @Override
  public void recordSupplied(RecordEvent re) {
    System.out.println("\nEvent "+ ++_iEvent);
    try {
      _conTest.next();
    } catch (IllegalArgumentException x) {
      System.out.println("End of test");
    }
      
  }

}
