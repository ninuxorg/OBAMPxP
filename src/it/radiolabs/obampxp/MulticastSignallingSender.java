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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.apache.log4j.Logger;

import prominence.util.Queue;


public class MulticastSignallingSender implements Runnable{
	
	private static final Logger log = 
		Logger.getLogger(MulticastSignallingSender.class);
	
	protected Thread exec;
	protected MulticastSocket msocket;
	protected InetAddress LocalAddress;
	protected int port;
	protected InetAddress MulticastAddress;
	public Queue<MulticastData> q;

	public MulticastSignallingSender (InetAddress LocalAddress_, int port_, InetAddress MulticastAddress_){

		LocalAddress = LocalAddress_;
		port = port_;
		MulticastAddress = MulticastAddress_;
		q = new Queue<MulticastData>();
		try {
			msocket = new MulticastSocket();
			msocket.setInterface(LocalAddress);
			msocket.setLoopbackMode(true);
		} catch (IOException e) {
			// FIXME: bad exception handling
			log.warn("I/O error while setting up multicasting socket",
					e);
		}

		exec = new Thread(this, "MULTICASTSIGNALLINGSENDER");
		exec.start ();

	}	  

	public void run() {
		while (true) {
			try {
				MulticastData msp = q.remove();
				DatagramPacket pkt = new DatagramPacket(
						msp.buffer,msp.buffer.length, MulticastAddress, port);
				msocket.setTimeToLive(msp.ttl);
				msocket.send(pkt);
			} catch (IOException e) {
				// FIXME: bad exception handling
				log.warn("I/O error while sending on a multicasting " +
						"socket", e);
			}
		}
	}
}
