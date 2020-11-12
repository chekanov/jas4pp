package test;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.Random;
import jplot.*;
import java.util.*;
import java.awt.*;
import java.lang.*;

import hep.aida.*;
import hep.aida.ref.*;
import javax.swing.*;
import graph.*;
import jhplot.*;
import jhplot.io.*;



/**
 * An example of how to embed a JAIDA IPlotter into your own application.
 */
public class Main
{
  
	
   public static void main(String[] args)
   {


HPlot c1 = new HPlot();
c1.visible();      
c1.setGTitle("Test"); 
c1.setNameX("X_{n}"); 
c1.setNameY("X-axis"); 
c1.setRangeY(0,100); 

P1D p2=new P1D("data set");

H1D h1 = new H1D("Simple1",20, -1.0, 1.0);
h1.setFill(true); 
h1.setFillColor(Color.red);
h1.setLineStyle(2);

Random rand = new Random();
for (int i=0; i<1000; i++)  {
                    h1.fill(rand.nextGaussian());
                    p2.add(10*rand.nextGaussian(),20+rand.nextGaussian()*10);
};
  
c1.setRangeY(0,100);
c1.setRangeX(-1.2,1.2);
                 
c1.draw(p2);
c1.draw(h1);

c1.export("test.png");
 
  }

}
  
