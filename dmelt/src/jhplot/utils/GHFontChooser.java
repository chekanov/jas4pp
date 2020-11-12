package jhplot.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ToolTipManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;


/**
 *  Font selection dialog
 *  
 * @author S.Chekanov
 *
 */


public class GHFontChooser extends JDialog {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

protected int Closed_Option = JOptionPane.CLOSED_OPTION;

  protected InputList fontNameInputList;

  protected InputList fontSizeInputList;

  protected MutableAttributeSet attributes;

  protected JCheckBox boldCheckBox = new JCheckBox("Bold");

  protected JCheckBox italicCheckBox = new JCheckBox("Italic");

  protected JCheckBox underlineCheckBox = new JCheckBox("Underline");

  protected JCheckBox strikethroughCheckBox = new JCheckBox("Strikethrough");

  protected JCheckBox subscriptCheckBox = new JCheckBox("Subscript");

  protected JCheckBox superscriptCheckBox = new JCheckBox("Superscript");

  protected ColorComboBox colorComboBox;
  
  protected ColorComboBox colorComboBoxBack;

  protected FontLabel previewLabel;
  
  protected String inputText;

  public static String[] fontNames;

  public static String[] fontSizes;

  public JTextField sepField;

  private int WIDTH,HEIGHT;

  /**
   * 
   * @param owner
   * @param inputString Input text
   */
  public GHFontChooser(Container owner, String inputString) {
   
	 
	  
	
	  
	 WIDTH=350;
	 HEIGHT=400;
	  
    getContentPane().setLayout(
        new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    
    
    setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    setTitle("Title editor");
    setModal( true );
    setResizable( false);

  
    
    
    GraphicsEnvironment ge = GraphicsEnvironment
    .getLocalGraphicsEnvironment();
    
    fontNames = ge.getAvailableFontFamilyNames();
    fontSizes = new String[] { "7","8", "9", "10", "11", "12","13", "14","15", "16",
        "17","18", "20", "22", "24", "26", "28", "30", "32", "34","36", "40", "48", "72" };
    
  
    fontNameInputList = new InputList(fontNames, "Name:");

    fontSizeInputList = new InputList(fontSizes, "Size:");
    
    
   
    
    JPanel p = new JPanel(new GridLayout(1, 2, 10, 2));
    
    
     // p.setPreferredSize( new Dimension(WIDTH,40) );
   
   
    
    p.setBorder(new TitledBorder(new EtchedBorder(), "Font"));
    p.add(fontNameInputList);
    fontNameInputList.setDisplayedMnemonic('n');
    fontNameInputList.setToolTipText("Font name");

    p.add(fontSizeInputList);
    fontSizeInputList.setDisplayedMnemonic('s');
    fontSizeInputList.setToolTipText("Font size");
 
     add( p);
    
    
  
    
    
    
    p = new JPanel(new GridLayout(1, 4, 10, 5));
    p.setBorder(new TitledBorder(new EtchedBorder(), "Effects"));
    boldCheckBox.setMnemonic('b');
    boldCheckBox.setToolTipText("Bold font");
    p.add(boldCheckBox);

    italicCheckBox.setMnemonic('i');
    italicCheckBox.setToolTipText("Italic font");
    p.add(italicCheckBox);


    colorComboBox = new ColorComboBox();
    colorComboBox.setBorder(new TitledBorder(new EtchedBorder(), "Font color"));
    colorComboBox.setToolTipText("Font color");
    ToolTipManager.sharedInstance().registerComponent(colorComboBox);
    p.add(colorComboBox);
    getContentPane().add(p);

    
    colorComboBoxBack = new ColorComboBox();
    colorComboBoxBack.setBorder(new TitledBorder(new EtchedBorder(), "Backg. color"));
    colorComboBoxBack.setToolTipText("Background color");
    ToolTipManager.sharedInstance().registerComponent(colorComboBoxBack);
    p.add(colorComboBoxBack);
    getContentPane().add(p);
    

    getContentPane().add(Box.createVerticalStrut(5));
    p = new JPanel();
    p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
    p.add(Box.createHorizontalStrut(10));

    JLabel lbl = new JLabel("Title:");
    lbl.setDisplayedMnemonic('t');
    p.add(lbl);
    p.add(Box.createHorizontalStrut(24));


    sepField = new JTextField(inputString,2);
    p.add(sepField);



    p.add(Box.createHorizontalStrut(10));
    getContentPane().add(p);

    p = new JPanel(new BorderLayout());
    p.setBorder(new TitledBorder(new EtchedBorder(), "Preview"));
    previewLabel = new FontLabel("Preview Font");

    p.add(previewLabel, BorderLayout.CENTER);
    getContentPane().add(p);

    p = new JPanel(new FlowLayout());
    JPanel p1 = new JPanel(new GridLayout(1, 2, 10, 2));
    JButton btOK = new JButton("OK");
    btOK.setToolTipText("Save and exit");
    getRootPane().setDefaultButton(btOK);
    ActionListener actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Closed_Option = JOptionPane.OK_OPTION;
        dispose();
      }
    };
    btOK.addActionListener(actionListener);
    p1.add(btOK);

