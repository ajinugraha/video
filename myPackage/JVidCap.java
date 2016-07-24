/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package myPackage;

/**
 *
 * @author hasrul
 */
import javax.media.*;
import javax.media.protocol.*;
import javax.media.control.*;
import javax.media.format.*;
import java.awt.*;
import com.sun.media.controls.VFlowLayout; // Lays components one below the other
import java.awt.event.*;
import java.util.*;

import net.jxta.peergroup.PeerGroup;

public class JVidCap extends Frame implements ItemListener, ActionListener {

    static SaEeDSharing launchSharing =null;

    private PeerGroup SaEeDGroup =null;

    // GUI components

    Panel jPanel1 = new Panel();
    Choice comboFileType = new Choice();
    VFlowLayout verticalFlowLayout1 = new VFlowLayout();
    Label jLabel1 = new Label();
    Checkbox checkVideo = new Checkbox();
    Choice comboEncoding = new Choice();
    Choice comboSize = new Choice();
    Checkbox checkAudio = new Checkbox();
    Choice comboSampling = new Choice();
    Checkbox radio16bit;
    Checkbox radio8bit;
    Panel jPanel2 = new Panel();
    Panel jPanel3 = new Panel();
    Checkbox radioMono = new Checkbox();
    Checkbox radioStereo = new Checkbox();
    Button buttonStart = new Button();
    Button buttonExit = new Button();
    Button buttonEnd = new Button();
    Panel panel1 = new Panel();
    GridLayout gridLayout1 = new GridLayout();
    GridLayout gridLayout2 = new GridLayout();

    // JMF objects
    Processor processor = null;
    DataSink datasink = null;
    Component monitor = null;
    DataSource datasource = null;
    String outputType = "video.quicktime";

    public JVidCap(PeerGroup group) {
	createGUI();
        fillGUI();
        this.SaEeDGroup=group;
    }

    private void startMonitoring() {
	// Close the previous processor, which in turn closes the capture device
	if (processor != null) {
	    processor.stop();
	    processor.close();
	}
	// Remove the previous monitor
	if (monitor != null) {
	    panel1.remove(monitor);
	    monitor = null;
	}

	AudioFormat af = null;
	VideoFormat vf = null;

	if (checkAudio.getState()) {
	    // Need audio
	    int samplingRate = Integer.parseInt(comboSampling.getSelectedItem());
	    int samplingSize = radio8bit.getState() ? 8:16;
	    int channels = radioMono.getState()? 1:2;
	    af = new AudioFormat(AudioFormat.LINEAR, samplingRate, samplingSize,
				 channels);
	}

	if (checkVideo.getState()) {
	    String encoding = comboEncoding.getSelectedItem();
	    String strSize = comboSize.getSelectedItem();
	    StringTokenizer st = new StringTokenizer(strSize, "x");
	    int sizeX = Integer.parseInt(st.nextToken());
	    int sizeY = Integer.parseInt(st.nextToken());
	    Dimension size = new Dimension(sizeX, sizeY);

	    vf = new VideoFormat(encoding, size, Format.NOT_SPECIFIED, null, 15f);
	}

	// Use CaptureUtil to create a monitored capture datasource
	datasource = CaptureUtil.getCaptureDS(vf, af);
    System.out.println(datasource.getContentType());

	if (datasource != null) {
	    // Set the preferred content type for the Processor's output
	    outputType = "video.quicktime";
	    if (comboFileType.getSelectedItem().equals("AVI"))
		outputType = "video.x_msvideo";
	    FileTypeDescriptor ftd = new FileTypeDescriptor(outputType);
	    Format [] formats = null;

	    if (af != null && vf != null) {
		formats = new Format[] { new AudioFormat(null),
					 new VideoFormat(null) };
	    }

	    if (af == null)
		formats = new Format[] {new VideoFormat(null)};

	    ProcessorModel pm = new ProcessorModel(datasource, formats, ftd);
	    try {
		processor = Manager.createRealizedProcessor(pm);
	    } catch (Exception me) {
		System.err.println(me);
		// Make sure the capture devices are released
		datasource.disconnect();
		return;
	    }

	    // Get the monitor control:
	    // Since there are more than one MonitorControl objects
	    // exported by the DataSource, we get the specific one
	    // that is also the MonitorStream object.
	    MonitorControl mc = (MonitorControl)datasource.getControl("videocapture.MonitorStream");
	    if (mc != null) {
		monitor = mc.getControlComponent();
		panel1.add(monitor);
		// Make sure the monitor is enabled
		mc.setEnabled(true);
		pack();
	    }
	}
    }

    private void startCapture() {
	enableComponents(false);
	buttonStart.setLabel("Pause");
	buttonEnd.setEnabled(true);
        //String str2 = "vfw:Microsoft WDM Image Capture (Win32):0";
	// Get the processor's output, create a DataSink and connect the two.
	DataSource outputDS = processor.getDataOutput();
	try {
	    MediaLocator ml = new MediaLocator("file:capture/video" + SaEeDGroup.getPeerName() + "." + (outputType.equals("video.x_msvideo")? "avi" : "mov"));
            //MediaLocator ml = new MediaLocator(str2);
	    datasink = Manager.createDataSink(outputDS, ml);
	    datasink.open();
	    datasink.start();
	} catch (Exception e) {
	    System.err.println(e);
	}
	processor.start();
	System.out.println("Started saving...");
    }

