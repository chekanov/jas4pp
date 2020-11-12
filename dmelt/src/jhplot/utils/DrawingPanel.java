
package jhplot.utils;

import javax.swing.*;
import java.awt.*;

import graph.*;
import jplot.*;

public class DrawingPanel extends JPanel {


  /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
private String sname;
  private Font f;
  private Color c;
  private FontMetrics fm;
  private int WIDTH=600;
  private int HEIGHT=400;
  private int xpos;
  private int ypos;

  DrawingPanel() {
    // Set background color for the applet's panel.
    setBackground(Color.WHITE);
    sname=" ";
    f=new Font("Lucida Sans", Font.BOLD, 16);
    c=Color.black;
    xpos=-1;
    ypos=-1;

  } // ctor


  public void setText(String sname, Font f, Color c ) {

      this.sname=sname;
      this.f=f;
      this.c=c;
      repaint(); 
  }


public void setText(String sname, Font f) {
          setText(sname, f, Color.black );
}


/*
 public String getName() {
    return "AntiAliasing";
  }

  public int getWidth() {
    return WIDTH;
  }

  public int getHeight() {
    return HEIGHT;
  }
*/


public void setText(String sname) {
          setText(sname, new Font("Lucida Sans", Font.BOLD, 16), Color.black );
}




public void setTextPosition(int xpos, int ypos) {
          this.xpos=xpos;
          this.ypos=ypos; 
          repaint();
}




  public void paintComponent(Graphics g)   {
   // Paint background
   super.paintComponent(g);


    // Most importantly, turn on anti-aliasing.
//    g.setStroke(new BasicStroke(2.0f)); // 2-pixel lines
//    g.setFont(new Font("Serif", Font.BOLD, 18)); // 18-point font
//    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, // Anti-alias!
//        RenderingHints.VALUE_ANTIALIAS_ON);

    
   // Set current drawing color
//   g.setColor(Color.BLACK);
   g.setFont(f);
   fm = getFontMetrics(f);
 
   // Get the drawing area
   int dY = getSize ().height;
   int dX = getSize ().width;
   WIDTH=dX;
   HEIGHT=dY;
 
   int midY = dY/2;
   int midX = dX/2;
   int rectX = 3 * dX/4;
   int rectY = 3 * dY/4;


// find a proper width of the  text
   String tmp=Translate.shrink( sname );
   int textWidth = fm.stringWidth(tmp);
   int textHeight = fm.getHeight();



    RTextLine text = new RTextLine();
    text.setFont(  f  );
    text.setColor( c  );
    String stext= Translate.decode(  sname   );
   // text.setRotation( (int)rotation ); 
    text.setText( stext  );

    if (xpos == -1 && ypos == -1) { 
    text.draw(g,midX-(int)(0.5*textWidth),midY+(int)(0.5*textHeight) );
    }  else {
    text.draw(g,midX-(int)(0.5*textWidth), xpos, ypos);
    }
 

 
  //  g.drawString("Java",midX,midY);


  } // paintComponent
} // class DrawingPanel