    JButton btCancel = new JButton("Cancel");
    btCancel.setToolTipText("Exit without save");
    actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        Closed_Option = JOptionPane.CANCEL_OPTION;
        dispose();
      }
    };
    btCancel.addActionListener(actionListener);
    p1.add(btCancel);
    p.add(p1);
    getContentPane().add(p);

  

    ListSelectionListener listSelectListener = new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        updatePreview();
      }
    };
    fontNameInputList.addListSelectionListener(listSelectListener);
    fontSizeInputList.addListSelectionListener(listSelectListener);

    actionListener = new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        updatePreview();
      }
    };
    boldCheckBox.addActionListener(actionListener);
    italicCheckBox.addActionListener(actionListener);
    colorComboBox.addActionListener(actionListener);

  
    
    
 //      setSize(size);
    

//  set visible and put on center
    // make sure the dialog is not too big
     pack();
  //   setVisible(true);
    
    
    
    
  }

  
  
  /**
   * Get outour string
   * 
   * @return
   */
  public String getInputText() {
	  return sepField.getText();
	  
  }
  
  
  
  
  
  
  public void setAttributes(AttributeSet a) {
    attributes = new SimpleAttributeSet(a);
    String name = StyleConstants.getFontFamily(a);
    fontNameInputList.setSelected(name);
    int size = StyleConstants.getFontSize(a);
    fontSizeInputList.setSelectedInt(size);
    boldCheckBox.setSelected(StyleConstants.isBold(a));
    italicCheckBox.setSelected(StyleConstants.isItalic(a));
    underlineCheckBox.setSelected(StyleConstants.isUnderline(a));
    strikethroughCheckBox.setSelected(StyleConstants.isStrikeThrough(a));
    subscriptCheckBox.setSelected(StyleConstants.isSubscript(a));
    superscriptCheckBox.setSelected(StyleConstants.isSuperscript(a));
    colorComboBox.setSelectedItem(StyleConstants.getForeground(a));
    colorComboBoxBack.setSelectedItem(StyleConstants.getBackground(a));
    updatePreview();
  }

  public AttributeSet getAttributes() {
    if (attributes == null)
      return null;
    StyleConstants.setFontFamily(attributes, fontNameInputList
        .getSelected());
    StyleConstants.setFontSize(attributes, fontSizeInputList
        .getSelectedInt());
    StyleConstants.setBold(attributes, boldCheckBox.isSelected());
    StyleConstants.setItalic(attributes, italicCheckBox.isSelected());
    StyleConstants.setUnderline(attributes, underlineCheckBox.isSelected());
    StyleConstants.setStrikeThrough(attributes, strikethroughCheckBox
        .isSelected());
    StyleConstants.setSubscript(attributes, subscriptCheckBox.isSelected());
    StyleConstants.setSuperscript(attributes, superscriptCheckBox
        .isSelected());
    StyleConstants.setForeground(attributes, (Color) colorComboBox
        .getSelectedItem());
    StyleConstants.setBackground(attributes, (Color) colorComboBoxBack
            .getSelectedItem());
    return attributes;
  }

  
  /**
   * 
   * @param owner
   * @param inputString Input text
   */
  public GHFontChooser(JComponent  owner) {
             this(owner,"test");
  }
  
  
  
  
  
  public int getOption() {
    return Closed_Option;
  }

  protected void updatePreview() {
    String name = fontNameInputList.getSelected();
    int size = fontSizeInputList.getSelectedInt();
    if (size <= 0)
      return;
    int style = Font.PLAIN;
    if (boldCheckBox.isSelected())
      style |= Font.BOLD;
    if (italicCheckBox.isSelected())
      style |= Font.ITALIC;

    Font fn = new Font(name, style, size);
    previewLabel.setFont(fn);

    Color c = (Color) colorComboBox.getSelectedItem();
    previewLabel.setForeground(c);
    previewLabel.repaint();
  }

  public static void main(String argv[]) {
    
    /*
    AdvancedFontChooser dlg = new AdvancedFontChooser(new JFrame());
    SimpleAttributeSet a = new SimpleAttributeSet();
    StyleConstants.setFontFamily(a, "Monospaced");
    StyleConstants.setFontSize(a, 12);
    dlg.setAttributes(a);
    dlg.setVisible(true);
    */
  }
}