    private void pauseCapture() {
	processor.stop();
	buttonStart.setLabel("Resume");
    }

    private void resumeCapture() {
	processor.start();
	buttonStart.setLabel("Pause");
    }

    private void stopCapture() {
	// Stop the capture and the file writer (DataSink)
	processor.stop();
	processor.close();
	datasink.close();
	processor = null;
	buttonEnd.setEnabled(false);
	// Restart monitoring
	startMonitoring();
	buttonStart.setLabel("Start");
	enableComponents(true);
	System.out.println("Done saving.");
    }


    private void enableComponents(boolean state) {
	comboFileType.setEnabled(state);
	comboEncoding.setEnabled(state);
	comboSize.setEnabled(state);
	checkAudio.setEnabled(state);
	comboSampling.setEnabled(state);
	radioMono.setEnabled(state);
	radioStereo.setEnabled(state);
	radio8bit.setEnabled(state);
	radio16bit.setEnabled(state);
	buttonExit.setEnabled(state);
    }


    void exit() {
	if (processor != null)
	    processor.close();
        this.setVisible(false);
 
        //Starting the Sharing Thread
        launchSharing.start();
    }

    private void fillGUI() {
	//comboFileType.add("QuickTime");
	comboFileType.add("AVI");

	comboEncoding.add("RGB"); // Add YUV, etc.
        comboEncoding.add("YUV");

	// Assume these sizes are available - might need modification
	// for certain capture cards / cameras.
	comboSize.add("160x120");
	comboSize.add("320x240");
	comboSize.add("640x480"); // Add more sizes if your card supports
	// Or detect them by getting a format list from the capture device info.

	// Assume these capture rates are available on sound card
	comboSampling.add("44100");
	comboSampling.add("22050");
	comboSampling.add("8000");

	// Initialize listeners
	checkAudio.addItemListener(this);
	comboSize.addItemListener(this);
	comboEncoding.addItemListener(this);
	comboSampling.addItemListener(this);
	comboFileType.addItemListener(this);
	radioStereo.addItemListener(this);
	radioMono.addItemListener(this);
	radio8bit.addItemListener(this);
	radio16bit.addItemListener(this);
	buttonStart.addActionListener(this);
	buttonEnd.addActionListener(this);

	// Create the datasource and processor
	startMonitoring();
    }

    public void itemStateChanged(ItemEvent sce) {
	Object source = sce.getSource();

	boolean enabled = checkAudio.getState();
	radioStereo.setEnabled(enabled);
	radioMono.setEnabled(enabled);
	radio8bit.setEnabled(enabled);
	radio16bit.setEnabled(enabled);
	comboSampling.setEnabled(enabled);

	if (source == comboSize || source == comboEncoding || source == comboSampling ||
	    source == radio16bit || source == radio8bit || source == radioMono ||
	    source == radioStereo || source == comboFileType) {

	    startMonitoring();
	}

    }

    public void actionPerformed(ActionEvent ae) {
	String action = ae.getActionCommand();
	if (action.equals("Start")) {
	    startCapture();
	} else if (action.equals("Pause")) {
	    pauseCapture();
	} else if (action.equals("Resume")) {
	    resumeCapture();
	} else if (action.equals("End")) {
	    stopCapture();
	} else if (action.equals("Exit")) {
	    exit();
	}
    }

    private void createGUI() {
        this.setTitle("VidCap");
        CheckboxGroup group1 = new CheckboxGroup();
        CheckboxGroup group2 = new CheckboxGroup();
        jPanel1.setLayout(verticalFlowLayout1);
        jLabel1.setText("File Format");
        checkVideo.setState(true);
        checkVideo.setEnabled(false);
        checkVideo.setLabel("Video");
        checkAudio.setState(true);
        checkAudio.setLabel("Audio");
        radio16bit = new Checkbox("16-Bit", group1, true);
        radio8bit = new Checkbox("8-Bit", group1, false);
        jPanel2.setLayout(gridLayout2);
        radioMono = new Checkbox("Mono", group2, false);
        radioStereo = new Checkbox("Stereo", group2, true);
        jPanel3.setLayout(gridLayout1);
        buttonStart.setLabel("Start");
        buttonExit.setLabel("Exit");
        buttonExit.addActionListener(this);
        buttonEnd.setEnabled(false);
        buttonEnd.setLabel("End");
        this.add(jPanel1, BorderLayout.WEST);
        jPanel1.add(jLabel1);
        jPanel1.add(comboFileType);
        jPanel1.add(checkVideo);
        jPanel1.add(comboEncoding);
        jPanel1.add(comboSize);
        jPanel1.add(checkAudio);
        jPanel1.add(comboSampling);
        jPanel1.add(jPanel3);
        jPanel3.add(radioMono, null);
        jPanel3.add(radioStereo, null);
        jPanel1.add(jPanel2);
        jPanel2.add(radio8bit, null);
        jPanel2.add(radio16bit, null);
        jPanel1.add(buttonStart);
        jPanel1.add(buttonEnd);
        jPanel1.add(buttonExit);
        this.add(panel1, BorderLayout.CENTER);

    }

//    public static void main(String[] args) {
//
//    }

}