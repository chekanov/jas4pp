/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:55:27 CST 2021
 */

package hep.io.root.interfaces;

public interface TBranchRef extends hep.io.root.RootObject, hep.io.root.interfaces.TBranch
{
	/** pointer to the TRefTable */
	hep.io.root.interfaces.TRefTable getRefTable();

	public final static int rootIOVersion=1;
}
