/*
 * (c) Copyright: Artenum SARL, 101-103 Boulevard Mac Donald,
 *                75019, Paris, France 2005.
 *                http://www.artenum.com
 *
 * License:
 *
 *  This program is free software; you can redistribute it
 *  and/or modify it under the terms of the Q Public License;
 *  either version 1 of the License.
 *
 *  This program is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 *  PURPOSE. See the Q Public License for more details.
 *
 *  You should have received a copy of the Q Public License
 *  License along with this program;
 *  if not, write to:
 *    Artenum SARL, 101-103 Boulevard Mac Donald,
 *    75019, PARIS, FRANCE, e-mail: contact@artenum.com
 */
package com.artenum.jyconsole.command;

/**
 * <pre>
 * &lt;b&gt;Project ref           :&lt;/b&gt; JyConsole project
 * &lt;b&gt;Copyright and license :&lt;/b&gt; See relevant sections
 * &lt;b&gt;Status                :&lt;/b&gt; under development
 * &lt;b&gt;Creation              :&lt;/b&gt; 23/06/2006
 * &lt;b&gt;Modification          :&lt;/b&gt;
 * &lt;b&gt;Description  :&lt;/b&gt; A runner for Jython commands that abstracts away the threading model.
 * 
 * </pre>
 * <table cellpadding="3" cellspacing="0" border="1" width="100%">
 * <tr BGCOLOR="#CCCCFF" CLASS="TableHeadingColor">
 * <td><b>Version number</b></td>
 * <td><b>Author (name, e-mail)</b></td>
 * <td><b>Corrections/Modifications</b></td>
 * </tr>
 * <tr>
 * <td>0.1</td>
 * <td>Colin Crist, colincrist@hermesjms.com</td>
 * <td>Contribution integrated by Sebastien Jourdain, jourdain@artenum.com</td>
 * </tr>
 * </table>
 * 
 * @author Colin Crist, colincrist@hermesjms.com
 * @author Sebastien Jourdain
 * @version 0.1
 */
public interface CommandRunner {
	/**
	 * Invoke a command asynchronously.
	 * 
	 * @param command
	 */
	public void invokeLater(Command command);

	/**
	 * Stop any currently executing commands and reset the runner to its initial
	 * state, the runner can still be used.
	 */
	public void reset();

	/**
	 * Stop the runner and frees any resources e.g. threads, once stopped it
	 * cannot be reused.
	 */
	public void stop();
}
