package org.freehep.jas.plugin.console;

import java.util.Properties;
import javax.swing.JComponent;
import org.freehep.application.PropertyUtilities;
import org.freehep.jas.services.PreferencesTopic;

/**
 * Console plugin settings GUI.
 *
 * @author Dmitry Onoprienko
 */
class ConsolePreferences implements PreferencesTopic {

// -- Private parts : ----------------------------------------------------------
    static private final String _keyPrefix = "core.console.";
    static private final String _redirectOutKey = "redirectOut";
    static private final String _redirectErrKey = "redirectErr";
    static private final String _autoShowOutKey = "autoShowOut";
    static private final String _autoShowErrKey = "autoShowErr";
    static private final String _defaultScrollbackKey = "defaultScrollback";
    static private final boolean _redirectOutDefault = true;
    static private final boolean _redirectErrDefault = false;
    static private final boolean _autoShowOutDefault = false;
    static private final boolean _autoShowErrDefault = false;
    static private final int _defaultScrollbackDefault = 1000;
    
    private boolean redirectStandardOutput;
    private boolean autoShowStandardOutput;
    private boolean redirectStandardError;
    private boolean autoShowStandardError;  
    private int defaultScrollback;
    
    private final ConsolePlugin _consolePlugin;

// -- Construction and initialization : ----------------------------------------
    ConsolePreferences(ConsolePlugin consolePlugin) {
        _consolePlugin = consolePlugin;
        Properties prop = _consolePlugin.getApplication().getUserProperties();
        redirectStandardOutput = PropertyUtilities.getBoolean(prop, _keyPrefix + _redirectOutKey, _redirectOutDefault);
        redirectStandardError = PropertyUtilities.getBoolean(prop, _keyPrefix + _redirectErrKey, _redirectErrDefault);
        autoShowStandardOutput = PropertyUtilities.getBoolean(prop, _keyPrefix + _autoShowOutKey, _autoShowOutDefault);
        autoShowStandardError = PropertyUtilities.getBoolean(prop, _keyPrefix + _autoShowErrKey, _autoShowErrDefault);
        defaultScrollback = PropertyUtilities.getInteger(prop, _keyPrefix + _defaultScrollbackKey, _defaultScrollbackDefault);
    }

// -- Implementing PreferencesTopic : ------------------------------------------
    @Override
    public boolean apply(JComponent panel) {
        ConsolePreferencesPanel gui = (ConsolePreferencesPanel) panel;
        
        Properties prop = _consolePlugin.getApplication().getUserProperties();
        PropertyUtilities.setBoolean(prop, _keyPrefix + _redirectOutKey, redirectStandardOutput = gui.isRedirectStandardOut());
        PropertyUtilities.setBoolean(prop, _keyPrefix + _redirectErrKey, redirectStandardError = gui.isRedirectStandardError());
        PropertyUtilities.setBoolean(prop, _keyPrefix + _autoShowOutKey, autoShowStandardOutput = gui.isAutoShowStandardOut());
        PropertyUtilities.setBoolean(prop, _keyPrefix + _autoShowErrKey, autoShowStandardError = gui.isAutoShowStandardError());
        PropertyUtilities.setInteger(prop, _keyPrefix + _defaultScrollbackKey, defaultScrollback = gui.getDefaultsScrollback());
        _consolePlugin.preferencesChanged(this);
        return true;
    }

    @Override
    public JComponent component() {
        final ConsolePreferencesPanel gui = new ConsolePreferencesPanel();
        gui.setRedirectStandardOut(redirectStandardOutput);
        gui.setRedirectStandardError(redirectStandardError);
        gui.setAutoShowStandardOut(autoShowStandardOutput);
        gui.setAutoShowStandardError(autoShowStandardError);
        gui.setDefaultsScrollback(defaultScrollback);
        return gui;
    }

    @Override
    public String[] path() {
        return new String[]{"General", "Console"};
    }        

    boolean isRedirectStandardOutput() {
        return redirectStandardOutput;
    }

    boolean isAutoShowStandardOutput() {
        return autoShowStandardOutput;
    }

    boolean isRedirectStandardError() {
        return redirectStandardError;
    }

    boolean isAutoShowStandardError() {
        return autoShowStandardError;
    }

    int getDefaultScrollback() {
        return defaultScrollback;
    }
}
