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
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
//import jxta libraries
import net.jxta.peergroup.PeerGroup;
import net.jxta.share.ContentAdvertisement;
import net.jxta.share.client.CachedListContentRequest;

//This class will search for specifed contents through SaEeD Group
public class SearchFile extends Thread
{
    //Defining Class Variables
    private JTextArea log=null;
    private PeerGroup SaEeDGroup=null;
    private String searchValue =null;
    protected ListRequestor reqestor =null;
    private JTable table = null;
    
    public static ContentAdvertisement [] contents=null;
    
    private boolean running = true;
    
    public SearchFile(PeerGroup group,String searchKey, JTextArea log, JTable table) 
    {
        this.SaEeDGroup = group;
        this.searchValue = searchKey;
        this.log = log;
        this.table = table;
        
    }
    public void run() //cause this thread to execute as long as needed to find 
    {                 // the Contents  
        while(true)
        {
        if(running == false){
            break;
        }
        reqestor = new ListRequestor(SaEeDGroup,searchValue,log, table);        
        reqestor.activateRequest();
        
            try{
            Thread.sleep(8*1000); //Time out for each search through network
            } catch(InterruptedException ie)
            {
                stopThread();
            }
        }
        log.append("[-]Searching for content is finished.\n");
     }        
    
    public void stopThread() //This method will stop search Process
    {
    running = false;
    if (reqestor != null){
    reqestor.cancel();
    }
    
}
    public void killThread() //This method will Terminate the Search Thread
    {
        log.append("[-]Searching Thread is stopping.\n");
        running =false;   
    }
    public ContentAdvertisement [] getContentAdvs() //Accessor to show found contents
    {
        return reqestor.searchResult;
    } 
}
//inner class for search
class ListRequestor extends CachedListContentRequest
{
    public static ContentAdvertisement [] searchResult = null;
    private JTextArea log = null;
    private JTable table =null;
    
    public ListRequestor(PeerGroup SaEeDGroup , String SubStr, JTextArea log,JTable table){
        super(SaEeDGroup,SubStr);
        this.log = log;
        this.table = table;
    }

    public void notifyMoreResults() //this method will notify user when new contents are found
    {
        log.append("[+]Searching for More Contents.\n");
        searchResult = getResults();
        //showing the results
        String [] titles = {"File Name" , "Size Bytes","Check Sum (CRC-32)"};
        //add new contents to Search table
        DefaultTableModel TableModel1 = new DefaultTableModel(titles, searchResult.length);
        table.setModel(TableModel1);
        
        for(int i=0; i < searchResult.length;i++){
            log.append("[*]Found: " + searchResult[i].getName()+"\n" +
                    "Size: " + searchResult[i].getLength() + " Bytes\n");
            table.setValueAt(searchResult[i].getName(),i,0);
            table.setValueAt(searchResult[i].getLength(),i,1);
            table.setValueAt(searchResult[i].getDescription(),i,2);
            //table.setValueAt(searchResult[i].getContentId(),i,2);
            
        }
    }
    public ContentAdvertisement [] getContentAdvs()//acessor to return contents
    { 
        return searchResult;
    }   
    
}