class InputList extends JPanel implements ListSelectionListener, ActionListener {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

protected JLabel label = new JLabel();

  protected JTextField textfield;
  

  protected JList list;

  protected JScrollPane scroll;

  public InputList(String[] data, String title) {
    setLayout(null);

    add(label);
    textfield = new OpelListText();
    textfield.setEditable(false);

    textfield.addActionListener(this);
    label.setLabelFor(textfield);
    add(textfield);
    list = new OpelListList(data);
    list.setVisibleRowCount(4);
    list.addListSelectionListener(this);
    scroll = new JScrollPane(list);
    add(scroll);
  }

  public InputList(String title, int numCols) {
    setLayout(null);
    label = new OpelListLabel(title, JLabel.LEFT);
    add(label);
    textfield = new OpelListText(numCols);
    textfield.addActionListener(this);
    label.setLabelFor(textfield);
    add(textfield);
    list = new OpelListList();
    list.setVisibleRowCount(4);
    list.addListSelectionListener(this);
    scroll = new JScrollPane(list);
    add(scroll);
  }

  public void setToolTipText(String text) {
    super.setToolTipText(text);
    label.setToolTipText(text);
    textfield.setToolTipText(text);
    list.setToolTipText(text);
  }

  public void setDisplayedMnemonic(char ch) {
    label.setDisplayedMnemonic(ch);
  }

  public void setSelected(String sel) {
    list.setSelectedValue(sel, true);
    textfield.setText(sel);
  }

  
  public String getSelected() {
    return textfield.getText();
  }

  
  
  
  
  public void setSelectedInt(int value) {
    setSelected(Integer.toString(value));
  }

  public int getSelectedInt() {
    try {
      return Integer.parseInt(getSelected());
    } catch (NumberFormatException ex) {
      return -1;
    }
  }

  public void valueChanged(ListSelectionEvent e) {
    Object obj = list.getSelectedValue();
    if (obj != null)
      textfield.setText(obj.toString());
  }

  public void actionPerformed(ActionEvent e) {
    ListModel model = list.getModel();
    String key = textfield.getText().toLowerCase();
    for (int k = 0; k < model.getSize(); k++) {
      String data = (String) model.getElementAt(k);
      if (data.toLowerCase().startsWith(key)) {
        list.setSelectedValue(data, true);
        break;
      }
    }
  }

  public void addListSelectionListener(ListSelectionListener lst) {
    list.addListSelectionListener(lst);
  }

  public Dimension getPreferredSize() {
 

	Insets ins = getInsets();
    Dimension labelSize = label.getPreferredSize();
    Dimension textfieldSize = textfield.getPreferredSize();
    Dimension scrollPaneSize = scroll.getPreferredSize();
    int w = Math.max(Math.max(labelSize.width, textfieldSize.width),
        scrollPaneSize.width);
    int h = labelSize.height + textfieldSize.height + scrollPaneSize.height;
    return new Dimension(w + ins.left + ins.right, h + ins.top + ins.bottom);
 
  
	//  return new Dimension(400, 500); 
  }

