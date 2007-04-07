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
package Multicast;

import java.net.*;

public class OverlayNeighbor {
  
  public byte IP;
  public InetAddress IPAddress;
  public int port;
  public boolean istree;//it identifies if it is a link of tree
  public int Number_of_TCS;//if it is 0 i have not already receaved a tree create: anybody can't delete it 
  public double DelayBudget;
  public byte TCSN;  
  public long Texpire;
  public double PingInterval;
  public int distance; //distance in hops from the node
  //public byte distance; //distance in hops from the node
  public byte last_ack_sn;
  public byte last_nack_sn;
  public byte last_nack_conf_sn;
  public byte last_conf_sn;
  public byte hello_seq_num;
  public byte fast_hello_seq_num;
  public boolean is_my_nearest;
  public boolean fast_hello;
  public InetAddress CoreAddress;
  public long last_pkt_sent_time;
  public long last_pkt_recv_time;
  public long last_failure_time;
  
    
  public OverlayNeighbor (byte IP_, InetAddress IPAddress_, int porta, long Texpire_) {
  	
  	IP = IP_;
  	IPAddress = IPAddress_;
    port = porta;
    istree = false;
    Number_of_TCS = 0;
 	DelayBudget = 0;
 	TCSN = 0;
 	Texpire = Texpire_;
    PingInterval = 0;
    last_ack_sn = 0;
    last_nack_sn = 0;
    last_nack_conf_sn = 0;
    last_conf_sn = 0;
    distance = 4;
    hello_seq_num = 0;
    fast_hello_seq_num = 0;
    fast_hello = false;
    CoreAddress = null;
    last_pkt_sent_time = 0;
    last_pkt_recv_time = 0;
    last_failure_time = -1000;
 	
  }
    	  
  public OverlayNeighbor (byte IP_, InetAddress IPAddress_) {
	  	
	  	IP = IP_;
	  	IPAddress = IPAddress_;
	    istree = false;
	    Number_of_TCS = 0;
	 	DelayBudget = 0;
	 	TCSN = 0;
	 	last_ack_sn = 0;
	    last_nack_sn = 0;
	    last_nack_conf_sn = 0;
	    last_conf_sn = 0;
	    distance = 4;
	    hello_seq_num = 0;
	    fast_hello_seq_num = 0;
	    fast_hello = false;
	    last_pkt_sent_time = 0;
	    last_pkt_recv_time = 0;
	    last_failure_time = -1000;
	 	
	  }
  
  public OverlayNeighbor (InetAddress IPAddress_) {
	  	
	  	IPAddress = IPAddress_;
	    istree = false;
	    Number_of_TCS = 0;
	 	DelayBudget = 0;
	 	TCSN = 0;
	 	last_ack_sn = 0;
	    last_nack_sn = 0;
	    last_nack_conf_sn = 0;
	    last_conf_sn = 0;
	    distance = 4;
	    hello_seq_num = 0;
	    fast_hello_seq_num = 0;
	    fast_hello = false;
	    last_pkt_sent_time = 0;
	    last_pkt_recv_time = 0;
	    last_failure_time = -1000;
	 	
	  }
   
  public InetAddress GetIPaddress(){
    	return IPAddress;
  }
  
  public int GetPortNumber(){
    	return port;
  }
  
   
  public boolean istreeValue(){
    	return istree;
  }
    
  public void SetIPaddress(InetAddress IPaddress_){
	  IPAddress = IPaddress_;
  	
  }
  
  public void SetPortNumber(int port_){
    	port = port_;
  }
 
  public void Setistree(boolean value){
    	istree = value;
  }
  
  public String getAddressString (){
	  String addr = IPAddress.toString().substring(1, IPAddress.toString().length());
  	return addr;
  }
  
  
  public void ResetNumberTCS(){
    	Number_of_TCS = 0;
  }
  
  public void Reset() {
	  	istree = false;
	    Number_of_TCS = 0;
	 	TCSN = 0;
	 	last_ack_sn = 0;
	    last_nack_sn = 0;
	    last_nack_conf_sn = 0;
	    last_conf_sn = 0;
	    distance = 4;
	    hello_seq_num = 0;
	    fast_hello_seq_num = 0;
	    fast_hello = false;
	    last_pkt_sent_time = 0;
	    last_pkt_recv_time = 0;
	    last_failure_time = -1000;
  }
   
}