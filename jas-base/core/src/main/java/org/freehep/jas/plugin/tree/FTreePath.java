package org.freehep.jas.plugin.tree;

import java.util.StringTokenizer;
import javax.swing.tree.TreePath;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class FTreePath {
    
    private String[] paths;
    private int length;
    private FTreePath parentPath = null;
    private TreePath treePath;
    
    public FTreePath(String path) {
        String[] paths = null;
        if (path.trim().equals("")) {
            paths = new String[] {""};
        } else {
            StringTokenizer st = new StringTokenizer(path,"/");
            int tokens = st.countTokens();
            paths = new String[tokens];
            for ( int i = 0; i < tokens; i++ )
                paths[i] = st.nextToken();
        }
        init(paths);
    }
    
    public FTreePath(String[] paths) {
        init(paths);
    }
        
    public FTreePath(FTreePath path) {
        init( path.getPath() );
    }

    private void init(String[] fullPath) {
        length = fullPath.length;
        paths = new String[ length ];
        for ( int i = 0; i < length; i++ ) 
            paths[i] = fullPath[i];
        treePath = new TreePath( paths );
        
    }
    
    public String getLastPathComponent() {
        return paths[ length-1 ];
    }
    
    public FTreePath getParentPath() {
        if ( length == 1 ) return null;
        if ( parentPath != null ) return parentPath;
        String[] parentPaths = new String[length-1];
        for ( int i = 0; i<length-1; i++ )
            parentPaths[i] = paths[i];
        parentPath = new FTreePath( parentPaths );
        return parentPath;
    }
    
    public String[] getPath() {
        return paths;
    }
        
    public String getPathComponent(int index) {
        if ( index < 0 || index > length-1 ) throw new IllegalArgumentException("Illegal index "+index);
        return paths[index];
    }
    
    public int getPathCount() {
        return length;
    }
    
    public boolean isDescendant(FTreePath FTreePath) {
        return treePath().isDescendant( FTreePath.treePath() );
    }
    
    public FTreePath pathByAddingChild( String child ) {
        String[] newPath = new String[length+1];
        for ( int i = 0; i<length; i++ )
            newPath[i] = paths[i];
        newPath[length] = child;
        return new FTreePath(newPath);
    }
    
    public FTreePath pathByAddingPath( String[] extraPath ) {
        String[] newPath = new String[length+extraPath.length];
        for ( int i = 0; i<length; i++ ) newPath[i] = paths[i];
        for ( int i = 0; i<extraPath.length; i++ ) newPath[length+i] = extraPath[i];
        return new FTreePath(newPath);
    }
    
    public String toString() {
        String pathName = "";
        for ( int i = 0; i < getPathCount(); i++ )
            pathName += "/"+getPathComponent(i);
        return pathName;
    }
    
    public boolean equals(Object o) {
        if ( ! ( o instanceof FTreePath ) ) return false;
        FTreePath path = (FTreePath)o;
        if ( path.getPathCount() != getPathCount() ) return false;
        for ( int i = 0; i < getPathCount(); i++ )
            if ( path.getPathComponent(i) != getPathComponent(i) ) return false;
        return true;
    }
        
    private TreePath treePath() {
        return treePath;
    }
}
