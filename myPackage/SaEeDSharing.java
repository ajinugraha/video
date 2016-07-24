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
import java.io.IOException;
import javax.swing.JTextArea;
//import jxta libraries
import net.jxta.exception.PeerGroupException;
import net.jxta.peergroup.PeerGroup;
import net.jxta.share.CMS;
import net.jxta.share.Content;
import net.jxta.share.ContentManager;
import net.jxta.share.SearchListener;

//This class will share contents through peers in SaEeD Group
public class SaEeDSharing extends Thread implements SearchListener 
{
        //Defining Class Variables
    private PeerGroup SaEeDGroup =null;
    private JTextArea log=null;
    private File myPath = null;
    //using Content Management Service Library for Sharing purposes
    private CMS cms =null;
    
    /** Creates a new instance of SaEeDSharing */
    public SaEeDSharing(PeerGroup group, JTextArea log, File givenPath) 
    {
        this.log = log;
        this.SaEeDGroup = group;
        this.myPath = givenPath;
        launchCMS();
    }
    private void launchCMS()
    {
        //This method will initializie the CMS library
        log.append("[+]Initialising CMS Libraries...\n");
        cms = new CMS();
        try {
            cms.init(SaEeDGroup,null,null);//binding CMS object to SaEeD Group
            if(cms.startApp(myPath) == -1){
                log.append("[-]Creating CMS object Failed.\n");
                System.out.println("[!]CMS Initilization Failed.\nExiting.");
                System.exit(-1);
            }else{
                log.append("[+]CMS object Successfully Created.\n");
            }
            //sharing all files in shared directory
            ContentManager contentManager = null;
            contentManager = cms.getContentManager();
            
            File [] list = myPath.listFiles();
            CheckSumCalc checkSum = new CheckSumCalc();
            
            for(int i=0;i<list.length;i++){
                if(list[i].isFile())
                {//Sharing Files and check sums in network
                    contentManager.share(list[i],checkSum.getFileSum(list[i]));                    
                }
            }                     
            log.append("======= Shared Contents =======\n");
            //viewing the shared contents
            Content [] content = cms.getContentManager().getContent();
            //also shows the share contents in log area
            for(int j=0;j< content.length;j++){
                log.append("[*]" + content[j].getContentAdvertisement().getName()+  "\tSum: " + 
                        content[j].getContentAdvertisement().getDescription()+"\n");
            }
            log.append("[+]All Content are Successfully Shared :-)\n");
            
        } catch (PeerGroupException ex) 
        {
            System.out.println("[!]CMS Initilization Failed.\nExiting.");
            ex.printStackTrace();
            System.exit(-1);
        }catch(IOException e){
            log.append("[-]Exception: " + e.getMessage()+ "\n[!]Make sure File: \"Shares.ser\" is Deleted before" +
                    " start the Service.\n");
            System.out.println("[-]Exception: " + e.getMessage());            
        }
        
        log.append("[===========================]\n");
        
    }
    public void stopCMS()//this method will stop Content management Service
    {
        log.append("[+]Stopping CMS Object.\n");
        cms.stopApp();
        log.append("[+]Deleting CMS Content Advertisements File.\n");
        File temp = new File(myPath.getAbsolutePath()+ File.separator +"shares.ser");        
        if(temp.delete())
        {   //also deletes the CMS data file
            log.append("[+]File \""+ myPath.getAbsolutePath()+ File.separator + "shares.ser\" successfully deleted.\n");
            System.out.println("[+]File shares.ser successfully deleted.");
        }else{
            log.append("[-]File shares.ser Not Found!\n");
        }
    }
    //Listener to shows requested queries
    public void queryReceived(String query){
        System.out.println("[Query Received]: " + query);
    }
    
}
