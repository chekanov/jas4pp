/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:49:28 CST 2021
 */

package hep.io.root.interfaces;

public interface Track extends hep.io.root.RootObject, hep.io.root.interfaces.SortableObject
{
	/** HEP ID number */
	int getPID();
	/** track charge */
	int getCharge();
	/** track transverse momentum */
	float getPT();
	/** track pseudorapidity */
	float getEta();
	/** track azimuthal angle */
	float getPhi();
	/** track pseudorapidity at the tracker edge */
	float getEtaOuter();
	/** track azimuthal angle at the tracker edge */
	float getPhiOuter();
	/** track vertex position (x component) */
	float getX();
	/** track vertex position (y component) */
	float getY();
	/** track vertex position (z component) */
	float getZ();
	/** track vertex position (z component) */
	float getT();
	/** track position (x component) at the tracker edge */
	float getXOuter();
	/** track position (y component) at the tracker edge */
	float getYOuter();
	/** track position (z component) at the tracker edge */
	float getZOuter();
	/** track position (z component) at the tracker edge */
	float getTOuter();
	/** track signed transverse impact parameter */
	float getDxy();
	/** signed error on the track signed transverse impact parameter */
	float getSDxy();
	/** X coordinate of point of closest approach to vertex */
	float getXd();
	/** Y coordinate of point of closest approach to vertex */
	float getYd();
	/** Z coordinate of point of closest approach to vertex */
	float getZd();
	/** reference to generated particle */
	hep.io.root.interfaces.TRef getParticle();

	public final static int rootIOVersion=2;
}
