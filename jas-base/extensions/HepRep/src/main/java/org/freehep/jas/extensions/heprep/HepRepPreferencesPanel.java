package org.freehep.jas.extensions.heprep;

import java.util.Collection;
import java.util.Iterator;
import org.freehep.util.FreeHEPLookup;
import org.openide.util.Lookup;
import hep.graphics.heprep.HepRepViewer;

/**
 *
 * @author  Tony Johnson
 */
public class HepRepPreferencesPanel extends javax.swing.JPanel
{
   
   /** Creates new form HepRepPreferencesPanel */
   public HepRepPreferencesPanel(HepRepPlugin plugin)
   {
      initComponents();
      FreeHEPLookup registry = plugin.getApplication().getLookup();
      Lookup.Result result = registry.lookup(new Lookup.Template(HepRepViewer.class));
      Collection viewers = result.allItems();
      for (Iterator iterator = viewers.iterator(); iterator.hasNext();)
      {
         Lookup.Item item = (Lookup.Item) iterator.next();
         String name = plugin.getViewerName(item);
         jComboBox1.addItem(name);
         if (plugin.getPreferredViewer().equals(name)) jComboBox1.setSelectedItem(name);
      }
   }
   boolean apply(HepRepPlugin plugin)
   {
      Object selected = jComboBox1.getSelectedItem();
      if (selected != null) plugin.setPreferredViewer(selected.toString());
      return true;
   }
   
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   private void initComponents()//GEN-BEGIN:initComponents
   {
      java.awt.GridBagConstraints gridBagConstraints;

      jLabel1 = new javax.swing.JLabel();
      jComboBox1 = new javax.swing.JComboBox();

      setLayout(new java.awt.GridBagLayout());

      jLabel1.setText("Choose Preferred HepRep Viewer");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
      add(jLabel1, gridBagConstraints);

      add(jComboBox1, new java.awt.GridBagConstraints());

   }//GEN-END:initComponents
   
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JComboBox jComboBox1;
   private javax.swing.JLabel jLabel1;
   // End of variables declaration//GEN-END:variables
   
}
