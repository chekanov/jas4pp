package jhplot.utils;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;


/**
 * A font selection dialog.
 * <p>
 * @author Ian Darwin, S,Chekanov 
 * @version $Id: ShowFonts.java,v 1.19 2008/03/20 20:44:56 ian Exp $
 */
public class ShowFonts extends JFrame  {

  // Results:

  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;

/** The font the user has chosen */
  protected Font resultFont;

  /** The resulting font name */
  protected String resultName;

  /** The resulting font size */
  protected int resultSize;

  /** The resulting boldness */
  protected boolean isBold;

  /** The resulting italicness */
  protected boolean isItalic;

  // Working fields

  /** Display text */
  protected String displayText = "Qwerty Yuiop";

  /** The list of Fonts */
  protected String fontList[];

  /** The font name chooser */
  protected List fontNameChoice;

  /** The font size chooser */
  protected List fontSizeChoice;

  /** The bold and italic choosers */
  Checkbox bold, italic;

  /** The list of font sizes */
  protected String fontSizes[] = { "8", "10", "11", "12", "14", "16", "18",
      "20", "24", "30", "36", "40", "48", "60", "72" };

  /** The index of the default size (e.g., 14 point == 4) */
  protected static final int DEFAULT_SIZE = 4;

  /**
   * The display area. Use a JLabel as the AWT label doesn't always honor
   * setFont() in a timely fashion :-)
   */
  protected JLabel previewArea;

  /**
   * Construct a FontChooser -- Sets title and gets array of fonts on the
   * system. Builds a GUI to let the user choose one font at one size.
   */
  public ShowFonts() {

    Container cp = getContentPane();
    JPanel top = new JPanel();
    top.setLayout(new FlowLayout());

    fontNameChoice = new List(8);
    top.add(fontNameChoice);

    Toolkit toolkit = Toolkit.getDefaultToolkit();
    // For JDK 1.1: returns about 10 names (Serif, SansSerif, etc.)
    // fontList = toolkit.getFontList();
    // For JDK 1.2: a much longer list; most of the names that come
    // with your OS (e.g., Arial), plus the Sun/Java ones (Lucida,
    // Lucida Bright, Lucida Sans...)
    fontList = GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getAvailableFontFamilyNames();

    for (int i = 0; i < fontList.length; i++)
      fontNameChoice.add(fontList[i]);
    fontNameChoice.select(0);

    fontSizeChoice = new List(8);
    top.add(fontSizeChoice);

    for (int i = 0; i < fontSizes.length; i++)
      fontSizeChoice.add(fontSizes[i]);
    fontSizeChoice.select(DEFAULT_SIZE);

    cp.add(top, BorderLayout.NORTH);

    Panel attrs = new Panel();
    top.add(attrs);
    attrs.setLayout(new GridLayout(0, 1));
    attrs.add(bold = new Checkbox("Bold", false));
    attrs.add(italic = new Checkbox("Italic", false));

    previewArea = new JLabel(displayText, JLabel.CENTER);
    previewArea.setSize(200, 50);
    cp.add(previewArea, BorderLayout.CENTER);

    Panel bot = new Panel();

    JButton okButton = new JButton("Apply");
    bot.add(okButton);
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        previewFont();
        dispose();
        setVisible(false);
      }
    });

    JButton pvButton = new JButton("Preview");
    bot.add(pvButton);
    pvButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        previewFont();
      }
    });

    JButton canButton = new JButton("Cancel");
    bot.add(canButton);
    canButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Set all values to null. Better: restore previous.
        resultFont = null;
        resultName = null;
        resultSize = 0;
        isBold = false;
        isItalic = false;

        dispose();
        setVisible(false);
      }
    });

    cp.add(bot, BorderLayout.SOUTH);

    previewFont(); // ensure view is up to date!
    setLocation(100 , 100);
    cp.setLayout(new GridLayout(0, 1)); // one vertical column
    setSize(400, 450);
    setVisible(true);
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

  }

  /**
   * Called from the action handlers to get the font info, build a font, and
   * set it.
   */
  protected void previewFont() {
    resultName = fontNameChoice.getSelectedItem();
    String resultSizeName = fontSizeChoice.getSelectedItem();
    int resultSize = Integer.parseInt(resultSizeName);
    isBold = bold.getState();
    isItalic = italic.getState();
    int attrs = Font.PLAIN;
    if (isBold)
      attrs = Font.BOLD;
    if (isItalic)
      attrs |= Font.ITALIC;
    resultFont = new Font(resultName, attrs, resultSize);
    // System.out.println("resultName = " + resultName + "; " +
    //     "resultFont = " + resultFont);
    previewArea.setFont(resultFont);
    //pack(); // ensure Dialog is big enough.
  }

  /** Retrieve the selected font name. */
  public String getSelectedName() {
    return resultName;
  }

  /** Retrieve the selected size */
  public int getSelectedSize() {
    return resultSize;
  }

  /** Retrieve the selected font, or null */
  public Font getSelectedFont() {
    return resultFont;
  }

  /** Simple main program to start it running */
  public static void main(String[] args) {
    final ShowFonts fc = new ShowFonts();
  }
}
