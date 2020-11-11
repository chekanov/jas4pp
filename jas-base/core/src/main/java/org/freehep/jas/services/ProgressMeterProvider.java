package org.freehep.jas.services;

import org.freehep.application.ProgressMeter;

/**
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: ProgressMeterProvider.java 13876 2011-09-20 00:52:21Z tonyj $
 */
public interface ProgressMeterProvider
{
   ProgressMeter getProgressMeter();
   void freeProgressMeter(ProgressMeter meter);
}
