package org.freehep.jas.extensions.root;

import hep.aida.IAnalysisFactory;
import hep.aida.ITreeFactory;
import java.io.IOException;
import org.freehep.application.Application;
import org.freehep.swing.wizard.Finishable;
import org.freehep.swing.wizard.WizardPage;

/**
 *
 * @author  Tony Johnson
 */
public class RootWizardPage extends WizardPage implements Finishable
{
   
   /** Creates new form RootWizardPanel */
   public RootWizardPage()
   {
      initComponents();
   }
   public void onFinish()
   {
      try
      {
         IAnalysisFactory factory = IAnalysisFactory.create();
         ITreeFactory tf = factory.createTreeFactory();
         //String options = isShowAllCycles() ? "showAllCycles" : "";
         String url = "root://"+hostField.getText()+"/"+fileField.getText();
         tf.create(url, "root", true, false, null);
         hostField.saveState();
         fileField.saveState();
         dispose(); // If no exception thrown
      }
      catch (IOException x)
      {
         Application.getApplication().error(this,"Error opening root file",x);
      }
   }
   /** This method is called from within the constructor to
    * initialize the form.
    * WARNING: Do NOT modify this code. The content of this method is
    * always regenerated by the Form Editor.
    */
   private void initComponents()//GEN-BEGIN:initComponents
   {
      java.awt.GridBagConstraints gridBagConstraints;
      javax.swing.JLabel jLabel1;
      javax.swing.JLabel jLabel2;

      jLabel1 = new javax.swing.JLabel();
      hostField = new org.freehep.application.RecentItemTextField();
      jLabel2 = new javax.swing.JLabel();
      fileField = new org.freehep.application.RecentItemTextField();
      browseButton = new javax.swing.JButton();
      authButton = new javax.swing.JButton();

      setLayout(new java.awt.GridBagLayout());

      setBorder(new javax.swing.border.EmptyBorder(new java.awt.Insets(10, 10, 10, 10)));
      jLabel1.setText("Host:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      add(jLabel1, gridBagConstraints);

      hostField.setKey("root.host.last");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      add(hostField, gridBagConstraints);

      jLabel2.setText("File:");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      add(jLabel2, gridBagConstraints);

      fileField.setKey("root.file.last");
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
      gridBagConstraints.weightx = 1.0;
      add(fileField, gridBagConstraints);

      browseButton.setText("Browse...");
      browseButton.setEnabled(false);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
      add(browseButton, gridBagConstraints);

      authButton.setText("Authentification...");
      authButton.setEnabled(false);
      gridBagConstraints = new java.awt.GridBagConstraints();
      gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
      gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
      add(authButton, gridBagConstraints);

   }//GEN-END:initComponents
   
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton authButton;
   private javax.swing.JButton browseButton;
   private org.freehep.application.RecentItemTextField fileField;
   private org.freehep.application.RecentItemTextField hostField;
   // End of variables declaration//GEN-END:variables
   
}