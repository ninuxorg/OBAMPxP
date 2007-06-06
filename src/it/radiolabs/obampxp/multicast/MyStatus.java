package it.radiolabs.obampxp.multicast;
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

/** Network address.   */
public class MyStatus {
    /** Host name */
    private InetAddress address;
    /** Host name */
    private int port;
    
    /**
     * Constructor
     * @param address network address to hold
     * @param port port number to hold
     * @throws Exception
     */
    public MyStatus( InetAddress address, int port ) throws Exception {
	this.address = address;
	this.port = port;
    }
    /**
     * Constructor
     * @param address network address to hold
     * @param port port number to hold
     * @throws Exception
     */
    public MyStatus( String address, int port ) throws Exception {
	this( InetAddress.getByName( address ), port );
    }
    /**
     * Constructor
     * @param address network address to hold
     * @param port port number to hold
     * @throws Exception
     */
    public MyStatus( String address, String port ) throws Exception  {
	this( InetAddress.getByName( address ), Integer.parseInt( port ) );
    }

    /** For serializability */
    protected MyStatus() {
	
    }

    /**
     * Get port
     * @return Port number 
     */
    public int getPort() {
	return this.port;
    }

    /**
     * Set port.
     * @param port port number
     */
    public void setPort( int port ) {
	this.port = port;
    }

    /**
     * Set Address.
     * @param address set the address this net address is pointing to
     */
    public void setAddress( InetAddress address ) {
	this.address = address;
    }

    /**
     * Get Address.
     * @return address
     */
    public InetAddress getAddress(  ) {
	return this.address;
    }

    /** To string */
    @Override
	public String toString() {
		return this.address.toString() + ":"
			+ (new Integer( port )).toString();
    }

}
