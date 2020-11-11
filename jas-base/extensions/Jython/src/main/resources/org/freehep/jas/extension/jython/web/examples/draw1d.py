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

text = LatexText("Random Gaussian Distrobution with #sigma = 1.0",60,20)
text.setFontSize(18)
text2 = LatexText("#mu = 0.0, R^2 = 5.2, #Theta = 4.0 ",60,40)
text2.setFontSize(18)

line1 = DataLine(-2.5,30.0,-2.5,0.0)
line1.setArrowSizeEnd(15)
line1.setArrowAngle(25)
line1.setLineColor(2)

line2 = DataLine(2.5,30.0,2.5,0.0)
line2.setArrowSizeEnd(15)
line2.setArrowAngle(25)
line2.setLineColor(3)

line3 = DataLine(-2.5,22.0,2.5,22.0)
line3.setArrowSizeEnd(15)
line3.setArrowSizeOrigin(15)
line3.setArrowAngle(15)
line3.setLineColor(4)

canvas.draw(histogram)
canvas.draw(text)
canvas.draw(text2)
canvas.draw(line1)
canvas.draw(line2)
canvas.draw(line3)
