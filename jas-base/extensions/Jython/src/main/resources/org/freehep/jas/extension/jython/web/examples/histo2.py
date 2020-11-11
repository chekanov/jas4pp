#  Histograms | C | 1.7 | S.Chekanov |  H1D histograms with some style 

from java.awt import Color,Font
from java.util import Random
from jhplot import *

c1 = HPlot("Canvas",600,400)
c1.getAntiAlias()
c1.setGTitle("Title")
# make the frame visible
c1.visible()
# set range
# c1.setRange(-4,4,0.0,100)

# set autorange
c1.setAutoRange()

h1 = H1D("e^{+}e^{-} &rarr; W^{+}W^{-} &rarr; 4 jets",20, -2.0, 2.0)
rand = Random()
# fill histogram
for i in range(500):
      h1.fill(rand.nextGaussian())

h1.setFill(1)
h1.setFillColor(Color.green)
h1.setErrX(0)
h1.setErrY(1)
h1.setPenWidthErr(2)

# h1.toTable()

h2 = H1D("e^{+}e^{-} &rarr; Z/&gamma; &rarr;  hadrons ",15, -2.0, 2.0)
h2.setFill(1)
h2.setErrX(0)
h2.setErrY(1)
h2.setFillColorTransparency(0.7)
h2.setFillColor(Color.red)
h2.setColor(Color.red)
h2.setErrColorY(Color.blue)
h2.setNameX("X of H2")
h2.setNameY("Y of H2")

for i in range(1000): 
      h2.fill(2+rand.nextGaussian())



c1.setLegendFont( Font("Lucida Sans", Font.BOLD, 18)  )
c1.setNameX("Text Examples:&minus; &theta; &pi; &omega; &int; &sum;")
c1.setNameY("Yaxis")
c1.setName("Canvas title: &radic;(1&minus; e)")

c1.draw(h1)
c1.draw(h2)

c1.export("histo2d.eps")
