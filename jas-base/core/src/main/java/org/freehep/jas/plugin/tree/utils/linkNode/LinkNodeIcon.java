package org.freehep.jas.plugin.tree.utils.linkNode;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import org.freehep.jas.plugin.tree.utils.linkNode.LinkNode;
import org.freehep.util.images.ImageHandler;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class LinkNodeIcon implements Icon {

    private static final Icon linkIcon = ImageHandler.getIcon("images/Link.gif", LinkNode.class);
    private Icon icon;
    private int height;
    private int width;
    
    LinkNodeIcon(Icon icon) {
        this.icon = icon;
        height = icon.getIconHeight();
        if ( linkIcon.getIconHeight() > height )
            height = linkIcon.getIconHeight();
        width = icon.getIconWidth();
        if ( linkIcon.getIconWidth() > width )
            width = linkIcon.getIconWidth();
    }
    
    public int getIconHeight() {
        return height;
    }
    
    public int getIconWidth() {
        return width;
    }
    
    public void paintIcon(Component c, Graphics g, int x, int y) {
        icon.paintIcon(c,g,x,y);
        linkIcon.paintIcon(c,g,x-1,y+icon.getIconHeight()-linkIcon.getIconHeight()+1);
    }   
}
