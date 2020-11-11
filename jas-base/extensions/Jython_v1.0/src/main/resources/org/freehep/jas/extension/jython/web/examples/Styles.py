from hep.aida import *
from java.util import Random
from java.lang import Boolean

true = Boolean("true")
false = Boolean("false")

af   = IAnalysisFactory.create()
tree = af.createTreeFactory().create()
hf   = af.createHistogramFactory(tree)

h1 = hf.createHistogram1D("h1",100,-5,5)
h2 = hf.createHistogram1D("h2",100,-5,5)
h3 = hf.createHistogram1D("h3",100,-5,5)

r = Random()

for i in range(10000):
  h1.fill(r.nextGaussian())
  h2.fill(r.nextGaussian())
  h3.fill(r.nextGaussian())

plotterFactory = af.createPlotterFactory()
plotter = plotterFactory.create("Styles.py plot")

#This are the default styles for the region
regionStyle = plotter.region(0).style()
regionStyle.dataStyle().fillStyle().setVisible(false)
regionStyle.dataStyle().lineStyle().setVisible(true)
regionStyle.dataStyle().markerStyle().setVisible(false)
regionStyle.dataStyle().errorBarStyle().setVisible(false)
regionStyle.dataStyle().lineStyle().setColor("black")
regionStyle.xAxisStyle().setLabel("E/m")
regionStyle.yAxisStyle().setLabel("# Evt")

regionStyle.legendBoxStyle().textStyle().setFont("Comics")
regionStyle.legendBoxStyle().textStyle().setFontSize(16)
regionStyle.legendBoxStyle().textStyle().setItalic(false)
regionStyle.legendBoxStyle().textStyle().setBold(true)

regionStyle.statisticsBoxStyle().textStyle().setFont("Comics")
regionStyle.statisticsBoxStyle().textStyle().setFontSize(14)
regionStyle.statisticsBoxStyle().textStyle().setBold(false)
regionStyle.statisticsBoxStyle().textStyle().setItalic(true)

regionStyle.titleStyle().textStyle().setFontSize(30)
regionStyle.titleStyle().textStyle().setColor("orange")

regionStyle.xAxisStyle().labelStyle().setFontSize(24)
regionStyle.xAxisStyle().labelStyle().setItalic(true)
regionStyle.xAxisStyle().labelStyle().setColor("black")
regionStyle.xAxisStyle().tickLabelStyle().setFontSize(14)
regionStyle.xAxisStyle().tickLabelStyle().setBold(true)
regionStyle.xAxisStyle().tickLabelStyle().setColor("blue")

regionStyle.yAxisStyle().labelStyle().setFontSize(24)
regionStyle.yAxisStyle().labelStyle().setItalic(true)
regionStyle.yAxisStyle().labelStyle().setColor("brown")
regionStyle.yAxisStyle().tickLabelStyle().setFontSize(14)
regionStyle.yAxisStyle().tickLabelStyle().setBold(true)
regionStyle.yAxisStyle().tickLabelStyle().setColor("green")


#This styles overwrite some of the region styles
style = plotterFactory.createPlotterStyle()
style.dataStyle().lineStyle().setVisible(false)
style.dataStyle().markerStyle().setVisible(true)
style.dataStyle().markerStyle().setParameter("size","12")
style.dataStyle().markerStyle().setParameter("shape","3")
style.dataStyle().markerStyle().setParameter("color","blue")

plotter.region(0).plot(h1)
plotter.region(0).plot(h2,style)

#Create a new dataStyle to be used to plot the third histogram
dataStyle = plotterFactory.createDataStyle()
dataStyle.markerStyle().setVisible(false)
dataStyle.errorBarStyle().setVisible(true)
dataStyle.lineStyle().setVisible(false)
dataStyle.errorBarStyle().setColor("green")
style.setDataStyle(dataStyle)

plotter.region(0).plot(h3,style)

plotter.show()

