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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;

public class pktTreeCreateNackConf {
	
	//packet variables: 3 Bytes
    protected byte MessageID;
    protected byte SourceIP;
    protected byte CoreAddress;
	protected byte SequenceNumber;	
        
	//complementary variables 
    protected InetAddress SourceIPAddress;	
	protected InetAddress MulticastIPAddress;
  	protected InetAddress IPCoreAddress;
  	protected byte[] address;
 	protected int port;  	  	
  	protected byte[] data;

  	
  	public pktTreeCreateNackConf(InetAddress address_) {
  	  	MessageID = 10;	
  	  	address = address_.getAddress();
  	}
  	
  	public pktTreeCreateNackConf(byte SequenceNumber_, InetAddress SourceIPAddress_, InetAddress IPCoreAddress_, Signalling agent) {
  		
  		synchronized(agent.signalling_use){	
  		MessageID = 10;
  		SequenceNumber = SequenceNumber_;
  		SourceIPAddress = SourceIPAddress_;
  		IPCoreAddress = IPCoreAddress_;
  		
                
           //  	byte ind [] = SourceIPAddress.getAddress();
	        this.SourceIP = agent.getIAddresspkt(SourceIPAddress);
        //byte indC [] = IPCoreAddress.getAddress();
        this.CoreAddress = agent.getIAddresspkt(IPCoreAddress);
	  		  		
  		  		
  	}
  	}
  	//it sets the packet by an input stream
  	public void SetpktTreeCreateNackConf(DataInputStream qI, Signalling agent){
  		
  		synchronized(agent.signalling_use){	
		try{
                    
                    //settings variables		  		  		
                    MessageID = qI.readByte();//10;
                    SourceIP = qI.readByte();
                    CoreAddress = qI.readByte();
                    SequenceNumber = qI.readByte();
                                                    
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
		  				  		
  		 } catch (IOException ex) {
  			
  		}
  		return;
        
    }
  	}
    
    public byte[] GetpktTreeCreateNackConf(){
  		
  		
  		try {
                    
                    ByteArrayOutputStream boStream = new ByteArrayOutputStream ();
                    DataOutputStream doStream = new DataOutputStream (boStream);
                    doStream.writeByte(MessageID);
                    doStream.writeByte(SourceIP);
                    doStream.writeByte(CoreAddress);
                    doStream.writeByte(SequenceNumber);       
                    
                    data = boStream.toByteArray ();
  			
                } catch (IOException ex) {
                    ex.printStackTrace ();
                }
        
        return data;
        
    }
      	  	  		
}