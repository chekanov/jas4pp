# Plotting function. Example from DMelt http://jwork.org/dmelt/
# S.Chekanov (ANL)

from java.awt import Font,Color
from jhplot  import HPlot,F1D

c1 = HPlot("Canvas",800,400,2, 1)
c1.setGTitle("Example of functions", Color.red) #put title
c1.setNameX("Xaxis")
c1.setNameY("Yaxis")
c1.setName("Canvas title")
c1.visible(1)
c1.setAutoRange()

f1 = F1D("2*exp(-x*x/50)+sin(pi*x)/x", -2.0, 5.0)
f1.setPenDash(4)
c1.draw(f1)

f1 = F1D("exp(-x*x/50)+pi*x", -2.0, 5.0)
f1.setColor(Color.green)
f1.setPenWidth(1)
c1.draw(f1)

c1.cd(2,1)
c1.setAutoRange()

f1 = F1D("20*x*x", -2.0, 5.0)
f1.setColor(Color.red)
f1.setPenWidth(3)
c1.draw(f1)

f1 = F1D("10*sqrt(x)+20*x", 0.1, 10.0)
f1.setColor(Color.blue)
f1.setPenWidth(3)
c1.draw(f1)

f1 = F1D("15*sqrt(x)+20*x*x", 0.1, 10.0)
f1.setColor(Color.blue)
f1.setPenDash(3)
c1.draw(f1);

c1.visible(1)

# export to some image (png,eps,pdf,jpeg...)
# c1.export(Editor.DocMasterName()+".png")
