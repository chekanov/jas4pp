package org.freehep.jas.plugin.tree;

import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import org.freehep.application.Application;
import org.freehep.application.studio.Studio;
import org.freehep.jas.plugin.tree.FTreeSortingChooser;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
class FTreePreferencesDialog extends javax.swing.JPanel {
    
    public FTreePreferencesDialog() {
        initComponents();
    }
    
    void apply() {
    }

    private void initComponents() {//GEN-BEGIN:initComponents
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        jButton1.setText("Sorting ...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        add(jButton1, new java.awt.GridBagConstraints());

    }//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        Application app = Studio.getApplication();
        Frame frame = (Frame) SwingUtilities.getAncestorOfClass(Frame.class, app);
        JDialog dlg = new JDialog(frame,true);
        FTreeSortingChooser sortingChooser = new FTreeSortingChooser(dlg);
        dlg.getContentPane().add(sortingChooser);
        dlg.setLocationRelativeTo(app);
        dlg.pack();
        dlg.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed
                    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    // End of variables declaration//GEN-END:variables
    
}
