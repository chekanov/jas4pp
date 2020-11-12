package jhplot.utils;

import java.awt.*;
import javax.swing.*;


import java.awt.event.*;

public class ShowColors extends JFrame {

   private Color color = Color.lightGray;
   private Container c;

   public ShowColors()
   {
      super( "Java Colors" );

      c = getContentPane();
      c.setLayout( new FlowLayout() );
      color =
           JColorChooser.showDialog( ShowColors.this,
                     "Choose a color", color );
   }

   public static void main( String args[] )
   {
      ShowColors app = new ShowColors();

      app.addWindowListener(
         new WindowAdapter() {
            public void windowClosing( WindowEvent e )
            {
               System.exit( 0 );
            }
         }
      );
   }
}


