package org.freehep.jas.extensions.recordloop;

import hep.aida.ITuple;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import org.freehep.application.ProgressMeter;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.application.studio.StudioListener;
import org.freehep.jas.event.ClassLoadEvent;
import org.freehep.jas.event.ClassLoadedEvent;
import org.freehep.jas.event.ClassUnloadEvent;
import org.freehep.jas.plugin.console.ConsoleOutputStream;
import org.freehep.jas.plugin.console.ConsoleService;
import org.freehep.jas.plugin.tree.FTree;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreeNodeAddedNotification;
import org.freehep.jas.plugin.tree.FTreeNodeObjectProvider;
import org.freehep.jas.plugin.tree.FTreeNodeRemovedNotification;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.jas.plugin.tree.FTreeProvider;
import org.freehep.jas.services.ProgressMeterProvider;
import org.freehep.record.loop.ConcurrentRecordLoop;
import org.freehep.record.loop.LoopEvent;
import org.freehep.record.loop.LoopListener;
import org.freehep.record.loop.RecordEvent;
import org.freehep.record.loop.RecordListener;
import org.freehep.record.loop.RecordLoop;
import org.freehep.record.source.NoSuchRecordException;
import org.freehep.record.source.RecordSource;
import org.freehep.util.FreeHEPLookup;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandState;
import org.freehep.util.images.ImageHandler;
import org.freehep.xml.menus.XMLMenuBuilder;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Lookup.Result;
import org.openide.util.Lookup.Template;
import org.xml.sax.SAXException;

/**
 * The main class of the "Record Loop" plugin.
 * 
 * @author tonyj
 * @version $Id: RecordLoopPlugin.java 14054 2012-10-31 02:00:11Z onoprien $
 */
public class RecordLoopPlugin extends Plugin implements StudioListener, LookupListener {

// -- Private parts : ----------------------------------------------------------
  
  private Result _result;
  private JasRecordLoop _loop;
  private SourceInfo _sourceInfo;
  
  private volatile List<RecordListener> _recordListeners = new ArrayList<RecordListener>(0);
  private final RecordListener _recordEventDispatcher;
  private volatile List<LoopListener> _loopListeners = new ArrayList<LoopListener>(0);
  private final LoopListener _loopEventDispatcher;
  private Map<String,Object> _loadedListeners;
  
  private ProgressMeter _meter;
  private ProgressMeterProvider _meterProvider;
  private float _loopNorm;
  
  private Commands _commandProcessor = new Commands();
  
  private RecordSourceList _model;
  private JComboBox _box;
  private JToolBar _toolbar;
  
  private FTree _defaultTree;
  private final static Icon _dataSetIcon = ImageHandler.getIcon("images/store.gif", RecordLoopPlugin.class);
  private boolean _dataSetsNodeCreated = false;

// -- Initialization : ---------------------------------------------------------
  
  public RecordLoopPlugin() {
    _recordEventDispatcher = new RecordListener() {
      public void recordSupplied(RecordEvent re) {
        for (RecordListener listener : _recordListeners) {
          listener.recordSupplied(re);
        }
      }
    };
    _loopEventDispatcher = new LoopListener() {
      public void process(LoopEvent le) {
        RecordLoopPlugin.this.process(le);
        for (LoopListener listener : _loopListeners) {
          listener.process(le);
        }
      }
    };
  }
  
