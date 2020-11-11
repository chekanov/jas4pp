/*
 * AIDAHTMLComponentFactory.java
 *
 * Created on August 19, 2004, 5:24 PM
 */

package org.freehep.jas.extension.aida;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.text.html.HTML.Attribute;

import hep.aida.*;

import jas.hist.DataSource;
import jas.hist.HasDataSource;
import jas.hist.JASHist;
import jas.hist.JASHistData;
import jas.hist.JASHistStyle;
import jas.hist.JASHist1DHistogramStyle;
import jas.hist.JASHist2DHistogramStyle;
import jas.hist.JASHistScatterPlotStyle;
import jas.hist.XMLHistBuilder;

import org.freehep.application.studio.Studio;
import org.freehep.jas.services.HTMLComponentFactory;
import org.freehep.jas.util.IgnoreCase;
import org.freehep.swing.ColorConverter;
import org.freehep.util.FreeHEPLookup;

/**
 *
 * @author  serbo
 */
public class AIDAHTMLComponentFactory implements HTMLComponentFactory {
    static final String[] pointStyles = {
        "dot","box","triangle","diamond","star", "vert_line", "horiz_line", "cross","circle","square"
    };
    
    private Studio app;
    private ITree aidaMasterTree;
    private AIDATreeDataSourceProvider adsp;
    private static final String[] classes = {
        "hep.aida.IDataPointSet",
        "hep.aida.IHistogram1D",
        "hep.aida.IHistogram2D",
        "hep.aida.ICloud1D",
        "hep.aida.ICloud2D",
        "hep.aida.IProfile1D",
        "hep.aida.IProfile2D",
        "hep.aida.IFunction"
    };
    
    public int getPointStyleIndex(String style) {
        int index = -1;
        for (int i=0; i<pointStyles.length; i++) {
            if (style.equalsIgnoreCase(pointStyles[i])) {
                index = i;
                break;
            }
        }
        return index;
    }
    
    void init(Studio app) {
        FreeHEPLookup lookup = app.getLookup();
        for (int i=0; i<classes.length; i++) {
            lookup.add(this, classes[i]);
        }
        this.app = app;
        adsp = new AIDATreeDataSourceProvider();
    }
    
