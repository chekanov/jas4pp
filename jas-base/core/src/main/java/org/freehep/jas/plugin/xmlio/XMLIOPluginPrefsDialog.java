package org.freehep.jas.plugin.xmlio;

import java.io.File;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
class XMLIOPluginPrefsDialog extends javax.swing.JPanel {
    
    private XMLIOPlugin plugin;
    private String defaultFileName;
    
    /** Creates new form PrefsDialog */
    public XMLIOPluginPrefsDialog(XMLIOPlugin plugin) {
        this.plugin = plugin;
        initComponents();
        defaultFileName = plugin.getDefaultFile();
        saveAtExitCheckBox.setSelected( plugin.getSaveAtExit() );
        restoreAtStartCheckBox.setSelected( plugin.getRestoreAtStart() );
    }
    
    void apply(XMLIOPlugin plugin) {
        plugin.setDefaultFile( defaultFileName );
        plugin.setRestoreAtStart( restoreAtStartCheckBox.isSelected() );
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        defaultFilePanel = new javax.swing.JPanel();
        defaultFileBrowseButton = new javax.swing.JButton();
        restorePanel = new javax.swing.JPanel();
        restoreAtStartCheckBox = new javax.swing.JCheckBox();
        savePanel = new javax.swing.JPanel();
        saveAtExitCheckBox = new javax.swing.JCheckBox();

        setLayout(new java.awt.GridBagLayout());

        setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        defaultFilePanel.setLayout(new java.awt.GridBagLayout());

        defaultFileBrowseButton.setText("Set Default Configuration File...");
        defaultFileBrowseButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                defaultFileBrowseButtonMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        defaultFilePanel.add(defaultFileBrowseButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(defaultFilePanel, gridBagConstraints);

        restorePanel.setLayout(new java.awt.GridLayout(1, 0));

        restoreAtStartCheckBox.setText("Restore configuration on start");
        restoreAtStartCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreAtStartCheckBoxActionPerformed(evt);
            }
        });

        restorePanel.add(restoreAtStartCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(restorePanel, gridBagConstraints);

        savePanel.setLayout(new java.awt.GridLayout());

        saveAtExitCheckBox.setText("Save configuration on exit");
        saveAtExitCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAtExitCheckBoxActionPerformed(evt);
            }
        });

        savePanel.add(saveAtExitCheckBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(savePanel, gridBagConstraints);

    }//GEN-END:initComponents

    private void saveAtExitCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAtExitCheckBoxActionPerformed
        plugin.setSaveAtExit( saveAtExitCheckBox.isSelected() );
    }//GEN-LAST:event_saveAtExitCheckBoxActionPerformed

    private void restoreAtStartCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreAtStartCheckBoxActionPerformed
        plugin.setRestoreAtStart( restoreAtStartCheckBox.isSelected() );
    }//GEN-LAST:event_restoreAtStartCheckBoxActionPerformed

    private void defaultFileBrowseButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_defaultFileBrowseButtonMouseClicked
        String fileName = plugin.chooseFile(new File( plugin.getDefaultFile() ), "Set" );
        if ( fileName != null ) 
            defaultFileName = fileName;
    }//GEN-LAST:event_defaultFileBrowseButtonMouseClicked
                        
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox saveAtExitCheckBox;
    private javax.swing.JCheckBox restoreAtStartCheckBox;
    private javax.swing.JPanel savePanel;
    private javax.swing.JButton defaultFileBrowseButton;
    private javax.swing.JPanel restorePanel;
    private javax.swing.JPanel defaultFilePanel;
    // End of variables declaration//GEN-END:variables
    
}
