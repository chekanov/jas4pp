/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:49:28 CST 2021
 */

package hep.io.root.interfaces;

public interface TLorentzVector extends hep.io.root.RootObject, hep.io.root.interfaces.TObject
{
	/** 3 vector component */
	hep.io.root.interfaces.TVector3 getP();
	/** time or energy of (x,y,z,t) or (px,py,pz,e) */
	double getE();

	public final static int rootIOVersion=4;
}
