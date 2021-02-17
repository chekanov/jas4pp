/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:54:20 CST 2021
 */

package hep.io.root.interfaces;

public interface ScalarHT extends hep.io.root.RootObject, hep.io.root.interfaces.TObject
{
	/** scalar sum of transverse momenta */
	float getHT();

	public final static int rootIOVersion=1;
}
