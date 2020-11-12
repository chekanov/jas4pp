/**
*    Copyright (C)  SCaVis project. The jHPLot package by S.Chekanov and Work.ORG
*    All rights reserved.
*
*    This program is free software; you can redistribute it and/or modify it under the terms
*    of the GNU General Public License as published by the Free Software Foundation; either
*    version 3 of the License, or any later version.
*
*    This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
*    without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
*    See the GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License along with this program;
*    if not, see <http://www.gnu.org/licenses>.
*
*    Additional permission under GNU GPL version 3 section 7:
*    If you have received this program as a library with written permission from the SCaVis team,
*    you can link or combine this library with your non-GPL project to convey the resulting work.
*    In this case, this library should be considered as released under the terms of
*    GNU Lesser public license (see <https://www.gnu.org/licenses/lgpl.html>),
*    provided you include this license notice and a URL through which recipients can access the
*    Corresponding Source.
**/
package jhplot.math.io;

import java.util.*;

/**
 * BSD License
 * 
 * @author Yann RICHET
 */
public class ArrayString {

   // private static int decimalSize = 10;

    private static String defaultWordDelimiter = " ";

    private static String defaultSentenceDelimiter = "\n";

    public static String printDoubleArray(double[][] m) {
        return printDoubleArray(m, defaultWordDelimiter, defaultSentenceDelimiter);
    }

    public static String printDoubleArray(double[] m) {
        return printDoubleArray(new double[][] { m });
    }

    public static String printIntArray(int[][] m) {
        return printIntArray(m, defaultWordDelimiter, defaultSentenceDelimiter);
    }

    public static String printIntArray(int[] m) {
        return printIntArray(new int[][] { m });
    }

