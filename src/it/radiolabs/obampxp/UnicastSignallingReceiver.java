package it.radiolabs.obampxp;
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
    Receiver di UDP packets
*/

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import prominence.util.Queue;

public class UnicastSignallingReceiver implements Runnable {
	
	protected Queue q; // TODO: add generic or remove if unused
	protected Thread exec;
	protected DatagramSocket dsocket;
	public int port;
	protected InetAddress LocalAddress;
	protected Signalling sig;
	
	
	
	
  public UnicastSignallingReceiver ( int porta, InetAddress LocalAddress_, Signalling sig_){
  	  		
  		port = porta;
  		LocalAddress = LocalAddress_;
  		sig=sig_;
  		exec = new Thread (this, "UNICASTSIGNALLINGRECEIVER");
        exec.start ();
  	  
  }	  
  
  
  public void run () {
  	
  		try {
  			
            DatagramSocket dsocket = new DatagramSocket(port, LocalAddress);
            dsocket.setReceiveBufferSize(65535);
            byte[] buffer_temp = new byte[ 65535 ] ;
            DatagramPacket dpacket_temp = new DatagramPacket( buffer_temp, buffer_temp.length ) ;
  
            while(true) {
            	dsocket.receive( dpacket_temp ) ;
            	byte buffer[] = new byte[ dpacket_temp.getLength()] ;
                System.arraycopy(buffer_temp,0,buffer,0,dpacket_temp.getLength());
                DatagramPacket dpacket =  new DatagramPacket(buffer, buffer.length );
                dpacket_temp.setLength(buffer_temp.length);
                
                synchronized(sig.signalling_use){
                	sig.update_last_recv_time(dpacket_temp.getAddress()); 
                	if (buffer[0]==100 && sig.sig_dump_box.getState()) sig.outputArea.append("received Alive Hello from " + dpacket_temp.getAddress() + "\n");
        		}
                if(buffer[0]!=100) sig.q.add(new SignallingElement(dpacket,dpacket_temp.getAddress()));
                
            }
  
    	} catch (IOException ex) {
      	ex.printStackTrace ();
    	}   
      
  }	
     
}

