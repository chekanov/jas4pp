/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:49:28 CST 2021
 */

package hep.io.root.interfaces;

public interface TVector3 extends hep.io.root.RootObject, hep.io.root.interfaces.TObject
{
	double getX();
	double getY();
	double getZ();

	public final static int rootIOVersion=3;
}
