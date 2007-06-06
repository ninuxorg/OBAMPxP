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

import java.net.InetAddress;;

public class Stato /*implements Serializable*/ {
    
    InetAddress MyAddress;
    byte[] LocalAddress;
    int MyPort;
    InetAddress CoreAddress;
    int CorePort;
    boolean is_core;
    
    byte TreeCreateSequenceNumber;
    byte TreeCreateAckSequenceNumber;
    byte TreeCreateConfSequenceNumber;
    byte TreeCreateNackSequenceNumber;
    byte TreeCreateNackConfSequenceNumber;
    byte hello_seq_num;
    byte HelloConfSequenceNumber;
    byte JoinSequenceNumber;
    
    
    byte LastTCSequenceNumber;
    byte LastTCAckSequenceNumber;
    byte LastTCConfSequenceNumber;
    byte LastTCNackSequenceNumber;
    byte LastTCNackConfSequenceNumber;
    byte LastHelloSequenceNumber;
    byte LastHelloConfSequenceNumber;
    byte FastHelloSequenceNumber;
    
    InetAddress ParentId;//last tree create valid incoming
    InetAddress LastTreeCreateIn;//this is for ACK and NACK status
    InetAddress LVTCS;
    InetAddress myNearest;
    boolean ACKStatus;
    boolean NACKStatus;
    boolean pendingPacket;
    int NeighborMinDistance;    
    double TCI;//Tree Create Intervall
    boolean found_neighbour;
    boolean joinACK;
    int MAX_TTL;
    byte request_number;
    int req_last_ttl;
    long req_timeout1;
    
    int hello_req_last_ttl;
    boolean hello_found_neighbour;
    int hello_count;
    int hello_max_ttl;
    int hello_request_number;
    long hello_timeout1;
    long hello_timeout2;
    
   
    /**
     * Constructor
     * @param address network address to hold
     * @param port port number to hold
     * @throws Exception
     */
    public Stato( InetAddress address, int port ) throws Exception {
		TCI = 10.0;
		MyAddress = address;
		LocalAddress = MyAddress.getAddress();
		MyPort = port;
		is_core = true;
		CoreAddress = address;
		ACKStatus = false;
		NACKStatus = false;
		joinACK = false;
		LastTreeCreateIn = InetAddress.getByName("0.0.0.0");
		ParentId = InetAddress.getByName("0.0.0.0");
		pendingPacket = false;
        hello_req_last_ttl = 1;
        found_neighbour = false;
        hello_found_neighbour = false;
        hello_count = 0;
        hello_timeout1 = 0;
        hello_timeout2 = 0;
		//TreeCreateSequenceNumber = (byte)254;
        TreeCreateSequenceNumber = 0;
        TreeCreateAckSequenceNumber = 0;
        TreeCreateNackSequenceNumber = 0;
        hello_seq_num = 0;
        JoinSequenceNumber = 0;
        
        FastHelloSequenceNumber = 0;
        //MESH_LIST = new Nodes[0];
       		
    }

    protected Stato() {
	
    }

    /**
     * Get port
     * @return Port number 
     */
    public int getPort() {
		return this.MyPort;
    }

    /**
     * Set port.
     * @param port port number
     */
    public void setPort( int port ) {
		this.MyPort = port;
    }

    /**
     * Set Address.
     * @param address set the address this net address is pointing to
     */
    public void setAddress( InetAddress address ) {
		this.MyAddress = address;
    }

    /**
     * Get Address.
     * @return address
     */
    public InetAddress getIPAddress(  ) {
	return this.MyAddress;
    }
    
    public byte getAddress(  ) {
    	byte ind [] = MyAddress.getAddress();
        byte IP = ind [3];
    	return IP;
    }
    
    public String getAddressString(  ) {
    	String myaddress = MyAddress.toString().substring(1, MyAddress.toString().length());
        
    	return myaddress;
    }
    
    public void setCoreAddress( InetAddress address ) {
	this.CoreAddress = address;
    }
    
    public InetAddress getIPCoreAddress(  ) {
	return this.CoreAddress;
    }
    
    public byte getCoreAddress(  ) {
    	byte ind [] = CoreAddress.getAddress();
        byte IP = ind [3];
    	return IP;
    }
    
    public void setCorePort( int porta ) {
	this.CorePort = porta;
    }
    
    public int getCorePort(  ) {
	return this.CorePort;
    }
    
    public void setTreeCreateSequenceNumber( byte sn ) {
	this.TreeCreateSequenceNumber = sn;
    }
    
   	public int getTreeCreateSequenceNumber(  ) {
	return this.TreeCreateSequenceNumber;
    }
   	
   	public int getAddressInt(InetAddress Address) {
   		
    	byte ind [] = Address.getAddress();
    	int int0 = (int)ind[0];
    	
		if (int0<0) int0=int0+256;
		int int1 = (int)ind[1];
		if (int1<0) int1=int1+256;
		int int2 = (int)ind[2];
		if (int2<0) int2=int2+256;
		int int3 = (int)ind[3];
		if (int3<0) int3=int3+256;
		
		Integer i1 = new Integer(int0);
        Integer i2 = new Integer(int1);
        Integer i3 = new Integer(int2);
        Integer i4 = new Integer(int3);
        
        String IPString = i1.toString()+i2.toString()+i3.toString()+i4.toString();
        Integer IPint = new Integer (IPString);
    	return IPint.intValue();
    }
    

    /** To string */
    //public String toString() {
	//return this.address.toString() + ":" + (new Integer( port )).toString();
    //}

}
