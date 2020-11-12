package jhepsim;


import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.artenum.jyconsole.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import jehep.shelljython.*;

/**
 * Application
 * 
 * @author S.Chekanov
 * 
 */
public class HepsimGUI {

	private JTabbedPane jtab;
	private mainGUI maingui;
	private JyShell jconsole;
	private JMenuBar menuBar;
	private JFrame frame;

	public HepsimGUI(String file) {

		SetEnv.init();
		Options.init();
		SetEnv.face(); // read fonts etc.

		JButton button = new JButton("Run");
		JButton reload = new JButton("Reload");

		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final String s = maingui.getEditor().getText();
				// InputStream stream = new
				// ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
				// System.out.println(s);
				// jconsole.runPythonCmd(s);
				jconsole.runPythonString(s);
				// jconsole.executePythonInputStream(stream);
				jtab.setSelectedIndex(1);

			}
		});

		reload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jconsole.clear();
				jtab.setSelectedIndex(1);
				// maingui.getEditor().setText("");
			}
		});

		frame = new JFrame("HepSim");

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitApplication();
			}
		});

		jconsole = new JyShell();
		maingui = new mainGUI();

		if (file != null) { 
			maingui.loadFile(file);
                        SetEnv.currentFile=getName(file);
                        frame.setTitle("File: "+SetEnv.currentFile);
                }

		jtab = new JTabbedPane();
		jtab.addTab("Editor ", maingui);
		jtab.addTab("Shell ", jconsole);

		frame.add(jtab, BorderLayout.CENTER);

		JPanel bpanel = new JPanel(new BorderLayout());
                String text="HepSim analyser";
                // JLabel jlab = new JLabel("<html><div style=\"text-align: center;\">" + text + "</html>");
                //jlab.setVerticalAlignment(SwingConstants.CENTER);
                JLabel jlab = new JLabel();
		reload.setPreferredSize(new Dimension(120, 24));
		button.setPreferredSize(new Dimension(120, 24));
		bpanel.add(reload, BorderLayout.WEST);
		bpanel.add(jlab, BorderLayout.CENTER);
		bpanel.add(button, BorderLayout.EAST);

		frame.add(bpanel, BorderLayout.SOUTH);

		menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		JMenu optionMenu = new JMenu("Options");
		JMenu aboutMenu = new JMenu("Help");
		menuBar.add(fileMenu);
		menuBar.add(optionMenu);
		menuBar.add(aboutMenu);

		MemoryMonitor mem = new MemoryMonitor();
		mem.setPreferredSize(new Dimension(140, 20));
		mem.setMaximumSize(new Dimension(200, 20));
		mem.setMinimumSize(new Dimension(120, 20));
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(mem);

		JMenuItem item11 = new JMenuItem(new AbstractAction("About") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				new AboutDialog(frame);
			}
		});

		aboutMenu.add(item11);

		JMenuItem item12 = new JMenuItem(new AbstractAction("Exit") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {

				int selectedOption = JOptionPane.showConfirmDialog(null,
						"Close the application?", "Do nothing",
						JOptionPane.YES_NO_OPTION);
				if (selectedOption == JOptionPane.YES_OPTION) {
					exitApplication();
				}

			}
		});

		JMenuItem item13 = new JMenuItem(new AbstractAction("Open ..") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {
				JFileChooser chooser = new JFileChooser();
				if (chooser.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION)
					return;
				File xfile = chooser.getSelectedFile();
				if (xfile == null) return; 
                                frame.setTitle("File: "+getName(xfile.getAbsolutePath()));
                                SetEnv.currentFile=xfile.getAbsolutePath();
				maingui.loadFile(xfile.getAbsolutePath());

			};
		});



                 JMenuItem item114 = new JMenuItem(new AbstractAction("Save") {
                        private static final long serialVersionUID = 1L;
                        public void actionPerformed(ActionEvent ae) {
                                if (SetEnv.currentFile !=  null) {
                                File file = new File(SetEnv.currentFile);
                                frame.setTitle("File: "+getName(SetEnv.currentFile));

                                BufferedWriter writer = null;
                                try {
                                        writer = new BufferedWriter(new FileWriter(file));
                                        writer.write(maingui.getText());
                                } catch (IOException e) {

                                        JOptionPane.showMessageDialog(frame, "Cannot be saved",
                                                        "ERROR", JOptionPane.ERROR_MESSAGE);
                                } finally {
                                        try {
                                                if (writer != null)
                                                        writer.close();
                                        } catch (IOException e) {
                                                JOptionPane.showMessageDialog(frame, "File Not Saved",
                                                                "ERROR", JOptionPane.ERROR_MESSAGE);

                                        }
                                }
                        }
                      }
                });









		JMenuItem item14 = new JMenuItem(new AbstractAction("Save As ..") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {

				JFileChooser chooser = new JFileChooser();
				if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION)
					return;
				File file = chooser.getSelectedFile();
				if (file == null)
					return;

                                SetEnv.currentFile=file.getAbsolutePath();
                                frame.setTitle("File: "+getName(SetEnv.currentFile));
				BufferedWriter writer = null;
				try {

					writer = new BufferedWriter(new FileWriter(file));
					writer.write(maingui.getText());

				} catch (IOException e) {

					JOptionPane.showMessageDialog(frame, "File Not Saved",
							"ERROR", JOptionPane.ERROR_MESSAGE);
				} finally {
					try {
						if (writer != null)
							writer.close();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(frame, "File Not Saved",
								"ERROR", JOptionPane.ERROR_MESSAGE);

					}
				}
			}

		});

                fileMenu.add(item114);
                fileMenu.add(item14);
		fileMenu.add(item13);
		fileMenu.addSeparator(); // exit is separate
		fileMenu.add(item12);

		JMenuItem item15 = new JMenuItem(new AbstractAction("Select font") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent ae) {

				Font f = FontChooser.showDialog(frame, "Choose font",
						maingui.getFont());
				if (f != null) {
					maingui.setTextFont(f);
					maingui.addNotify();
					SetEnv.font_family = f.getFamily();
					SetEnv.font_size = Integer.toString(f.getSize());

					SetEnv.font_face = "normal";
					if (f.isBold())
						SetEnv.font_face = "bold";
					if (f.isItalic())
						SetEnv.font_face = "italic";
					if (f.isBold() & f.isItalic()) {
						SetEnv.font_face = "bold";
						SetEnv.font_style = "italic";
					}

					SetEnv.globalFont = f;

				}

			};
		});

		optionMenu.add(item15);


                frame.setTitle("File: "+getName(SetEnv.currentFile));
		frame.setJMenuBar(menuBar);
		// set size
		int w = Options.getIntOption("frame.width");
		int h = Options.getIntOption("frame.height");
		Dimension res = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension size = new Dimension(Math.min(w, res.width), Math.min(h,
				res.height));
		frame.setPreferredSize(size);
		//frame.setLocation(Options.getIntOption("frame.x"),
	        //			Options.getIntOption("frame.y"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	}

	/**
	 * Exit application.
	 */
	public void exitApplication() {

		Dimension dim;
		Point pos;

		// store mainframe dimensions
		dim = frame.getSize();
		Options.setIntOption("frame.width", dim.width);
		Options.setIntOption("frame.height", dim.height);
		pos = frame.getLocation();
		Options.setIntOption("frame.x", pos.x);
		Options.setIntOption("frame.y", pos.y);

		// fonts
		Options.setOption("article.font.family", SetEnv.font_family);
		Options.setOption("article.font.name", SetEnv.font_name);
		Options.setOption("article.font.style", SetEnv.font_style);
		Options.setOption("article.font.size", SetEnv.font_size);
		Options.setOption("article.font.face", SetEnv.font_face);

		Options.save();
		System.exit(0);
	}



       public String getName(String name) {

       if (name == null) return "HepSim";

       String fileName = new String(name);

       if (fileName.lastIndexOf("/")<0) return fileName;
 
       return fileName.substring(fileName.lastIndexOf("/") + 1).substring(fileName.lastIndexOf("\\") + 1);

       }


}