  protected void init() throws SAXException, IOException {
    
    Studio app = getApplication();
    FreeHEPLookup lookup = app.getLookup();
    _meterProvider = (ProgressMeterProvider) lookup.lookup(ProgressMeterProvider.class);
    _meter = _meterProvider.getProgressMeter();
    if (_meter != null) {
      if (_meter.getModel() == null) _meter.setModel(new DefaultBoundedRangeModel());
      _meter.setShowStopButton(false);
      _meter.setStopEnabled(false);
    }
    
    // Configure menu and toolbar

    XMLMenuBuilder builder = app.getXMLMenuBuilder();
    URL xml = getClass().getResource("RecordLoop.menus");
    builder.build(xml);

    // Make sure any registered RecordListener are added to MyChain
    
    Template template = new Template();
    _result = app.getLookup().lookup(template);
    _result.addLookupListener(this);

    // Add a master tree lookup for ITuple nodes?
    // This makes us dependent on AIDA??
    
    FTreeProvider defaultTreeProvider = (FTreeProvider) app.getLookup().lookup(FTreeProvider.class);
    _defaultTree = defaultTreeProvider.tree();

    // Register the standard adapters
    
    defaultTreeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new DataSetsAdapter(), RecordLoopPlugin.class);
    defaultTreeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new DataSetAdapter(), RecordSource.class);
    defaultTreeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new ITupleAdapter(), ITuple.class);

    // Create the eventloop

    _loop = new JasRecordLoop(app);
    lookup.add(_loop);
    lookup.add(this);
    _loop.addRecordListener(_recordEventDispatcher);
    _loop.addLoopListener(_loopEventDispatcher);
    
    // Create record loop toolbar with the source selection combo box

    _toolbar = builder.getToolBar("recordLoop");
    _model = new RecordSourceList();
    _result.addLookupListener(_model);
    
    _box = new JComboBox(_model);
    _box.setEnabled(_box.getItemCount() > 0);
    _box.setRenderer(new RecordSourceRenderer());
    _box.setMinimumSize(new Dimension(100, 10));
    _toolbar.add(_box, 0);
    app.addToolBar(_toolbar, "Record Loop", Studio.TOOLBAR_PROGRAM);

    // Register command processsor
    
    app.getCommandTargetManager().add(_commandProcessor);

    // Listen for new loaded classes
    
    app.getEventSender().addEventListener(this, ClassLoadEvent.class);
    
    // Plugin settings 
    
    RecordLoopProperties pref = new RecordLoopProperties(this);
    pref.apply();
    lookup.add(pref);
    
    // Test
    
//    app.getLookup().add(new Test("Through lookup"));
//    _loop.addLoopListener(new Test("Loop"));
//    Test both = new Test("both");
//    app.getLookup().add(both);
//    _loop.addLoopListener(both);
  }
  
// -- Getters : ----------------------------------------------------------------

  /** Returns record loop object used by this plugin. */
  public RecordLoop getRecordLoop() {
    return _loop;
  }

// -- Operations : -------------------------------------------------------------
  
  public void closeRecordSource(RecordSource source) {
    FreeHEPLookup lookup = getApplication().getLookup();
    lookup.remove(source);
    if (_result.allInstances().contains(source)) {
      getApplication().error("Source is in use, cannot close");
    } else {
      try {
        source.close();
      } catch (IOException x) {
        getApplication().error("Error when closing the source", x);
      }
    }
  }


// -- Implementing LookupListener : --------------------------------------------

  /**
   * Called in response to any Lookup events.
   * Maintains lists of registered RecordListeners and LoopListeners.
   */
  public void resultChanged(LookupEvent lookupEvent) {
    ArrayList<RecordListener> recordListeners = new ArrayList<RecordListener>();
    ArrayList<LoopListener> loopListeners = new ArrayList<LoopListener>();
    for (Object item : _result.allInstances()) {
      if (item instanceof RecordListener) {
        recordListeners.add((RecordListener)item);
      }
      if (item instanceof LoopListener) {
        loopListeners.add((LoopListener)item);
      }
    }
    recordListeners.trimToSize();
    loopListeners.trimToSize();
    _recordListeners = recordListeners;
    _loopListeners = loopListeners;
  }
  
// -- Implementing StudioListener : --------------------------------------------

  /**
   * Called in response to Studio events.
   * Creates an instance of a newly loaded listener class, and adds it to the loop.
   * Removes listeners when all classes are unloaded.
   */
  public void handleEvent(EventObject event) {
    if (event instanceof ClassLoadedEvent) {
      Class x = ((ClassLoadedEvent) event).getLoadedClass();
      if (RecordListener.class.isAssignableFrom(x) || LoopListener.class.isAssignableFrom(x)) {
        try {
          Object l = x.newInstance();
          if (_loadedListeners == null) _loadedListeners = Collections.synchronizedMap(new HashMap<String,Object>());
          Object oldListener = _loadedListeners.put(x.getName(), l);
          if (oldListener != null) {
            if (oldListener instanceof RecordListener) _loop.removeRecordListener((RecordListener)oldListener);
            if (oldListener instanceof LoopListener) _loop.removeLoopListener((LoopListener)oldListener);
          }
          if (l instanceof RecordListener) _loop.addRecordListener((RecordListener)l);
          if (l instanceof LoopListener) _loop.addLoopListener((LoopListener)l);
        } catch (InstantiationException xx) {
          getApplication().error("Error instantiating " + x.getName(), xx);
        } catch (ExceptionInInitializerError xx) {
          getApplication().error("Error instantiating " + x.getName(), xx.getException());
        } catch (IllegalAccessException xx) {
          getApplication().error("Could not add class " + x.getName() + " to record loop, missing public constructor?", xx);
        }
      }
    } else if (event instanceof ClassUnloadEvent && _loadedListeners != null) {
      for (Object o : _loadedListeners.values()) {
        if (o instanceof RecordListener) _loop.removeRecordListener((RecordListener)o);
        if (o instanceof LoopListener) _loop.removeLoopListener((LoopListener)o);
      }
      _loadedListeners = null;
    }
  }
  
  
