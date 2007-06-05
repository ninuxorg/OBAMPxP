package it.radiolabs.obampxp.multicast;

import java.net.*;


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
    public String toString() {
	return this.address.toString() + ":" + (new Integer( port )).toString();
    }

}
