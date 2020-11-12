package jhepsim;

/**
 * Main Application.
 * 
 * @author S.Chekanov
 * 
 */
public class Main {

	public static String file;

	public static void main(String[] args) {

		file = null;
		if (args.length > 0) {
			file = args[0];
		}
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	}

	private static void createAndShowGUI() {
		
		
        
		
		new HepsimGUI(file);
	}

}
