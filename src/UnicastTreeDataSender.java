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

import org.apache.log4j.Logger;

import prominence.util.Queue;

public class UnicastTreeDataSender implements Runnable{
	
	private static final Logger log =
		Logger.getLogger(UnicastTreeDataSender.class);
	
	protected Thread exec;
	protected Queue<UnicastData> q;
	
	private DatagramSocket unicast_tree_data_socket;
	private InetAddress LocalAddress;
	private int local_port;
	private int unicast_tree_data_port;
	private Signalling sig;
	
	public UnicastTreeDataSender (InetAddress LocalAddress_, int local_port_, int unicast_tree_data_port_, Signalling sig_) {
		q = new Queue<UnicastData>();
		LocalAddress = LocalAddress_;
		local_port = local_port_;
		unicast_tree_data_port=unicast_tree_data_port_;
		sig = sig_;
		try {
			unicast_tree_data_socket = new DatagramSocket(local_port, LocalAddress);
			unicast_tree_data_socket.setSendBufferSize(65535);
			unicast_tree_data_socket.setBroadcast(false);
		} catch (IOException ex) {
	      	ex.printStackTrace ();
		}
		exec = new Thread (this, "UNICASTTREEDATASENDER");
		exec.start ();
	};
	
	public void run() {
		while (true) {
			UnicastData utd = q.remove();
			DatagramPacket pkt = new DatagramPacket(utd.pbuffer,utd.pbuffer.length, utd.pdest, unicast_tree_data_port);
			synchronized(sig.signalling_use){
				sig.update_last_send_time(utd.pdest);
			}
			try {
				unicast_tree_data_socket.send(pkt);
			} catch (IOException e) {
				// FIXME: bad exception handling
				log.warn("I/O error while sending UDP packet", e);
			}
		}
		
	}
	
}
