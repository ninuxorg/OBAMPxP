/*    
    Copyright (C) 2007  Andrea Detti, Remo Pomposini, Roberto Zanetti
    This file is part of "Obamp Proxy",

    "Obamp Proxy" is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    "Obamp Proxy" is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with "Obamp Proxy"; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
    
*/

import java.awt.Button;
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.io.IOException;

import org.apache.log4j.Logger;


public class CollabTool extends Frame {
	protected static int id = 0;
	
	private static final Logger log =
		Logger.getLogger(CollabTool.class);
	
	protected Label label1, label2, label3, label4, label5, label6, label7, label8, label9, label10, label11, label12, label13, label14, label15;
    protected TextField textField1, textField2, tree1, tree2, tree3, tree4, tree5, tree6, tree7, tree8, tree9, tree10;
    protected TextArea outputArea1, outputArea2, outputArea3;
    protected Button button0, button1, button2, button3, button4, button5, button6;
    protected Checkbox sig_dump_box, enable_broadcast;
    protected Panel imagePanel;
    
    public Signalling sig;
    protected Stato state;
    protected DataManager dm;
    protected Image logo;
    protected Toolkit toolkit;
    protected Graphics graphics; 
    protected int number_of_click_button2;
    protected int number_of_click_button3;
    protected int number_of_click_button4;
    protected int number_of_click_button5;
	protected int local;
		    
  public CollabTool ( String arg[] ) throws IOException {
    super ("OBAMP Proxy");
    super.setResizable(true);
    toolkit = Toolkit.getDefaultToolkit();
    Interfaccia();
    number_of_click_button2 = 0;
    number_of_click_button3 = 0;
    number_of_click_button4 = 0;
    number_of_click_button5 = 0;
    local = 0;
  }
  
  public void update( Graphics g) {
	   paint(g);
	   }

  public void paint(Graphics g) {
	  //g.drawImage(logoRadiolabs, 300, 55, this);
	  g.drawImage(logo, 20, 635, this);
    }
 
  public void Interfaccia() {
	  		
	this.setLayout(null);
	this.setBackground(Color.decode("#D3D3D3"));//#CCFFFF
	
    try {
    	MediaTracker mt1 = new MediaTracker (this);
    	logo = Toolkit.getDefaultToolkit().createImage("./image/logo.gif");
        mt1.addImage(logo, 0);
		mt1.waitForID(0);
	} catch (InterruptedException e) {
		e.printStackTrace();
		log.info("Interrupted in Interfaccia()", e);
	}
    
	sig_dump_box = new Checkbox();
	sig_dump_box.setBounds(171,73,10,10);
	add(sig_dump_box);
	
	enable_broadcast = new Checkbox();
	enable_broadcast.setBounds(450,73,10,10);
	add(enable_broadcast);
	
	label3 = new Label("Control message dump");
    label3.setBounds(20, 70, 150, 15);
    this.add(label3);
    
	label9 = new Label("Channel:");
    label9.setBounds(20, 110, 60, 15);
    this.add(label9);
    
    label7 = new Label("Enable Broadcast");
    label7.setBounds(335, 70, 150, 15);
    this.add(label7);
    
    outputArea1 = new TextArea("", 0, 0, 1);
    outputArea1.setBounds(20, 130, 460, 250);
    this.add(outputArea1);
    
    label4 = new Label("Neighbors Table");
    label4.setBounds(20, 390, 150, 15);
    this.add(label4);
    
    outputArea2 = new TextArea("", 0, 0, 3);
    outputArea2.setBounds(20, 410, 460, 120);
    this.add(outputArea2);
    
    outputArea3 = new TextArea("", 0, 0, 1);
    outputArea3.setBounds(20, 555, 460, 70);
    this.add(outputArea3);
    
    label8 = new Label("Joined List");
    label8.setBounds(20, 535, 150, 15);
    this.add(label8);
    
    label5 = new Label("Core Id:"+"0");
    label5.setBounds(220, 40, 250, 15);
    this.add(label5);
    
    label6 = new Label("Parent Id:"+"0");
    label6.setBounds(220, 55, 250, 15);
    this.add(label6);
    
	button0 = new Button("Join");
    button0.setBounds(30, 40, 80, 25);
    this.add(button0);
    
    button1 = new Button("1");
    button1.setBounds(80, 107, 20, 20);
    this.add(button1);
    
    button2 = new Button("2");
    button2.setBounds(110, 107, 20, 20);
    this.add(button2);
    
    button3 = new Button("3");
    button3.setBounds(140, 107, 20, 20);
    this.add(button3);
    
    //button4 = new Button("4");
    //button4.setBounds(170, 107, 20, 20);
    //this.add(button4);
    
    //button5 = new Button("5");
    //button5.setBounds(200, 107, 20, 20);
    //this.add(button5);
    
	label11 = new Label("ON");
    label11.setBounds(78, 90, 30, 20);
    this.add(label11);
        
	label12 = new Label("OFF");
    label12.setBounds(108, 90, 30, 20);
    this.add(label12);
    
	label13 = new Label("OFF");
    label13.setBounds(138, 90, 30, 20);
    this.add(label13);
    
	//label14 = new Label("OFF");
    //label14.setBounds(168, 90, 30, 20);
    //this.add(label14);
	
    //label15 = new Label("OFF");
    //label15.setBounds(198, 90, 30, 20);
    //this.add(label15);
    
    setSize(500, 710);
    setVisible(true);
        
	}
	
//--------------------------------------------------

