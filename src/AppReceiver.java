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
/*
 * AppReceiver.java
 *
 * Created on 24 novembre 2005, 12.11
 */

/**
 *
 * @author Remo
 */
import java.net.*;
import java.io.*;
import java.awt.*;

import org.apache.log4j.Logger;

import prominence.util.Queue;
import Multicast.*;

public class AppReceiver implements Runnable {
	
	private static final Logger log = 
		Logger.getLogger(AppReceiver.class);
	
	protected Queue q;
	protected Thread exec;
	protected DatagramSocket dsocketR;
	protected DatagramPacket dpacketR;
	protected InetAddress LocalAddress;
	public int port;
	protected TextArea outputArea;
	protected MyStatus state;
	protected DataManager dm;
	protected int channelID;
	
	
  public AppReceiver ( int porta, InetAddress LocalAddress_ , int ChannelID_){//String args[] ) {
 
  	port = porta;
  	LocalAddress = LocalAddress_;
  	channelID = ChannelID_;
  	exec = new Thread (this, "APPRECEIVER");
  	exec.start ();
  	  
  }	  
  
  
   	
  public void setDataManager (DataManager dm_) {
     	this.dm = dm_;
     
   	} 
  
  public void setTextArea (TextArea area) {
     this.outputArea = area;
  }
    
    
    
  public void sendData(byte[] data){
      
  }
  
  static protected DatagramPacket buildPacket( InetAddress host, int port, byte[] data ) throws IOException {
        
        return new DatagramPacket (data, data.length, host, port);
  
  }
  
  public void run () {
  	
  		try {
  			

        	DatagramSocket dsocketR = new DatagramSocket(port, LocalAddress);
        	dsocketR.setReceiveBufferSize(65535);  
        	dsocketR.setBroadcast(false);
        	DatagramPacket dpacket_temp ;
        	byte buffer_temp[] = new byte[65535] ;
        	dpacket_temp = new DatagramPacket( buffer_temp, buffer_temp.length ) ;
        	
        	while(true) {   
        		      
                dsocketR.receive( dpacket_temp ) ;
                byte buffer[] = new byte[dpacket_temp.getLength()+11] ;
                System.arraycopy(buffer_temp,0,buffer,11,dpacket_temp.getLength());
                buffer[10] = (byte) channelID;
                DatagramPacket dpacket =  new DatagramPacket(buffer, buffer.length );
                dpacket_temp.setLength(buffer_temp.length);    
                
                if (log.isDebugEnabled())
    				log.debug("received new DatagramPacket: " + dpacket);
                
                dm.qApp.add(dpacket);
                
                Integer req = new Integer(1);
                dm.qReq.add(req);
                           	
        	}
  		  
    	} catch (IOException ex) {
    		// FIXME: bad exception handling
    		log.warn("failed to get socket on "+LocalAddress+":"+port, ex);
    	}   
      
  }	
     
}
