/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package myPackage;


import java.io.File;



public class Rename {

    public Rename(String fileName){
                File oldfile =new File("oldfile.txt");
		File newfile =new File("newfile.txt");

		if(oldfile.renameTo(newfile)){
			System.out.println("Rename succesful");
		}else{
			System.out.println("Rename failed");
		}

    }
}
