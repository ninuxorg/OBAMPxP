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

import java.net.*;

public class pktHelloAlive {
	
	
	//packet variables: 1 Byte
    protected byte SourceIP;
        	
 	//complementary variables 
    protected InetAddress SourceIPAddress;	
  	protected byte[] data = new byte[1];  
  	
  	public pktHelloAlive(InetAddress address_, Signalling agent) {
  		
  		synchronized(agent.signalling_use){	
  		SourceIPAddress = address_;
		//byte ind [] = SourceIPAddress.getAddress();
		this.SourceIP = agent.getIAddresspkt(SourceIPAddress);
		data[0] = SourceIP;	
  	}
  	}
  	
  	public byte[] GetpktHelloAlive(){
  		data[0]=100;
  		return data;  
    }
  		
}