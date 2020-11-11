package org.freehep.jas.extension.aida;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.*;

import hep.aida.IManagedObject;

import hep.aida.ref.event.AIDAListener;
import hep.aida.ref.event.Connectable;
import hep.aida.ref.event.ConnectEvent;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.event.ObserverAdapter;

import jas.hist.DataSource;
import jas.hist.JASHist;
import jas.hist.JASHistData;

/**
 * @version $Id: AIDAConnectionObserver.java 13893 2011-09-28 23:42:34Z tonyj $
 */
public abstract class AIDAConnectionObserver implements AIDAListener {
    
    protected Vector sources;
    
    public AIDAConnectionObserver() {
        this(null);
    }

    public AIDAConnectionObserver(JASHist jasHist) {
        this.sources = new Vector();
        if (jasHist != null) setObservables(jasHist);
    }

    /**
     * This method is called when ConnectionEvent is received from
     * observable, or when adding/removing observables
     */
    public abstract void connectAction(ConnectEvent event, boolean connect);
    
    public void removeObservable(Object thing) {
        if (sources.size() > 0) {
            synchronized (sources) {
                boolean b = sources.remove(thing);
                if (thing instanceof IsObservable) {
                    ((IsObservable) thing).removeListener(this);
                    
                    ConnectEvent event = new ConnectEvent(thing);
                    if (thing instanceof Connectable) event.setConnected(((Connectable) thing).isConnected());
                    stateChangedAction(event);
                }
                //System.out.println("clearObservable  removed="+b+", observable="+thing);
            }
        }
    }
    
    public void removeObservables() {
        if (sources.size() > 0) {
            synchronized (sources) {
                for (int i=0; i<sources.size(); i++) {
                    IsObservable o = (IsObservable) sources.get(i);
                    if (o != null) o.removeListener(this);
                }
                sources.clear();
                stateChangedAction(null);
            }
        }
    }
    
    public void addObservable(IsObservable observable) {
        if (observable == null) return;
        synchronized (sources) {
            if (!sources.contains(observable)) {
                sources.add(observable);
                observable.addListener(this);
                observable.setValid(this);
            }
            
            ConnectEvent event = new ConnectEvent(observable);
            if (observable instanceof Connectable) event.setConnected(((Connectable) observable).isConnected());
            stateChangedAction(event);
        }
     }

    public void addObservables(IsObservable[] observables) {
        if (observables == null) return;
        for (int i=0; i<observables.length; i++) {
            addObservable(observables[i]);
        }
    }
    
    public void setObservables(JASHist jasHist) {
        if (jasHist == null) return;
        removeObservables();
        IsObservable[] observables = null;
        ArrayList list = new ArrayList();
        
        Enumeration e1 = jasHist.getDataSources();
        if (e1 != null) {
            while (e1.hasMoreElements()) {
                JASHistData data = (JASHistData) e1.nextElement();
                DataSource ds = data.getDataSource();
                if (ds instanceof IsObservable) list.add(ds);
                else if (ds instanceof ObserverAdapter) {
                    IsObservable observable = ((ObserverAdapter) ds).getObservable();
                    if (observable != null) list.add(observable);
                }
            }
        }
        
        Enumeration e2 = jasHist.get1DFunctions();
        if (e2 != null) {
            while (e2.hasMoreElements()) {
                JASHistData data = (JASHistData) e2.nextElement();
                DataSource ds = data.getDataSource();
                if (ds instanceof IsObservable) list.add(ds);
                else if (ds instanceof ObserverAdapter) {
                    IsObservable observable = ((ObserverAdapter) ds).getObservable();
                    if (observable != null) list.add(observable);
                }
            }
        }
        
        observables = new IsObservable[list.size()];
        observables = (IsObservable[]) list.toArray(observables);
        addObservables(observables);
    }
    
    protected boolean checkConnection() {
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
        //System.out.println("AIDAConnectionObserver.stateChanged === Got event, connect="+connect+", name="+name+", source="+obj);

        if (obj instanceof IsObservable) observable = (IsObservable) obj;
        else
            throw new RuntimeException("Wrong Data Type: "+obj);
        
        //System.out.println("AIDARegion.stateChanged :: name="+name+", connect="+connect);  
        if (obj != null) {
            synchronized (obj) {
                stateChangedAction(event);
            }
        } else  {
            stateChangedAction(event);
        }
    }
   
    protected void stateChangedAction(ConnectEvent event) {
        boolean connect = checkConnection();
        invokeOnSwingThread(new ConnectThread(event, connect, this));
    }
    
    class ConnectThread implements Runnable {
        ConnectEvent event;
        IsObservable observable;
        boolean connect;
        AIDAListener listener;
        
        ConnectThread(ConnectEvent event, boolean connect, AIDAListener listener) {
            this.event = event;
            this.connect = connect;
            this.listener = listener;
            this.observable = null;
            
            if (event != null) {
                Object obj = event.getSource();
                if (obj instanceof IsObservable) observable = (IsObservable) obj;
            }
        }
        
        public void run() {
            //System.out.println("ConnectThread.run connect="+connect+", observable="+observable);
            try {
                if (observable != null) {
                    connectAction(event, connect);
                    synchronized (observable) {
                        observable.setValid(listener);
                    }
                } else {
                    connectAction(event, connect);
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
