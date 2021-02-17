/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Jan 15 18:49:11 PST 2021
 */
package hep.io.root.interfaces;

public interface TTree extends hep.io.root.RootObject, TNamed, TAttLine, TAttFill, TAttMarker
{
	public final static int rootIOVersion = 5;

	/** Autosave tree when fAutoSave bytes produced */
	long getAutoSave();

	TBranch getBranch(int index);

	TBranch getBranch(String name);

	/** List of Branches */
	TObjArray getBranches();

	/** Number of entries */
	long getEntries();

	/** Number of entries to estimate histogram limits */
	long getEstimate();

	/** Index of sorted values */
	int[] getIndex();

	/** Sorted index values */
	double[] getIndexValues();

	/** Direct pointers to individual branch leaves */
	TObjArray getLeaves();

	/** Maximum number of entries to process */
	long getMaxEntryLoop();

	/** Maximum total size of buffers kept in memory */
	long getMaxVirtualSize();

	//TODO: Som automated way of adding these
	int getNBranches();

	/** Number of autosaved bytes */
	long getSavedBytes();

	/** Number of runs before prompting in Scan */
	int getScanField();

	/** Timer interval in milliseconds */
	int getTimerInterval();

	/** Total number of bytes in all branches before compression */
	long getTotBytes();

	/** Update frequency for EntryLoop */
	int getUpdate();

	/** Total number of bytes in all branches after compression */
	long getZipBytes();


	/** List of aliases for expressions based on the tree branches. */
	hep.io.root.interfaces.TList getAliases();
	
	/** pointer to list of friend elements */
	hep.io.root.interfaces.TList getFriends();
	
	/** pointer to a list of user objects associated to this Tree */
	hep.io.root.interfaces.TList getUserInfo();
	
	/** Branch supporting the TRefTable (if any) */
	hep.io.root.interfaces.TBranchRef getBranchRef();

}
