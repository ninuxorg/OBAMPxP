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
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import prominence.util.Queue;


public class MulticastTreeDataReceiver implements Runnable {
	
	protected Queue q; // TODO: add generic or remove if unused
	protected Thread exec;
	protected MulticastSocket msocket;
	protected DatagramPacket dpack;
	private InetAddress LocalAddress; 
	public int port;
	protected DataManager dm;
	private InetAddress MulticastAddress; 
	
	
	
  public MulticastTreeDataReceiver ( InetAddress LocalAddress_, int porta ,InetAddress MulticastAddress_ ){
	  
	  LocalAddress = LocalAddress_;	
	  port = porta;
	  MulticastAddress = MulticastAddress_;	
	  exec = new Thread (this,"MULTICASTTREEDATARECEIVER");
	  exec.start ();
  	  
  }	  
  
   	
  public void setDataManager (DataManager dm_) {
     	this.dm = dm_;
     
   	}  
  
  public void run () {
  	
  		try {
  			MulticastSocket msocket = new MulticastSocket(port);
  			msocket.setInterface(LocalAddress);
        	msocket.setReceiveBufferSize(65535);
        	msocket.setLoopbackMode(true);
        	msocket.joinGroup(MulticastAddress);
        	DatagramPacket dpacket_temp ;
  			byte [] buffer_temp = new byte[65535] ;
  			dpacket_temp = new DatagramPacket( buffer_temp, buffer_temp.length ) ;
  			
  			while(true) {
  				        		
                msocket.receive( dpacket_temp ) ;
                byte buffer[] = new byte[ dpacket_temp.getLength()] ;
                System.arraycopy(buffer_temp,0,buffer,0,dpacket_temp.getLength());
                DatagramPacket dpacket =  new DatagramPacket(buffer, buffer.length ) ;
                dpacket_temp.setLength(buffer_temp.length);              
                
                dm.q.add(dpacket);
                
                Integer req = new Integer(1);
                dm.qReq.add(req);
                             	
        	}
            
        	
  		  
    	} catch (IOException ex) {
      	ex.printStackTrace ();
    	}   
      
  }	
     
}

