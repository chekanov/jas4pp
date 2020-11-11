package org.freehep.jas.extension.spreadsheet;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.KeyEvent;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.freehep.util.images.ImageHandler;

/**
 *
 * @author tonyj
 */
class FindDialog extends JOptionPane
{
   private JTextField textField;
   private JCheckBox caseSensitiveBox;
   private JCheckBox matchCellBox;
   /** Creates a new instance of FindDialog */
   FindDialog(String findValue, boolean mCase, boolean mCell)
   {
      textField = new JTextField(findValue);

      caseSensitiveBox = new JCheckBox("Match Case");
      caseSensitiveBox.setMnemonic(KeyEvent.VK_M);
      caseSensitiveBox.setSelected(mCase);

      matchCellBox = new JCheckBox("Match Entire Cell Only");
      matchCellBox.setMnemonic(KeyEvent.VK_E); 
      matchCellBox.setSelected(mCell);

      JPanel box = new JPanel(new BorderLayout(0, 5));

      box.add(textField, BorderLayout.NORTH);
      box.add(caseSensitiveBox, BorderLayout.WEST);
      box.add(matchCellBox, BorderLayout.EAST);
      setMessage(box);
      Icon findIcon = ImageHandler.getIcon("image/find32.gif",FindDialog.class); 
      setIcon(findIcon);
      setOptionType(OK_CANCEL_OPTION);
   }
    boolean isCaseSensitive(){
	return caseSensitiveBox.isSelected();		
    }
    
    boolean isCellMatching(){
	return matchCellBox.isSelected();
    }
    
    String getString(){
	return textField.getText();
    }   
    int show(Component parent, String title)
    {
       JDialog dlg = createDialog(parent,title);
       textField.selectAll();
       textField.requestFocus();
       dlg.pack();
       dlg.setVisible(true);
       Object object = getValue();
       return object instanceof Integer ? ((Integer) object).intValue() : CLOSED_OPTION;
    }
}
