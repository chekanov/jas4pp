# Histogram example from DMelt http://jwork.org/dmelt/
# S.Chekanov (ANL)

from jhplot  import HPlot3D,SHPlot3D,H2D
from java.util import Random


# build a standard canvas
c1 = HPlot3D("Canvas",600,400)

c1.setGTitle("Global title")
c1.setNameX("Xaxis")
c1.setNameY("Yaxis")
c1.visible(1)

h1 = H2D("My 2D Test",20,-3.0, 3.0, 20, -3.0, 3.0)
rand = Random();
for i in range(500):
               h1.fill(rand.nextGaussian(),rand.nextGaussian())
c1.draw(h1);

# export to some image (png,eps,pdf,jpeg...)
c1.export("histo2D.pdf")
