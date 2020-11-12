/**
 * Project        : ArtTk
 * Copyright      : (c) Artenum SARL, 24 rue Louis Blanc
 *                  75010, Paris, France 2009-2010
 *                  http://www.artenum.com
 *                  All copyright and trademarks reserved.
 * Email          : contact@artenum.com
 * Licence        : cf. LICENSE.txt
 * Developed By   : Artenum SARL
 * Authors        : Sebastien Jourdain      (jourdain@artenum.com)
 *                  Benoit thiebault        (thiebault@artenum.com)
 *                  Jeremie Turbet (JeT)    (turbet@artenum.com)
 *                  Julien Forest           (j.forest@artenum.com)
 * Created        : 11 Nov. 2005
 * Modified       : 23 Aug. 2010
 */
package com.artenum.tk.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;

import javax.swing.JPopupMenu;

public class ToolBox {
    public static Frame getParentFrame(Component c) {
        Component currentComponent = c;
        while ((currentComponent != null) && !(currentComponent instanceof Frame)) {
            if (currentComponent instanceof JPopupMenu) {
                currentComponent = ((JPopupMenu) currentComponent).getInvoker();
            } else {
                currentComponent = currentComponent.getParent();
            }
        }

        return (Frame) currentComponent;
    }

    public static Dialog getParentDialog(Component c) {
        Component currentComponent = c;
        while ((currentComponent != null) && !(currentComponent instanceof Dialog)) {
            if (currentComponent instanceof JPopupMenu) {
                currentComponent = ((JPopupMenu) currentComponent).getInvoker();
            } else {
                currentComponent = currentComponent.getParent();
            }
        }

        return (Dialog) currentComponent;
    }
    
    
}
