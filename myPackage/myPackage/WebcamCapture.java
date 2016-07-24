/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package myPackage;

/**
 *
 * @author hasrul
 */

import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import javax.media.*;
import javax.media.format.*;
import javax.media.util.*;
import javax.media.control.*;
import javax.media.protocol.*;
import java.util.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import com.sun.image.codec.jpeg.*;

import net.jxta.peergroup.PeerGroup;

public class WebcamCapture extends Panel implements ActionListener
{
  private PeerGroup SaEeDGroup =null;
  public Player player = null;
  public CaptureDeviceInfo di = null;
  public MediaLocator ml = null;
  public JButton capture = null;
  public Buffer buf = null;
  public Image img = null;
  public VideoFormat vf = null;
  public BufferToImage btoi = null;
  public ImagePanel imgpanel = null;


  public WebcamCapture(PeerGroup group)
  {
      this.SaEeDGroup = group;
    setLayout(new BorderLayout());
    setSize(320,550);

    imgpanel = new ImagePanel();
    capture = new JButton("Capture");
    capture.addActionListener(this);

    String str1 = "vfw:Logitech USB Video Camera:0";
    String str2 = "vfw:Microsoft WDM Image Capture (Win32):0";
//    di = CaptureDeviceManager.getDevice(str2);
//    ml = di.getLocator();
    ml = new MediaLocator(str2);

    try
    {
      player = Manager.createRealizedPlayer(ml);
      player.start();
      Component comp;

      if ((comp = player.getVisualComponent()) != null)
      {
        add(comp,BorderLayout.NORTH);
      }
      add(capture,BorderLayout.CENTER);
      add(imgpanel,BorderLayout.SOUTH);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }


  }


  public void playerclose()
  {
    player.stop();
    player.close();
    player.deallocate();
  }

  /* public void windowClosing(WindowEvent e) {
        playerclose();
    }*/

  public void actionPerformed(ActionEvent e)
  {
    JComponent c = (JComponent) e.getSource();

    if (c == capture)
    {
      // Grab a frame
      FrameGrabbingControl fgc = (FrameGrabbingControl)
      player.getControl("javax.media.control.FrameGrabbingControl");
      buf = fgc.grabFrame();

      // Convert it to an image
      btoi = new BufferToImage((VideoFormat)buf.getFormat());
      img = btoi.createImage(buf);

      // show the image
      imgpanel.setImage(img);
      
      // save image
      saveJPG(img,"capture/image" + SaEeDGroup.getPeerName()  + ".jpg");

      System.out.println("[+]"+"saved image"+SaEeDGroup.getPeerName()+".jpg");

//      JFileChooser jf = new JFileChooser("Save Capture");
//        ExtensionFileFilter filefilter = new ExtensionFileFilter(null,new String[]{"jpg", "jpeg"});
//        jf.addChoosableFileFilter(filefilter);
//
//        int option = jf.showSaveDialog(null);
//        if (option == JFileChooser.APPROVE_OPTION){
//            if (jf.getSelectedFile() != null)
//                saveJPG(img,jf.getSelectedFile().getPath());
//        }
    }
  }

  class ImagePanel extends Panel
  {
    public Image myimg = null;

    public ImagePanel()
    {
      setLayout(null);
      setSize(320,240);
    }

    public void setImage(Image img)
    {
      this.myimg = img;
      repaint();
    }

    public void paint(Graphics g)
    {
      if (myimg != null)
      {
        g.drawImage(myimg, 0, 0, this);
      }
    }
  }


  public static void saveJPG(Image img, String s)
  {
    BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_RGB);
    Graphics2D g2 = bi.createGraphics();
    g2.drawImage(img, null, null);

    FileOutputStream out = null;
    try
    {
      out = new FileOutputStream(s);
    }
    catch (java.io.FileNotFoundException io)
    {
      System.out.println("File Not Found");
    }

    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
    JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
    param.setQuality(0.5f,false);
    encoder.setJPEGEncodeParam(param);

    try
    {
      encoder.encode(bi);
      out.close();
    }
    catch (java.io.IOException io)
    {
      System.out.println("IOException");
    }

  }

}

