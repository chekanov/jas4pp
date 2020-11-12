package org.lcsim.event.base;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.lcsim.conditions.ConditionsManager;
import org.lcsim.conditions.ConditionsSet;
import org.lcsim.conditions.ConditionsConverter;
import org.lcsim.geometry.Subdetector;

/**
 * Currently assumes that each subdetector has a single sampling fraction. 
 * This is obviously not true if the subdetector has more than one layering scheme, 
 * or if a single "subdetector" represents more than one physical detector (e.g. barrel and endcap).
 *
 * @author Tony Johnson <tonyj@slac.stanford.edu>
 */
public class SamplingFractionManager {
    
    private static SamplingFractionManager theSamplingFractionManager = new SamplingFractionManager();
    private ConditionsManager manager;

    private SamplingFractionManager() {
        manager = ConditionsManager.defaultInstance();
        manager.registerConditionsConverter(new SamplingFractionConverter());
    }

    public static SamplingFractionManager defaultInstance() {
        return theSamplingFractionManager;
    }

    public double getCorrectedEnergy(double rawEnergy, int layer, Subdetector detector) {
        SamplingFraction sf = manager.getCachedConditions(SamplingFraction.class, "SamplingFractions/" + detector.getName()).getCachedData();
        return sf.getCorrectedEnergy(rawEnergy, layer);
    }
    
    private static class SamplingFraction {
        private final double defaultSamplingFraction;
        private final boolean digital;
        private final Map<Integer, Double> layerMap = new HashMap<Integer, Double>();

        private SamplingFraction(ConditionsSet set) {
            defaultSamplingFraction = set.getDouble("samplingFraction");
            digital = set.getBoolean("digital", false);
            Pattern pattern = Pattern.compile("samplingFraction\\[((\\d+(-\\d+)?)(,\\d+(-\\d+)?)*)\\]");
            Pattern p2 = Pattern.compile(",?(\\d+)(-(\\d+))?");

            for (Object o : set.keySet()) {
                String key = o.toString();

                // Get rid of embedded whitespace, and match
                Matcher matcher = pattern.matcher(key.replaceAll("\\s", ""));
                if (matcher.matches()) {
                    double s = set.getDouble(key);
                    String layers = matcher.group(1);
                    Matcher m2 = p2.matcher(layers);
                    while (m2.find()) {
                        int start = Integer.parseInt(m2.group(1));
                        int end = m2.group(3) == null ? -1 : Integer.parseInt(m2.group(3));
                        if (end > start) {
                            for (int i = start; i <= end; i++) {
                                layerMap.put(i, s);
                            }
                        } else {
                            layerMap.put(start, s);
                        }
                    }
                }
            }
        }

        double getCorrectedEnergy(double rawEnergy, int layer) {
            Double layerSF = layerMap.get(layer);
            double samplingFraction = layerSF == null ? defaultSamplingFraction : layerSF;
            return (digital ? 1 : rawEnergy) / samplingFraction;
        }
    }

    private static class SamplingFractionConverter implements ConditionsConverter<SamplingFraction> {
        public Class<SamplingFractionManager.SamplingFraction> getType() {
            return SamplingFraction.class;
        }

        public SamplingFractionManager.SamplingFraction getData(ConditionsManager manager, String name) {
            ConditionsSet conditions = manager.getConditions(name);
            return new SamplingFraction(conditions);
        }
    }
}