    public javax.swing.JComponent getComponent(String name, java.util.Map attributes) {
        JComponent component = null;
        JASHist plot = null;
        if (app == null) app = (Studio) Studio.getApplication();
        
        String path = (String) IgnoreCase.getIgnoreCase(attributes, "DATA");
        
        try {
            if (IgnoreCase.containsIgnoreCase(attributes, "XMLURL")) {
                String xmlURLString = (String) IgnoreCase.getIgnoreCase(attributes, "XMLURL");
                //System.out.println("\t\tXML File: "+xmlURLString);
                URL baseURL = (URL) IgnoreCase.getIgnoreCase(attributes, "BASEURL");
                URL xmlURL = new URL(baseURL, xmlURLString);
                InputStream is = xmlURL.openStream();
                InputStreamReader reader = new InputStreamReader(is);
                XMLHistBuilder builder = new XMLHistBuilder(reader, xmlURLString);
                plot = builder.getSoloPlot();
                plot.setAllowUserInteraction(false);
                component = plot;
                reader.close();
            } else {
                plot = new JASHist();
                DataSource ds = adsp.getDataSource(path);
                
                JASHistData data = plot.addData(ds);
                JASHistStyle style = data.getStyle();
                
                plot.setForegroundColor(Color.black);
                plot.setBackground(Color.white);
                plot.setDataAreaColor(Color.white);
                
                data.show(true);
                //plot.setVisible(true);
                
                // Change defaults for plotting 1D histograms
                if (name.indexOf("2D") <= 0) {
                    plot.getYAxis().setAllowSuppressedZero(false);
                    if (style instanceof JASHist1DHistogramStyle) {
                        ((JASHist1DHistogramStyle) style).setHistogramFill(false);
                        ((JASHist1DHistogramStyle) style).setHistogramBarLineWidth(1.0f);
                        ((JASHist1DHistogramStyle) style).setHistogramBarLineColor(Color.black);
                        ((JASHist1DHistogramStyle) style).setShowErrorBars(false);
                        ((JASHist1DHistogramStyle) style).setErrorBarWidth(1.0f);
                        ((JASHist1DHistogramStyle) style).setErrorBarColor(Color.black);
                        ((JASHist1DHistogramStyle) style).setLinesBetweenPointsWidth(1.0f);
                        ((JASHist1DHistogramStyle) style).setLineColor(Color.black);
                    }
                } else {
                    ((JASHist2DHistogramStyle) style).setHistStyle(JASHist2DHistogramStyle.STYLE_BOX);
                    ((JASHist2DHistogramStyle) style).setShapeColor(Color.black);
                }
                
                if (name.equalsIgnoreCase("hep.aida.IDataPointSet")) {
                    if (style instanceof JASHist1DHistogramStyle) {
                        ((JASHist1DHistogramStyle) style).setShowLinesBetweenPoints(true);
                        ((JASHist1DHistogramStyle) style).setLineColor(Color.blue);
                        
                        ((JASHist1DHistogramStyle) style).setShowDataPoints(true);
                        ((JASHist1DHistogramStyle) style).setDataPointColor(Color.black);
                        ((JASHist1DHistogramStyle) style).setDataPointStyle(JASHist1DHistogramStyle.SYMBOL_DOT);
                        ((JASHist1DHistogramStyle) style).setDataPointSize(5);
                        
                        ((JASHist1DHistogramStyle) style).setShowErrorBars(false);
                        ((JASHist1DHistogramStyle) style).setShowHistogramBars(false);
                    }
                }
                
                configurePlot(plot, attributes, data);
                
                component = plot;
                //data.show(true);
                
                plot.setAllowUserInteraction(true);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Can not create plot for path : "+path, e);
        }
        
        return component;
    }
    
    private void configurePlot(JASHist plot, Map attributes, JASHistData data) {
        JASHistStyle style = data.getStyle();
        Iterator it = attributes.keySet().iterator();
        
        while (it.hasNext()) {
            try {
                Object objKey   = it.next();
                Object objVal = attributes.get(objKey);
                
                String key   = objKey.toString();
                String value = objVal.toString();
                
                configurePlotProperties(plot,  key,  value);
                
                if (style instanceof JASHist1DHistogramStyle) configurePlotStyle((JASHist1DHistogramStyle) style, key, value);
                else if (style instanceof JASHist2DHistogramStyle) configurePlotStyle((JASHist2DHistogramStyle) style, key, value);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void configurePlotProperties(JASHist plot, String key, String value) throws Exception {
        boolean b = toBoolean(value);
        
        if (key.equalsIgnoreCase("xMin")) {
            double d = Double.parseDouble(value);
            plot.getXAxis().setMin(d);
        } else if (key.equalsIgnoreCase("xMax")) {
            double d = Double.parseDouble(value);
            plot.getXAxis().setMax(d);
        } else if (key.equalsIgnoreCase("yMin")) {
            double d = Double.parseDouble(value);
            plot.getYAxis().setMin(d);
        } else if (key.equalsIgnoreCase("y0Min")) {
            double d = Double.parseDouble(value);
            plot.getYAxis(0).setMin(d);
        } else if (key.equalsIgnoreCase("y1Min")) {
            double d = Double.parseDouble(value);
            plot.getYAxis(1).setMin(d);
        } else if (key.equalsIgnoreCase("yMax")) {
            double d = Double.parseDouble(value);
            plot.getYAxis().setMax(d);
        } else if (key.equalsIgnoreCase("y0Max")) {
            double d = Double.parseDouble(value);
            plot.getYAxis(0).setMax(d);
        } else if (key.equalsIgnoreCase("y1Max")) {
            double d = Double.parseDouble(value);
            plot.getYAxis(1).setMax(d);
            
        } else if (key.equalsIgnoreCase("plotForegroundColor") || key.equalsIgnoreCase("foregroundColor")) {
            plot.setForegroundColor(toColor(value));
        } else if (key.equalsIgnoreCase("plotBackgroundColor") || key.equalsIgnoreCase("backgroundColor")) {
            plot.setBackground(toColor(value));
        } else if (key.equalsIgnoreCase("dataAreaColor")) {
            plot.setDataAreaColor(toColor(value));
            
        } else if (key.equalsIgnoreCase("XAxisAllowSuppressedZero")) {
            plot.getXAxis().setAllowSuppressedZero(b);
        } else if (key.equalsIgnoreCase("YAxisAllowSuppressedZero")) {
            plot.getYAxis().setAllowSuppressedZero(b);
        } else if (key.equalsIgnoreCase("Y0AxisAllowSuppressedZero")) {
            plot.getYAxis(0).setAllowSuppressedZero(b);
        } else if (key.equalsIgnoreCase("Y1AxisAllowSuppressedZero")) {
            plot.getYAxis(1).setAllowSuppressedZero(b);
        } else if (key.equalsIgnoreCase("XAxisLogarithmic")) {
            plot.getXAxis().setLogarithmic(b);
        } else if (key.equalsIgnoreCase("YAxisLogarithmic")) {
            plot.getYAxis().setLogarithmic(b);
        } else if (key.equalsIgnoreCase("Y0AxisLogarithmic")) {
            plot.getYAxis(0).setLogarithmic(b);
        } else if (key.equalsIgnoreCase("Y1AxisLogarithmic")) {
            plot.getYAxis(1).setLogarithmic(b);
            
        } else if (key.equalsIgnoreCase("width") || key.equalsIgnoreCase("plotWidth")) {
            int d = Integer.parseInt(value);
            Dimension dim = plot.getPreferredSize();
            dim.width = d;
            plot.setPreferredSize(dim);
            plot.setMaximumSize(dim);
        } else if (key.equalsIgnoreCase("height") || key.equalsIgnoreCase("plotHeight")) {
            int d = Integer.parseInt(value);
            Dimension dim = plot.getPreferredSize();
            dim.height = d;
            plot.setPreferredSize(dim);
            plot.setMaximumSize(dim);
        }
    }
    
    private void configurePlotStyle(JASHist1DHistogramStyle style, String key, String value) throws Exception {
        boolean b = toBoolean(value);
        
        if      (key.equalsIgnoreCase("histogramBarsFilled") || key.equalsIgnoreCase("histogramFill")) style.setHistogramFill(toBoolean(value));
        else if (key.equalsIgnoreCase("showHistogramBars"     )) style.setShowHistogramBars(toBoolean(value));
        else if (key.equalsIgnoreCase("showErrorBars"         )) style.setShowErrorBars(toBoolean(value));
        else if (key.equalsIgnoreCase("showDataPoints"        )) style.setShowDataPoints(toBoolean(value));
        else if (key.equalsIgnoreCase("showLinesBetweenPoints")) style.setShowLinesBetweenPoints(toBoolean(value));
        else if (key.equalsIgnoreCase("dataPointSize"         )) { int i = Integer.parseInt(value); style.setDataPointSize(i); }
        else if (key.equalsIgnoreCase("histogramBarColor"     )) style.setHistogramBarColor(toColor(value));
        else if (key.equalsIgnoreCase("errorBarColor"         )) style.setErrorBarColor(toColor(value));
        else if (key.equalsIgnoreCase("dataPointColor"        )) style.setDataPointColor(toColor(value));
        else if (key.equalsIgnoreCase("lineColor"             )) style.setLineColor(toColor(value));
        else if (key.equalsIgnoreCase("dataPointStyle"        )) style.setDataPointStyle(getPointStyleIndex(value));
    }
    
    private void configurePlotStyle(JASHist2DHistogramStyle style, String key, String value) throws Exception {
        if      (key.equalsIgnoreCase("startDataColor"    )) style.setStartDataColor(toColor(value));
        else if (key.equalsIgnoreCase("endDataColor"      )) style.setEndDataColor(toColor(value));
        else if (key.equalsIgnoreCase("showOverflow"      )) style.setShowOverflow(toBoolean(value));
        else if (key.equalsIgnoreCase("showPlot"          )) style.setShowPlot(toBoolean(value));
        else if (key.equalsIgnoreCase("histStyle"         )) style.setHistStyle(toStyle(value));
        else if (key.equalsIgnoreCase("colorMapScheme"    )) style.setColorMapScheme(toColorMapScheme(value));
        else if (key.equalsIgnoreCase("shapeColor"        )) style.setShapeColor(toColor(value));
        else if (key.equalsIgnoreCase("overflowBinColor"  )) style.setOverflowBinColor(toColor(value));
        else if (key.equalsIgnoreCase("logZ"              )) style.setLogZ(toBoolean(value));
        
    }
    
    public Color toColor(String value) throws Exception {
        return ColorConverter.get(value); 
    }
    private int toStyle(String s) {
        if (s.equalsIgnoreCase("STYLE_BOX")) return JASHist2DHistogramStyle.STYLE_BOX;
        if (s.equalsIgnoreCase("STYLE_ELLIPSE")) return JASHist2DHistogramStyle.STYLE_ELLIPSE;
        if (s.equalsIgnoreCase("STYLE_COLORMAP")) return JASHist2DHistogramStyle.STYLE_COLORMAP;
        return JASHist2DHistogramStyle.STYLE_BOX;
    }
    private int toColorMapScheme(String s) {
        if (s.equalsIgnoreCase("COLORMAP_WARM")) return JASHist2DHistogramStyle.COLORMAP_WARM;
        if (s.equalsIgnoreCase("COLORMAP_COOL")) return JASHist2DHistogramStyle.COLORMAP_COOL;
        if (s.equalsIgnoreCase("COLORMAP_THERMAL")) return JASHist2DHistogramStyle.COLORMAP_THERMAL;
        if (s.equalsIgnoreCase("COLORMAP_RAINBOW")) return JASHist2DHistogramStyle.COLORMAP_RAINBOW;
        if (s.equalsIgnoreCase("COLORMAP_GRAYSCALE")) return JASHist2DHistogramStyle.COLORMAP_GRAYSCALE;
        if (s.equalsIgnoreCase("COLORMAP_USERDEFINED")) return JASHist2DHistogramStyle.COLORMAP_USERDEFINED;
        return JASHist2DHistogramStyle.COLORMAP_WARM;
    }
    private boolean toBoolean(String value) {
        boolean b = false;
        if (value == null || value.equalsIgnoreCase("true") || value.equalsIgnoreCase("")) b = true;
        return b;
    }
}
