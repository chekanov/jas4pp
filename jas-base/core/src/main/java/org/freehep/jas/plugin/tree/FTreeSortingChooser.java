package org.freehep.jas.plugin.tree;

import java.awt.Component;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.AbstractListModel;
import javax.swing.DefaultButtonModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import org.freehep.jas.plugin.tree.FTreeNodeSorter;
import org.freehep.jas.plugin.tree.FTreeSelectionManager;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
class FTreeSortingChooser extends javax.swing.JPanel {
    
    private String sortingString;
    private SortedListModel availableListModel;
    private SortedListModel selectedListModel;
    private FTreePlugin thePlugin;
    private DefaultFTree tree;
    private String allTreeString = "All trees";
    private String[] treeNames;
    private JDialog dlg;
    
    public FTreeSortingChooser(JDialog dlg) {
        this( null, null, dlg );
    }
    
    public FTreeSortingChooser(DefaultFTree tree, String sortingString, JDialog dlg) {
        this.thePlugin = FTreePlugin.plugin();
        this.dlg = dlg;
        initComponents();
        
        initPanel( tree, sortingString );
                
    }
    
    private void initPanel( DefaultFTree tree, String sortingString ) {
        
        this.sortingString = sortingString;
        this.tree = tree;

        ArrayList trees = thePlugin.trees();
        int size = trees.size();
        
        if ( size == 1 ) {
            jPanel2.remove(treeComboBox);
            treeNames = new String[size];
            treeNames[0] = ( (DefaultFTree) trees.get(0) ).name();
            if ( tree == null )
                tree = (DefaultFTree) trees.get(0);
        }
        else {
            size++;
            treeNames = new String[size];
            treeNames[0] = allTreeString;
            for ( int i = 0; i < trees.size(); i++ )
                treeNames[i+1] = ( (DefaultFTree) trees.get(i) ).name();
            treeComboBox.setModel( new DefaultComboBoxModel( treeNames ) );
            if ( tree != null )
                treeComboBox.setSelectedItem( tree.name() );
            treeCheckBox.setText(treeCheckBox.getText()+" for tree");
        }
                
        foldersCheckBox.setSelected(false);
        if ( tree.selectedNodes() == null ) {
            foldersCheckBox.setEnabled(false);
        }
        else {
            FTreeNode[] nodes = tree.selectedNodes();
            for ( int i = 0; i < nodes.length; i++ ) {
                DefaultFTreeNode node = (DefaultFTreeNode) nodes[i];
                if ( node.getAllowsChildren() ) {
                    foldersCheckBox.setSelected( true );
                    break;
                }
            }
        }
        
        treeCheckBox.setSelected( ! foldersCheckBox.isSelected() );
        
        if (  sortingString == null )
            sortingString = ( (DefaultFTreeNode) tree.root()).sortingString();
        
        Collection availableSorters = FTreeNodeSorterManager.availableSorters();
        Collection selectedSorters = FTreeNodeSorterManager.sortingComparator(sortingString).sorters();
        
        availableListModel = new SortedListModel( availableSorters, selectedSorters );
        availableList.setModel(availableListModel);
        availableList.setAutoscrolls(true);
        selectedListModel = new SortedListModel( selectedSorters );
        selectedList.setModel( selectedListModel );
                
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
        downButton.setEnabled(false);
        upButton.setEnabled(false);
        
        recursiveCheckBox.setSelected(true);
    }
    
    
    private class SortedListModel extends AbstractListModel {
        
        private ArrayList s = new ArrayList();
        
        SortedListModel( Collection sorters, Collection sorters1 ) {
            if ( sorters != null ) {
                Iterator iter = sorters.iterator();
                while ( iter.hasNext() )
                    s.add( iter.next() );
            }
            if ( sorters1 != null ) {
                Iterator iter = sorters1.iterator();
                while ( iter.hasNext() )
                    s.remove( iter.next() );
            }
        }
        
        SortedListModel( Collection sorters ) {
            this( sorters, null );
        }
        
        public int getSize() {
            return s.size();
        }
        
        public Object getElementAt(int i) {
            return ((FTreeNodeSorter) s.get(i)).algorithmName();
        }
        
        public void remove(int i) {
            s.remove(i);
        }
        
        public void add( Object o ) {
            s.add(o);
        }
        
        public Object get( int i ) {
            return s.get(i);
        }
        
        public void move( int i, int j ) {
            Object o = s.get(i);
            s.remove(i);
            s.add(j,o);
        }
        
        public ArrayList list() {
            return s;
        }
    }
        
