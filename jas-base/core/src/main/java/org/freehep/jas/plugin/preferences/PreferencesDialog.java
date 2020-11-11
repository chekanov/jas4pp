package org.freehep.jas.plugin.preferences;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.freehep.application.studio.Studio;
import org.freehep.jas.services.PreferencesTopic;

/**
 *
 * @author  tonyj
 */
class PreferencesDialog extends JOptionPane implements TreeSelectionListener
{
   private JLabel prefLabel;
   private PreferencesTree tree;
   private JScrollPane scrollPane;
   private Studio app;
   private PreferencesTopic current;
   private String[] myOptions = {"Ok", "Apply", "Cancel"};
   
   /** Creates a new instance of PreferencesDialog */
   public PreferencesDialog(Studio app)
   {
      this.app = app;
      
      tree = new PreferencesTree(app.getLookup());
      tree.addTreeSelectionListener(this);
      
      JPanel box = new JPanel(new BorderLayout());
      box.add(new JScrollPane(tree),BorderLayout.WEST);
      
      JPanel jPanel2 = new JPanel(new GridBagLayout());
      GridBagConstraints gridBagConstraints;
      
      prefLabel = new JLabel("Preferences: ");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
      gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
      gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
      jPanel2.add(prefLabel, gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      jPanel2.add(new JSeparator(), gridBagConstraints);

      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
      gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      scrollPane = new JScrollPane(new JTextPane());
      jPanel2.add(scrollPane, gridBagConstraints);
      box.add(jPanel2,BorderLayout.CENTER);      
      setMessage(box);
      setMessageType(PLAIN_MESSAGE);
      setOptionType(YES_NO_CANCEL_OPTION);
      setOptions(myOptions);
      setPreferredSize(new Dimension(500,350));
   }
   void showDialog()
   {
      JDialog dlg = this.createDialog(app,"Preferences");
      dlg.setVisible(true);
      
   }
   void selectTopic(String[] topic)
   {
      // FixMe:
   }
   public void valueChanged(TreeSelectionEvent e)
   {
      PreferencesTopic topic = tree.getSelectedTopic();
      if (topic != current)
      {
         if (current != null)
         {
            boolean ok = current.apply((JComponent) scrollPane.getViewport().getView());
            if (!ok)
            { 
               tree.setSelectionPath(e.getOldLeadSelectionPath());
               return;
            }
         }
         current = topic;
         if (topic != null)
         {
            JComponent panel = topic.component();
            scrollPane.setViewportView(panel);
            StringBuffer label = new StringBuffer();
            String[] path = topic.path();
            for (int i=0; i<path.length; i++) 
            {
               label.append(path[i]);
               label.append(' ');
            }
            prefLabel.setText("Preferences: "+label);
         }  else {
           scrollPane.setViewportView(null);
           prefLabel.setText("Preferences: "+ tree.getSelectionPath().getLastPathComponent().toString());
         }
      }
   }
   public void setValue(Object newValue)
   {
      boolean ok = newValue == myOptions[0];
      boolean apply = newValue == myOptions[1];
      boolean close = true;
      if (ok || apply) close = apply();
      close &= !apply;
      if (close) super.setValue(newValue);
   }
   private boolean apply()
   {
      if (current != null)
      {
         return current.apply((JComponent) scrollPane.getViewport().getView());
      }
      else return true;
   }
}
