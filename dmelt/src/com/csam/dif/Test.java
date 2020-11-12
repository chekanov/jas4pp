/*
 * Test.java
 *
 * Created on January 29, 2008, 12:08 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.csam.dif;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author ncrause
 */
public class Test {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            InputStream inStream = new FileInputStream("C:/test.dif");
            DIFSheet sheet = new DIFSheet(inStream);
            System.out.println(sheet);
            
            sheet.setTitle("My Sheet");
            sheet.getRow(3).createCell(3).setCellValue("New Cell");
            
            OutputStream outStream = new FileOutputStream("C:/test2.dif");
            sheet.write(outStream);
            outStream.close();
        } catch (Throwable thrown) {
            thrown.printStackTrace();
        }
    }
    
}
