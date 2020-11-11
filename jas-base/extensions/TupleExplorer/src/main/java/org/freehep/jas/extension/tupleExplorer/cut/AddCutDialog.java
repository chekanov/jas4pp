package org.freehep.jas.extension.tupleExplorer.cut;

import gnu.jel.CompilationException;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;
import org.freehep.jas.extension.tupleExplorer.cut.Numeric1DCut;
import org.freehep.jas.extension.tupleExplorer.jel.JELColumn;
import org.freehep.jas.extension.tupleExplorer.cut.NTupleListModel;
import org.freehep.jas.extension.tupleExplorer.cut.NTupleListCellRenderer;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.jel.JELCut;
import org.freehep.jas.extension.tupleExplorer.jel.NTupleCompiledExpression;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeCut;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeCutSet;
import org.freehep.jas.plugin.tree.FTreePath;


/**
 *
 * @author tonyj
 */
public class AddCutDialog extends javax.swing.JDialog implements DocumentListener, ChangeListener {
    
    /** Creates new form Numeric1DCutDialog */
    
    private boolean addToDefaults;
    
    public AddCutDialog(java.awt.Frame parent, MutableTupleTree tt) {
        this(parent,tt, true);
    }
    public AddCutDialog(java.awt.Frame parent, MutableTupleTree tt, boolean addToDefaults) {
        super(parent, true);
        init(tt);
        this.addToDefaults = addToDefaults;
    }
    public AddCutDialog(java.awt.Dialog parent, MutableTupleTree tt) {
        this(parent,tt,true);
    }
    public AddCutDialog(java.awt.Dialog parent, MutableTupleTree tt, boolean addToDefaults) {
        super(parent,true);
        init(tt);
        this.addToDefaults = addToDefaults;
    }
    private void init(MutableTupleTree tt) {
        this.tuple = tt.rootMutableTuple();
        this.tt = tt;
        initComponents();
        getRootPane().setDefaultButton(okButton);
        numericExpression.getDocument().addDocumentListener(this);
        numericExpression.getDocument().addDocumentListener(new DocumentListener() {
            public void removeUpdate(javax.swing.event.DocumentEvent documentEvent) {}
            public void insertUpdate(javax.swing.event.DocumentEvent documentEvent) {
                expressionCheckbox.setSelected(true);
            }
            public void changedUpdate(javax.swing.event.DocumentEvent documentEvent) {
                expressionCheckbox.setSelected(true);
            }
        });
        cutName.getDocument().addDocumentListener(this);
        expressionName.getDocument().addDocumentListener(this);
        cutSetName.getDocument().addDocumentListener(this);
        expression.getDocument().addDocumentListener(this);
        jTabbedPane1.addChangeListener(this);
        expressionCheckbox.addChangeListener(this);
        doEnable();
    }
    private void doEnable() {
        if (jTabbedPane1.getSelectedIndex() == 0) {
            okButton.setEnabled(cutName.getText().length() > 0 &&
            (columnCheckbox.isSelected() ||
            numericExpression.getText().length() > 0));
        }
        else if (jTabbedPane1.getSelectedIndex() == 1) {
            okButton.setEnabled(expressionName.getText().length() > 0 &&
            expression.getText().length() > 0);
        }
        else {
            okButton.setEnabled(cutSetName.getText().length() > 0);
        }
    }
    public void removeUpdate(javax.swing.event.DocumentEvent documentEvent) {
        doEnable();
    }
    
    public void insertUpdate(javax.swing.event.DocumentEvent documentEvent) {
        doEnable();
    }
    
