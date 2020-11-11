package hep.aida.ref.histogram;

/**
 * Implementation of IProfile.
 *
 * @author The AIDA team at SLAC.
 *
 */

import hep.aida.IProfile;
import hep.aida.ref.event.HistogramEvent;
import hep.aida.ref.event.IsObservable;
import java.io.Serializable;

public abstract class Profile extends AbstractBaseHistogram implements IProfile, IsObservable,Serializable  {
    
    private String options;
    
    /** 
     * Create a new Profile
     * @param name      The name of the Profile as a ManagedObject.
     * @param title     The title of the Profile.
     * @param dimension The dimension of the Profile.
     *
     */
    public Profile(String name, String title, int dimension) {
        this( name, title, dimension, "");
    }
    
    public Profile(String name, String title, int dimension, String options) {
        super(name, title, dimension);        
        this.options = options;
    }

    protected java.util.EventObject createEvent()
    {
       return new HistogramEvent(this);
    }

    protected String options() {
        return options;
    }

    public int nanEntries() {
        return allEntries()-entries()-extraEntries();
    }
}