    private void updateToolTipsText(String selectedSorter, JList list) {
        String toolTipsText = "";
        if ( selectedSorter != null ) {
            Collection availableSorters = FTreeNodeSorterManager.availableSorters();
            Iterator iter = availableSorters.iterator();
            while ( iter.hasNext() ) {
                FTreeNodeSorter sorter = (FTreeNodeSorter) iter.next();
                if ( sorter.algorithmName().equals( selectedSorter ) ) {
                    toolTipsText = sorter.description();
                    break;
                }
            }
        }
        list.setToolTipText(toolTipsText);       
    }
        
    private void updateButtons() {
        int selectedIndex = availableList.getSelectedIndex();
        addButton.setEnabled(selectedIndex != -1);
        selectedIndex = selectedList.getSelectedIndex();
        removeButton.setEnabled( ((SortedListModel)selectedList.getModel()).getSize() > 1 && selectedIndex != -1 );
        upButton.setEnabled(selectedIndex != -1 && selectedListModel.getSize() > 1 && selectedIndex > 0);
        downButton.setEnabled(selectedIndex != -1 && selectedListModel.getSize() > 1 && selectedIndex < selectedListModel.getSize()-1);
        
    }
    
    private void clearSelection() {
        selectedList.clearSelection();
        availableList.clearSelection();
        updateButtons();
    }
    
    void apply() {
        String sortingString = FTreeNodeSorterManager.sortingString(((SortedListModel)selectedList.getModel()).list());
        boolean recursively = recursiveCheckBox.isSelected();
        if ( foldersCheckBox.isSelected() ) {
            FTreeNode[] nodes = tree.selectedNodes();
            for ( int i = 0; i < nodes.length; i++ ) {
                DefaultFTreeNode node = (DefaultFTreeNode) nodes[i];
                if ( node.getAllowsChildren() )
                    node.applySorting( sortingString, recursively );
            }
        }
        if ( treeCheckBox.isSelected() ) {
            String[] selectedTreeNames;
            String selectedTree = (String) treeComboBox.getSelectedItem();
            if ( selectedTree!= null && selectedTree.equals(allTreeString) ) {
                selectedTreeNames = new String[treeNames.length - 1];
                for ( int i = 0; i < selectedTreeNames.length; i++ )
                    selectedTreeNames[i] = treeNames[i+1];
            } else {
                selectedTreeNames = new String[1];
                selectedTreeNames[0] = treeNames[0];
            }
            
            for ( int i = 0; i < selectedTreeNames.length; i++ ) {
                DefaultFTree tree = (DefaultFTree)thePlugin.tree( selectedTreeNames[i] );
                DefaultFTreeNode root = (DefaultFTreeNode)tree.root();
                root.applySorting( sortingString, recursively );
                thePlugin.setTreeSortingAlgorithm(selectedTreeNames[i], sortingString);
                thePlugin.setIsTreeSortingRecursive(selectedTreeNames[i], recursively);
            }
        }
    }

    private void initComponents() {//GEN-BEGIN:initComponents
        java.awt.GridBagConstraints gridBagConstraints;

        title = new javax.swing.JLabel();
        mainSelectionPanel = new javax.swing.JPanel();
        applyPanel = new javax.swing.JPanel();
        applyText = new javax.swing.JLabel();
        recursiveCheckBox = new javax.swing.JCheckBox();
        foldersCheckBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        treeCheckBox = new javax.swing.JCheckBox();
        treeComboBox = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel1 = new javax.swing.JPanel();
        sortingSelectionPanel = new javax.swing.JPanel();
        sortingLists = new javax.swing.JPanel();
        selectedText = new javax.swing.JLabel();
        availableText = new javax.swing.JLabel();
        availableListScrollPane = new javax.swing.JScrollPane();
        availableList = new javax.swing.JList();
        selectedListScrollPane = new javax.swing.JScrollPane();
        selectedList = new javax.swing.JList();
        listButtonsPanel = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        upButton = new javax.swing.JButton();
        downButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JSeparator();
        exitPanel = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        applyButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        title.setFont(new java.awt.Font("Dialog", 0, 14));
        title.setText("Choose the combination of sorting algorithms");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 6, 4);
        add(title, gridBagConstraints);

        mainSelectionPanel.setLayout(new java.awt.GridBagLayout());

        mainSelectionPanel.setBorder(new javax.swing.border.EtchedBorder(java.awt.Color.lightGray, null));
        applyPanel.setLayout(new java.awt.GridBagLayout());

