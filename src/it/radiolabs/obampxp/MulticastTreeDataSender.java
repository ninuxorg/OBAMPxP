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

public class MulticastTreeDataSender implements Runnable{
	

	private static final Logger log = 
		Logger.getLogger(MulticastTreeDataSender.class);
	
	protected Thread exec;
	protected Queue<UnicastData> q;
	private MulticastSocket multicast_tree_data_socket;
	private int multicast_tree_data_port;
	private InetAddress LocalAddress;
	
	
	public MulticastTreeDataSender (InetAddress LocalAddress_, int multicast_tree_data_port_) {
		q = new Queue<UnicastData>();
		LocalAddress = LocalAddress_;
		multicast_tree_data_port = multicast_tree_data_port_;
		
		try {
			multicast_tree_data_socket = new MulticastSocket();
			multicast_tree_data_socket.setInterface(LocalAddress);
			multicast_tree_data_socket.setSendBufferSize(65535);
			multicast_tree_data_socket.setLoopbackMode(true);
			multicast_tree_data_socket.setTimeToLive(1);
		} catch (IOException ex) {
	      	ex.printStackTrace ();
		}
		exec = new Thread (this, "MULTICASTTREEDATASENDER");
		exec.start ();
	};
	
	public void run() {
		while (true) {
			UnicastData utd = q.remove();	// UnicastData format used also in case of multicast 
			DatagramPacket pkt = new DatagramPacket(utd.pbuffer,utd.pbuffer.length, utd.pdest, multicast_tree_data_port);
			try {
				multicast_tree_data_socket.send(pkt);
			} catch (IOException e) {
				log.warn("I/O error while sending UDP packet");
			}
		}
		
	}
	
}

