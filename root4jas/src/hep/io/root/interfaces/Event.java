/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:49:28 CST 2021
 */

package hep.io.root.interfaces;

public interface Event extends hep.io.root.RootObject, hep.io.root.interfaces.TObject
{
	/** event number */
	long getNumber();
	float getReadTime();
	float getProcTime();

	public final static int rootIOVersion=1;
	public final static int rootCheckSum=1897686336;
}