// -- Local methods : ----------------------------------------------------------

  private void setCurrentRecordSource(final SourceInfo info) {
    SourceInfo old = _sourceInfo;
    try {
      _sourceInfo = info;
      _loop.setRecordSource(info == null ? null : info.source);
      setMeter();
    } catch (IllegalStateException x) {
      _sourceInfo = old;
      getApplication().error("Cannot change record source", x);
    }
  }
 
  /** Sets progress meter based on position in the current source. */
  private void setMeter() {
    if (_meter == null) return;
    _meter.setIndeterminate(false);
    int progress = 0;
    if (_sourceInfo != null) {
      try {
        progress = _sourceInfo.getProgress();
      } catch (UnsupportedOperationException x) {}
    }
    _meter.getModel().setValue(progress);
    _meter.setStopEnabled(false);
  }

  
// -- Command processor class : ------------------------------------------------

  public class Commands extends CommandProcessor {

    public void enableRewind(CommandState state) {
      state.setEnabled(_loop.isEnabled(RecordLoop.Command.REWIND));
    }

    public void onRewind() {
      try {
        if (_meter != null) _meter.setIndeterminate(true);
        _loop.execute(RecordLoop.Command.REWIND);
      } catch (IllegalStateException x) {
        setMeter();
      }
    }

    public void enablePrev(CommandState state) {
      state.setEnabled(_loop.isEnabled(RecordLoop.Command.PREVIOUS));
    }

    public void onPrev() {
      try {
        _loop.execute(RecordLoop.Command.PREVIOUS);
      } catch (IllegalStateException x) {}
    }

    public void enablePause(CommandState state) {
      state.setEnabled(_loop.isEnabled(RecordLoop.Command.PAUSE));
    }

    public void onPause() {
      try {
        _loop.execute(RecordLoop.Command.PAUSE);
      } catch (IllegalStateException x) {}
      setChanged();
    }

    public void enableNext(CommandState state) {
      state.setEnabled(_loop.isEnabled(RecordLoop.Command.NEXT));
    }

    public void onNext() {
      try {
        _loop.execute(RecordLoop.Command.NEXT);
      } catch (IllegalStateException x) {}
    }

    public void enableGoN(CommandState state) {
      state.setEnabled(_loop.isEnabled(RecordLoop.Command.GO_N));
    }

    public void onGoN() {
      int maxRecords = 10;
      try {
        long max = _sourceInfo.getRemaining();
        if (max < Integer.MAX_VALUE) maxRecords = (int)max;
      } catch (UnsupportedOperationException x) {}
      GoOptions opts = new GoOptions(true, maxRecords);
      int rc = JOptionPane.showConfirmDialog(getApplication(), opts, "Go...", JOptionPane.OK_CANCEL_OPTION);
      if (rc == JOptionPane.OK_OPTION) {
        if (opts.isStopAfter()) {
          long nRecords = opts.getMaxRecords();
          _loop.execute(RecordLoop.Command.GO_N, nRecords);
        } else {
          _loop.execute(RecordLoop.Command.GO);
        }
      }
    }

    public void enableGo(CommandState state) {
      state.setEnabled(_loop.isEnabled(RecordLoop.Command.GO));
    }

    public void onGo() {
      try {
        _loop.execute(RecordLoop.Command.GO);
      } catch (IllegalStateException x) {}
    }

    public void enableJump(CommandState state) {
      state.setEnabled(_loop.isEnabled(RecordLoop.Command.JUMP));
    }

    public void onJump() {
      JumpOptions gt = new JumpOptions(_sourceInfo.source);
      int rc = JOptionPane.showConfirmDialog(getApplication(), gt.getPanel(), "Jump To Record", JOptionPane.OK_CANCEL_OPTION);
      if (rc == JOptionPane.OK_OPTION) {
        _loop.execute(RecordLoop.Command.JUMP, gt.getParameters());
      }
    }

    public void enableRefresh(CommandState state) {
      state.setEnabled(_loop.isEnabled(RecordLoop.Command.REFRESH));
    }

    public void onRefresh() {
      try {
        _loop.execute(RecordLoop.Command.REFRESH);
      } catch (IllegalStateException x) {}
    }

    public void enableStop(CommandState state) {
      state.setEnabled(_loop.isEnabled(RecordLoop.Command.STOP));
    }

    public void onStop() {
      try {
        _loop.execute(RecordLoop.Command.STOP);
        setChanged();
      } catch (IllegalStateException x) {}
    }

    public void enablePref(CommandState state) {
      state.setEnabled(_loop.getState() != RecordLoop.State.LOOPING);
    }

    public void onPref() {
      try {
        final RecordLoopProperties pref = (RecordLoopProperties) RecordLoopPlugin.this.getApplication().getLookup().lookup(RecordLoopProperties.class);
        final JComponent component = pref.component();
        JButton apply = new JButton("Apply");
        apply.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            pref.apply(component);
          }
        });
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createLoweredBevelBorder());
        panel.add(component);
        Object[] options = new Object[]{"OK", apply, "Cancel"};
        int out = JOptionPane.showOptionDialog(null, panel, "Record loop", 
                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (out == JOptionPane.YES_OPTION) pref.apply(component);
      } catch (IllegalStateException x) {}
    }
  }
  
