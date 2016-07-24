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

import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;

//This class will create initialization configuration for class 
//and Sets Peers Share folder
public class FirstTimeCheck extends JFileChooser{
    
    /** Creates a new instance of FirstTimeCheck */
    private boolean firstTime = true;
    public JTextArea log;
    private String SharedPath="unknown";
    
    public FirstTimeCheck(JTextArea txt) 
    {
        this.log = txt;
    }
    public void searchForConfigFile(){ //Search for Configuration file
        log.append("[+]Searching for Configuration file.\n");
        File configFile = new File("config.ini");
        if(configFile.exists() && configFile.isFile()){
            log.append("[+]Configuration file Found.\n");
            readingConfigFile(configFile);            
        }
        else{
           log.append("[-]Configuration file !NOT! Found.\n"); 
           createConfigFile(); 
        }
        
        
    }
    public void readingConfigFile(File file) //Starts reading Configuration file and finds shared path from it.
    {
        log.append("[+]Reading file: " + file.getName()+"\n");
        try {
            
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str,path;
            int index;
            while((str=in.readLine())!=null){
                if(str.startsWith("SharedFolder")){
                    index = str.indexOf("=");
                    path = str.substring(index+1);
                    log.append("[+]Shared Path is: " + path + "\n");
                    File temp = new File(path);
                    if(temp.exists()){
                        log.append("[+]Shared Path Exists\n");
                        SharedPath = temp.getAbsolutePath();
                        
                    }else{
                        log.append("[-]Path NOT Exists!!!\n");
                        createShareFolder(temp);
                    }
                }
            }
            in.close();
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public void createShareFolder(File pathname) //if the shared path doesnot exit this method will create it
    {
        log.append("[+]Creating Share Folder...\n");
        if(pathname.mkdir()){
            log.append("[+]Shared Folder Successfully Created.\n");
            SharedPath = pathname.getAbsolutePath();
        }
        
    }
    public void createConfigFile()
    {
        File config = new File("config.ini");
        //Default path
        String Path="C:\\SaEeD";
        
        log.append("**** Please Select Your Share Folder ****\n");
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retVal =chooser.showOpenDialog(this);
        if(retVal == JFileChooser.APPROVE_OPTION)
        {
           log.append("[+]Selected Path is: " + chooser.getSelectedFile().getAbsolutePath()+"\n");
           Path = chooser.getSelectedFile().getAbsolutePath();
        }        
        SharedPath = Path;
        try {            
            boolean success = config.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(config));
            if(success){
                log.append("[+]Config.ini file Successfully Created.\n");
                log.append("[+]Writing Data into Configuration File\n");
                out.write("SharedFolder=" + Path);
                out.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        readingConfigFile(config);
    }
    public String getSharedPath(){
        return SharedPath;        
    }
    public boolean isFirstTime() //Search for initialization file, if not found assumes that it is
    {                           // the first time that program is being executed and will create 
                                //Initialization File
        File configFile = new File("config.ini");
        if(configFile.exists() && configFile.isFile()){
            firstTime = false;
        }
        else{
            firstTime =true;
        }
        return firstTime;
    }
}
