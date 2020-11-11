import org.jlab.groot.data.*;
import org.jlab.groot.ui.*;
import org.jlab.groot.fitter.*;
import org.jlab.groot.math.*;

double[] barberx = new double[]{3.95,4.05,4.15,4.25,4.35,4.45,4.55,4.65,4.75};
double[] barbery = new double[]{0.8,3.1,10.0,29.8,31.2,42.5,28.5,39.8,29.7};
double[] barberyerr = new double[]{0.0,2.0,4.0,6.0,6.5,8.0,6.0,7.0,6.5};

double[] bodenkampx = new double[]{((5.102+4.740)/2.0),((5.464+5.102)/2.0),((5.464+5.826)/2.0),((5.826+6.188)/2.0),((6.188+6.550)/2.0)};
double[] bodenkampy = new double[]{75.8,82.1,65.0,89.6,84.9};
double[] bodenkampyerr = new double[]{13.4,16.7,13.5,17.5,15.7};

GraphErrors barber = new GraphErrors();
GraphErrors bodemkamp = new GraphErrors();

for(int i=0; i< barberx.length; i++){
     barber.addPoint(barberx[i], barbery[i], 0, barberyerr[i]);
}
for(int i=0; i< bodenkampx.length; i++){
     bodemkamp.addPoint(bodenkampx[i], bodenkampy[i], 0, bodenkampyerr[i]);
}

barber.setTitleX("E#gamma [GeV]");
barber.setTitleY("#sigma [nb]");

bodemkamp.setTitleX("E#gamma [GeV]");
bodemkamp.setTitleY("#sigma [nb]");

F1D func1 = new F1D("func1","[p0]+[p1]*x+[p2]*x*x",3.95,4.8);
func1.setLineWidth(2);
func1.setLineStyle(4);

DataFitter.fit(func1,barber,"Q");

F1D func2 = new F1D("func2","[p0]+[p1]*x",4.5,6.4);
func2.setLineWidth(2);
func2.setLineStyle(3);
func2.setLineColor(4);

DataFitter.fit(func2,bodemkamp,"Q");

String label = String.format("f = %.3f+(%.3f)x+(%.3f)x^2",
            func1.getParameter(0), func1.getParameter(1),
            func1.getParameter(2));

LatexText text = new LatexText(label,60,20);
text.setFontSize(18);
text.setColor(7);
TCanvas c = new TCanvas("c",800,400);

c.divide(2,1);
c.cd(0).draw(barber).draw(func1,"same").draw(text);
c.cd(1).draw(bodemkamp).draw(func2,"same");