    public void changedUpdate(javax.swing.event.DocumentEvent documentEvent) {
        doEnable();
    }
    public void stateChanged(javax.swing.event.ChangeEvent changeEvent) {
        doEnable();
    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */



    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        numericExpression = new org.freehep.jas.extension.tupleExplorer.jel.ExpressionField(tuple, tt);
        expression = new org.freehep.jas.extension.tupleExplorer.jel.ExpressionField(tuple, tt);
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        helpButton = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        mainPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        cutName = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        cutType = new javax.swing.JComboBox();
        columnCheckbox = new javax.swing.JRadioButton();
        column = new javax.swing.JComboBox();
        expressionCheckbox = new javax.swing.JRadioButton();
        GeneralPanel = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        expressionName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        cutSetPanel = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        cutSetName = new javax.swing.JTextField();
        add = new javax.swing.JButton();
        remove = new javax.swing.JButton();
        newCut = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Add Cut...");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

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
        buttonPanel.add(helpButton);

        getContentPane().add(buttonPanel, java.awt.BorderLayout.SOUTH);

        jTabbedPane1.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 1, 1, 1)));
        mainPanel.setLayout(new java.awt.GridBagLayout());

        mainPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        jLabel1.setText("Cut Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mainPanel.add(jLabel1, gridBagConstraints);

        cutName.setColumns(15);
        cutName.setToolTipText("Name for the cut");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        mainPanel.add(cutName, gridBagConstraints);

        jLabel2.setText("Cut Type");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        mainPanel.add(jLabel2, gridBagConstraints);

        cutType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "x > Cut", "x < Cut", "Cut1 < x < Cut2", "x < Cut1 || x > Cut2" }));
        cutType.setToolTipText("Type of cut");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mainPanel.add(cutType, gridBagConstraints);

        buttonGroup1.add(columnCheckbox);
        columnCheckbox.setSelected(true);
        columnCheckbox.setText("Column");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        mainPanel.add(columnCheckbox, gridBagConstraints);

        column.setModel(new NTupleListModel(tuple,Number.class));
        column.setToolTipText("Column to apply cut to");
        column.setRenderer(new NTupleListCellRenderer());
        column.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        mainPanel.add(column, gridBagConstraints);

        buttonGroup1.add(expressionCheckbox);
        expressionCheckbox.setText("Expression");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        mainPanel.add(expressionCheckbox, gridBagConstraints);

        jTabbedPane1.addTab("Numeric 1D Cut", null, mainPanel, "");

        GeneralPanel.setLayout(new java.awt.GridBagLayout());

        GeneralPanel.setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
        jLabel4.setText("Cut Name");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        GeneralPanel.add(jLabel4, gridBagConstraints);

        expressionName.setColumns(15);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        GeneralPanel.add(expressionName, gridBagConstraints);

        jLabel5.setText("Expression");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        GeneralPanel.add(jLabel5, gridBagConstraints);

        numericExpression.setToolTipText("Numeric Expression to Cut On");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainPanel.add(numericExpression, gridBagConstraints);

        expression.setToolTipText("Enter boolean expression");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        GeneralPanel.add(expression, gridBagConstraints);

        jTabbedPane1.addTab("General Cut", null, GeneralPanel, "");

        cutSetPanel.setLayout(new java.awt.BorderLayout());

        jPanel2.setLayout(new java.awt.GridBagLayout());

        jLabel3.setText("Cut Name: ");
        jPanel2.add(jLabel3, new java.awt.GridBagConstraints());

        cutSetName.setColumns(10);
        cutSetName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutSetNameActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(cutSetName, gridBagConstraints);

        add.setMnemonic('A');
        add.setText("Add...");
        add.setToolTipText("Add existing cut to cut set");
        add.setEnabled(false);
        jPanel2.add(add, new java.awt.GridBagConstraints());

        remove.setMnemonic('R');
        remove.setText("Remove");
        remove.setToolTipText("Remove selected cuts from cut set");
        remove.setEnabled(false);
        remove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeActionPerformed(evt);
            }
        });

        jPanel2.add(remove, new java.awt.GridBagConstraints());

        newCut.setMnemonic('N');
        newCut.setText("New...");
        newCut.setToolTipText("Add new cut to cut set");
        newCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newCutActionPerformed(evt);
            }
        });

        jPanel2.add(newCut, new java.awt.GridBagConstraints());

        cutSetPanel.add(jPanel2, java.awt.BorderLayout.NORTH);

        jTable1.setModel(new CutSetTable(temp));
        jTable1.setPreferredScrollableViewportSize(new java.awt.Dimension(250, 100));
        jScrollPane1.setViewportView(jTable1);

        cutSetPanel.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Cut Set", null, cutSetPanel, "");

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.NORTH);

        pack();
    }
    // </editor-fold>//GEN-END:initComponents
    
    private void removeActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_removeActionPerformed
    {//GEN-HEADEREND:event_removeActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_removeActionPerformed
    
    private void newCutActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newCutActionPerformed
    {//GEN-HEADEREND:event_newCutActionPerformed
        AddCutDialog dlg = new AddCutDialog(this,tt, false);
        dlg.pack();
        dlg.setLocationRelativeTo(this);
        dlg.setVisible(true);
        Cut cut = dlg.getCut();
        if (cut != null) temp.addCut(cut);
    }//GEN-LAST:event_newCutActionPerformed
    
    private void cutSetNameActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cutSetNameActionPerformed
    {//GEN-HEADEREND:event_cutSetNameActionPerformed
        // Add your handling code here:
    }//GEN-LAST:event_cutSetNameActionPerformed
    
    private void columnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_columnActionPerformed
    {//GEN-HEADEREND:event_columnActionPerformed
        columnCheckbox.setSelected(true);
    }//GEN-LAST:event_columnActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed
        try {
            
            if (jTabbedPane1.getSelectedIndex() == 0) {
                String colName = cutName.getText();
                boolean newName = true;
                for ( int i = 0; i < tuple.columns(); i++ ) {
                    if ( tuple.columnName(i).equals(colName) ) newName = false;
                }
                if ( newName ) {
                MutableTupleColumn col;
                FTreePath path;
                if (columnCheckbox.isSelected()) {
                    col = (MutableTupleColumn) column.getSelectedItem();
                    path = col.treePath();
                }
                else {
                    NTupleCompiledExpression c = numericExpression.compile(Double.TYPE);
                    col = new JELColumn(tuple,tt,colName,numericExpression.getText(),c);
                    path = ( (JELColumn)col).getLeadingPath();
                }
                int type = cutType.getSelectedIndex();
                cut = new Numeric1DCut(colName, new NTupleCutDataSet( col, colName, tuple ),type );
                cut = new MutableTupleTreeCut( cut, path );
                tt.addCut((MutableTupleTreeCut)cut, addToDefaults);
                dispose();
                } else
                    JOptionPane.showMessageDialog(this,"Name already in use: "+colName,"Error",JOptionPane.ERROR_MESSAGE);                    

                
            }
            else if (jTabbedPane1.getSelectedIndex() == 1) {
                String colName = expressionName.getText();
                for ( int i = 0; i < tuple.columns(); i++ )
                    if ( tuple.columnName(i).equals(colName) ) JOptionPane.showMessageDialog(this,"Name already in use: "+colName,"Error",JOptionPane.ERROR_MESSAGE);
                NTupleCompiledExpression c = expression.compile(Boolean.TYPE);
                JELCut jelc = new JELCut(colName,tuple,tt,c,expression.getText());
                cut = jelc;
                cut = new MutableTupleTreeCut( cut, jelc.getLeadingPath() );
                tt.addCut( (MutableTupleTreeCut)cut, addToDefaults );
                dispose();
            }
            else {
                String cutName = cutSetName.getText();
                MutableTupleTreeCutSet cs = new MutableTupleTreeCutSet(cutName);
                for (int i=0; i<temp.getNCuts(); i++) {
                    cs.addCut(temp.getCut(i));
                }
                tt.addCut(cs);
                dispose();
            }
        }
        catch (CompilationException x) {
            String message = x.getMessage();
            JOptionPane.showMessageDialog(this,x,"Compilation Error",JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_okButtonActionPerformed
    
    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog
    public Cut getCut() {
        return cut;
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel GeneralPanel;
    private javax.swing.JButton add;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JComboBox column;
    private javax.swing.JRadioButton columnCheckbox;
    private javax.swing.JTextField cutName;
    private javax.swing.JTextField cutSetName;
    private javax.swing.JPanel cutSetPanel;
    private javax.swing.JComboBox cutType;
    private javax.swing.JRadioButton expressionCheckbox;
    private javax.swing.JTextField expressionName;
    private javax.swing.JButton helpButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton newCut;
    private javax.swing.JButton okButton;
    private javax.swing.JButton remove;
    // End of variables declaration//GEN-END:variables
    private MutableTuple tuple;
    private MutableTupleTree tt;
    private Cut cut;
    private MutableTupleTreeCutSet temp = new MutableTupleTreeCutSet("temp");
    
    private org.freehep.jas.extension.tupleExplorer.jel.ExpressionField numericExpression;
    private org.freehep.jas.extension.tupleExplorer.jel.ExpressionField expression;
}