package org.freehep.jas.extension.compiler;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collections;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.freehep.application.RecentItemTextField;
import org.freehep.application.studio.Studio;

/**
 * 
 * @author tonyj
 * @version $Id: LoadDialog.java 13884 2011-09-20 23:10:19Z tonyj $
 */
public class LoadDialog extends JOptionPane implements ActionListener, KeyListener
{
   private JButton ok = new JButton("OK");
   private JButton cancel = new JButton("Cancel");
   private JButton classPath = new JButton("Set Class Path...");
   private RecentItemTextField field = new RecentItemTextField("loadClass",8,true);
   private Studio app;
   private JASClassManager manager;

   public LoadDialog(Studio app, JASClassManager manager)
   {
      this.app = app;
      this.manager = manager;
      
      JButton[] buttons = { ok, cancel };
      setOptions(buttons);

      for (int i = 0; i < buttons.length; i++)
         buttons[i].addActionListener(this);

      JPanel message = new JPanel();
      message.add(new JLabel("Class:"), BorderLayout.WEST);
      message.add(field, BorderLayout.CENTER);
      message.add(classPath,BorderLayout.EAST);
      setMessage(message);
      
      field.setMinWidth(200);
      field.addKeyListener(this);
      classPath.addActionListener(this);
      
      enableButtons();
   }
   public void actionPerformed(ActionEvent e)
   {
      Object source = e.getSource();
      if (source == ok)
      {
         String className = field.getText();
         try
         {
            manager.loadClass(className);
            setValue(source);
            field.saveState();
         }
         catch (Throwable t)
         {
            app.error(this,"Error loading class: "+className,t);
         }
      }
      else if (source == classPath)
      {
         ClassPathPanel panel = new ClassPathPanel(app,manager.getClasspathFiles());
         int rc = JOptionPane.showConfirmDialog(this,panel,"Set Class Path...",JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
         if (rc == JOptionPane.OK_OPTION)
         {
            manager.setClasspathFiles(panel.update());
         }
      }
      else setValue(source);
   }
   private void enableButtons()
   {
      ok.setEnabled(field.getText().length() > 0);
   }
   public void keyPressed(KeyEvent e)
   {
      enableButtons();      
   }
   public void keyReleased(KeyEvent e)
   {
      enableButtons();      
   }
   public void keyTyped(KeyEvent e)
   {
      enableButtons();      
   }
   void showDialog(Component parent)
   {
      JDialog dlg = createDialog(parent,"Load...");
      dlg.getRootPane().setDefaultButton(ok);
      dlg.setVisible(true);
   }
}