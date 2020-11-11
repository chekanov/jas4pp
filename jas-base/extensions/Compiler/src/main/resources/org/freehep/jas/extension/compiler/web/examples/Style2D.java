import hep.aida.*;
import java.util.Random;

public class Style2D {

	public static void main( String[] argv ) {

		IAnalysisFactory af = IAnalysisFactory.create();
		ITree tree = af.createTreeFactory().create();
		IHistogramFactory hf = af.createHistogramFactory(tree);

		Random r = new Random();

		IPlotter plotter = af.createPlotterFactory().create("Style2D.java Plot");
		plotter.createRegions(2,2);
		plotter.show();

		IHistogram2D h2 = hf.createHistogram2D("Histogram 2D",40,-3,3,40,-3,3);
		ICloud2D c2 = hf.createCloud2D("Cloud 2D");

		IPlotterStyle style = plotter.region(0).style();
		style.regionBoxStyle().backgroundStyle().setColor("yellow");
		style.regionBoxStyle().foregroundStyle().setColor("green");
		style.setParameter("hist2DStyle","ellipse");
		style.dataStyle().markerStyle().setColor("blue");
		plotter.region(0).setTitle("Histogram 2D");
		plotter.region(0).plot(h2);

		style = plotter.region(1).style();
		style.statisticsBoxStyle().setVisible(false);
		style.setParameter("hist2DStyle","colorMap");
		style.dataStyle().fillStyle().setParameter("colorMapScheme","rainbow");
		plotter.region(1).setTitle("Color Map");
		plotter.region(1).plot(h2);


		style = plotter.region(2).style();
		style.dataStyle().markerStyle().setColor("red");
		style.dataStyle().markerStyle().setShape("1");
		style.dataStyle().markerStyle().setParameter("size","7");
		plotter.region(2).setTitle("Cloud 2D");
		plotter.region(2).plot(c2);

		plotter.region(3).style().setParameter("showAsScatterPlot","false");
		plotter.region(3).setTitle("Binned Cloud 2D");
		plotter.region(3).plot(c2);

		for (int i=0; i<10000; i++) {
			h2.fill(r.nextGaussian(),r.nextGaussian());
			c2.fill(r.nextGaussian(),r.nextGaussian());
		}

	}
}