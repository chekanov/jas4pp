/*
 *  Application.java
 *  allusionsApp
 *
 *  Created by Matthieu Cormier on Fri Jun 20 2003.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package com.apple.eawt;

//  This is a stub interface for non Mac OS X java 1.4 environments

public class Application {
	public Application() {
	}

	public void addApplicationListener(final ApplicationListener listenToMe) {
		// I don't feel like listening today!!
	}

	public boolean getEnabledPreferencesMenu() {
		return false;
	}

	public void removeApplicationListener(
			final ApplicationListener dontListenToMe) {
		// don't worry I haven't been
	}

	public void setEneablePreferencesMenu(final boolean enable) {
		// yeah, like, what-ever!
	}

	public static java.awt.Point getMouseLocationOnScreen() {
		return null;
	}
}
