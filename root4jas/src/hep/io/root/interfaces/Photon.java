/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:54:20 CST 2021
 */

package hep.io.root.interfaces;

public interface Photon extends hep.io.root.RootObject, hep.io.root.interfaces.SortableObject
{
	/** photon transverse momentum */
	float getPT();
	/** photon pseudorapidity */
	float getEta();
	/** photon azimuthal angle */
	float getPhi();
	/** photon energy */
	float getE();
	/** particle arrival time of flight */
	float getT();
	/** ratio of the hadronic versus electromagnetic energy deposited in the calorimeter */
	float getEhadOverEem();
	/** references to generated particles */
	hep.io.root.interfaces.TRefArray getParticles();
	float getIsolationVar();
	float getIsolationVarRhoCorr();
	float getSumPtCharged();
	float getSumPtNeutral();
	float getSumPtChargedPU();
	float getSumPt();

	public final static int rootIOVersion=3;
	public final static int rootCheckSum=58691563;
}
