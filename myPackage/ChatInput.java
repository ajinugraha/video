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

import java.awt.Color;
import java.io.FileInputStream;
import javax.swing.JTextArea;
//importing JXTA libraries
import net.jxta.document.AdvertisementFactory;
import net.jxta.document.MimeMediaType;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.Message.ElementIterator;
import net.jxta.endpoint.MessageElement;
import net.jxta.peergroup.PeerGroup;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.PipeAdvertisement;

// this class will create input pipe for chatting services and shows the incoming messages
public class ChatInput extends Thread implements PipeMsgListener 
{   //Class variables
    private JTextArea txtChat=null , log=null;
    private PeerGroup SaEeDGroup=null;
    private String myPeerID = null;
    private PipeService myPipeService =null;
    private PipeAdvertisement pipeAdv =null;
    private InputPipe pipeInput = null;
    
    /** Creates a new instance of ChatInput */
    public ChatInput(PeerGroup group, JTextArea log, JTextArea chat) 
    {
        this.log = log;
        this.txtChat = chat;
        this.SaEeDGroup = group;
        getServices();
        
    }
    
    private void getServices()
    {   //Obtaining Peer Group services
        log.append("[+]Getting Services for Chat component...\n");
        myPipeService = SaEeDGroup.getPipeService();
        myPeerID = SaEeDGroup.getPeerID().toString();
        
        try{ //Creates input pipe
            FileInputStream is = new FileInputStream("saeedPipe.adv");
            pipeAdv = (PipeAdvertisement)AdvertisementFactory.newAdvertisement(MimeMediaType.XMLUTF8,is);
            is.close();
        }catch(Exception e){
            log.append("[+]Exception: " + e.getMessage()+"\n");
            e.printStackTrace();
            System.exit(-1);
        }
        log.append("[+]Input Pipe Successfully Created.\n");
        
    }
    public void startListening() //This method will start listening for incoming messages thro created pipe
    {
        log.append("[+]Start Listening for Incoming Messages.\n");
        try{
            pipeInput = myPipeService.createInputPipe(pipeAdv,this);
            
        }catch(Exception e){
            log.append("[-]Exception: " + e.getMessage()+"\n");
            return;
        }
        if(pipeInput == null){
            log.append("[-]Failure in Opening Input Pipe :-(\n");
            System.exit(-1);
        }
    }
    public void stopListening() //This method will stop input pipe
    {
        pipeInput.close();
        log.append("[-]Input Pipe Closed for Incomming Message.\n");
    }
    //this listener will respond to incoming messages and show them in Designated area
    public void pipeMsgEvent(PipeMsgEvent ev)
    {
        log.append("[+]Message Received...\n");
        Message myMessage = null;
        try{
            myMessage = ev.getMessage();
            if(myMessage == null){
                return;
            }
        }catch(Exception e){
            System.out.println("[-]Exception happend when trying to get Message element!");
            e.printStackTrace();
        }
        //Assigning values to wanted Tages
        ElementIterator el = myMessage.getMessageElements();
        MessageElement me = myMessage.getMessageElement("peerName");
        MessageElement me2 = myMessage.getMessageElement("peerID");
        MessageElement me3 = myMessage.getMessageElement("chatMessage");
        MessageElement me4 = myMessage.getMessageElement("Time");
        
        String peerName = me.toString();
        String peerID = me2.toString();
        String msgContent = me3.toString();
        String sentTime = me4.toString();
        if(me.toString() == null || me2.equals(myPeerID)){
            return;
        }
        else{
         txtChat.append("[ " + me+ "@" + me4 +"]  " + me3 + "\n");
         
        }  
    }
    
}
