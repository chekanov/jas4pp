//Copyright 2001-2005 FreeHep
package org.freehep.graphics2d.font;

/**
 * Provides conversions between unicodes, names, and encodings for any
 * particular encoding.
 * 
 * @author Sami Kama
 * @version $Id: freehep-graphics2d/src/main/java/org/freehep/graphics2d/font/CharTable.java 7aee336a8992 2005/11/25 23:19:05 duns $
 */
public interface CharTable {

    /**
     * Converts unicode character to name.
     * 
     * @param c unicode character
     * @return name
     */
    public String toName(char c);

    /**
     * Converts unicode Character object to name.
     * 
     * @param c unicode Character object
     * @return name
     */
    public String toName(Character c);

    /**
     * Converts character code into a name.
     * 
     * @param enc code
     * @return name
     */
    public String toName(int enc);

    /**
     * Converts character code Integer object into a name.
     * 
     * @param enc code Integer object
     * @return name
     */
    public String toName(Integer enc);

    /**
     * Converts name into character code.
     * 
     * @param name name of the character
     * @return character code
     */
    public int toEncoding(String name);

    /**
     * Converts a unicode into a character code.
     * 
     * @param unicode unicode character
     * @return character code
     */
    public int toEncoding(char unicode);

    /**
     * Converts a name to a unicode character.
     * 
     * @param name of the character
     * @return unicode character
     */
    public char toUnicode(String name);

    /**
     * Returns the name of the table.
     * 
     * @return table name
     */
    public String getName();

    /**
     * Returns the encoding name of the table.
     * 
     * @return encoding name
     */
    public String getEncoding();
}
