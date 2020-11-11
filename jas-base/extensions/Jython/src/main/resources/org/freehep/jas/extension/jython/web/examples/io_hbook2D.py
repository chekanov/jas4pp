# Input/Output | C | 1.7 | S.Chekanov | 2D histograms saved in HBook (by a fortran program) 

from java.awt import Color,Font 
from jhplot  import HPlot3D,H2D,H1D,HBook 

# make main canvas
c1 = HPlot3D("Canvas")
c1.visible()
c1.setGTitle("2D Histogram filled by XML CFBook", Color.blue) #put title

# input file
DataDir=SystemDir+fSep+"macros"+fSep+"examples"+fSep+"data"+fSep+"cpp.xml";

# create HBook singleton 
hb = HBook("my hbook")
# read histogram from XML file
hb.read(DataDir)

# print all histograms
print hb.listH2D()
# get 3D histograms
h1 = hb.getH2D(0)
c1.draw(h1)


# c1.export(Editor.DocMasterName()+".png")


