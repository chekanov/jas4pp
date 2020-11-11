package org.freehep.jas.plugin.console;

import java.io.InputStream;

/**
 * An InputStream for reading input typed into a console.
 *
 * @author tonyj
 * @version $Id: ConsoleInputStream.java 14068 2012-12-04 00:32:28Z tonyj $
 */
public abstract class ConsoleInputStream extends InputStream {

    private String prompt;
    private String oneTimePrompt;
    private String entry;

    /**
     * Sets the prompt. The prompt will be displayed in the console when input
     * is needed from the user.
     *
     * @param prompt The prompt string
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    /**
     * Sets the prompt. The new prompt will be used only once and then the
     * default prompt will be restored.
     *
     * @param prompt The prompt string
     */
    public void setOneTimePrompt(String prompt) {
        this.oneTimePrompt = prompt;
    }

    /**
     * Get the current (permanent) prompt.
     *
     * @return The prompt string
     */
    public String getPrompt() {
        return prompt;
    }

    String getCurrentPrompt() {
        return oneTimePrompt != null ? oneTimePrompt : prompt;
    }

    void clearOneTimePrompt() {
        oneTimePrompt = null;
    }

    /**
     * The initial entry is prefilled into the input area following the prompt.
     * Unlike the prompt it can be cleared by the user (using backspace for
     * example). The initial entry is used only for a single prompt, and is then
     * cleared.
     *
     * @param entry The string to pre fill the input area.
     */
    public void setInitialEntry(String entry) {
        this.entry = entry;
    }

    String getInitialEntry() {
        String result = entry;
        entry = null;
        return result;
    }
}