        applyText.setFont(new java.awt.Font("Dialog", 0, 14));
        applyText.setText("Apply to:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        applyPanel.add(applyText, gridBagConstraints);

        recursiveCheckBox.setFont(new java.awt.Font("Dialog", 0, 14));
        recursiveCheckBox.setText("recursively");
        recursiveCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recursiveCheckBoxActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        applyPanel.add(recursiveCheckBox, gridBagConstraints);

        foldersCheckBox.setFont(new java.awt.Font("Dialog", 0, 14));
        foldersCheckBox.setText("selected folders");
        foldersCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                foldersCheckBoxActionPerformed(evt);
            }
        });
        foldersCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                foldersCheckBoxMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        applyPanel.add(foldersCheckBox, gridBagConstraints);

        jPanel2.setLayout(new java.awt.GridBagLayout());

        treeCheckBox.setFont(new java.awt.Font("Dialog", 0, 14));
        treeCheckBox.setText("root node");
        treeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                treeCheckBoxActionPerformed(evt);
            }
        });
        treeCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeCheckBoxMouseClicked(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel2.add(treeCheckBox, gridBagConstraints);

        treeComboBox.setFont(new java.awt.Font("Dialog", 0, 12));
        jPanel2.add(treeComboBox, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        applyPanel.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        applyPanel.add(jSeparator1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 4, 0, 0);
        mainSelectionPanel.add(applyPanel, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        sortingSelectionPanel.setLayout(new java.awt.GridBagLayout());

        sortingLists.setLayout(new java.awt.GridBagLayout());

        selectedText.setFont(new java.awt.Font("Dialog", 0, 14));
        selectedText.setText("Selected list");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        sortingLists.add(selectedText, gridBagConstraints);

        availableText.setFont(new java.awt.Font("Dialog", 0, 14));
        availableText.setText("Available");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        sortingLists.add(availableText, gridBagConstraints);

        availableListScrollPane.setPreferredSize(new java.awt.Dimension(134, 100));
        availableList.setBorder(new javax.swing.border.EtchedBorder(java.awt.Color.darkGray, java.awt.Color.lightGray));
        availableList.setFont(new java.awt.Font("Dialog", 0, 14));
        availableList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        availableList.setPreferredSize(null);
        availableList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                availableListFocusGained(evt);
            }
        });
        availableList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                availableListValueChanged(evt);
            }
        });

        availableListScrollPane.setViewportView(availableList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        sortingLists.add(availableListScrollPane, gridBagConstraints);

        selectedListScrollPane.setPreferredSize(new java.awt.Dimension(134, 100));
        selectedList.setBorder(new javax.swing.border.EtchedBorder(java.awt.Color.darkGray, null));
        selectedList.setFont(new java.awt.Font("Dialog", 0, 14));
        selectedList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        selectedList.setPreferredSize(null);
        selectedList.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                selectedListFocusGained(evt);
            }
        });
        selectedList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                selectedListValueChanged(evt);
            }
        });

        selectedListScrollPane.setViewportView(selectedList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        sortingLists.add(selectedListScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        sortingSelectionPanel.add(sortingLists, gridBagConstraints);

        listButtonsPanel.setLayout(new java.awt.GridBagLayout());

        addButton.setFont(new java.awt.Font("Dialog", 0, 12));
        addButton.setText("Add");
        addButton.setPreferredSize(new java.awt.Dimension(80, 26));
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        listButtonsPanel.add(addButton, gridBagConstraints);

        removeButton.setFont(new java.awt.Font("Dialog", 0, 12));
        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        listButtonsPanel.add(removeButton, gridBagConstraints);

        upButton.setFont(new java.awt.Font("Dialog", 0, 12));
        upButton.setText("Up");
        upButton.setPreferredSize(new java.awt.Dimension(80, 26));
        upButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                upButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        listButtonsPanel.add(upButton, gridBagConstraints);

        downButton.setFont(new java.awt.Font("Dialog", 0, 12));
        downButton.setText("Down");
        downButton.setPreferredSize(new java.awt.Dimension(80, 26));
        downButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        listButtonsPanel.add(downButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        sortingSelectionPanel.add(listButtonsPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        jPanel1.add(sortingSelectionPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        mainSelectionPanel.add(jPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mainSelectionPanel.add(jSeparator2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 4, 3, 4);
        add(mainSelectionPanel, gridBagConstraints);

        exitPanel.setLayout(new java.awt.GridBagLayout());

        cancelButton.setFont(new java.awt.Font("Dialog", 0, 12));
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 3);
        exitPanel.add(cancelButton, gridBagConstraints);

        applyButton.setFont(new java.awt.Font("Dialog", 0, 12));
        applyButton.setText("Apply");
        applyButton.setPreferredSize(new java.awt.Dimension(73, 26));
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        exitPanel.add(applyButton, gridBagConstraints);

        okButton.setFont(new java.awt.Font("Dialog", 0, 12));
        okButton.setText("OK");
        okButton.setPreferredSize(new java.awt.Dimension(73, 26));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        exitPanel.add(okButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        add(exitPanel, gridBagConstraints);

    }//GEN-END:initComponents
    
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        apply();
        setVisible(false);
        dlg.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed
    
    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        apply();
    }//GEN-LAST:event_applyButtonActionPerformed
    
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        setVisible(false);
        dlg.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed
    
    private void foldersCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_foldersCheckBoxMouseClicked
        foldersCheckBox.setSelected(true);
    }//GEN-LAST:event_foldersCheckBoxMouseClicked
    
    private void treeCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeCheckBoxMouseClicked
        treeCheckBox.setSelected(true);
    }//GEN-LAST:event_treeCheckBoxMouseClicked
    
    private void downButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downButtonActionPerformed
        int i = selectedList.getSelectedIndex();
        selectedListModel.move(i, i+1);
        selectedList.updateUI();
        clearSelection();
    }//GEN-LAST:event_downButtonActionPerformed
    
    private void upButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_upButtonActionPerformed
        int i = selectedList.getSelectedIndex();
        selectedListModel.move(i, i-1);
        selectedList.updateUI();
        clearSelection();
    }//GEN-LAST:event_upButtonActionPerformed
    
    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        int i = selectedList.getSelectedIndex();
        Object o = selectedListModel.get(i);
        selectedListModel.remove(i);
        availableListModel.add(o);
        selectedList.updateUI();
        availableList.updateUI();
        clearSelection();
    }//GEN-LAST:event_removeButtonActionPerformed
    
    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
        int i = availableList.getSelectedIndex();
        Object o = availableListModel.get(i);
        availableListModel.remove(i);
        selectedListModel.add(o);
        availableList.updateUI();
        selectedList.updateUI();
        clearSelection();
    }//GEN-LAST:event_addButtonActionPerformed
    
    private void recursiveCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recursiveCheckBoxActionPerformed
        clearSelection();
    }//GEN-LAST:event_recursiveCheckBoxActionPerformed
    
    private void foldersCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_foldersCheckBoxActionPerformed
        clearSelection();
        treeCheckBox.setSelected(false);
    }//GEN-LAST:event_foldersCheckBoxActionPerformed
    
    private void treeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_treeCheckBoxActionPerformed
        clearSelection();
        foldersCheckBox.setSelected(false);
    }//GEN-LAST:event_treeCheckBoxActionPerformed
    
    private void selectedListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_selectedListValueChanged
        updateButtons();
        updateToolTipsText((String)selectedList.getSelectedValue(), selectedList);
    }//GEN-LAST:event_selectedListValueChanged
    
    private void availableListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_availableListValueChanged
        updateButtons();
        updateToolTipsText((String)availableList.getSelectedValue(), availableList);
    }//GEN-LAST:event_availableListValueChanged
    
    private void selectedListFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_selectedListFocusGained
        availableList.clearSelection();
    }//GEN-LAST:event_selectedListFocusGained
    
    private void availableListFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_availableListFocusGained
        selectedList.clearSelection();
    }//GEN-LAST:event_availableListFocusGained
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JButton applyButton;
    private javax.swing.JPanel applyPanel;
    private javax.swing.JLabel applyText;
    private javax.swing.JList availableList;
    private javax.swing.JScrollPane availableListScrollPane;
    private javax.swing.JLabel availableText;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton downButton;
    private javax.swing.JPanel exitPanel;
    private javax.swing.JCheckBox foldersCheckBox;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JPanel listButtonsPanel;
    private javax.swing.JPanel mainSelectionPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JCheckBox recursiveCheckBox;
    private javax.swing.JButton removeButton;
    private javax.swing.JList selectedList;
    private javax.swing.JScrollPane selectedListScrollPane;
    private javax.swing.JLabel selectedText;
    private javax.swing.JPanel sortingLists;
    private javax.swing.JPanel sortingSelectionPanel;
    private javax.swing.JLabel title;
    private javax.swing.JCheckBox treeCheckBox;
    private javax.swing.JComboBox treeComboBox;
    private javax.swing.JButton upButton;
    // End of variables declaration//GEN-END:variables
    
}