  public Dimension getMaximumSize() {
    return getPreferredSize();
  }

  public Dimension getMinimumSize() {
    return getPreferredSize();
  }

  public void doLayout() {
    Insets ins = getInsets();
    Dimension size = getSize();
    int x = ins.left;
    int y = ins.top;
    int w = size.width - ins.left - ins.right;
    int h = size.height - ins.top - ins.bottom;

    Dimension labelSize = label.getPreferredSize();
    label.setBounds(x, y, w, labelSize.height);
    y += labelSize.height;
    Dimension textfieldSize = textfield.getPreferredSize();
    textfield.setBounds(x, y, w, textfieldSize.height);
    y += textfieldSize.height;
    scroll.setBounds(x, y, w, h - y);
  }

  public void appendResultSet(ResultSet results, int index,
      boolean toTitleCase) {
    textfield.setText("");
    DefaultListModel model = new DefaultListModel();
    try {
      while (results.next()) {
        String str = results.getString(index);
        if (toTitleCase) {
          str = Character.toUpperCase(str.charAt(0))
              + str.substring(1);
        }

        model.addElement(str);
      }
    } catch (SQLException ex) {
      System.err.println("appendResultSet: " + ex.toString());
    }
    list.setModel(model);
    if (model.getSize() > 0)
      list.setSelectedIndex(0);
  }

  class OpelListLabel extends JLabel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpelListLabel(String text, int alignment) {
      super(text, alignment);
    }

    public AccessibleContext getAccessibleContext() {
      return InputList.this.getAccessibleContext();
    }
  }

  class OpelListText extends JTextField {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpelListText() {
    }

    public OpelListText(int numCols) {
      super(numCols);
 
    }

    public AccessibleContext getAccessibleContext() {
      return InputList.this.getAccessibleContext();
    }
  }

  class OpelListList extends JList {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OpelListList() {
    }

    public OpelListList(String[] data) {
      super(data);
    }

    public AccessibleContext getAccessibleContext() {
      return InputList.this.getAccessibleContext();
    }
  }

  // Accessibility Support

  public AccessibleContext getAccessibleContext() {
    if (accessibleContext == null)
      accessibleContext = new AccessibleOpenList();
    return accessibleContext;
  }

  protected class AccessibleOpenList extends AccessibleJComponent {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getAccessibleName() {
      System.out.println("getAccessibleName: " + accessibleName);
      if (accessibleName != null)
        return accessibleName;
      return label.getText();
    }

    public AccessibleRole getAccessibleRole() {
      return AccessibleRole.LIST;
    }
  }
}

class FontLabel extends JLabel {
  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

public FontLabel(String text) {
    super(text, JLabel.CENTER);
    setBackground(Color.white);
    setForeground(Color.black);
    setOpaque(true);
    setBorder(new LineBorder(Color.black));
    setPreferredSize(new Dimension(120, 40));
  }
}

class ColorComboBox extends JComboBox {

	
	private static final long serialVersionUID = 1L;

public ColorComboBox() {
    int[] values = new int[] { 0, 128, 192, 255 };
    for (int r = 0; r < values.length; r++)
      for (int g = 0; g < values.length; g++)
        for (int b = 0; b < values.length; b++) {
          Color c = new Color(values[r], values[g], values[b]);
          addItem(c);
        }
    setRenderer(new ColorComboRenderer1());

  }

  class ColorComboRenderer1 extends JPanel implements ListCellRenderer {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Color m_c = Color.black;

    public ColorComboRenderer1() {
      super();
      setBorder(new CompoundBorder(new MatteBorder(2, 10, 2, 10,
          Color.white), new LineBorder(Color.black)));
    }

    public Component getListCellRendererComponent(JList list, Object obj,
        int row, boolean sel, boolean hasFocus) {
      if (obj instanceof Color)
        m_c = (Color) obj;
      return this;
    }

    public void paint(Graphics g) {
      setBackground(m_c);
      super.paint(g);
    }

  }

}
