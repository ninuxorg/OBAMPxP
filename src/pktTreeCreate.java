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

import java.net.InetAddress;

public class pktTreeCreate {
	
	
	//packet variables: 14 Bytes
	protected byte MessageID;
    protected byte SourceIP;
	protected byte SequenceNumber;	
    protected byte CoreAddress;
    protected byte delay;
    protected byte TTL;
    protected byte[] Fired_Node_Map;
    	
    //complementary variables 
	protected InetAddress SourceIPAddress;	
	protected InetAddress IPCoreAddress;	
	protected byte[] address;
 	 	
  	protected byte[] data = new byte [14];  
  	
  	
  	public pktTreeCreate(InetAddress address_) {
  	  	MessageID = 5;	
  	  	address = address_.getAddress();
  	}
  	
  	public pktTreeCreate(pktTreeCreate pkt_) {
  	  	MessageID = 5;	
  	}
  	
        
    public pktTreeCreate(byte SequenceNumber_, InetAddress SourceIPAddress_, InetAddress IPCoreAddress_, byte delay_, byte[] node_map, Signalling agent) {
    	
    synchronized(agent.signalling_use){	
  		MessageID = 5;
  		SequenceNumber = SequenceNumber_;
  		SourceIPAddress = SourceIPAddress_;
  		IPCoreAddress = IPCoreAddress_;
  		delay = delay_;
  		TTL = 1;
                
        //byte ind [] = SourceIPAddress.getAddress();
        this.SourceIP = agent.getIAddresspkt(SourceIPAddress);
        //byte indC [] = this.IPCoreAddress.getAddress();
        this.CoreAddress = agent.getIAddresspkt(IPCoreAddress);

        data[0] = MessageID;
		data[1] = TTL;
		data[2] = this.SourceIP;
		data[3] = this.SequenceNumber;
		data[4] = this.CoreAddress;
		data[5] = delay;
		for (int element = 6; element < node_map.length; element++){
			data[element] = node_map[element];
		}

     }   
  	}
   	
  	//it sets the packet by an input stream
 
    public void SetpktTreeCreate(byte[] pkt, Signalling agent){
    	
    	synchronized(agent.signalling_use){	  		
		data = pkt;        
		//settings variables
		MessageID = 5;//pkt[0];
		TTL = pkt[1];
		SourceIP = pkt[2];
		SequenceNumber = pkt[3];
		CoreAddress = pkt[4];
		delay = pkt[5];
		            
		//byte ind [] = new byte[4];
		//ind[0] = address[0];
		//ind[1] = address[1];
		//ind[2] = address[2];
		//ind[3] = SourceIP;
		this.SourceIPAddress = agent.getAddresspkt(SourceIP);
		//byte indC [] = new byte[4];
		//indC[0] = address[0];
		//indC[1] = address[1];
		//indC[2] = address[2];
		//indC[3] = CoreAddress;
		this.IPCoreAddress = agent.getAddresspkt(CoreAddress);
  		
		return;
        
    }
    }
  	
  	public byte[] getData(){
  		
  		return data;
		
    }
  		
}