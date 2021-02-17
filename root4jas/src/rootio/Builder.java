/**
*    This program is free software; you can redistribute it and/or modify it under the terms
*    of the GNU General Public License as published by the Free Software Foundation; either
*    version 3 of the License, or any later version.
*
*    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
*    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*    See the GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License along with this program;
*    if not, see <http://www.gnu.org/licenses>.
*
*    Additional permission under GNU GPL version 3 section 7:
*    If you have received this program as a library with written permission from the DataMelt team,
*    you can link or combine this library with your non-GPL project to convey the resulting work.
*    In this case, this library should be considered as released under the terms of
*    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
*    provided you include this license notice and a URL through which recipients can access the
*    Corresponding Source.
**/

package rootio;


import hep.io.root.RootClassNotFound;
import hep.io.root.RootFileReader;
import hep.io.root.interfaces.*;
import java.util.List;
import hep.io.root.util.*;
import hep.io.root.util.InterfaceBuilder;
import hep.io.root.RootClass;
import hep.io.root.RootMember;
import hep.io.root.interfaces.TStreamerInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Builds a Java interface corresponding to a RootClass.  
 * @author TonyJ amd S.Chekanov
 */
public class Builder
{


     /**
     *  Create ROOT class interfaces. 
     *  @param path to ROOT file.
     *  @param className ROOT class name 
     */
     public  Builder(String path, String className)  throws java.io.IOException, RootClassNotFound  {
 

      RootFileReader rfr = new RootFileReader(path);
      hep.io.root.core.RootClassFactory rcf = rfr.getFactory();
      InterfaceBuilder ib = new InterfaceBuilder(new File("."));


      if (className != null)
              if (className.length()>1) {
              RootClass rc = rcf.create( className );
              File f = ib.write(rc);
              System.out.println("Created " + f.getPath());
      };


 
    if (className == null || className.length()<1) {

         List list = rfr.streamerInfo();
         for (Iterator i = list.iterator(); i.hasNext();)
         {
            TStreamerInfo info = (TStreamerInfo) i.next();
            String name = info.getName();

            // See if this class already exists
            try
            {
               Class k = Class.forName("hep.io.root.interfaces." + name);
               System.out.println("Skipping " + name);
            }
            catch (ClassNotFoundException x)
            {
               RootClass rc = rcf.create(name);
               File f = ib.write(rc);
               System.out.println("Created " + f.getPath());
            }
         }
      }

   }


}


  
