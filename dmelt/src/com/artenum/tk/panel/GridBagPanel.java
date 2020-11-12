/**
 * Project        : ArtTk
 * Copyright      : (c) Artenum SARL, 24 rue Louis Blanc
 *                  75010, Paris, France 2009-2010
 *                  http://www.artenum.com
 *                  All copyright and trademarks reserved.
 * Email          : contact@artenum.com
 * Licence        : cf. LICENSE.txt
 * Developed By   : Artenum SARL
 * Authors        : Sebastien Jourdain      (jourdain@artenum.com)
 *                  Benoit thiebault        (thiebault@artenum.com)
 *                  Jeremie Turbet (JeT)    (turbet@artenum.com)
 *                  Julien Forest           (j.forest@artenum.com)
 * Created        : 11 Nov. 2005
 * Modified       : 23 Aug. 2010
 */
package com.artenum.tk.panel;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

/**
 * 
 */
public class GridBagPanel extends JPanel {

	/** Generated serial version id. */
	private static final long serialVersionUID = 4701719869705620555L;

	/** Current component grid bag constraints. */

	private GridBagConstraints constraints;

	/** Default constraints. */
	private GridBagConstraints defaultConstraints;

	/** The type of container. */
	private PanelType type;

	/** Current x position in the grid. */
	private int currentGridX;

	/** Current y position in the grid. */
	private int currentGridY;

	/**
	 * Boolean indicating whether the position of the component in the grid has
	 * been set manually or not.
	 */
	private boolean positionSet = false;

	private GridBagPanel( Builder builder ) {
		this.setLayout(new GridBagLayout());

		if (builder.containerName != null) {
			this.setBorder(BorderFactory.createTitledBorder(builder.containerName));
		}

		this.defaultConstraints = builder.defaultConstraints;
		this.constraints = new GridBagConstraints();
		this.type = builder.type;
		this.currentGridX = 0;
		this.currentGridY = 0;

		resetConstraints();
	}

	public GridBagPanel addComponent( Component component ) {
		switch (type) {
		case GRID:
			if (!positionSet) {
				constraints.gridx = currentGridX;
				constraints.gridy = currentGridY;
				currentGridX += constraints.gridwidth;
			}
			break;
		case HBOX:
			constraints.gridx = currentGridX;
			constraints.gridy = 0;

			currentGridX += constraints.gridwidth;
			break;
		case VBOX:
			constraints.gridx = 0;
			constraints.gridy = currentGridY;

			currentGridY++;
			break;
		default:
			break;
		}
		this.add(component, constraints);
		resetConstraints();

		return this;
	}

	public GridBagPanel newLine() {
		currentGridX = 0;
		currentGridY++;
		return this;
	}

	public GridBagPanel nextGridX() {
		currentGridX++;
		return this;
	}

	public GridBagPanel nextGridY() {
		currentGridY++;
		return this;
	}

	public GridBagPanel anchor( int anchor ) {
		constraints.anchor = anchor;
		return this;
	}

	public GridBagPanel fill( int fill ) {
		constraints.fill = fill;
		return this;
	}

	public GridBagPanel gridheight( int gridheight ) {
		constraints.gridheight = gridheight;
		return this;
	}

	public GridBagPanel gridwidth( int gridwidth ) {
		constraints.gridwidth = gridwidth;
		return this;
	}

	public GridBagPanel span( int gridwidth , int gridheight ) {
		constraints.gridwidth = gridwidth;
		constraints.gridheight = gridheight;
		return this;
	}

	public GridBagPanel gridx( int gridx ) {
		constraints.gridx = gridx;
		positionSet = true;
		return this;
	}

	public GridBagPanel gridy( int gridy ) {
		constraints.gridy = gridy;
		positionSet = true;
		return this;
	}

	public GridBagPanel position( int gridx , int gridy ) {
		constraints.gridx = gridx;
		constraints.gridy = gridy;
		positionSet = true;
		return this;
	}

	public GridBagPanel insets( Insets someInsets ) {
		constraints.insets = someInsets;
		return this;
	}

	public GridBagPanel insets( int top , int left , int bottom , int right ) {
		constraints.insets = new Insets(top, left, bottom, right);
		return this;
	}

	public GridBagPanel ipadx( int ipadx ) {
		constraints.ipadx = ipadx;
		return this;
	}

	public GridBagPanel ipady( int ipady ) {
		constraints.ipady = ipady;
		return this;
	}

	public GridBagPanel padding( int ipadx , int ipady ) {
		constraints.ipadx = ipadx;
		constraints.ipady = ipady;
		return this;
	}

	public GridBagPanel weightx( double weightx ) {
		constraints.weightx = weightx;
		return this;
	}

	public GridBagPanel weighty( double weighty ) {
		constraints.weighty = weighty;
		return this;
	}

	public GridBagPanel weight( double weightx , double weighty ) {
		constraints.weightx = weightx;
		constraints.weighty = weighty;
		return this;
	}
	
	public int getCurrentGridX() {
		return currentGridX;
	}
	
	public int getCurrentGridY() {
		return currentGridY;
	}

	private void resetConstraints() {
		constraints = (GridBagConstraints) defaultConstraints.clone();

		positionSet = false;
	}

	public static class Builder {

		/** The default grid bag constraints. */
		private GridBagConstraints defaultConstraints;
		/** The type of container. */
		private PanelType type;
		/** The name of the container. */
		private String containerName = null;

		public Builder() {
			initDefaultConstraints();
		}

		public Builder( String aName ) {
			containerName = aName;
			initDefaultConstraints();
		}

		private void initDefaultConstraints() {
			defaultConstraints = new GridBagConstraints();
			defaultConstraints.anchor = GridBagConstraints.CENTER;
			defaultConstraints.fill = GridBagConstraints.HORIZONTAL;
			defaultConstraints.gridheight = 1;
			defaultConstraints.gridwidth = 1;
			defaultConstraints.insets = new Insets(5, 5, 5, 5);
			defaultConstraints.ipadx = 0;
			defaultConstraints.ipady = 0;
			defaultConstraints.weightx = 1;
			defaultConstraints.weighty = 1;
		}

		public Builder anchor( int anchor ) {
			defaultConstraints.anchor = anchor;
			return this;
		}

		public Builder fill( int fill ) {
			defaultConstraints.fill = fill;
			return this;
		}

		public Builder insets( Insets someInsets ) {
			defaultConstraints.insets = someInsets;
			return this;
		}

		public Builder insets( int top , int left , int bottom , int right ) {
			defaultConstraints.insets = new Insets(top, left, bottom, right);
			return this;
		}

		public Builder ipadx( int ipadx ) {
			defaultConstraints.ipadx = ipadx;
			return this;
		}

		public Builder ipady( int ipady ) {
			defaultConstraints.ipady = ipady;
			return this;
		}

		public Builder padding( int ipadx , int ipady ) {
			defaultConstraints.ipadx = ipadx;
			defaultConstraints.ipady = ipady;
			return this;
		}

		public GridBagPanel build( PanelType aType ) {
			type = aType;
			return new GridBagPanel(this);
		}
	}
}
