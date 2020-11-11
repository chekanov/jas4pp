package org.freehep.jas.extension.spreadsheet;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.freehep.util.images.ImageHandler;


/**
 *
 * @author tonyj
 */
class NewDialog extends JOptionPane
{
   private JCheckBox saveAsDefault = new javax.swing.JCheckBox("Save as default");
   private SpinnerNumberModel rows;
   private SpinnerNumberModel columns;

   /** Creates a new instance of FindDialog */
   NewDialog(int r, int c)
   {
      rows = new SpinnerNumberModel(r,1,1000,1);
      columns = new SpinnerNumberModel(c,1,1000,1);
      JPanel box = new JPanel(new GridBagLayout());
      GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
      box.add(new JLabel("Rows:"), gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      box.add(new JSpinner(rows), gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 5);
      box.add(new JLabel("Columns:"), gridBagConstraints);
      
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      box.add(new JSpinner(columns), gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
      box.add(saveAsDefault, gridBagConstraints);
      setMessage(box);

      Icon icon = ImageHandler.getIcon("image/spread32.gif", NewDialog.class);
      setIcon(icon);
      setOptionType(OK_CANCEL_OPTION);
   }
   int getRows()
   {
      return rows.getNumber().intValue();
   }
   int getColumns()
   {
      return columns.getNumber().intValue();
   }
   boolean getSaveAsDefault()
   {
      return saveAsDefault.isSelected();
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