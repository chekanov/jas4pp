import java.util.Random
import java.awt.Color
import jhplot.HPlot3D
import jhplot.P2D

c1 = new HPlot3D("Canvas",600,400)
c1.setGTitle("Interactive 3D plot with 2 sets of points")
c1.setRange(-5,10,-5,5,-10,30)
c1.setNameX("X")
c1.setNameY("Y")
c1.visible(true)

rand = new Random()
//create P2D objects in 3D 
h1= new P2D("3D Gaussian 1")
h1.setSymbolSize(2);
h1.setSymbolColor(Color.blue)

for (i=0; i<500; i++) { 
               x=1+rand.nextGaussian()
               y=1+0.5*rand.nextGaussian()
               z=10+4.5*rand.nextGaussian()
               h1.add(x,y,z)
}


// create P2D objects in 3D
h2= new P2D("3D Gaussian 2")
h2.setSymbolSize(4);
h2.setSymbolColor(Color.red);

for (i=0; i<500; i++) {
               x=2+2*rand.nextGaussian()
               y=4+0.5*rand.nextGaussian()
               z=6+1.5*rand.nextGaussian()
               h2.add(x,y,z)
}


// draw them 
c1.draw(h1)
c1.draw(h2)


