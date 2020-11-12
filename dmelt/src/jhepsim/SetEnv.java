package jhepsim;


import java.awt.Font;

public class SetEnv {

	public static String font_family = "Arial";
	public static String font_name = "Arial";
	public static String font_size = "14";
	public static String font_face = "plain";
	public static String font_style = "normal";
	public static String INIFILE;
	public static String OSsys;
	public static Font globalFont = new Font("Arial", Font.PLAIN, 14);
        public static String currentFile=null;


	// public static Font globalFont= new Font("Arial",Font.BOLD,14);
	// public static Font globalFont= new Font("Arial",Font.BOLD,14);
	// public static Font globalFont= new Font("Monospaced",Font.BOLD,14);
	// public static Font globalFont= new Font("Courier New", Font.BOLD, 14);

	public static String outputString = "";

	public static void init() {
		String OS = System.getProperty("os.name").toLowerCase();
		String fSep = System.getProperty("file.separator");

		if (OS.indexOf("windows") > -1 || OS.indexOf("nt") > -1) {
			OSsys = "windows";
			INIFILE = "jhepsim.ini";
		} else {
			// linux/unix/BS-based/mac
			OSsys = "unix/linux";
			INIFILE = ".jhepsim";
		}

		INIFILE = System.getProperty("user.home") + fSep + INIFILE;

	}

	
	/**
	 * Set faces.
	 */
	public static void face() {

		font_family = Options.getOption("article.font.family");
		font_name = Options.getOption("article.font.name");
		font_style = Options.getOption("article.font.style");
		font_size = Options.getOption("article.font.size");
		font_face = Options.getOption("article.font.face");

		int fsize = 14;
		try {
			fsize = Integer.parseInt(font_size);
		} catch (Exception e) {
			System.out.println("Error with font size");

		}

		int a = Font.PLAIN;
		if (font_face == "bold")
			a = Font.BOLD;
		if (font_face == "italic")
			a = Font.ITALIC;
		globalFont = new Font(font_family, a, fsize);

	}

}