// -- Responding to loop events : ----------------------------------------------

  /** Called by record loop for status updates. */
  public void process(LoopEvent event) {
    switch (event.getEventType()) {
      
      case PROGRESS:
        if (_meter != null && _loopNorm > 0.f) {
          _meter.getModel().setValue(Math.round(event.getCountableConsumed()*_loopNorm));
        }
        break;
        
      case RESET:
        _sourceInfo.position = -1L;
        if (_meter != null) {
          _meter.getModel().setValue(0);
          _meter.setIndeterminate(false);
        }
        _commandProcessor.setChanged();
        break;
        
      case RESUME:
        if (_meter != null) {
          RecordLoop.Command cmd = event.getCommand();
          switch (cmd) {
            case GO_N:
            case GO:
              long loopSize = 0L;
              if (cmd == RecordLoop.Command.GO_N) {
                loopSize = (Long) event.getCommandParameters()[0];
              } else {
                try {
                  loopSize = _sourceInfo.getRemaining();
                } catch (UnsupportedOperationException x) {
                }
              }
              if (loopSize == 0L) {
                _meter.setIndeterminate(true);
                _loopNorm = -1.f;
              } else {
                _loopNorm = 100.f/loopSize;
                _meter.getModel().setValue(0);
                long nReport = loopSize / 100L + 1;
                _loop.setProgressByRecords(nReport);
              }
              break;
            default:
              _meter.setIndeterminate(true);
          }
        }
        getApplication().getStatusBar().setMessage("Processing records...");
        _commandProcessor.setChanged();
        break;
        
      case SUSPEND:
        
        _sourceInfo.shiftPosition(event.getConsumed());
        _commandProcessor.setChanged();
        String message = event.getCountableConsumed() +" events processed in "+ event.getTimeInLoop()/1000L +" seconds";
        getApplication().getStatusBar().setMessage(message);
        setMeter();
        Throwable x = event.getException();
        if (x != null) {
          if (x instanceof NoSuchRecordException) {
            RecordLoop.Command cmd = event.getCommand();
            if (cmd == RecordLoop.Command.GO_N) {
              getApplication().error("End of source reached");
            } else if (cmd != RecordLoop.Command.GO) {
              getApplication().error("No such record", x);
            }
          } else if (x instanceof IOException) {
            getApplication().error("Failed to retrieve record", x);
          } else {
            getApplication().error("Error: ", x);
          }
        }
        break;
        
      case CONFIGURE:
        setMeter();
        
      default:
        _commandProcessor.setChanged();
    }
    _box.setEnabled(_model.getSize() > 1 && _loop.isEnabled(RecordLoop.Command.SET_SOURCE));
  }

