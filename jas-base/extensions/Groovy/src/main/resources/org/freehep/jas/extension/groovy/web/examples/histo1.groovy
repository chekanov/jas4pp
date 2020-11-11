import java.awt.*
import java.util.*
import jhplot.*

c1 = new HPlot("Canvas",600,400,1, 1)
// c1.doc() # view documetation
c1.setGTitle("Global labels: F_{2},  x_{&gamma;}  #bar{p}p F_{2}^{c#bar{c}}"); 
c1.visible()
c1.setAutoRange()
h1 = new H1D("Simple1",100, -2, 2.0)
rand = new Random()
// fill histogram
for (int i=0; i<1000; i++)
      h1.fill(rand.nextGaussian())      

c1.draw(h1)

c1.setAutoRange()
h1.setPenWidthErr(2)
c1.setNameX("Xaxis")
c1.setNameY("Yaxis")
c1.setName("Canvas title")
c1.drawStatBox(h1)

/*
# make exact copy
# h2=h1.copy()
# show as a table
# HTable(h1)
# c1.draw(h2) 
*/

// print statistics
// stat=h1.getStat()

// set HLabel in the normilised coordinate system
lab=new HLabel("HLabel in NDC", 0.15, 0.7, "NDC")
lab.setColor(Color.blue)
c1.add(lab)
c1.update()

c1.export("histo1.eps")
