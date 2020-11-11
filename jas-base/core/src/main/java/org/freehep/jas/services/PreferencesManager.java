package org.freehep.jas.services;

/**
 * Interface for interacting with the preferences manager
 * @author tonyj
 */
public interface PreferencesManager
{
    void showPreferences();
    void showPreferences(String[] topic);
}
