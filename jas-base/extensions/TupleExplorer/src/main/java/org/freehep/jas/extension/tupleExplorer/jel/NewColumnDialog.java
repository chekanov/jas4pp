package org.freehep.jas.extension.tupleExplorer.jel;

import gnu.jel.CompilationException;
import javax.swing.JOptionPane;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;

/**
 *
 * @author  tonyj
 */
public class NewColumnDialog extends javax.swing.JDialog implements javax.swing.event.DocumentListener {
    private MutableTuple tuple;
    private MutableTupleTree tupleTree;
    
    /** Creates new form NewColumnDialog */
    public NewColumnDialog(java.awt.Frame frame, MutableTuple tuple, MutableTupleTree tupleTree) {
        super(frame, true);
        this.tuple = tuple;
        this.tupleTree = tupleTree;
        initComponents();
        doEnable();
        getRootPane().setDefaultButton(okButton);
        nameField.getDocument().addDocumentListener(this);
        expressionField.getDocument().addDocumentListener(this);
    }
    private void doEnable() {
        okButton.setEnabled(nameField.getText().length()>0 && expressionField.getText().length()>0);
    }
    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        nameField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        expressionField = new org.freehep.jas.extension.tupleExplorer.jel.ExpressionField(tuple, tupleTree);
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Define New Column");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        mainPanel.setLayout(new java.awt.GridBagLayout());

        mainPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        jLabel1.setText("Column Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mainPanel.add(jLabel1, gridBagConstraints);

        nameField.setColumns(30);
        nameField.setToolTipText("Enter name for new column");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        mainPanel.add(nameField, gridBagConstraints);

        jLabel2.setText("Expression");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mainPanel.add(jLabel2, gridBagConstraints);

        expressionField.setToolTipText("Expression to be evaluated");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainPanel.add(expressionField, gridBagConstraints);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        okButton.setMnemonic('O');
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(okButton);

        cancelButton.setMnemonic('C');
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(cancelButton);

        helpButton.setMnemonic('H');
        helpButton.setText("Help");
        helpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpButtonActionPerformed(evt);
            }
        });

        buttonPanel.add(helpButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }//GEN-END:initComponents
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed
        String colName = nameField.getText();
        MutableTupleColumn col = null;
        try {
            tuple.columnByName(colName);
            JOptionPane.showMessageDialog(this,"Column "+colName+" already in use","Error",JOptionPane.ERROR_MESSAGE);
        } catch ( RuntimeException re ) {
            try {
                NTupleCompiledExpression exp = expressionField.compile(null);
                col = new JELColumn(tuple,tupleTree,colName,expressionField.getText(),exp);
                if ( ! ( (JELColumn) col).getLeadingPath().getParentPath().isDescendant( tuple.treePath() ) )
                    JOptionPane.showMessageDialog(this,"Invalid expression "+expressionField.getText()+". Only columns at or below the level "+tuple.treePath()+" can be used.","Compilation Error",JOptionPane.ERROR_MESSAGE);
                else {
                    tuple.addMutableTupleColumn(col);
                    dispose();
                }
            }
            catch (CompilationException x) {
                JOptionPane.showMessageDialog(this,"Invalid expression "+expressionField.getText()+". It cannot be compiled!","Compilation Error",JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_okButtonActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void helpButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_helpButtonActionPerformed
    {//GEN-HEADEREND:event_helpButtonActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_helpButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    
    public void removeUpdate(javax.swing.event.DocumentEvent documentEvent) {
        doEnable();
    }
    
    public void insertUpdate(javax.swing.event.DocumentEvent documentEvent) {
        doEnable();
    }
    
    public void changedUpdate(javax.swing.event.DocumentEvent documentEvent) {
        doEnable();
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField nameField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton okButton;
    private org.freehep.jas.extension.tupleExplorer.jel.ExpressionField expressionField;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton helpButton;
    // End of variables declaration//GEN-END:variables
    
}
