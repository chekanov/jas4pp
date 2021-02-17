/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:54:20 CST 2021
 */

package hep.io.root.interfaces;

public interface MissingET extends hep.io.root.RootObject, hep.io.root.interfaces.TObject
{
	/** mising transverse energy */
	float getMET();
	/** mising energy pseudorapidity */
	float getEta();
	/** mising energy azimuthal angle */
	float getPhi();

	public final static int rootIOVersion=1;
	public final static int rootCheckSum=1034148504;
}
