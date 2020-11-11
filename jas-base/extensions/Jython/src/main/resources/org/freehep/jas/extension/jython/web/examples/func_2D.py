# Example from DMelt http://jwork.org/dmelt/
# S.Chekanov (ANL)

from jhplot  import HPlot3D,F2D

c1 = HPlot3D("Canvas",600,400)
c1.setNameX("Xaxis")
c1.setNameY("Yaxis")
c1.setNameY("Zaxis")
c1.visible()

c1.setRange(0,10,0,10,0,10)
f1 = F2D("2*exp(-x*y/20)+10*sin(pi*x)+x*y")
c1.draw(f1)

# export to some image (png,eps,pdf,jpeg...)
c1.export("func_2D.eps")
