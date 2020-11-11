package org.freehep.jas.plugin.tree.utils.flashingNode;

import java.awt.Color;

/**
 * Interface to be implemented by any node that is going to flash.
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public interface FlashingNode {
    
    void setIsFlashing( boolean isFlashing );

    boolean isFlashing();
    
    Color flashingColor();
    
}
