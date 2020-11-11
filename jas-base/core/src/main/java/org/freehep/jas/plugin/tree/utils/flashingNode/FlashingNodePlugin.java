package org.freehep.jas.plugin.tree.utils.flashingNode;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import org.freehep.application.studio.Plugin;
import org.freehep.application.studio.Studio;
import org.freehep.jas.plugin.tree.FTree;
import org.freehep.jas.plugin.tree.FTreeNode;
import org.freehep.jas.plugin.tree.DefaultFTreeNodeAdapter;
import org.freehep.jas.plugin.tree.FTreeNodeRepaintNotification;
import org.freehep.jas.plugin.tree.FTreeProvider;
import org.freehep.util.commanddispatcher.CommandProcessor;
import org.freehep.util.commanddispatcher.CommandSourceAdapter;
import org.freehep.util.commanddispatcher.CommandState;

public class FlashingNodePlugin extends Plugin {
    
    private FTreeNode[] selectedNodes;
    private FTree tree;
    private boolean isFlashed = true;        
            
    @Override
    protected void postInit() {

        Studio app = getApplication();
        
        FTreeProvider treeProvider = ( (FTreeProvider) app.getLookup().lookup(FTreeProvider.class) );
        tree = treeProvider.tree();
        
        treeProvider.treeNodeAdapterRegistry().registerNodeAdapter(new FlashingNodeAdapter(), FlashingNode.class);
        
        app.getCommandTargetManager().add( new FlashingNodePluginCommands(this) );
        
        int delay = 1000; //milliseconds
        ActionListener taskPerformer = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                isFlashed = ! isFlashed;
                tree.treeChanged( new FTreeNodeRepaintNotification(FlashingNodePlugin.this, tree.root().path(), true) );
            }
        };
        new Timer(delay, taskPerformer).start();
    }
    
    class FlashingNodeAdapter extends DefaultFTreeNodeAdapter {
        
        public FlashingNodeAdapter() {
            super(500);
        }
        
        public JPopupMenu modifyPopupMenu(FTreeNode[] nodes, JPopupMenu menu) {
            selectedNodes = nodes[0].tree().selectedNodes();
            JMenuItem startItem = new JMenuItem("Start Flashing");
            FlashingNodePlugin.this.getApplication().getCommandTargetManager().add(new CommandSourceAdapter(startItem));
            menu.add(startItem);
            JMenuItem stopItem = new JMenuItem("Stop Flashing");
            FlashingNodePlugin.this.getApplication().getCommandTargetManager().add(new CommandSourceAdapter(stopItem));
            menu.add(stopItem);
            return menu;
        }
        
        public Component treeCellRendererComponent(Component component, FTreeNode node, boolean sel, boolean expanded, boolean leaf, boolean hasFocus) {
            FlashingNode flashingNode = (FlashingNode)node.objectForClass(FlashingNode.class);
            if ( flashingNode.isFlashing() )
                if ( isFlashed )
                    component.setForeground(flashingNode.flashingColor());
            return component;
        }
    }
    
    
    class FlashingNodePluginCommands extends CommandProcessor {
        
        private FlashingNodePlugin plugin;
        
        FlashingNodePluginCommands( FlashingNodePlugin plugin ) {
            this.plugin = plugin;
        }
        
        public void onStartFlashing() {
            for ( int i = 0; i < selectedNodes.length; i++ ) {
                FlashingNode flashingNode = (FlashingNode) selectedNodes[i].objectForClass(FlashingNode.class);
                if ( flashingNode != null ) 
                    flashingNode.setIsFlashing(true);
            }
        }
        
        public void enableStartFlashing(CommandState state) {
            for ( int i = 0; i < selectedNodes.length; i++ ) {
                FlashingNode flashingNode = (FlashingNode) selectedNodes[i].objectForClass(FlashingNode.class);
                if ( flashingNode != null && ! flashingNode.isFlashing() ) {
                    state.setEnabled(true);
                    return;
                }
            }
            state.setEnabled(false);
        }
        
        public void onStopFlashing() {
            for ( int i = 0; i < selectedNodes.length; i++ ) {
                FlashingNode flashingNode = (FlashingNode) selectedNodes[i].objectForClass(FlashingNode.class);
                if ( flashingNode != null )
                    flashingNode.setIsFlashing(false);
            }
        }
        
        public void enableStopFlashing(CommandState state) {
            for ( int i = 0; i < selectedNodes.length; i++ ) {
                FlashingNode flashingNode = (FlashingNode) selectedNodes[i].objectForClass(FlashingNode.class);
                if ( flashingNode != null && flashingNode.isFlashing() ) {
                    state.setEnabled(true);
                    return;
                }
            }
            state.setEnabled(false);
        }
    }    
}
