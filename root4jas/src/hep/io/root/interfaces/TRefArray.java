/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:49:28 CST 2021
 */

package hep.io.root.interfaces;

public interface TRefArray extends hep.io.root.RootObject, hep.io.root.interfaces.TSeqCollection
{
	/** Pointer to Process Unique Identifier */
	hep.io.root.interfaces.TProcessID getPID();
	/** [fSize] To store uids of referenced objects */
	int[] getUIDs();
	/** Lower bound of the array */
	int getLowerBound();
	/** Last element in array containing an object */
	int getLast();

	public final static int rootIOVersion=1;
	public final static int rootCheckSum=1207554269;
}
