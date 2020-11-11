package org.freehep.jas.extension.tupleExplorer.cut;

import java.util.Iterator;
import org.freehep.jas.extension.tupleExplorer.TupleExplorerPlugin;
import org.freehep.jas.extension.tupleExplorer.cut.AbstractCut;
import org.freehep.jas.extension.tupleExplorer.cut.AbstractCutWithVariables;
import org.freehep.jas.extension.tupleExplorer.cut.CutSet;
import org.freehep.jas.extension.tupleExplorer.cut.CutVariableSynchronization;
import org.freehep.jas.extension.tupleExplorer.cut.NTupleCutDataSet;
import org.freehep.jas.extension.tupleExplorer.cut.Numeric1DCutVariable;
import org.freehep.jas.extension.tupleExplorer.jel.JELCut;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTuple;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleColumn;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTree;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeCut;
import org.freehep.jas.extension.tupleExplorer.mutableTuple.MutableTupleTreeCutSet;
import org.freehep.jas.plugin.tree.FTreePath;
import org.freehep.xml.io.XMLIOFactory;
import org.freehep.xml.io.XMLIOManager;
import org.freehep.xml.io.XMLIOProxy;
import org.jdom.Element;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class CutFactoryAndProxy implements XMLIOFactory, XMLIOProxy {
    
    private Class[] classes = {CutSet.class,CutVariableSynchronization.class,Numeric1DCut.class,
    NTupleCutDataSet.class, Numeric1DCutVariable.class, JELCut.class, MutableTupleTreeCut.class, MutableTupleTreeCutSet.class};
    
    public Class[] XMLIOFactoryClasses() {
        return classes;
    }
    
    public Object createObject(Class objClass) throws IllegalArgumentException {
        if ( objClass == CutSet.class )
            return new CutSet("");
        else if ( objClass == Numeric1DCut.class )
            return new Numeric1DCut("");
        else if ( objClass == CutVariableSynchronization.class )
            return new CutVariableSynchronization();
        else if ( objClass == NTupleCutDataSet.class )
            return new NTupleCutDataSet();
        else if ( objClass == Numeric1DCutVariable.class )
            return new Numeric1DCutVariable("");
        else if ( objClass == MutableTupleTreeCut.class )
            return new MutableTupleTreeCut();
        else if ( objClass == MutableTupleTreeCutSet.class )
            return new MutableTupleTreeCutSet("");
        throw new IllegalArgumentException("Cannot create object ot type "+objClass);
    }
    
    public Class[] XMLIOProxyClasses() {
        return classes;
    }
    
    public void restore(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        if ( obj instanceof MutableTupleTreeCut ) {
            MutableTupleTreeCut cutVar = (MutableTupleTreeCut)obj;
            Cut c = (Cut) xmlioManager.restore( (Element)nodeEl.getChild("InnerCut").getChildren().get(0) );
            cutVar.setCut( c );
            cutVar.setDeepestPath( new FTreePath( nodeEl.getAttributeValue("deepestPath")) );
        } else if ( obj instanceof AbstractCut ) {
            
            
            AbstractCut cut = (AbstractCut) obj;
            cut.setName( nodeEl.getAttributeValue( "cutName" ) );
            cut.setState( Integer.parseInt( nodeEl.getAttributeValue( "cutState" ) ) );
            Element child = nodeEl.getChild( "CutDataSet" );
            if ( child != null )
                cut.setCutDataSet( (CutDataSet) xmlioManager.restore((Element)child.getChildren().get(0)) );
            
            if ( cut instanceof AbstractCutWithVariables ) {
                AbstractCutWithVariables acut = (AbstractCutWithVariables) cut;
                Element e = nodeEl.getChild( "CutVariables" );
                for (Iterator childIt = e.getChildren().iterator(); childIt.hasNext(); ) {
                    Element childEl = (Element) childIt.next();
                    AbstractCutVariable cutVar = (AbstractCutVariable) xmlioManager.restore( childEl );
                    acut.addCutVariable( cutVar );
                }
                
                if ( acut instanceof Numeric1DCut ) {
                    Numeric1DCut ncut = (Numeric1DCut) acut;
                    ncut.setState( ncut.getState() );
                    String sync = nodeEl.getAttributeValue("isSynchronized");
                    if ( sync != null )
                        ncut.synchronizeSliders();
                }
            }
            
            if ( obj instanceof CutSet ) {
                CutSet cutSet = (CutSet)obj;
                for (Iterator cutIt = nodeEl.getChildren().iterator(); cutIt.hasNext(); ) {
                    Element cutEl = (Element) cutIt.next();
                    Cut c = ( Cut ) xmlioManager.restore( cutEl );
                    cutSet.addCut( c );
                    int cutState = Integer.parseInt( cutEl.getAttributeValue( "cutState" ) );
                    cutSet.setCutState( c, cutState);
                }
            }
        } else if ( obj instanceof CutVariableSynchronization ) {
            CutVariableSynchronization sync = (CutVariableSynchronization) obj;
            sync.setSyncType( Integer.valueOf( nodeEl.getAttributeValue("type") ).intValue() );
            sync.setSyncScale( Double.valueOf( nodeEl.getAttributeValue("scale") ).doubleValue() );
            sync.setSyncDiff( Double.valueOf( nodeEl.getAttributeValue("diff") ).doubleValue() );
        } else if ( obj instanceof NTupleCutDataSet ) {
            NTupleCutDataSet cutDataSet = (NTupleCutDataSet) obj;
            cutDataSet.setDataMaxValue( Double.parseDouble( nodeEl.getAttributeValue("max") ) );
            cutDataSet.setDataMinValue( Double.parseDouble( nodeEl.getAttributeValue("min") ) );
            MutableTupleTree tupTree = (MutableTupleTree) xmlioManager.restore( (Element)nodeEl.getChildren().get(0));
            cutDataSet.setNTupleAndColumn( tupTree.mutableTupleForPath( new FTreePath(nodeEl.getAttributeValue("tuppath")) ), tupTree.mutableTupleColumnForPath( new FTreePath(nodeEl.getAttributeValue("colpath")) ) );
            
        } else if ( obj instanceof Numeric1DCutVariable ) {
            Numeric1DCutVariable cutVar = (Numeric1DCutVariable)obj;
            CutDataSet cutDataSet = (CutDataSet) xmlioManager.restore( (Element)nodeEl.getChildren().get(0) );
            cutVar.setCutDataSet( cutDataSet );
            cutVar.setName( nodeEl.getAttributeValue( "name" ) );
            cutVar.setValue( Double.parseDouble( nodeEl.getAttributeValue( "value" ) ) );
            cutVar.setState( Integer.parseInt( nodeEl.getAttributeValue( "state" ) ) );
        } else
            throw new IllegalArgumentException("Cannot restore object ot type "+obj.getClass());
    }
    
    public void save(Object obj, XMLIOManager xmlioManager, Element nodeEl) throws IllegalArgumentException {
        if ( obj instanceof MutableTupleTreeCut ) {
            MutableTupleTreeCut cutVar = (MutableTupleTreeCut) obj;
            nodeEl.setAttribute( "deepestPath", cutVar.cutDeepestPath().toString() );
            Cut innerCut = cutVar.cut();
            if ( innerCut != null ) {
                Element innerCutEl = new Element("InnerCut");
                innerCutEl.addContent( xmlioManager.save( innerCut ) );
                nodeEl.addContent(innerCutEl);
            }
        } else if ( obj instanceof AbstractCut ) {
            AbstractCut cut = (AbstractCut) obj;
            nodeEl.setAttribute( "cutName", cut.getName() );
            nodeEl.setAttribute( "cutState", String.valueOf( cut.getState() ) );
            CutDataSet cutDataSet = cut.getCutDataSet();
            if ( cutDataSet != null ) {
                Element cutDataSetEl = new Element("CutDataSet");
                cutDataSetEl.addContent( xmlioManager.save( cutDataSet ) );
                nodeEl.addContent(cutDataSetEl);
            }
            
            if ( cut instanceof AbstractCutWithVariables ) {
                AbstractCutWithVariables acut = (AbstractCutWithVariables) cut;
                Element el = new Element( "CutVariables" );
                for ( int i = 0; i < acut.getNCutVariables(); i++ )
                    el.addContent( xmlioManager.save( acut.getCutVariable( i ) ) );
                nodeEl.addContent(el);
                
                if ( acut instanceof Numeric1DCut ) {
                    Numeric1DCut ncut = (Numeric1DCut) acut;
                    nodeEl.setAttribute( "cutType", String.valueOf( ncut.getOriginalCutType() ) );
                    if ( ncut.getNCutVariables() == 2 )
                        if ( ncut.isSynchronized() )
                            nodeEl.setAttribute("isSynchronized","true");
                }
            }
            
            if ( obj instanceof CutSet ) {
                CutSet cutSet = (CutSet) obj;
                int nCuts = cutSet.getNCuts();
                for ( int nCut = 0; nCut < nCuts; nCut++ ) {
                    Element cutEl;
                    Cut c = cutSet.getCut( nCut );
                    cutEl = xmlioManager.save( c );
                    cutEl.setAttribute( "cutState", String.valueOf( cutSet.getCutState( c ) ) );
                    nodeEl.addContent( cutEl );
                }
            }
            
        } else if ( obj instanceof CutVariableSynchronization ) {
            CutVariableSynchronization sync = (CutVariableSynchronization) obj;
            nodeEl.setAttribute("type",String.valueOf(sync.syncType()));
            nodeEl.setAttribute("scale",String.valueOf(sync.syncScale()));
            nodeEl.setAttribute("diff",String.valueOf(sync.syncDiff()));
        } else if ( obj instanceof NTupleCutDataSet ) {
            NTupleCutDataSet cutDataSet = (NTupleCutDataSet) obj;
            nodeEl.setAttribute("max", String.valueOf( cutDataSet.getDataMaxValue() ) );
            nodeEl.setAttribute("min", String.valueOf( cutDataSet.getDataMinValue() ) );
            nodeEl.setAttribute("colpath",cutDataSet.getNTupleColumn().treePath().toString());
            nodeEl.setAttribute("tuppath",cutDataSet.getNTuple().treePath().toString());
            nodeEl.addContent( xmlioManager.save( cutDataSet.getNTuple().mutableTupleTree()) );
        } else if ( obj instanceof Numeric1DCutVariable ) {
            Numeric1DCutVariable cutVar = (Numeric1DCutVariable)obj;
            nodeEl.setAttribute( "name", cutVar.getName() );
            nodeEl.setAttribute( "state", String.valueOf( cutVar.getState() ) );
            nodeEl.setAttribute( "value", String.valueOf( cutVar.getValue() ) );
            nodeEl.addContent( xmlioManager.save( cutVar.getCutDataSet() ) );
        } else
            throw new IllegalArgumentException("Cannot save object ot type "+obj.getClass());
    }
}
