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

import java.awt.TextArea;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastSignallingReceiver implements Runnable {
		
	protected Thread exec;
	protected MulticastSocket msocket;
	protected Signalling sig;
	protected InetAddress LocalAddress;
	protected int port;
	protected InetAddress MulticastAddress;
	protected TextArea outputArea;
	
	
	
	public MulticastSignallingReceiver (InetAddress LocalAddress_, int port_, InetAddress MulticastAddress_,Signalling sig_){
		  
		LocalAddress = LocalAddress_;
  		port = port_;
  		MulticastAddress = MulticastAddress_;
  		sig = sig_;
  		exec = new Thread (this, "MULTICASTSIGNALLINGRECEIVER");
        exec.start ();
  	  
	}	  
   
  	public void run () {
  		
  		try {
	        msocket = new MulticastSocket(port);
	        msocket.setInterface(LocalAddress);
	        msocket.setReceiveBufferSize(65535);   
            msocket.joinGroup(MulticastAddress);
            msocket.setLoopbackMode(true);
            byte buffer_temp[] = new byte[65535] ;
            DatagramPacket dpacket_temp = new DatagramPacket( buffer_temp, buffer_temp.length ) ;
         
            while(true) {
            	msocket.receive( dpacket_temp ) ;
            	byte buffer[] = new byte[ dpacket_temp.getLength()] ;
                System.arraycopy(buffer_temp,0,buffer,0,dpacket_temp.getLength());
                DatagramPacket dpacket =  new DatagramPacket(buffer, buffer.length );
                dpacket_temp.setLength(buffer_temp.length);
                sig.q.add(new SignallingElement(dpacket,dpacket_temp.getAddress()));
                
		    }
        } catch (IOException ex) {
        	ex.printStackTrace ();
    	}   
      
  	}	
     
}