/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:55:27 CST 2021
 */

package hep.io.root.interfaces;

public interface TRefTable extends hep.io.root.RootObject, hep.io.root.interfaces.TObject
{
	/** dummy for backward compatibility */
	int getSize();
	/** array of Parent objects  (eg TTree branch) holding the referenced objects */
	hep.io.root.interfaces.TObjArray getParents();
	/** Object owning this TRefTable */
	hep.io.root.interfaces.TObject getOwner();
	/** UUIDs of TProcessIDs used in fParentIDs */

	public final static int rootIOVersion=3;
}