	public boolean handleEvent(Event event) {
		
        if (event.id == Event.WINDOW_DESTROY) {
        	setVisible(false);         // hide the Frame
            dispose();      // tell windowing system to free resources
            System.exit(0); // exit
            return true;
        }
        if (event.target == button0 && event.id == Event.ACTION_EVENT && sig==null) {
            button1_Clicked(event);
        } 
        if (event.target == button2 && event.id == Event.ACTION_EVENT) {
            button2_Clicked(event);
        } 
        if (event.target == button3 && event.id == Event.ACTION_EVENT) {
            button3_Clicked(event);
        } 
        if (event.target == button4 && event.id == Event.ACTION_EVENT) {
            button4_Clicked(event);
        } 
        if (event.target == button5 && event.id == Event.ACTION_EVENT) {
            button5_Clicked(event);
        } 
        return super.handleEvent(event);
    }
    
//--------------------------------------------------

	void button1_Clicked(Event event) {
     	try {
     	sig = new Signalling(enable_broadcast);
     	sig.setTextArea(outputArea1, outputArea2,outputArea3,label5,label6,sig_dump_box);
     	dm = new DataManager(this);
    	dm.setTextArea(outputArea1, enable_broadcast);
    	dm.setSignalling(sig);
    	dm.startThread();
        local = 1;
    	} catch (Exception ex) {
           		log.error("Error in button1_Clicked", ex);
        }
    	
    }
    
	void button2_Clicked(Event event) {
     	try {
     	if (local==1){	if (number_of_click_button2==0){
     			
     		    label12.setText("ON");
    			dm.StartChannelTwo();
    			number_of_click_button2++;
    			
     		}
     	}
    	} catch (Exception ex) {
    		log.error("Error in button2_Clicked", ex);
        }
    	
    }
	
	void button3_Clicked(Event event) {
     	try {
     		if (local==1){	
     		if (number_of_click_button3==0){
     			
     		    label13.setText("ON");
    			dm.StartChannelThree();
    			number_of_click_button3++;
    			
     		}
     		}
    	} catch (Exception ex) {
    		log.error("Error in button3_Clicked", ex);
        }
    	
    }
    
	void button4_Clicked(Event event) {
     	try {
     		if (local==1){	if (number_of_click_button4==0){
    			label14.setText("ON");
    			dm.StartChannelFour();
    			number_of_click_button4++;
    			
     		}
     		}
    	} catch (Exception ex) {
    		log.error("Error in button4_Clicked", ex);
        }
    	
    }
	
	
	void button5_Clicked(Event event) {
     	try {
     		if (local==1){	
     		if (number_of_click_button5==0){
    			label15.setText("ON");
    			dm.StartChannelFive();
    			number_of_click_button5++;
    			
     	}
     		}
    	} catch (Exception ex) {
    		log.error("Error in button4_Clicked", ex);
        }
    	
    }
	
	
	static public void main (String args[]) throws IOException { 
			Log4jInit.init();

			new CollabTool( args );
	}

}














