package jhepsim;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Font;

import java.net.*;
import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

import org.fife.ui.rtextarea.*;
import org.fife.ui.rsyntaxtextarea.*;

/**
 * This is editor
 * 
 * @author sergei
 * 
 */

public class mainGUI extends JPanel {

	private RSyntaxTextArea textArea;
	private RTextScrollPane sp;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public mainGUI() {

		setLayout(new BorderLayout());
		textArea = new RSyntaxTextArea(20, 60);
		textArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PYTHON);
		textArea.setCodeFoldingEnabled(true);
		textArea.setAntiAliasingEnabled(true);
		textArea.setFont(SetEnv.globalFont);
		sp = new RTextScrollPane(textArea);
		sp.setFoldIndicatorEnabled(true);

		// Add an item to the popup menu that opens the file whose name is
		// specified at the current caret position.
		JPopupMenu popup = textArea.getPopupMenu();
		popup.addSeparator();
		popup.add(new JMenuItem(new OpenFileAction()));

		add(sp);

	}

	/**
	 * Loads a file's contents into the text area, or displays an error message
	 * if the file does not exist.
	 * 
	 * @param file
	 *            The file to load.
	 **/
	public void loadFile(String filename) {

		if (filename.startsWith("http") || filename.startsWith("ftp")) {

			System.out.println("Open=" + filename);
			try {
				URL oracle = new URL(filename);
				BufferedReader r = new BufferedReader(new InputStreamReader(
						oracle.openStream()));
				textArea.read(r, null);
				r.close();
				// String s="";
				// String inputLine;
				// while ((inputLine = in.readLine()) != null)
				// s=s+inputLine;
				// in.close();
			} catch (Exception ioe) {
				ioe.printStackTrace();
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}

		} else {

			File file = new File(filename);
			if (file.isDirectory()) { // Clicking on a space character
				JOptionPane
						.showMessageDialog(this, file.getAbsolutePath()
								+ " is a directory", "Error",
								JOptionPane.ERROR_MESSAGE);
				return;
			} else if (!file.isFile()) {
				JOptionPane.showMessageDialog(this,
						"No such file: " + file.getAbsolutePath(), "Error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			try {
				BufferedReader r = new BufferedReader(new FileReader(file));
				textArea.read(r, null);
				r.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
				UIManager.getLookAndFeel().provideErrorFeedback(textArea);
			}

		}

	}

	/**
	 * * Changes the styles used by the editor via an XML file specification.
	 * This * method is preferred because of its ease and modularity.
	 * */
	private void changeStyleViaThemeXml() {
		try {
			Theme theme = Theme.load(getClass().getResourceAsStream(
					"/eclipse_theme.xml"));
			theme.apply(textArea);
		} catch (IOException ioe) { // Never happens
			ioe.printStackTrace();
		}
	}

	

	
	/**
	 * Set text font
	 * @param font
	 */
	public void setTextFont(Font font) {
		if (font != null) {
			SyntaxScheme ss = textArea.getSyntaxScheme();
			ss = (SyntaxScheme) ss.clone();
			for (int i = 0; i < ss.getStyleCount(); i++) {
				if (ss.getStyle(i) != null) {
					ss.getStyle(i).font = font;
				}
			}
			textArea.setSyntaxScheme(ss);
			textArea.setFont(font);
		}
	}
	
	
	
	
	/**
	 * An action that gets the filename at the current caret position and tries
	 * to open that file. If there is a selection, it uses the selected text as
	 * the filename.
	 */
	private class OpenFileAction extends TextAction {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public OpenFileAction() {
			super("Open File");
		}

		public void actionPerformed(ActionEvent e) {

			JTextComponent tc = getTextComponent(e);
			String filename = null;

			
			try {
				int selStart = tc.getSelectionStart();
				int selEnd = tc.getSelectionEnd();
				if (selStart != selEnd) {
					filename = tc.getText(selStart, selEnd - selStart);
				} else {
					filename = getFilenameAtCaret(tc);
				}
			} catch (BadLocationException ble) {
				ble.printStackTrace();
				UIManager.getLookAndFeel().provideErrorFeedback(tc);
				return;
			}

			loadFile(filename);

		}

		/**
		 * Gets the filename that the caret is sitting on. Note that this is a
		 * somewhat naive implementation and assumes filenames do not contain
		 * whitespace or other "funny" characters, but it will catch most common
		 * filenames.
		 * 
		 * @param tc
		 *            The text component to look at.
		 * @return The filename at the caret position.
		 * @throws BadLocationException
		 *             Shouldn't actually happen.
		 */
		public String getFilenameAtCaret(JTextComponent tc)
				throws BadLocationException {
			int caret = tc.getCaretPosition();
			int start = caret;
			Document doc = tc.getDocument();
			while (start > 0) {
				char ch = doc.getText(start - 1, 1).charAt(0);
				if (isFilenameChar(ch)) {
					start--;
				} else {
					break;
				}
			}
			int end = caret;
			while (end < doc.getLength()) {
				char ch = doc.getText(end, 1).charAt(0);
				if (isFilenameChar(ch)) {
					end++;
				} else {
					break;
				}
			}
			return doc.getText(start, end - start);
		}

		public boolean isFilenameChar(char ch) {
			return Character.isLetterOrDigit(ch) || ch == ':' || ch == '.'
					|| ch == File.separatorChar;
		}

	}

	/**
	 * Return editor
	 * 
	 * @return
	 **/
	public RTextScrollPane getScrollPane() {

		return sp;
	}

	
	/**
	 * Get text from this editor.
	 * @return
	 */
	public String getText() {

		return textArea.getText();
		
	}
	
	
	/**
	 * Return editor
	 * 
	 * @return
	 */
	public RSyntaxTextArea getEditor() {

		return textArea;
	}

}
