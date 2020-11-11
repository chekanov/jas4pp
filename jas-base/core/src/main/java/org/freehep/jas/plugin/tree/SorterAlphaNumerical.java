package org.freehep.jas.plugin.tree;

import java.lang.Integer;

/**
 *
 * @author The FreeHEP team @ SLAC.
 */
public class SorterAlphaNumerical implements FTreeNodeSorter {
    
    public String algorithmName() {
        return "Alpha-Numerical";
    }
    
    public String description() {
        return "Sorts nodes alphabetically and numerically";
    }
    
    public int sort(FTreeNode node1, FTreeNode node2) {
        DefaultFTreeNode n1 = (DefaultFTreeNode)node1;
        DefaultFTreeNode n2 = (DefaultFTreeNode)node2;
        String name1 = n1.path().getLastPathComponent();
        String name2 = n2.path().getLastPathComponent();
        
        int l = name1.length();
        if ( name2.length() < l ) l = name2.length();
        
        int ld = -1;
        for ( int i = 0; i < l; i++ )
            if ( name1.charAt(i) != name2.charAt(i) ) {
                ld = i;
                break;
            }

        if ( ld == -1 )
            return name1.compareTo( name2 );
        
        for ( int i = ld-1; i > -1; i-- )
            if ( ! Character.isDigit( name1.charAt(i) ) )
                break;
            else
                ld--;

        if ( ! Character.isDigit(name1.charAt(ld)) || ! Character.isDigit(name2.charAt(ld)) )
            return name1.compareTo( name2 );
        
        int le1 = ld;
        for ( int i = ld+1; i < name1.length(); i++ )
            if ( Character.isDigit(name1.charAt(i)) )
                le1 = i;
            else
                break;

        int le2 = ld;
        for ( int i = ld+1; i < name2.length(); i++ )
            if ( Character.isDigit(name2.charAt(i)) )
                le2 = i;
            else
                break;

        int num1 = Integer.parseInt(name1.substring(ld,le1+1));
        int num2 = Integer.parseInt(name2.substring(ld,le2+1));

        int result = num1 > num2 ? 1 : -1; 
        if ( num1 == num2 )
            result = 0;
        return result;
        
    }
    
}
