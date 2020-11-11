/*
 * AidaStatistics.java
 *
 * Created on March 19, 2001, 12:58 PM
 */

package hep.aida.ref.plotter.adapter;

import hep.aida.IAnnotation;
import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import jas.hist.ExtendedStatistics;

import java.util.ArrayList;

/**
 *
 * @author  manj
 * @version $Id: AIDAHistogramStatistics1D.java 10734 2007-05-16 19:31:00Z serbo $
 */
class AIDAHistogramStatistics1D implements ExtendedStatistics {
    /** Creates new AidaStatistics */
    AIDAHistogramStatistics1D(IHistogram1D histo) {
        this.histo = histo;
    }
    /**
     * @return A list of statistic names
     */
    public String[] getStatisticNames() {
        ArrayList stat = new ArrayList();
        for ( int i = 0; i < statNames.length; i++ )
            stat.add(statNames[i]);
        if ( ((Integer) getExtendedStatistic(outOfRangeStat)).intValue() != 0 )
            stat.add(outOfRangeStat);
        if ( ((Integer) getExtendedStatistic(nanExtStat)).intValue() != 0 )
            stat.add(nanExtStat);
        if ( ((Integer) getExtendedStatistic("Entries")).intValue() != ((Double) getExtendedStatistic(sumOfWeightsStat)).doubleValue() )
            stat.add(sumOfWeightsStat);
        
        addAnnotationStatistics(stat);
        
        String[] statArray = new String[stat.size()];
        for ( int i = 0; i < stat.size(); i++ )
            statArray[i] = (String)stat.get(i);
        return statArray;
    }
    /**
     * @param The statistic name, as returned by getStatisticNames()
     * @return The value for the statistic
     */
    public double getStatistic(String name) {
        if (name.equals("Mean"))       return histo.mean();
        if (name.equals("Rms"))        return histo.rms();
        return 0;
    }
    public Object getExtendedStatistic(String name) {
        if (name.equals("Entries"))        return new Integer(histo.entries());
        if (name.equals(outOfRangeStat))   return new Integer(histo.extraEntries());
        if (name.equals(overflowStat))     return new Integer(histo.binEntries(IAxis.OVERFLOW_BIN));
        if (name.equals(underflowStat))    return new Integer(histo.binEntries(IAxis.UNDERFLOW_BIN));
        if (name.equals(nanExtStat))       return new Integer(histo.nanEntries());
        if (name.equals(sumOfWeightsStat)) return new Double(histo.sumBinHeights());
        
        IAnnotation an = histo.annotation();
        if (an == null) return null;
        String v = null;
        try {
            if (an.hasKey("stat."+name))
                v = an.value("stat."+name);
            else if (an.hasKey("stat:"+name))
                v = an.value("stat:"+name);
        } catch (IllegalArgumentException e) {}
        return v;
    }
    
    private void addAnnotationStatistics(ArrayList stat) {
        IAnnotation an = histo.annotation();
        if (an == null) return;
        boolean isOutOfRangeSet = false;
        int n = an.size();
        for (int i=0; i<n; i++) {
            try {
                String key = an.key(i);
                String statVal = an.value(i);
                if (key.toLowerCase().startsWith("stat.") || key.toLowerCase().startsWith("stat:")) {
                    String statKey = key.substring(5);
                    if (statVal.equalsIgnoreCase("false")) {
                        stat.remove(statKey);
                        continue;
                    }
                    if (  overflowStat.equals(statKey) && ((Integer) getExtendedStatistic(statKey)).intValue() == 0 ) continue;
                    if ( underflowStat.equals(statKey) && ((Integer) getExtendedStatistic(statKey)).intValue() == 0 ) continue;
                    if (outOfRangeStat.equals(statKey)) {
                        isOutOfRangeSet = true;
                        continue;
                    }
                    stat.add(statKey);
                }
            } catch (IllegalArgumentException e) {}
        }
        if (!isOutOfRangeSet && (stat.contains(overflowStat) || stat.contains(underflowStat)))
            stat.remove(outOfRangeStat);
    }
    
    private IHistogram1D histo;
    private final String[] statNames = {"Entries","Mean","Rms"};
    private final String nanExtStat = "NaN";
    private final String sumOfWeightsStat = "SumOfWeights";
    private final String outOfRangeStat = "OutOfRange";
    private final String overflowStat  = "Overflow";
    private final String underflowStat = "Underflow";
}
