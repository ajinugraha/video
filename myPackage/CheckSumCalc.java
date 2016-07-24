/*
 * Student Name: Seyed Saeed Ghiassy
 * Student ID  : 05994390
 * Supervisor  : Dr.Fakas
 * Project Name: Peer-to-Peer File Sharing Application Using JXTA technology
 * Unit Name   : Final Year Project
 * Unit Code   : 63CP3261
 * DeadLine    : 21-April-2008
 * University  : Manchester Metropolitan University
 * E-mail      : seyed.ghiassy@student.mmu.ac.uk
 * Softwares   : JXTA Version 2.4.1, JDK Version 1.6.0_05, NetBeans IDE 5.5
 */
package myPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;

//This Class generates CRC-32 Check sum to make sure that files which transfered 
//are not corrupted
public class CheckSumCalc {
        
    private long Result =0;
    /** Creates a new instance of CheckSumCalc */
    public CheckSumCalc() 
    {
    }
    public String getFileSum(File filename)//this method will return String of CheckSum file :-)
    {
        //CRC-32  check sum
        try {
            CheckedInputStream cis = new CheckedInputStream(
                    new FileInputStream(filename),new Adler32());
            byte[] tempBuf = new byte[512];
            while (cis.read(tempBuf) >= 0) {
        }
        Result = cis.getChecksum().getValue();
        cis.close();    
        } catch (FileNotFoundException ex) {
            System.out.println("[-]File not Found :-(");
            ex.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return Long.toHexString(Result);
    }
    
}
