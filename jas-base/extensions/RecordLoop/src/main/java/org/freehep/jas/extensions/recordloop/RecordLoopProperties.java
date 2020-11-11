package org.freehep.jas.extensions.recordloop;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.swing.*;
import org.freehep.application.PropertyUtilities;
import org.freehep.jas.services.PreferencesTopic;
import org.freehep.record.loop.ConcurrentRecordLoop;

/**
 * Record loop plugin settings GUI.
 *
 * @author Dmitry Onoprienko
 */
class RecordLoopProperties implements PreferencesTopic {
  
// -- Private parts : ----------------------------------------------------------

  static private final String _keyPrefix = "extensions.recordloop.";
  
  static private final String _stopOnRewindKey = "stopOnRewind";
  static private final String _stopOnEOFKey = "stopOnEOF";
  static private final String _concurrentKey = "concurrent";
  static private final String _nThreadsKey = "nThreads";
  
  static private final boolean _stopOnRewindDefault = true;
  static private final boolean _stopOnEOFDefault = true;
  static private final boolean _concurrentDefault = false;
  static private final int _nThreadsDefault = 1;
  
  private final RecordLoopPlugin _recordLoopPlugin;


// -- Construction and initialization : ----------------------------------------
  
  RecordLoopProperties(RecordLoopPlugin recordLoopPlugin) {
    _recordLoopPlugin = recordLoopPlugin;
  }
  
// -- Implementing PreferencesTopic : ------------------------------------------

  @Override
  public boolean apply(JComponent panel) {
    try {
      GUI gui = (GUI) panel;
      boolean stopOnRewind = gui.stopOnRewindBox.isSelected();
      boolean stopOnEOF = gui.stopOnEOFBox.isSelected();
      boolean concurrent = gui.concurrentBox.isSelected();
      int nThreads = (Integer) gui.nThreadsSpinner.getValue();
      if (nThreads < 1 || nThreads > 99) {
        _recordLoopPlugin.getApplication().error("The number of threads should be between 1 and 99");
        return false;
      }
      Properties prop = _recordLoopPlugin.getApplication().getUserProperties();
      ConcurrentRecordLoop loop = (ConcurrentRecordLoop) _recordLoopPlugin.getRecordLoop();
      try {
        loop.setNumberOfThreads(concurrent ? nThreads : 0);
        PropertyUtilities.setBoolean(prop, _keyPrefix + _concurrentKey, concurrent);
        PropertyUtilities.setInteger(prop, _keyPrefix + _nThreadsKey, nThreads);
      } catch (IllegalStateException x) {
        _recordLoopPlugin.getApplication().error("Cannot modify record loop properties while the loop is running");
      }
      PropertyUtilities.setBoolean(prop, _keyPrefix + _stopOnRewindKey, stopOnRewind);
      PropertyUtilities.setBoolean(prop, _keyPrefix + _stopOnEOFKey, stopOnEOF);
      loop.setStopOnRewind(stopOnRewind);
      loop.setStopOnEOF(stopOnEOF);
    } catch (ClassCastException x) {}
    return true;
  }

  @Override
  public JComponent component() {
    return new GUI();
  }

  @Override
  public String[] path() {
    return new String[] {"Record Loop"};
  }
  
// -- Apply current preferences to the loop : ----------------------------------

  public void apply() {
    Properties prop = _recordLoopPlugin.getApplication().getUserProperties();
    boolean stopOnRewind = PropertyUtilities.getBoolean(prop, _keyPrefix + _stopOnRewindKey, _stopOnRewindDefault);
    boolean stopOnEOF = PropertyUtilities.getBoolean(prop, _keyPrefix + _stopOnEOFKey, _stopOnEOFDefault);
    boolean concurrent = PropertyUtilities.getBoolean(prop, _keyPrefix + _concurrentKey, _concurrentDefault);
    int nThreads = PropertyUtilities.getInteger(prop, _keyPrefix + _nThreadsKey, _nThreadsDefault);
    if (nThreads < 1 || nThreads > 99) {
      nThreads = _nThreadsDefault;
      PropertyUtilities.setInteger(prop, _keyPrefix + _nThreadsKey, nThreads);
    }
    ConcurrentRecordLoop loop = (ConcurrentRecordLoop) _recordLoopPlugin.getRecordLoop();
    loop.setNumberOfThreads(concurrent ? nThreads : 0);
    loop.setStopOnRewind(stopOnRewind);
    loop.setStopOnEOF(stopOnEOF);
  }
  
// -- GUI panel class : --------------------------------------------------------
  
