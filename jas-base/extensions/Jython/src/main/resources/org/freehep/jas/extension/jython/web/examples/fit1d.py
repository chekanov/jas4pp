# show a simple histogram using GROOT

from java.util import *
from javax.swing import *
from org.jlab.groot.data import *
from org.jlab.groot.math import *
from org.jlab.groot.fitter import *
from org.jlab.groot.ui import *

canvas = TCanvas("c",600,450)

histogram = H1F("histogram",100,-5,5)
randomGenerator = Random()
for i in range(1000):
     histogram.fill(randomGenerator.nextGaussian())

     
canvas.draw(histogram)

func = F1D("func","[amp]*gaus(x,[mean],[sigma])",-5.0,5.0)
func.setParameter(0,50.0)
func.setParameter(1,0.0)
func.setParameter(2,1.0)

DataFitter.fit(func,histogram,"")

histogram.setOptStat("11111111111")
histogram.setTitle("Fit of Random Gaussian")
histogram.setTitleX("random gaussian")
histogram.setTitleY("counts")

func.setLineWidth(3)
func.setLineStyle(4)
func.setLineColor(2)
canvas.draw(func,"same")

#canvas.update()