    public static String printDoubleArray(double[][] m, String wordDelimiter,
            String sentenceDelimiter) {

        StringBuffer str = new StringBuffer(25 * m.length * m[0].length);

        // //can't use format because of infinty which become "?" strings...
        // DecimalFormat format = new DecimalFormat();
        // format.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        // format.setMinimumIntegerDigits(1);
        // format.setMaximumFractionDigits(decimalSize);
        // format.setMinimumFractionDigits(decimalSize);
        // format.setGroupingUsed(false);

        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                // String s = format.format(m[i][j]); // format the number
                str = str.append(wordDelimiter);
                // str = str.append(s);
                str = str.append(Double.toString(m[i][j]));
            }
            if (i < m.length - 1) {
                str = str.append(sentenceDelimiter);
            }
        }
        return str.toString();

    }

    public static String printIntArray(int[][] m, String wordDelimiter,
            String sentenceDelimiter) {

        StringBuffer str = new StringBuffer(25 * m.length * m[0].length);

        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                str = str.append(wordDelimiter);
                str = str.append(m[i][j]);
            }
            if (i < m.length - 1) {
                str = str.append(sentenceDelimiter);
            }
        }
        return str.toString();
    }

    public static double[][] readStringDouble(String s) {
        return readStringDouble(s, defaultWordDelimiter,
                defaultSentenceDelimiter);
    }

    public static double[] readString1DDouble(String s) {
        return readString1DDouble(s, defaultWordDelimiter,
                defaultSentenceDelimiter);
    }

    public static double[] readString1DDouble(String s, String wordDelimiter,
            String sentenceDelimiter) {
        double[][] d = readStringDouble(s, wordDelimiter, sentenceDelimiter);
        double[] d1D = null;
        if (d.length > 1) {
            d1D = new double[d.length];
            for (int i = 0; i < d1D.length; i++)
                d1D[i] = d[i][0];
        } else
            d1D = d[0];
        return d1D;
    }

    public static double[][] readStringDouble(String s, String wordDelimiter,
            String sentenceDelimiter) {

        double[][] array;

        String delimiterString = wordDelimiter;
        String newlineString = sentenceDelimiter;

        StringTokenizer linesTokenizer = new StringTokenizer(s, newlineString);
        StringTokenizer wordsTokenizer;

        Vector<double[]> lines = new Vector<double[]>();
        String line;

        int linereadFailed = 0;
        do {
            Vector<Double> words = new Vector<Double>();
            line = linesTokenizer.nextToken();
            wordsTokenizer = new StringTokenizer(line, delimiterString);
            int readFailed = 0;

            do {
                try {
                    words
                            .addElement(Double.valueOf(wordsTokenizer
                                    .nextToken()));
                } catch (NumberFormatException ex) {
                    readFailed++;
                }
            } while (wordsTokenizer.hasMoreElements());

            if (readFailed == 0) {
                double[] line_i = new double[words.size()];

                // words.copyInto(line_i);
                for (int i = 0; i < line_i.length; i++) {
                    line_i[i] = ((Double) words.get(i)).doubleValue();
                }
                lines.add(line_i);
            } else {
                linereadFailed++;
            }
        } while (linesTokenizer.hasMoreElements());

        if (linereadFailed != 0) {
            System.out.println("warning : " + linereadFailed
                    + " lines ignored!");
        }

        /*
         * do { Vector words = new Vector(); wordsTokenizer = new
         * StringTokenizer(linesTokenizer.nextToken(), delimiterString); do {
         * words.addElement(Double.valueOf(wordsTokenizer.nextToken())); } while
         * (wordsTokenizer.hasMoreElements()); double[] line_i = new
         * double[words.size()]; //words.copyInto(line_i); for (int i = 0; i <
         * line_i.length; i++) { line_i[i] = ( (Double)
         * words.get(i)).doubleValue(); } lines.add(line_i); } while
         * (linesTokenizer.hasMoreElements());
         */

        array = new double[lines.size()][];
        lines.copyInto(array);

        return array;
    }

    public static int[][] readStringInt(String s) {
        return readStringInt(s, defaultWordDelimiter,
                defaultSentenceDelimiter);
    }

    public static int[] readString1DInt(String s) {
        return readString1DInt(s, defaultWordDelimiter,
                defaultSentenceDelimiter);
    }

    public static int[] readString1DInt(String s, String wordDelimiter,
            String sentenceDelimiter) {
        int[][] d = readStringInt(s, wordDelimiter, sentenceDelimiter);
        int[] d1D = null;
        if (d.length > 1) {
            d1D = new int[d.length];
            for (int i = 0; i < d1D.length; i++)
                d1D[i] = d[i][0];
        } else
            d1D = d[0];
        return d1D;
    }
    
    public static int[][] readStringInt(String s, String wordDelimiter,
            String sentenceDelimiter) {

        int[][] array;

        String delimiterString = wordDelimiter;
        String newlineString = sentenceDelimiter;

        StringTokenizer linesTokenizer = new StringTokenizer(s, newlineString);
        StringTokenizer wordsTokenizer;

        Vector<int[]> lines = new Vector<int[]>();

        int linereadFailed = 0;
        do {
            Vector<Integer> words = new Vector<Integer>();
            wordsTokenizer = new StringTokenizer(linesTokenizer.nextToken(),
                    delimiterString);
            int readFailed = 0;

            do {
                try {
                    words.addElement(Integer
                            .valueOf(wordsTokenizer.nextToken()));
                } catch (NumberFormatException ex) {
                    readFailed++;
                }
            } while (wordsTokenizer.hasMoreElements());

            if (readFailed == 0) {
                int[] line_i = new int[words.size()];

                // words.copyInto(line_i);
                for (int i = 0; i < line_i.length; i++) {
                    line_i[i] = ((Integer) words.get(i)).intValue();
                }
                lines.add(line_i);
            } else {
                linereadFailed++;
            }
        } while (linesTokenizer.hasMoreElements());

        if (linereadFailed != 0) {
            System.out.println("warning : " + linereadFailed
                    + " lines ignored!");
        }

        /*
         * do { Vector words = new Vector(); wordsTokenizer = new
         * StringTokenizer(linesTokenizer.nextToken(), delimiterString); do {
         * words.addElement(Double.valueOf(wordsTokenizer.nextToken())); } while
         * (wordsTokenizer.hasMoreElements()); double[] line_i = new
         * double[words.size()]; //words.copyInto(line_i); for (int i = 0; i <
         * line_i.length; i++) { line_i[i] = ( (Double)
         * words.get(i)).doubleValue(); } lines.add(line_i); } while
         * (linesTokenizer.hasMoreElements());
         */

        array = new int[lines.size()][];
        lines.copyInto(array);

        return array;
    }

}