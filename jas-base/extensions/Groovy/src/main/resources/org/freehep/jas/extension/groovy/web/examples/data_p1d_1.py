# Example from DMelt http://jwork.org/dmelt/
# S.Chekanov (ANL)

from jhplot  import  *
from java.awt import Color

p1=P1D("1st data set with errors on Y")
p1.add(1,2,1)
p1.add(2,3,0.5)
p1.add(4,5,0.5)

p2=P1D("2nd data set without errors")
p2.add(-1,3)
p2.add(5,-2)
p2.add(1,0)
p2.setColor(Color.red)

c1 = HPlot("Canvas")
c1.visible()
c1.setAutoRange()

c1.draw(p1)
c1.draw(p2)
c1.export("data_p1d_1.eps")
