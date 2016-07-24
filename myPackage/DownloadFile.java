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
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.plaf.ProgressBarUI;
import net.jxta.peergroup.PeerGroup;
import net.jxta.share.ContentAdvertisement;
import net.jxta.share.client.GetContentRequest;

//This class runs as Thread and start Downloading the File as soon as called
public class DownloadFile extends Thread
{
    private PeerGroup SaEeDGroup=null;
    protected GetRemoteFile myDonwloader = null;
    private JTextArea log;
    public DownloadFile(PeerGroup group, ContentAdvertisement contentAdv, File destination , JTextArea log,
                         JProgressBar progress) 
    {
        this.log = log;
        this.log.append("[+]Starting Download Object.\n");
        //inner classes used here for better performance
        myDonwloader = new GetRemoteFile(group, contentAdv, destination, this.log, progress);        
        
    }
    
}
//inner class which handles download requestes
class GetRemoteFile extends GetContentRequest
{
    private JProgressBar progressBar = null;
    private JTextArea log =null;
    private boolean downloadFinished = false;
    public GetRemoteFile(PeerGroup group, ContentAdvertisement contentAdv, File destination , JTextArea log,
                         JProgressBar progress)
    {
        super(group, contentAdv, destination);
        this.progressBar = progress;
        this.log = log;
        this.log.append("[+]Download in Progress.\n");
    }
    
    public void notifyUpdate(int percentage) //this method will notify about download progress
    {
     progressBar.setValue(percentage);   
    }
    
    public void notifyDone()//this method will return message about download process 
    {
        log.append("[+]Donwloading Process is sucessfully finished.\n");
    }

    public void notifyFailure()//this method will return message if download failed 
    {
        log.append("[-]Downloading File is Failed!!!!!\n");
    }

}
