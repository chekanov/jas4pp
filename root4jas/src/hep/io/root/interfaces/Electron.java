/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:49:28 CST 2021
 */

package hep.io.root.interfaces;

public interface Electron extends hep.io.root.RootObject, hep.io.root.interfaces.SortableObject
{
	/** electron transverse momentum */
	float getPT();
	/** electron pseudorapidity */
	float getEta();
	/** electron azimuthal angle */
	float getPhi();
	/** particle arrival time of flight */
	float getT();
	/** electron charge */
	int getCharge();
	/** ratio of the hadronic versus electromagnetic energy deposited in the calorimeter */
	float getEhadOverEem();
	/** reference to generated particle */
	hep.io.root.interfaces.TRef getParticle();
	float getIsolationVar();
	float getIsolationVarRhoCorr();
	float getSumPtCharged();
	float getSumPtNeutral();
	float getSumPtChargedPU();
	float getSumPt();

	public final static int rootIOVersion=3;
	public final static int rootCheckSum=985081563;
}
