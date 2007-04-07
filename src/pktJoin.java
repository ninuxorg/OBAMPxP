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
import java.io.*;
import java.net.*;

import Multicast.*;
import prominence.util.Queue; 

public class pktJoin {
	
	
	//packet variables: 11 Bytes
    protected byte MessageID;
    protected byte SourceIP;
	protected byte SequenceNumber;	
    
        	
 	//complementary variables 
    protected InetAddress SourceIPAddress;	
	
	protected byte[] address;
 	protected byte[] data = new byte[11];  
  	
  	
  	public pktJoin(InetAddress address_) {
  	  	MessageID = 15;	
  	  	address = address_.getAddress();
  	  	
  	}
  	
  	
  	public pktJoin(byte SequenceNumber_, InetAddress SourceIPAddress_, byte[] node_map, Signalling agent) {
  		
  		synchronized(agent.signalling_use){
  		MessageID = 15;
        SequenceNumber = SequenceNumber_;
  		SourceIPAddress = SourceIPAddress_;
  		
                
        //byte ind [] = SourceIPAddress.getAddress();
        this.SourceIP = agent.getIAddresspkt(SourceIPAddress);
      
        data[0]= MessageID;
        data[1] = this.SourceIP;
		data[2] = this.SequenceNumber;
		
		for (int element = 0; element < node_map.length; element++){
			data[element+3] = node_map[element];
		}
		
  		  		  		
  	}
  }  	
  	  	
  	//it sets the packet by an input stream
  	public void SetpktJoin(byte[] pkt, Signalling agent){
  		
  		synchronized(agent.signalling_use){	
		data = pkt;       
		//settings variables		  		  		
		MessageID = 15;//15;
		SourceIP = pkt[1];
		SequenceNumber = pkt[2];
		
		                            
        //byte ind [] = new byte[4];
		//ind[0] = address[0];
		//ind[1] = address[1];
		//ind[2] = address[2];
		//ind[3] = SourceIP;
		this.SourceIPAddress = agent.getAddresspkt(SourceIP);
  		return;
        
    }
}

public byte[] getData(){
  		
  		return data;
		
    }
  		
}