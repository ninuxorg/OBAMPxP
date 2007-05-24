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
import java.net.DatagramSocket;
import java.net.InetAddress;

import prominence.util.Queue;


public class UnicastSignallingSender implements Runnable{ 
	
	protected Thread exec;
	protected DatagramSocket dsocket;
	protected InetAddress LocalAddress;
	protected int local_port;
	protected int remote_port;
	protected Queue q;     
	protected Signalling sig;

  public UnicastSignallingSender (InetAddress LocalAddress_, int local_port_, int remote_port_, Signalling sig_) throws IOException {
	  	
	  LocalAddress = LocalAddress_;	
	  local_port = local_port_;
	  remote_port = remote_port_;	// destination port
	  	sig = sig_;
	  	q = new Queue();
	  	try {
			dsocket = new DatagramSocket(local_port, LocalAddress);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  	
	  	exec = new Thread (this, "UNICASTSIGNALLINGSENDER");
	    exec.start();  
	  
	  }	

  public void run() {
	// TODO Auto-generated method stub
	while (true) {
		UnicastData utd = (UnicastData)q.remove();
		DatagramPacket pkt = new DatagramPacket(utd.pbuffer,utd.pbuffer.length, utd.pdest, remote_port);
		synchronized(sig.signalling_use){
			sig.update_last_send_time(utd.pdest);
		}
		try {
			dsocket.send(pkt);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
 }
  
}