  private class GUI extends JPanel {
    
    private final JCheckBox stopOnRewindBox;
    private final JCheckBox stopOnEOFBox;
    
    private final JCheckBox concurrentBox;
    private final JLabel nThreadsLabel;
    private final Box nThreadsPanel;
    private final JSpinner nThreadsSpinner;
    private final JButton nThreadsButton;
    
    private final int HSPACE = 10;
    private final int VSPACE = 5;
    
    GUI() {
      setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
      Properties prop = _recordLoopPlugin.getApplication().getUserProperties();
      
      stopOnRewindBox = new JCheckBox("Stop on rewind");
      stopOnRewindBox.setToolTipText("If checked, rewinding the source while the loop is paused will stop the run");
      stopOnRewindBox.setSelected(PropertyUtilities.getBoolean(prop, _keyPrefix + _stopOnRewindKey, _stopOnRewindDefault));
      add(stopOnRewindBox);
      
      stopOnEOFBox = new JCheckBox("Stop on end-of-source");
      stopOnEOFBox.setToolTipText("If checked, exhausting the source while executing GO or GO_N commands will stop the run");
      stopOnEOFBox.setSelected(PropertyUtilities.getBoolean(prop, _keyPrefix + _stopOnEOFKey, _stopOnEOFDefault));
      add(stopOnEOFBox);
      
      add(add(Box.createRigidArea(new Dimension(0, VSPACE))));
      
      // Parallel processing :
      
      concurrentBox = new JCheckBox("Enable");
      concurrentBox.setToolTipText("If checked, records may be processed in parallel in multiple threads");
      concurrentBox.setSelected(PropertyUtilities.getBoolean(prop, _keyPrefix + _concurrentKey, _concurrentDefault));
      concurrentBox.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
          setEnabled();
        }
      });
      add(concurrentBox);

      String tt = "<html>Number of worker threads used for processing records.<br>"
              + "If this is set to 1 while parallel processing is enabled,<br>"
              + "records will be fetched from the source in parallel to being<br>"
              + "processed by clients, but the processing itself will be done<br>"
              + "sequentially in a single thread.</html>";
      nThreadsLabel = new JLabel("Number of worker threads : ");
      nThreadsLabel.setToolTipText(tt);
      nThreadsSpinner = new JSpinner(new SpinnerNumberModel(PropertyUtilities.getInteger(prop, _keyPrefix + _nThreadsKey, _nThreadsDefault), 1, 99, 1)) {
        public Dimension getMaximumSize() {
          int h = getPreferredSize().height;
          h = nThreadsLabel == null ? h : Math.max(h, nThreadsLabel.getPreferredSize().height); 
          h = nThreadsButton == null ? h : Math.max(h, nThreadsButton.getPreferredSize().height); 
          return new Dimension(super.getMaximumSize().width, h);
        }
      };
      nThreadsSpinner.setAlignmentX(LEFT_ALIGNMENT);
      nThreadsSpinner.setToolTipText(tt);
      
      nThreadsButton = new JButton("Auto");
      nThreadsButton.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e) {
          int n = Runtime.getRuntime().availableProcessors() - 1;
          if (n > 99) n = 99;
          if (n < 1) n=1;
          nThreadsSpinner.setValue(n);
        }
      });

      Box parallelPanel = Box.createVerticalBox();
      parallelPanel.setBorder(BorderFactory.createTitledBorder("Parallel processing"));
      parallelPanel.setAlignmentX(LEFT_ALIGNMENT);
      Box box = Box.createHorizontalBox();
      box.add(concurrentBox);
      box.add(Box.createHorizontalGlue());
      parallelPanel.add(box);
      nThreadsPanel = Box.createHorizontalBox();
      nThreadsPanel.setEnabled(concurrentBox.isSelected());
      nThreadsPanel.add(nThreadsLabel);
      nThreadsPanel.add(nThreadsSpinner);
      nThreadsPanel.add(add(Box.createRigidArea(new Dimension(HSPACE, 0))));
      nThreadsPanel.add(nThreadsButton);
      nThreadsPanel.add(Box.createHorizontalGlue());
      parallelPanel.add(nThreadsPanel);
      add(parallelPanel);
      add(Box.createVerticalGlue());
      setEnabled();
    }
    
    private void setEnabled() {
      boolean b = concurrentBox.isSelected();
      nThreadsLabel.setEnabled(b);
      nThreadsSpinner.setEnabled(b);
      nThreadsButton.setEnabled(b);
    }
  
  }
  
// -----------------------------------------------------------------------------
}
