// Example from DMelt http://jwork.org/dmelt/
// S.Chekanov (ANL)
 
import jhplot.*
import java.util.*

c1 = new HPlot3D("Canvas",600,400)
c1.setGTitle("F2D and H2D objects")
c1.setTextBottom("Global X")
c1.setTextLeft("Global Y")

c1.setNameX("X")
c1.setNameY("Y")

c1.setColorMode(4)
c1.visible()

h1 = new H2D("My 2D Test 1",30,-3.0, 3.0, 30, -3.0, 3.0)
f1 = new F2D("8*(x*x+y*y)", -3.0, 3.0, -3.0, 5.0)
rand = new Random()
for (int i=0; i<1000; i++)
               h1.fill(0.4*rand.nextGaussian(),rand.nextGaussian())
c1.draw(h1,f1)

//export to some image (png,eps,pdf,jpeg...)
c1.export("image.eps")
