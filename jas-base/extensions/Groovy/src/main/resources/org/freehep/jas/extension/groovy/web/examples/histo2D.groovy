//Histogram example from DMelt http://jwork.org/dmelt/
//S.Chekanov (ANL)

import jhplot.*;
import java.util.*;

// new build a standard canvas
c1 = new HPlot3D("Canvas",600,400)

c1.setGTitle("Global title")
c1.setNameX("Xaxis")
c1.setNameY("Yaxis")
c1.visible()

h1 = new H2D("My 2D Test",20,-3.0, 3.0, 20, -3.0, 3.0)
rand = new Random();
for (int i=0; i<1000; i++)  h1.fill(rand.nextGaussian(),rand.nextGaussian())
c1.draw(h1);

// export to some image (png,eps,pdf,jpeg...)
c1.export("histo2D.pdf")