// -- Standard DefaultFTreeNodeAdapter classes : -------------------------------
  
  private class ITupleAdapter extends DefaultFTreeNodeAdapter implements ActionListener {

    ITupleAdapter() {
      super(50, new DataSetObjectProvider());
    }

    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
      JMenuItem item = new JMenuItem("Create Record Source");
      item.addActionListener(this);
      menu.add(item);
      return menu;
    }

    public void actionPerformed(ActionEvent e) {
      FTreeNode[] nodes = _defaultTree.selectedNodes();
      ITuple tuple = (ITuple) nodes[0].objectForClass(ITuple.class);
      ITupleRecordSource rs = new ITupleRecordSource(tuple);
      getApplication().getLookup().add(rs);
    }
  }

  private class DataSetsAdapter extends DefaultFTreeNodeAdapter implements ActionListener {

    DataSetsAdapter() {
      super(100, new DataSetObjectProvider());
    }

    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
      JMenuItem item = new JMenuItem("Close All");
      item.addActionListener(this);
      menu.add(item);
      return menu;
    }

    public void actionPerformed(ActionEvent e) {
      FreeHEPLookup lookup = getApplication().getLookup();
      Template template = new Template(RecordSource.class);
      Result result = lookup.lookup(template);
      for (Iterator i = result.allInstances().iterator(); i.hasNext();) {
        lookup.remove(i.next());
      }
    }

    public boolean allowsChildren(FTreeNode node, boolean allowsChildren) {
      return true;
    }
  }

  private class DataSetAdapter extends DefaultFTreeNodeAdapter implements ActionListener {

    DataSetAdapter() {
      super(100, new DataSetObjectProvider());
    }

    public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
      JMenuItem item = new JMenuItem("Make Current Record Source");
      SourceInfo info = (SourceInfo) nodes[0].objectForClass(SourceInfo.class);
      item.setEnabled(info != _model.getSelectedItem());
      item.addActionListener(this);
      menu.add(item);
      item = new JMenuItem("Close data source");
      item.setActionCommand("close");
      item.addActionListener(this);
      menu.add(item);
      return menu;
    }

    public void actionPerformed(ActionEvent e) {
      FTreeNode[] nodes = _defaultTree.selectedNodes();
      SourceInfo info = (SourceInfo) nodes[0].objectForClass(SourceInfo.class);
      if (e.getActionCommand().equals("close")) {
        RecordSource source = info.source;
        closeRecordSource(source);
      } else {
        _box.setSelectedItem(info);
        _box.repaint();
      }
    }

    public Icon icon(FTreeNode node, Icon oldIcon, boolean selected, boolean expanded) {
      return _dataSetIcon;
    }

    public boolean doubleClick(FTreeNode node) {
      SourceInfo info = (SourceInfo) node.objectForClass(SourceInfo.class);
      _box.setSelectedItem(info);
      _box.repaint();
      return true;
    }
  }

  private class DataSetObjectProvider implements FTreeNodeObjectProvider {

    public Object objectForNode(FTreeNode node, Class clazz) {
      FTreePath path = node.path();
      return _model.forName(path.getLastPathComponent());
    }
  }
  
