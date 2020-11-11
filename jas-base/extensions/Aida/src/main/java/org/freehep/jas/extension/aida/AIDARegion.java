/*
 * JASPlotterRegion.java
 *
 * Created on September 27, 2002, 2:21 PM
 */

package org.freehep.jas.extension.aida;

import hep.aida.IBaseHistogram;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IManagedObject;
import hep.aida.IPlottable;
import hep.aida.IPlotter;
import hep.aida.IPlotterStyle;

import hep.aida.ref.event.AIDAListener;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.event.Connectable;
import hep.aida.ref.event.ConnectEvent;
import hep.aida.ref.plotter.PlotterRegion;

import jas.hist.DataSource;

import org.freehep.jas.services.PlotRegion;
import org.freehep.jas.services.Plotter;
import org.freehep.jas.services.PlotFactory;
import org.freehep.jas.plugin.plotter.DefaultPlotter;
import org.freehep.jas.plugin.plotter.JAS3Plot;

import java.awt.Color;
import java.util.Vector;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * A dummy implementation of PlotterRegion
 * @author tonyj
 * @version $Id: AIDARegion.java 16235 2015-02-20 04:37:31Z onoprien $
 */
class AIDARegion extends PlotterRegion implements AIDAListener {
    
    private PlotRegion region;
    private PlotFactory factory;
    private Plotter plot;
    private Vector sources;
    private JPanel noConnectionPanel;
    
    AIDARegion(PlotFactory factory, PlotRegion region) {
        this(factory, region, null);
    }
    
    AIDARegion(PlotFactory factory, PlotRegion region, IPlotter plotter) {
        super(plotter);
        this.factory = factory;
        this.region = region;
        this.sources = new Vector();
        noConnectionPanel = new JPanel();
    }
    
    public void addToRegion(Object thing, IPlotterStyle styleObj, String options) {
        if ( plot == null || getPlot() == null ) {
            Class clazz = thing.getClass();
            if ( IBaseHistogram.class.isAssignableFrom(clazz) ||
                    IDataPointSet.class.isAssignableFrom(clazz) ||
                    IFunction.class.isAssignableFrom(clazz) ) {
                createPlot();
            } else createPlot(thing);
        }
        plot.plot(thing,getMode(options), styleObj, options);
        //        add(thing,styleObj, options);
        
        setConnection(thing);
    }
    
    void setAidaPlotter( Plotter plot ) {
        this.plot = plot;
        this.setPlot( ( (DefaultPlotter) plot ).getPlot() );
    }
    
    void createPlot() {
        if (plot == null) {
            plot = factory.createPlotterFor(IBaseHistogram.class);
            setAidaPlotter(plot);
            ( (AIDAPlot) plot ).setPlotterRegion(this);
            region.showPlot(plot);
        }
    }
    
    void createPlot(Object thing) {
        Class clazz = null;
        if (thing instanceof IPlottable) {
            clazz = getClassForPlottable((IPlottable) thing);
        }
        if (clazz == null) clazz = thing.getClass();
        if (plot == null) {
            plot = factory.createPlotterFor(clazz);
            region.showPlot(plot);
        }
    }
    
    public void removeObjectFromRegion(Object thing) {
        super.removeObjectFromRegion(thing);
        clearObservable(thing);
    }
    
    public void removeDataSourceFromRegion( DataSource ds ) {
        ( (JAS3Plot) getPlot() ).removeJAS3Data( ds );
    }
    
    public void clearRegion() {
        super.clearRegion();
        region.clear();
        plot = null;
        clearObservables();
    }
    private void clearObservable(Object thing) {
        if (sources.size() > 0) {
            synchronized (sources) {
                boolean b = sources.remove(thing);
                if (thing instanceof IsObservable) {
                    ((IsObservable) thing).removeListener(this);
                    connectAction((IsObservable) thing);
                }
                //System.out.println("clearObservable  removed="+b+", observable="+thing);
            }
        }
    }
    private void clearObservables() {
        if (sources.size() > 0) {
            synchronized (sources) {
                for (int i=0; i<sources.size(); i++) {
                    IsObservable o = (IsObservable) sources.get(i);
                    if (o != null) o.removeListener(this);
                }
                sources.clear();
            }
            connectAction(null);
        }
    }
    
    private Class getClassForPlottable(IPlottable thing) {
        Class clazz = null;
        String type = thing.type();
        try {
            clazz = Class.forName(type);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return clazz;
    }
    
    private void setConnection(Object thing) {
        if (thing instanceof IsObservable) {
            IsObservable observable = (IsObservable) thing;
            if (!sources.contains(observable)) {
                observable.addListener(this);
                sources.add(observable);
                observable.setValid(this);
            }
            synchronized (observable) {
                connectAction(observable);
            }
        }
    }
    
    private boolean checkConnection() {
        boolean connection = true;
        Object[] array = sources.toArray();
        if (array == null || array.length == 0) return connection;
        
        for (int i=0; i<array.length; i++) {
            if (array[i] instanceof Connectable) {
                if (!((Connectable) array[i]).isConnected()) {
                    connection = false;
                    break;
                }
            }
        }
        
        return connection;
    }
    
    // Listern here for connect/disconnect events
    public void stateChanged(java.util.EventObject e) {
        if (!(e instanceof ConnectEvent)) return;
        
        ConnectEvent event = (ConnectEvent) e;
        Object obj = event.getSource();
        boolean connect = event.isConnected();
        IsObservable observable = null;
        
        String name = null;
        if (obj instanceof IManagedObject) name = ((IManagedObject) obj).name();
        //System.out.println("AIDARegion.stateChanged === Got event, conect="+connect+", name="+name+", source="+obj);
        
        if (obj instanceof IsObservable) observable = (IsObservable) obj;
        else
            throw new RuntimeException("Wrong Data Type: "+obj);
        
        //System.out.println("AIDARegion.stateChanged :: name="+name+", connect="+connect);
        synchronized (obj) {
            connectAction(observable);
        }
    }
    
    void connectAction(IsObservable obj) {
        boolean connect = checkConnection();
        invokeOnSwingThread(new ConnectThread(obj, connect, noConnectionPanel, this));
    }
    
    class ConnectThread implements Runnable {
        
        IsObservable observable;
        boolean connect;
        AIDAListener listener;
        JComponent component;
        
        ConnectThread(IsObservable observable, boolean connect, JComponent component, AIDAListener listener) {
            this.observable = observable;
            this.connect = connect;
            this.listener = listener;
            this.component = component;
        }
        
        public void run() {
            try {
                if (observable != null) {
                    synchronized (observable) {
                        if (connect) {
                            if (plot != null && plot.viewable() != null)
                                ((JComponent) plot.viewable()).setBackground(Color.white);
                        } else ((JComponent) plot.viewable()).setBackground(Color.red);
                        observable.setValid(listener);
                    }
                } else {
                    if (plot != null && plot.viewable() != null) {
                        if (connect) ((JComponent) plot.viewable()).setBackground(Color.white);
                        else ((JComponent) plot.viewable()).setBackground(Color.red);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }
    
    private static void invokeOnSwingThread(Runnable run) {
        SwingUtilities.invokeLater(run);
    }
}
