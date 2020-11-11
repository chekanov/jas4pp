package org.freehep.jas.extensions.conditions;

import java.net.URL;
import java.util.ArrayList;
import org.freehep.application.mdi.PageContext;
import org.freehep.application.mdi.PageEvent;
import org.freehep.application.mdi.PageListener;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.conditions.ConditionsManager;
import org.freehep.record.loop.LoopEvent;
import org.freehep.record.loop.LoopListener;
import static org.freehep.record.loop.RecordLoop.Event.RESET;
import static org.freehep.record.loop.RecordLoop.Event.SUSPEND;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @version $Id: $
 * @author Dmitry Onoprienko
 */
public class ConditionsPlugin extends Plugin implements PageListener, LoopListener, LookupListener {

// -- Private parts : ----------------------------------------------------------
  
  private PageContext _treePage;
  private ArrayList<PageContext> _dataPages;
  private int _currentPage;
  
  private Lookup.Result _lookup;

// -- Construction and initialization : ----------------------------------------
  
  @Override
  protected void init() throws Throwable {

    super.init();
    Studio app = getApplication();

    XMLMenuBuilder builder = app.getXMLMenuBuilder();
    URL xml = getClass().getResource("conditions.menus");
    builder.build(xml);
    
    app.getCommandTargetManager().add(new ConditionsCommandHandler());

  }

  @Override
  protected void postInit() {
//    (new Demo()).init();
    _lookup = getApplication().getLookup().lookup(new Lookup.Template(ConditionsManager.class));
  }
  
  
// -- Getters : ----------------------------------------------------------------
  
  ConditionsTreePanel getTreePanel() {
    return _treePage == null ? null : (ConditionsTreePanel) _treePage.getPage();
  }
  
  
// -- Updating the viewer : ----------------------------------------------------
  
  private void update() {
    if (_treePage == null) return;
    getTreePanel().update();
  }

// -- Listening to events : ----------------------------------------------------

  /** Respond to page event. */
  @Override
  public void pageChanged(PageEvent pe) {
    if (_dataPages == null || _treePage == null) return;
    PageContext page = pe.getPageContext();
    int iPage = _dataPages.indexOf(page);
    if (iPage == -1) { // event from control page
      switch (pe.getID()) {
        case PageEvent.PAGECLOSED:
          _treePage = null;
          _lookup.removeLookupListener(this);
          for (PageContext p : _dataPages) {
            p.close();
          }
          _dataPages = null;
          break;
        case PageEvent.PAGESELECTED:
          _dataPages.get(_currentPage).requestShow();
          break;
      }
    } else { // event from data page
      switch (pe.getID()) {
        case PageEvent.PAGECLOSED:
          if (_dataPages.size() == 1) {
            _dataPages = null;
            _lookup.removeLookupListener(this);
            _treePage.close();
            _treePage = null;
          } else {
            _dataPages.remove(iPage);
            if (iPage == _currentPage) {
              _currentPage = 0;
              _dataPages.get(_currentPage).requestShow();
            }
          }
          break;
        case PageEvent.PAGESELECTED:
          _treePage.requestShow();
          break;
      }
    }
  }
  
  /** Respond to record loop event. */
  @Override
  public void process(LoopEvent event) {
    switch (event.getEventType()) {
      case SUSPEND:
      case RESET:
        update();
    }
  }
  
  /** Respond to lookup event. */
  @Override
  public void resultChanged(LookupEvent le) {
    update();
  }


// -- Command handler : --------------------------------------------------------
    
  private class ConditionsCommandHandler extends CommandProcessor {

    public void onNewConditionsBrowser() {
      Studio app = getApplication();
      if (_treePage == null) {
        _treePage = app.getControlManager().openPage(new ConditionsTreePanel(ConditionsPlugin.this), "Conditions", null);
        _treePage.addPageListener(ConditionsPlugin.this);
        _dataPages = new ArrayList<PageContext>(1);
        _lookup.addLookupListener(ConditionsPlugin.this);
        app.getLookup().add(ConditionsPlugin.this);
      }
      ConditionsDataPanel dataPanel = new ConditionsDataPanel(ConditionsPlugin.this);
      PageContext dataPage = app.getPageManager().openPage(dataPanel, "Conditions Data", null);
      _dataPages.add(dataPage);
      _dataPages.trimToSize();
      _currentPage = _dataPages.size()-1;
      dataPage.addPageListener(ConditionsPlugin.this);
      dataPanel.setPage(dataPage);
      if (_dataPages.size() == 1) update();
      _treePage.requestShow();
      dataPanel.pageChanged(new PageEvent(dataPage, PageEvent.PAGEOPENED));
    }
    
  }
    

}
