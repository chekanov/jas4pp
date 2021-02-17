/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:49:28 CST 2021
 */

package hep.io.root.interfaces;

public interface GenParticle extends hep.io.root.RootObject, hep.io.root.interfaces.SortableObject
{
	/** particle HEP ID number | hepevt.idhep[number] */
	int getPID();
	/** particle status | hepevt.isthep[number] */
	int getStatus();
	/** 0 or 1 for particles from pile-up interactions */
	int getIsPU();
	/** particle 1st mother | hepevt.jmohep[number][0] - 1 */
	int getM1();
	/** particle 2nd mother | hepevt.jmohep[number][1] - 1 */
	int getM2();
	/** particle 1st daughter | hepevt.jdahep[number][0] - 1 */
	int getD1();
	/** particle last daughter | hepevt.jdahep[number][1] - 1 */
	int getD2();
	/** particle charge */
	int getCharge();
	/** particle mass */
	float getMass();
	/** particle energy | hepevt.phep[number][3] */
	float getE();
	/** particle momentum vector (x component) | hepevt.phep[number][0] */
	float getPx();
	/** particle momentum vector (y component) | hepevt.phep[number][1] */
	float getPy();
	/** particle momentum vector (z component) | hepevt.phep[number][2] */
	float getPz();
	/** particle transverse momentum */
	float getPT();
	/** particle pseudorapidity */
	float getEta();
	/** particle azimuthal angle */
	float getPhi();
	/** particle rapidity */
	float getRapidity();
	/** particle vertex position (t component) | hepevt.vhep[number][3] */
	float getT();
	/** particle vertex position (x component) | hepevt.vhep[number][0] */
	float getX();
	/** particle vertex position (y component) | hepevt.vhep[number][1] */
	float getY();
	/** particle vertex position (z component) | hepevt.vhep[number][2] */
	float getZ();

	public final static int rootIOVersion=1;
}
