/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Feb 15 17:49:28 CST 2021
 */

package hep.io.root.interfaces;

public interface HepMCEvent extends hep.io.root.RootObject, hep.io.root.interfaces.Event
{
	/** unique signal process id | signal_process_id() */
	int getProcessID();
	/** number of multi parton interactions | mpi () */
	int getMPI();
	/** weight for the event */
	float getWeight();
	/** energy scale, see hep-ph/0109068 | event_scale() */
	float getScale();
	/** QED coupling, see hep-ph/0109068 | alphaQED() */
	float getAlphaQED();
	/** QCD coupling, see hep-ph/0109068 | alphaQCD() */
	float getAlphaQCD();
	/** flavour code of first parton | pdf_info()->id1() */
	int getID1();
	/** flavour code of second parton | pdf_info()->id2() */
	int getID2();
	/** fraction of beam momentum carried by first parton ("beam side") | pdf_info()->x1() */
	float getX1();
	/** fraction of beam momentum carried by second parton ("target side") | pdf_info()->x2() */
	float getX2();
	/** Q-scale used in evaluation of PDF's (in GeV) | pdf_info()->scalePDF() */
	float getScalePDF();
	/** PDF (id1, x1, Q) | pdf_info()->pdf1() */
	float getPDF1();
	/** PDF (id2, x2, Q) | pdf_info()->pdf2() */
	float getPDF2();

	public final static int rootIOVersion=2;
}
