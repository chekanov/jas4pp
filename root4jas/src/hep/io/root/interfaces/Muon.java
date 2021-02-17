/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:49:28 CST 2021
 */

package hep.io.root.interfaces;

public interface Muon extends hep.io.root.RootObject, hep.io.root.interfaces.SortableObject
{
	/** muon transverse momentum */
	float getPT();
	/** muon pseudorapidity */
	float getEta();
	/** muon azimuthal angle */
	float getPhi();
	/** particle arrival time of flight */
	float getT();
	/** muon charge */
	int getCharge();
	/** reference to generated particle */
	hep.io.root.interfaces.TRef getParticle();
	float getIsolationVar();
	float getIsolationVarRhoCorr();
	float getSumPtCharged();
	float getSumPtNeutral();
	float getSumPtChargedPU();
	float getSumPt();

	public final static int rootIOVersion=3;
}
