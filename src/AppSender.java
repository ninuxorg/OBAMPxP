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
import java.net.InetAddress;
import prominence.util.*;
import java.net.DatagramSocket;
import java.net.DatagramPacket;

public class AppSender implements Runnable{
	
	protected Thread exec;
	protected Queue q ;
	private int appTXport ;
	private DatagramSocket application_socket;
	protected InetAddress LocalAddress;
	protected int socketPort;
	
	
	public AppSender (InetAddress LocalAddress_, int socketPort_, int appTXport_) {
		
		LocalAddress = LocalAddress_;
		socketPort = socketPort_;
		appTXport = appTXport_;
		q = new Queue();
		try {
			application_socket = new DatagramSocket(socketPort, LocalAddress);
			application_socket.setSendBufferSize(65535);
			application_socket.setBroadcast(false);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		exec = new Thread (this, "APPSENDER");
		exec.start ();
	}
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			DatagramPacket pkt = (DatagramPacket)q.remove();
			try {
				DatagramPacket pkt_to_send = new DatagramPacket(pkt.getData(),11,pkt.getLength()-11,LocalAddress,appTXport);
				application_socket.send(pkt_to_send);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	};
}
