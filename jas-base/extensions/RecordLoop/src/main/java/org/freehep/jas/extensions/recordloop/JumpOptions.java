package org.freehep.jas.extensions.recordloop;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.*;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;

import org.freehep.record.source.DefaultRecordTag;
import org.freehep.record.source.RecordSource;
import org.freehep.record.source.RecordTag;

/**
 * Class that interacts with user and gets parameters of "Go To" command.
 *
 * @author Dmitry Onoprienko
 * @version $Id: $
 */
public final class JumpOptions {

// -- Private parts : ----------------------------------------------------------

  static private final int RELATIVE = 0;
  static private final int INDEX = 1;
  static private final int TAG = 2;
  static private final String[] MODE = new String[] {"Shift", "Index", "Tag"};

  private RecordSource _source;

  private JPanel _mainPanel;
  private ActionListener _radioListener;
  private ButtonGroup _rbGroup;

  private JSpinner _relativeSpinner;
  private JFormattedTextField _indexBox;
  private JComboBox _tagBox;
  private List<RecordTag> _tagList;


// -- Construction : -----------------------------------------------------------

  public JumpOptions(RecordSource source) {

    _source = source;

    _mainPanel = new JPanel();
    _mainPanel.setLayout(new BoxLayout(_mainPanel, BoxLayout.Y_AXIS));
    _mainPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

    _rbGroup = new ButtonGroup();
    _radioListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(MODE[RELATIVE])) {
          if (_relativeSpinner != null) _relativeSpinner.setEnabled(true);
          if (_indexBox != null) _indexBox.setEnabled(false);
          if (_tagBox != null) _tagBox.setEnabled(false);
        } else if (command.equals(MODE[INDEX])) {
          if (_relativeSpinner != null) _relativeSpinner.setEnabled(false);
          if (_indexBox != null) _indexBox.setEnabled(true);
          if (_tagBox != null) _tagBox.setEnabled(false);
        } else {
          if (_relativeSpinner != null) _relativeSpinner.setEnabled(false);
          if (_indexBox != null) _indexBox.setEnabled(false);
          if (_tagBox != null) _tagBox.setEnabled(true);
        }
      }
    };

    JPanel panel;
    JRadioButton rb;
    if (_source.supportsShift()) {
      panel = new JPanel();
      panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      rb = new JRadioButton(MODE[RELATIVE]);
      rb.setMnemonic(KeyEvent.VK_R);
      rb.setActionCommand(MODE[RELATIVE]);
      rb.setSelected(true);
      rb.addActionListener(_radioListener);
      _rbGroup.add(rb);
      panel.add(rb);
      panel.add(Box.createRigidArea(new Dimension(3, 3)));
      panel.add(Box.createHorizontalGlue());
      _relativeSpinner = new JSpinner();
      panel.add(_relativeSpinner);
      _mainPanel.add(panel);
    }
    if (_source.supportsIndex()) {
      panel = new JPanel();
      panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      rb = new JRadioButton(MODE[INDEX]);
      rb.setMnemonic(KeyEvent.VK_A);
      rb.setActionCommand(MODE[INDEX]);
      rb.setSelected(false);
      rb.addActionListener(_radioListener);
      _rbGroup.add(rb);
      panel.add(rb);
      panel.add(Box.createRigidArea(new Dimension(3, 3)));
      panel.add(Box.createHorizontalGlue());
      _indexBox = new JFormattedTextField(0);
      _indexBox.setEnabled(false);
      panel.add(_indexBox);
      _mainPanel.add(panel);
    }
    if (_source.supportsTag()) {
      panel = new JPanel();
      panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
      panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
      rb = new JRadioButton(MODE[TAG]);
      rb.setMnemonic(KeyEvent.VK_T);
      rb.setActionCommand(MODE[TAG]);
      rb.setSelected(false);
      rb.addActionListener(_radioListener);
      _rbGroup.add(rb);
      panel.add(rb);
      panel.add(Box.createHorizontalGlue());
      _mainPanel.add(panel);
      panel.add(Box.createRigidArea(new Dimension(3,3)));
      panel.add(Box.createHorizontalGlue());
      _tagBox = createTagBox();
      _tagBox.setEnabled(false);
      panel.add(_tagBox);
    }

  }

// -- Showing the dialog : -----------------------------------------------------

  /** Returns GUI panel for selecting record to be loaded. */
  public JPanel getPanel() {
    return _mainPanel;
  }

  /** Returns currently selected options. */
  public Object[] getParameters() {
    String mode = _rbGroup.getSelection().getActionCommand();
    if (mode.equals(MODE[RELATIVE])) {
      int skip = (Integer) _relativeSpinner.getValue();
      return new Object[] {true, Long.valueOf(skip)};
    } else if (mode.equals(MODE[INDEX])) {
      int index = (Integer) _indexBox.getValue();
      return new Object[] {false, Long.valueOf(index)};
    } else {
      int i = _tagBox.getSelectedIndex();
      RecordTag tag = (i>0) ? _tagList.get(i) : _source.parseTag((String)_tagBox.getSelectedItem());
      return new Object[] {tag};
    }
  }

  public JComboBox createTagBox() {
    try {
      _tagList = _source.getTags();
    } catch (UnsupportedOperationException x) {
      _tagList = null;
    }
    JComboBox out = null;
    int nTags = _tagList == null ? 0 : _tagList.size();
    RecordTag currentTag = null;
    try {
      currentTag = _source.getCurrentTag();
    } catch (IllegalStateException x) {}
    if (_tagList != null && nTags < 30) {
      String[] names = new String[nTags];
      for (int i = 0; i < nTags; i++) {
        names[i] = _tagList.get(i).humanReadableName();
      }
      out = new JComboBox(names);
      if (currentTag != null) {
        out.setSelectedItem(currentTag.humanReadableName());
      }
    } else {
      out = new JComboBox();
      if (currentTag != null) {
        out.addItem(currentTag.humanReadableName());
        out.setSelectedIndex(0);
      }
    }
    out.setEditable(true);
    return out;
  }

}

