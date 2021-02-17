/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:49:28 CST 2021
 */

package hep.io.root.interfaces;

public interface Jet extends hep.io.root.RootObject, hep.io.root.interfaces.SortableObject
{
	/** jet transverse momentum */
	float getPT();
	/** jet pseudorapidity */
	float getEta();
	/** jet azimuthal angle */
	float getPhi();
	/** particle arrival time of flight */
	float getT();
	/** jet invariant mass */
	float getMass();
	/** jet radius in pseudorapidity */
	float getDeltaEta();
	/** jet radius in azimuthal angle */
	float getDeltaPhi();
	int getFlavor();
	int getFlavorAlgo();
	int getFlavorPhys();
	/** 0 or 1 for a jet that has been tagged as containing a heavy quark */
	int getBTag();
	int getBTagAlgo();
	int getBTagPhys();
	/** 0 or 1 for a jet that has been tagged as a tau */
	int getTauTag();
	/** tau charge */
	int getCharge();
	/** ratio of the hadronic versus electromagnetic energy deposited in the calorimeter */
	float getEhadOverEem();
	/** number of charged constituents */
	int getNCharged();
	/** number of neutral constituents */
	int getNNeutrals();
	/** (sum pt of charged pile-up constituents)/(sum pt of charged constituents) */
	float getBeta();
	/** (sum pt of charged constituents coming from hard interaction)/(sum pt of charged constituents) */
	float getBetaStar();
	/** average distance (squared) between constituent and jet weighted by pt (squared) of constituent */
	float getMeanSqDeltaR();
	/** average pt between constituent and jet weighted by pt of constituent */
	float getPTD();
	/** (sum pt of constituents within a ring 0.1*i < DeltaR < 0.1*(i+1))/(sum pt of constituents) */
	float[] getFracPt();
	/** N-subjettiness */
	float[] getTau();
	/** first entry (i = 0) is the total Trimmed Jet 4-momenta and from i = 1 to 4 are the trimmed subjets 4-momenta */
	hep.io.root.interfaces.TLorentzVector[] getTrimmedP4();
	/** first entry (i = 0) is the total Pruned Jet 4-momenta and from i = 1 to 4 are the pruned subjets 4-momenta */
	hep.io.root.interfaces.TLorentzVector[] getPrunedP4();
	/** first entry (i = 0) is the total SoftDropped Jet 4-momenta and from i = 1 to 4 are the pruned subjets 4-momenta */
	hep.io.root.interfaces.TLorentzVector[] getSoftDroppedP4();
	/** number of subjets trimmed */
	int getNSubJetsTrimmed();
	/** number of subjets pruned */
	int getNSubJetsPruned();
	/** number of subjets soft-dropped */
	int getNSubJetsSoftDropped();
	/** references to constituents */
	hep.io.root.interfaces.TRefArray getConstituents();
	/** references to generated particles */
	hep.io.root.interfaces.TRefArray getParticles();
	hep.io.root.interfaces.TLorentzVector getArea();

	public final static int rootIOVersion=3;
	public final static int rootCheckSum=1545509169;
}
