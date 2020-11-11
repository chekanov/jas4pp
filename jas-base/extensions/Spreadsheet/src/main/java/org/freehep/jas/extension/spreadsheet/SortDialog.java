package org.freehep.jas.extension.spreadsheet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;

import org.freehep.util.images.ImageHandler;

import org.sharptools.spreadsheet.CellRange;
import org.sharptools.spreadsheet.JSpreadsheet;


/**
 *
 * @author tonyj
 */
class SortDialog extends JOptionPane
{
   private JComboBox primary;
   private JComboBox tiebreaker;
   private JRadioButton ascending1;
   private JRadioButton ascending2;
   private JRadioButton descending1;
   private JRadioButton descending2;

   /** Creates a new instance of FindDialog */
   SortDialog(boolean byRow, CellRange range)
   {
      //gets parameters for combo box in dialog
      Vector first = new Vector();
      Vector second = new Vector();
      second.add("None");
      if (byRow)
      {
         for (int i = range.getStartRow(); i <= range.getEndRow(); i++)
         {
            first.add("Row " + JSpreadsheet.translateRow(i));
            second.add("Row " + JSpreadsheet.translateRow(i));
         }
      }
      else
      {
         for (int i = range.getStartCol(); i <= range.getEndCol(); i++)
         {
            first.add("Column " + JSpreadsheet.translateColumn(i));
            second.add("Column " + JSpreadsheet.translateColumn(i));
         }
      }

      primary = new JComboBox(first);
      primary.setSelectedIndex(0);

      tiebreaker = new JComboBox(second);
      tiebreaker.setSelectedIndex(0);

      JPanel box = new JPanel();

      ascending1 = new JRadioButton("Ascending");
      descending1 = new JRadioButton("Descending");
      ascending2 = new JRadioButton("Ascending");
      descending2 = new JRadioButton("Descending");

      ButtonGroup group = new ButtonGroup();
      ButtonGroup group2 = new ButtonGroup();

      ascending1.setSelected(true);
      group.add(ascending1);
      group.add(descending1);

      ascending2.setSelected(true);
      group2.add(ascending2);
      group2.add(descending2);

      box.setLayout(new GridLayout(0, 3, 10, 5));

      // define key shortcut
      JLabel sortLabel = new JLabel("Sort By:");
      sortLabel.setLabelFor(primary);
      sortLabel.setDisplayedMnemonic(KeyEvent.VK_S);
      ascending1.setMnemonic(KeyEvent.VK_A);
      descending1.setMnemonic(KeyEvent.VK_D);

      box.add(sortLabel);
      box.add(new JLabel(""));
      box.add(new JLabel(""));
      box.add(primary);
      box.add(ascending1);
      box.add(descending1);


      // define key shortcut
      sortLabel = new JLabel("Then By:");
      sortLabel.setLabelFor(tiebreaker);
      sortLabel.setDisplayedMnemonic(KeyEvent.VK_T);
      ascending2.setMnemonic(KeyEvent.VK_C);
      descending2.setMnemonic(KeyEvent.VK_E);

      box.add(sortLabel);
      box.add(new JLabel(""));
      box.add(new JLabel(""));
      box.add(tiebreaker);
      box.add(ascending2);
      box.add(descending2);


      //Border padding = BorderFactory.createEmptyBorder(20, 20, 20, 0);
      //box.setBorder(padding);
      setMessage(box);

      Icon findIcon = ImageHandler.getIcon("image/sort32.gif", SortDialog.class);
      setIcon(findIcon);
      setOptionType(OK_CANCEL_OPTION);
   }

   public int getCriteriaA()
   {
      return primary.getSelectedIndex();
   }

   public int getCriteriaB()
   {
      return tiebreaker.getSelectedIndex() - 1; // Subtract NONE
   }

   public boolean firstAscending()
   {
      return ascending1.isSelected();
   }

   public boolean secondAscending()
   {
      return ascending2.isSelected();
   }

   int show(Component parent, String title)
   {
      JDialog dlg = createDialog(parent, title);
      dlg.pack();
      dlg.setVisible(true);

      Object object = getValue();
      return (object instanceof Integer) ? ((Integer) object).intValue() : CLOSED_OPTION;
   }
}