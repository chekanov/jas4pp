import hep.aida.*;
import java.util.Random;

public class Style1D {

	public static void main( String[] argv ) {

		IAnalysisFactory af = IAnalysisFactory.create();
		ITree tree = af.createTreeFactory().create();
		IHistogramFactory hf = af.createHistogramFactory(tree);

		IHistogram1D h1 = hf.createHistogram1D("Histogram 1D",50,-3,3);

		IPlotter plotter = af.createPlotterFactory().create("Style1D Example");
		plotter.region(0).plot(h1);
		plotter.show();

		IPlotterStyle style = plotter.region(0).style();

                style.regionBoxStyle().backgroundStyle().setColor("255,255,104");
                style.dataBoxStyle().backgroundStyle().setColor("204,255,255");
                style.dataBoxStyle().borderStyle().setBorderType("shadow");
                style.dataStyle().lineStyle().setVisible(false);
                style.dataStyle().fillStyle().setVisible(false);
                style.dataStyle().markerStyle().setVisible(true);
                style.dataStyle().markerStyle().setShape("circle");

		Random r = new Random();
		for (int i=0; i<10000; i++) 
			h1.fill(r.nextGaussian());
	}
}



