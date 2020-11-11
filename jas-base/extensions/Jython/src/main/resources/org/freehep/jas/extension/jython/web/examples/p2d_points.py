# Data points in 3D. Example from DMelt http://jwork.org/dmelt/
# S.Chekanov (ANL)
 
from java.util import Random
from java.awt import Color
from jhplot  import HPlot3D, P2D

c1 = HPlot3D("Canvas",600,400)
c1.setGTitle("Interactive 3D plot with 2 sets of points") 
# define range in Xmin, Xmax, Ymin, Ymax,  Zmin, Zmax 
# if this range is not set, it will be set automatically
c1.setRange(-5,10,-5,5,-10,30)
c1.setNameX("X")
c1.setNameY("Y")
c1.visible(1)

# create random generator
rand = Random()
# create P2D objects in 3D 
h1= P2D("3D Gaussian 1")
h1.setSymbolSize(2);
h1.setSymbolColor(Color.blue)

for i in range(500):
               x=1+rand.nextGaussian()
               y=1+0.5*rand.nextGaussian()
               z=10+4.5*rand.nextGaussian()
               h1.add(x,y,z)


# create P2D objects in 3D
h2= P2D("3D Gaussian 2")
h2.setSymbolSize(4);
h2.setSymbolColor(Color.red);

for i in range(100):
               x=2+2*rand.nextGaussian()
               y=4+0.5*rand.nextGaussian()
               z=6+1.5*rand.nextGaussian()
               h2.add(x,y,z)


# draw them 
c1.draw(h1)
c1.draw(h2)

# export to some image (png,eps,pdf,jpeg...)

