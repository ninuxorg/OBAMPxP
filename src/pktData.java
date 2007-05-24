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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class pktData {
	
	
	//packet variables: 10 Bytes
    protected byte MessageID;
    protected byte DataSource;
    protected byte previousHop;
    protected byte[] Fired_Node_Map;
    
	protected int ID;
	
	protected byte[] msg;
	protected byte[] mess;
	protected byte[] message;  	
	
 	
 	//complementary variables 
    protected DatagramPacket dpacketRCV;
    protected DatagramPacket packetRTP;
    protected InetAddress SourceIPAddress;
    protected InetAddress prevHopIPAddress;
    protected byte[] bufferR;  
 	protected int port;  	  	
  	protected byte[] data;  
  	
  	
    public pktData(byte SequenceNumber_, InetAddress SourceIPAddress_) {
  		
  		MessageID = SequenceNumber_;
        byte ind [] = SourceIPAddress_.getAddress();
        this.DataSource = ind [3];
  		//DataSource = SourceIPAddress_;
  		
  		  		
  	}
        
  	public pktData(int SequenceNumber_, InetAddress SourceIPAddress_, byte[] data_) {
  		
  		ID = SequenceNumber_;
  		SourceIPAddress = SourceIPAddress_;
  		msg = data_;
  		  		
  	}
  	
  	public pktData() {
  		
  		ID = 0;
  	  		  		
  	}
  	
  	public byte[] GetMessage(){
  		
  		try{
  			ByteArrayOutputStream boStream = new ByteArrayOutputStream ();
        	DataOutputStream doStream = new DataOutputStream (boStream);
       		doStream.write (message);
        	mess = boStream.toByteArray ();	       	        	
        	
        	} catch (IOException ex) {
  			
  		}
  		return mess;
          	
  	}
  	
  	
  	//it sets the packet by an input stream
  	public void SetpktData(DatagramPacket pkt_, int app_port){//DataInputStream qI){
  		  		
		try{
                    
	        byte recvHDR[]= new byte [3];
	        byte recvPayload[]= new byte [pkt_.getLength()-recvHDR.length];
	        
	        System.arraycopy(pkt_.getData(), 0, recvHDR, 0, recvHDR.length);
	        System.arraycopy(pkt_.getData(), recvHDR.length, recvPayload, 0, recvPayload.length);
	        this.MessageID = recvHDR[0];
	        this.DataSource = recvHDR[1];
	        byte ind [] = InetAddress.getLocalHost().getAddress();
	        ind [3] = DataSource;
	        this.SourceIPAddress = InetAddress.getByAddress(ind);
	        this.previousHop = recvHDR[2];
	        byte prevInd [] = InetAddress.getLocalHost().getAddress();
	        prevInd [3] = previousHop;
	        this.prevHopIPAddress = InetAddress.getByAddress(prevInd);
	        this.packetRTP = new DatagramPacket (recvPayload, recvPayload.length, InetAddress.getLocalHost(), app_port);
	    		  		
  		 } catch (IOException ex) {
  		}
  		return;
        
    }
        
    	public void SetpktData(byte[] pkt_){//DataInputStream qI){
  		  		
		try{  		
		  		
		  	//byte[] SourceIP = {qI.readByte(),qI.readByte(),qI.readByte(),qI.readByte()};
		  	//ID = qI.readInt();		  		
		  	//SourceIPAddress = InetAddress.getByAddress(SourceIP);
		  	//message = qI.readUTF();
            ByteArrayInputStream biStream = new ByteArrayInputStream( pkt_ ) ;
            DataInputStream diStream = new DataInputStream( biStream ) ;
            byte[] SourceIP = {diStream.readByte(),diStream.readByte(),diStream.readByte(),diStream.readByte()};
            ID = diStream.readInt();
            diStream.readFully(mess);
            SourceIPAddress = InetAddress.getByAddress(SourceIP);
            //String mg = diStream.readUTF();
                    
		  	//ByteArrayOutputStream boStream = new ByteArrayOutputStream ();
        		//DataOutputStream doStream = new DataOutputStream (boStream);
       			//doStream.writeUTF (message);
        		//mess = boStream.toByteArray ();	
		  		
		  		
  		 } catch (IOException ex) {
  		}
  		return;
        
    }
  	
  	public byte[] GetpktData(DatagramPacket pkt_){
  		  		
  		try {  	
                    
            byte myadd[] = InetAddress.getLocalHost().getAddress();
            byte b_type[]= new byte [3];
            b_type[0] = this.MessageID;
            b_type[1] = this.DataSource;
            b_type[2] = myadd[3];
            
            this.bufferR = new byte [pkt_.getLength()+b_type.length];
            //byte recvHDR[]= new byte [2];
            //byte recvPayload[]= new byte [dpacketR_tmp.getLength()+b_type.length-2];
            
            
            System.arraycopy(b_type, 0, bufferR, 0, b_type.length);
		    System.arraycopy(pkt_.getData(), 0, bufferR, b_type.length, pkt_.getLength());
                    
                    //System.arraycopy(bufferR, 0, recvHDR, 0, recvHDR.length);
                    //System.arraycopy(bufferR, recvHDR.length, recvPayload, 0, recvPayload.length);
                    //dpacketRCV = new DatagramPacket( bufferR, bufferR.length ) ;
                    
                    //ByteArrayInputStream biStream = new ByteArrayInputStream( msg ) ;
                    //DataInputStream diStream = new DataInputStream( biStream ) ;
                    //String mg = diStream.readUTF();
                    /*
                    byte[] SourceIP = SourceIPAddress.getAddress();
  			  			
                    ByteArrayOutputStream boStream = new ByteArrayOutputStream ();
                    DataOutputStream doStream = new DataOutputStream (boStream);
                    doStream.writeByte(SourceIP[0]);
                    doStream.writeByte(SourceIP[1]);
                    doStream.writeByte(SourceIP[2]);
                    doStream.writeByte(SourceIP[3]);
                    doStream.writeInt(ID);
                    //doStream.writeUTF (mg);
                    doStream.write(msg);
        	       	        	
                    data = boStream.toByteArray ();
                    */
            } catch (IOException ex) {
               ex.printStackTrace ();
            }
        return bufferR;
        //return dpacketRCV;
        
    }
    
    public byte[] ForwardpktData(){
  		  		
  		try {  			
  			  			  			  			
  			byte[] SourceIP = SourceIPAddress.getAddress();
  			  			
  			ByteArrayOutputStream boStream = new ByteArrayOutputStream ();
        	DataOutputStream doStream = new DataOutputStream (boStream);
       		doStream.writeByte(SourceIP[0]);
        	doStream.writeByte(SourceIP[1]);
        	doStream.writeByte(SourceIP[2]);
        	doStream.writeByte(SourceIP[3]);
        	doStream.writeInt(ID);
        	doStream.write (mess);
        	       	        	
        	data = boStream.toByteArray ();
        	
        } catch (IOException ex) {
           		ex.printStackTrace ();
        }
        
        return data;
        
    }
  		
}