// -- Handling RecordSources :  ------------------------------------------------
  
  private final class SourceInfo {
    
    final String name;
    final RecordSource source;
    long position = -1L;
    
    SourceInfo(RecordSource source, String name) {
      this.name = name == null ? source.getName() : name;
      this.source = source;
    }
    
    /** Returns estimated number of records processed from the source, throws UnsupportedOperationException if unknown. */
    long getProcessed() {
      long index = -2L;
      try {
        index = source.getCurrentIndex();
      } catch (UnsupportedOperationException x) {
      } catch (IllegalStateException x) {
      }
      if (index == -2L) index = position;
      if (index == -2L) throw new UnsupportedOperationException();
      return index;
    }
    
    /** Returns estimated number of records remaining in the source, throws UnsupportedOperationException if unknown. */
    long getRemaining() {
      long size = source.getEstimatedSize();
      long remaining = source.getEstimatedSize() - getProcessed();
      if (remaining < 0L) throw new UnsupportedOperationException();
      return remaining;
    }

    /** Returns progress through the source in per cent, zero if unknown. */
    int getProgress() {
      try {
        long size = source.getEstimatedSize();
        if (size < 1L) {
          return 0;
        } else {
          long progress = Math.round(getProcessed()*100./size);
          if (progress < 0L || progress > 100L) {
            return 0;
          } else {
            return (int)progress;
          }
        }
      } catch (UnsupportedOperationException x) {
        return 0;
      }
    }
    
    void shiftPosition(long shift) {
      try {
        position = source.getCurrentIndex();
        return;
      } catch (UnsupportedOperationException x) {
      } catch (IllegalStateException x) {
      }
      if (position != -2L) position += shift;
    }
    
  }

  private final class RecordSourceList extends AbstractListModel implements ComboBoxModel, LookupListener {

    private final List<SourceInfo> content = new ArrayList<SourceInfo>(1);
    private final Set<RecordSource> sources = new HashSet<RecordSource>();
    private final Map<String, SourceInfo> nameMap = new HashMap<String, SourceInfo>();

    RecordSourceList() {
      for (Object item : _result.allInstances()) {
        if (item instanceof RecordSource) add((RecordSource) item);
      }
      if (!content.isEmpty()) setSelectedItem(content.get(0));
    }

    public void resultChanged(LookupEvent lookupEvent) {
      
      Collection instances = _result.allInstances();
      
      // add new sources
      
      Iterator i = instances.iterator();
      int index = content.size();
      while (i.hasNext()) {
        Object item = i.next();
        if (item instanceof RecordSource) {
          if (!sources.contains(item)) {
            add((RecordSource) item);
            fireIntervalAdded(_result, index, index);
            index++;
          }
        }
      }
      
      // compile a list of sources to remove and find the first remaining source
      
      boolean removeCurrent = false;
      SourceInfo firstSource = null;
      HashSet<SourceInfo> removed = new HashSet<SourceInfo>();
      for (SourceInfo info : content) {
        if (!instances.contains(info.source)) {
          if (info.equals(_sourceInfo)) {
            removeCurrent = true;
          } else {
            removed.add(info);
          }
        } else {
          if (firstSource == null) firstSource = info;
        }
      }
      
      // remove sources
      
      for (SourceInfo info : removed) remove(info);
      
      if (removeCurrent) {
        SourceInfo info = _sourceInfo;
        setCurrentRecordSource(firstSource);
        if (info == _sourceInfo) {
          getApplication().getLookup().add(_sourceInfo.source);
        } else {
          remove(info);
        }
      }
      
      // setEnable(...) for record loop toolbar and source combo box
      
      boolean enabled = content.size() > 1 && _loop.isEnabled(RecordLoop.Command.SET_SOURCE);
      if (_box.isEnabled() != enabled) _box.setEnabled(enabled);
      enabled = !content.isEmpty();
      if (_toolbar.isVisible() != enabled) _toolbar.setVisible(enabled);
    }

    private void add(RecordSource source) {
      String ourName = source.getName();
      for (int i = 1; nameMap.containsKey(ourName); i++) {
        ourName = source.getName() + " (" + i + ")";
      }
      SourceInfo info = new SourceInfo(source, ourName);
      if (_sourceInfo == null) setCurrentRecordSource(info);
      if (content.isEmpty()) {
        if (!_dataSetsNodeCreated) {
          _dataSetsNodeCreated = true;
          FTreePath path = new FTreePath("DataSets");
          _defaultTree.treeChanged(new FTreeNodeAddedNotification(this, path, RecordLoopPlugin.class));
        }
      }
      content.add(info);
      nameMap.put(ourName, info);
      sources.add(source);
      FTreePath path = new FTreePath(new String[]{"DataSets", ourName});
      _defaultTree.treeChanged(new FTreeNodeAddedNotification(this, path, source.getClass()));
    }
    
    private void remove(SourceInfo info) {
      int index = content.indexOf(info);
      content.remove(info);
      sources.remove(info.source);
      nameMap.remove(info.name);
      fireIntervalRemoved(_result, index, index);
      FTreePath path = new FTreePath(new String[]{"DataSets", info.name});
      _defaultTree.treeChanged(new FTreeNodeRemovedNotification(this, path));
    }

    public Object getElementAt(int index) {
      return content.get(index);
    }

    public Object getSelectedItem() {
      return _sourceInfo;
    }

    public int getSize() {
      return content.size();
    }

    public void setSelectedItem(Object anItem) {
      setCurrentRecordSource((SourceInfo) anItem);
    }

    Object forName(String name) {
      return nameMap.get(name);
    }
  }

  private class RecordSourceRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      if (value instanceof SourceInfo) {
        setText(((SourceInfo) value).name);
        setIcon(_dataSetIcon);
      }
      return this;
    }
  }
  
}
