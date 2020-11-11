# Example from DMelt http://jwork.org/dmelt/
# S.Chekanov (ANL)

from java.awt import Color
from jhplot  import *

c1 = HPlot("Canvas",600,400,2,1)
c1.setGTitle("Example of P0D data array", Color.blue) #put title
c1.visible()
c1.setAutoRange()

p0= P0D("Normal distribution")
p0.randomNormal(1000, 0.0, 1.0)
c1.setNameX("X")
c1.setNameY("Y") 

# make a new copy and transform.
func1="x*cos(x)+2"
p01=p0.copy(func1)
p01.func(F1D(func1))
	
# make a new copy and transform.
func1="exp(x)-2"
p02=p0.copy(func1)
p02.func(F1D(func1))
	
# draw as histogram
h1=p0.getH1D(20)
c1.draw(h1)
h2=p01.getH1D(100)
h2.setFill(1)
h2.setFillColor(Color.blue)
h2.setColor(Color.red)
c1.draw(h2)

# make a new plot
c1.cd(2,1)
c1.setAutoRange()
c1.setNameX("X")
c1.setNameY("Y") 
h3=p02.getH1D(100)
h3.setFill(1)
h3.setFillColor(Color.green )
h3.setColor(Color.red)
c1.draw(h3)
