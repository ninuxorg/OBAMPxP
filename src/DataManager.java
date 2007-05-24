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

import java.awt.Checkbox;
import java.awt.TextArea;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Hashtable;

import prominence.util.Queue;
import Multicast.OverlayNeighbor;


public class  DataManager implements Runnable{

	protected Thread exec;
	protected Signalling sig;
	protected InetAddress myIP;
	protected TextArea outputArea;
	protected Hashtable nb;
	protected Hashtable dataSource;
	protected Hashtable cacheID;
	protected byte myID;
	protected Queue q;
	protected Queue qApp;
	protected Queue qReq;
	    
    protected UnicastTreeDataSender UnicastTreeDataSender_;
    protected AppSender AppSender_1;
    protected AppSender AppSender_2;
    protected AppSender AppSender_3;
    protected AppSender AppSender_4;
    protected AppSender AppSender_5;
    protected byte mIP;
    protected int dataport;
    protected int appRCVportChannelOne;
    protected int appTXportChannelOne;
    protected int appRCVportChannelTwo;
    protected int appTXportChannelTwo;
    protected int appRCVportChannelThree;
    protected int appTXportChannelThree;
    protected int appRCVportChannelFour;
    protected int appTXportChannelFour;
    protected int appRCVportChannelFive;
    protected int appTXportChannelFive;
    
    private byte ind [];
    private DB_Contest db;
    private UnicastTreeDataReceiver dataR;
    private AppReceiver appR_1;
    private AppReceiver appR_2;
    private AppReceiver appR_3;
    private AppReceiver appR_4;
    private AppReceiver appR_5;
    private rcvPKTid []dataCache_ChannelOne;
    private rcvPKTid []dataCache_ChannelTwo;
    private rcvPKTid []dataCache_ChannelThree;
    private rcvPKTid []dataCache_ChannelFour;
    private rcvPKTid []dataCache_ChannelFive;
    private int data_counter_Channel_One;
    private int data_counter_Channel_Two;
    private int data_counter_Channel_Three;
    private int data_counter_Channel_Four;
    private int data_counter_Channel_Five;
	private byte[] rcvPKT;	
	private byte[] previousHOP;
	private boolean ret;
	private CollabTool collab;
	
	private MulticastTreeDataSender MdataS;
	private MulticastTreeDataReceiver MdataR;
	private int multicast_data_port;
	private InetAddress multicast_data_address;
	protected Checkbox enable_broadcast;
	
	public DataManager (CollabTool collab_) throws Exception {
     
		collab = collab_;
        db = new DB_Contest("obamp.cfg");
        dataport = new Integer( db.get("data_port")).intValue();
    	appRCVportChannelOne = new Integer( db.get("app_rcv_port_channel_one")).intValue();
    	appTXportChannelOne = new Integer( db.get("app_tx_port_channel_one")).intValue();
    	appRCVportChannelTwo = new Integer( db.get("app_rcv_port_channel_two")).intValue();
    	appTXportChannelTwo = new Integer( db.get("app_tx_port_channel_two")).intValue();
    	appRCVportChannelThree = new Integer( db.get("app_rcv_port_channel_three")).intValue();
    	appTXportChannelThree = new Integer( db.get("app_tx_port_channel_three")).intValue();
    	appRCVportChannelFour = new Integer( db.get("app_rcv_port_channel_four")).intValue();
    	appTXportChannelFour = new Integer( db.get("app_tx_port_channel_four")).intValue();
    	appRCVportChannelFive = new Integer( db.get("app_rcv_port_channel_five")).intValue();
    	appTXportChannelFive = new Integer( db.get("app_tx_port_channel_five")).intValue();
    	multicast_data_port = new Integer (db.get("multicast_data_port")).intValue();
    	multicast_data_address = InetAddress.getByName(db.get("multicast_address"));
    	myIP = InetAddress.getByName(db.get("local_address"));
    	
    	q = new Queue();
     	qApp = new Queue();
     	qReq = new Queue();
     	
     	
     	dataCache_ChannelOne = new rcvPKTid[100];
     	dataCache_ChannelTwo = new rcvPKTid[100];
     	dataCache_ChannelThree = new rcvPKTid[100];
     	dataCache_ChannelFour = new rcvPKTid[100];
     	dataCache_ChannelFive = new rcvPKTid[100];
     	                                    
     	ind = myIP.getAddress();
        mIP = ind [3];
        previousHOP = myIP.getAddress();
        data_counter_Channel_One=0;
        data_counter_Channel_Two=0;
        data_counter_Channel_Three=0;
        data_counter_Channel_Four=0;
        data_counter_Channel_Five=0;
        
     	dataSource = new Hashtable();
     	
     	cacheID = new Hashtable();
     	myID = 0;
     	cacheID.put(new Integer(myID), new Long(System.currentTimeMillis()));
     	dataSource.put(new Byte(mIP), cacheID);
    	
        new purge_cacheID(this);
        
     	exec = new Thread (this, "DATAMANAGER");
     	//exec.setPriority(1);
     	exec.start ();
     
   	}
   	
   	public void setTextArea (TextArea area1, Checkbox enable_broadcast_) {
     	this.outputArea = area1;
     	enable_broadcast = enable_broadcast_;
     	
    }
 
	public void setSignalling (Signalling sig_) {
     	this.sig = sig_;
     
   	}
        
       
   	
   	public void setHash (Hashtable nb_) {
     	this.nb = nb_;
     
   	}
   	
   	
   	public void purge () {
   		
   		Enumeration e = dataSource.keys(); // get all keys stored in Hashtable 
    	while (e.hasMoreElements()) {
    		Object key = e.nextElement(); // nextElement returns an Object
    		Hashtable cache = (Hashtable)dataSource.get(key); // nextElement returns an Object
    		
    		Enumeration en = cache.keys(); // get all keys stored in Hashtable 
    		while (en.hasMoreElements()) {
    			Object id = en.nextElement(); // nextElement returns an Object
    			Long value = (Long)cache.get(id); // nextElement returns an Object
    			long timearrival = value.longValue();
    			if ((System.currentTimeMillis()-timearrival)>10000){
    				cache.remove(id);		
    			}
    		    	
    		}
    		
    	}
   	}
   	
   	public void node_map_insert(String addr, byte[] pkt_) {
   		
   		int id = 0;
		for (int t=0; t<sig.mesh_list_vector.length; t++){
					
			if(sig.mesh_list_vector[t].equals(addr)){
				break;
			}
			else{
				id++;
				continue;
			}
		}
   		int ref_byte,ref_bit;
        byte insert_char=0x01;
        ref_byte=(int)id/8;
        ref_bit=id % 8;
        insert_char=(byte)(insert_char<<ref_bit);
        pkt_[ref_byte+2]=(byte)(pkt_[ref_byte+2]|(insert_char));
        return;
   	}
   	
   	public boolean node_map_lookup(String addr, byte[] pkt_) {
       
   		int id = 0;
		for (int t=0; t<sig.mesh_list_vector.length; t++){
					
			if(sig.mesh_list_vector[t].equals(addr)){
				break;
			}
			else{
				id++;
				continue;
			}
		}
        int ref_byte,ref_bit;
        byte insert_char=0x01;
        ref_byte=(int)id/8;
        ref_bit=(id) % (8);
        insert_char=(byte)(insert_char<<ref_bit);
        if(((pkt_[ref_byte+2]) & (insert_char))==0x00) {
                return(false);
        } else {
                return(true);
        }
   	}
         	

	public boolean checkData(rcvPKTid id, byte ChannelID){
		
		ret = false;
		switch ((int)ChannelID){
		case 1: 	
			for (short i=0; i<100; i++) {
			rcvPKTid key = dataCache_ChannelOne[i];
			if (key!=null)
			if (key.sourceIP == rcvPKT[1] && key.sequence_number == rcvPKT[0]) {
				ret = true;
				break;
			}
			
		}
			break;
		case 2:
			for (short i=0; i<100; i++) {
				rcvPKTid key = dataCache_ChannelTwo[i];
				if (key!=null)
				if (key.sourceIP == rcvPKT[1] && key.sequence_number == rcvPKT[0]) {
					ret = true;
					break;
				}
				
			}
			break;
		case 3: 	
			for (short i=0; i<100; i++) {
			rcvPKTid key = dataCache_ChannelThree[i];
			if (key!=null)
			if (key.sourceIP == rcvPKT[1] && key.sequence_number == rcvPKT[0]) {
				ret = true;
				break;
			}
			
		}
			break;
		case 4: 	
			for (short i=0; i<100; i++) {
			rcvPKTid key = dataCache_ChannelFour[i];
			if (key!=null)
			if (key.sourceIP == rcvPKT[1] && key.sequence_number == rcvPKT[0]) {
				ret = true;
				break;
			}
			
		}
			break;
		case 5: 	
			for (short i=0; i<100; i++) {
			rcvPKTid key = dataCache_ChannelFive[i];
			if (key!=null)
			if (key.sourceIP == rcvPKT[1] && key.sequence_number == rcvPKT[0]) {
				ret = true;
				break;
			}
			
		}
			break;
		
		}
	
		return ret;
	}
		  	
   	
   	
    public void receiveData (DatagramPacket pkt_) {
    	
    	
    	rcvPKT = pkt_.getData();//new byte[];
    	forwardData(rcvPKT);
    	
    switch ((int)rcvPKT[10]){
    	
       case 1: //Channel ONE
    		
    		rcvPKTid pktID1 = new rcvPKTid(rcvPKT[0], rcvPKT[1]);
        	if (rcvPKT[1] == mIP) return;
        	if (checkData(pktID1,rcvPKT[10])){
        		return;
        	}else {
        		dataCache_ChannelOne[data_counter_Channel_One % 100]=pktID1;
        		//forwardData(rcvPKT);
        		AppSender_1.q.add(pkt_);
        	}
        	data_counter_Channel_One ++;
    	break;
    	
    	case 2://Channel TWO
    		if(collab.label12.getText().equals("ON")){
    		rcvPKTid pktID2 = new rcvPKTid(rcvPKT[0], rcvPKT[1]);
    	if (rcvPKT[1] == mIP) return;
    	if (checkData(pktID2,rcvPKT[10])){
    		return;
    	}else {
    		dataCache_ChannelTwo[data_counter_Channel_Two % 100]=pktID2;
    		//forwardData(rcvPKT);
    		AppSender_2.q.add(pkt_);
    	}
    	data_counter_Channel_Two ++;
    		}
    		break;
    		
     case 3: //Channel THREE
    	 if(collab.label13.getText().equals("ON")){
    		rcvPKTid pktID3 = new rcvPKTid(rcvPKT[0], rcvPKT[1]);
        	if (rcvPKT[1] == mIP) return;
        	if (checkData(pktID3,rcvPKT[10])){
        		return;
        	}else {
        		dataCache_ChannelThree[data_counter_Channel_Three % 100]=pktID3;
        		//forwardData(rcvPKT);
        		AppSender_3.q.add(pkt_);
        	}
        	data_counter_Channel_Three ++;
    	 }
    	break;
    	
 	case 4://Channel FOUR
 		if(collab.label14.getText().equals("ON")){
		rcvPKTid pktID4 = new rcvPKTid(rcvPKT[0], rcvPKT[1]);
	    if (rcvPKT[1] == mIP) return;
	    if (checkData(pktID4,rcvPKT[10])){
		return;
	    }else {
		dataCache_ChannelFour[data_counter_Channel_Four % 100]=pktID4;
		//forwardData(rcvPKT);
		AppSender_4.q.add(pkt_);
	    }
	    data_counter_Channel_Four ++;
 		}
		break;
    	
case 5://Channel FIVE
	if(collab.label15.getText().equals("ON")){
		rcvPKTid pktID5 = new rcvPKTid(rcvPKT[0], rcvPKT[1]);
	if (rcvPKT[1] == mIP) return;
	if (checkData(pktID5,rcvPKT[10])){
		return;
	}else {
		dataCache_ChannelFive[data_counter_Channel_Five % 100]=pktID5;
		//forwardData(rcvPKT);
		AppSender_5.q.add(pkt_);
	}
	data_counter_Channel_Five ++;
	}
		break;
    
    
    }
    		
    	
    }
    public void sendData (DatagramPacket packet) {
   			
    	UnicastData[] UnicastTreeDataSendArray = new UnicastData[64];
    	UnicastData[] MulticastTreeDataSendArray = new UnicastData[64];
    	
    	boolean multicast_done = false;
    	int number_of_sent=0;
    	int number_of_multicast_sent = 0;
    	
    	int dataID = (int)myID;
        if(dataID<0)
            dataID = dataID + 256;
        dataID = dataID + 1;
        if(dataID>255)
            dataID = 1;
        myID = (byte)dataID;
        
    	byte[] buffer = packet.getData();
        buffer[0] = myID;
        buffer[1] = mIP;
        node_map_insert(myIP.toString().substring(1, myIP.toString().length()), buffer);    
        
        synchronized(sig.signalling_use){
        
	    	Enumeration en = sig.neighbors.keys(); // get all keys stored in Hashtable 
	    	
	        while (en.hasMoreElements()) {
	    		Object key = en.nextElement(); // nextElement returns an Object
	    		OverlayNeighbor  value = (OverlayNeighbor)sig.neighbors.get(key); // nextElement returns an Object
	    		
	    		if(enable_broadcast.getState()){
	    		if (value.istree == true && value.distance>1){
	    			node_map_insert(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), buffer);   
	    			number_of_sent++;
	    			UnicastTreeDataSendArray[number_of_sent-1] = new  UnicastData(buffer,value.IPAddress);
	    			//if (sig.sig_dump_box.getState())  outputArea.append("Send Data to "+ value.IPAddress + "\n");
	    		} else {
	    			if (value.distance==1) {
	    				node_map_insert(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), buffer); 
	    				if (multicast_done==false) {
	    					number_of_multicast_sent++;
	    					MulticastTreeDataSendArray[number_of_multicast_sent-1] = new  UnicastData(buffer,multicast_data_address);
	    					//if (sig.sig_dump_box.getState())  outputArea.append("Send Broadcast Data"+ "\n");
	    					multicast_done=true;
	    				}
	    			}
	    		}
	    		}
	    		else if(value.istree){
	    			node_map_insert(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), buffer);   
	    			number_of_sent++;
	    			UnicastTreeDataSendArray[number_of_sent-1] = new  UnicastData(buffer,value.IPAddress);
	    		}
	        
	        
	        } //while
        }
        for (int i=0;i<number_of_multicast_sent;i++) {
        	MdataS.q.add(MulticastTreeDataSendArray[i]);
        	//if (sig.sig_dump_box.getState())  outputArea.append("Send Broadcast Data"+ "\n");
        }
        for (int i=0;i<number_of_sent;i++) {
        	UnicastTreeDataSender_.q.add(UnicastTreeDataSendArray[i]);
        }
    }
    
    public void forwardData (byte[] buffer) {
        
    	UnicastData[] UnicastTreeDataSendArray = new UnicastData[64];
    	UnicastData[] MulticastTreeDataSendArray = new UnicastData[64];
    	
    	int number_of_sent=0;
    	int number_of_multicast_sent=0;
    	boolean multicast_done = false;
    	
    	synchronized(sig.signalling_use){
    	
    		Enumeration en = sig.neighbors.keys(); // get all keys stored in Hashtable 
	    	while (en.hasMoreElements()) {
	            Object key = en.nextElement(); // nextElement returns an Object
	            OverlayNeighbor  value = (OverlayNeighbor)sig.neighbors.get(key); // nextElement returns an Object
	            
	            if(enable_broadcast.getState()){ 
	            
	            	if (value.istree==true && value.distance>1 && !node_map_lookup(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), buffer)){
	                	node_map_insert(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), buffer);   
	        			number_of_sent++;
	        			UnicastTreeDataSendArray[number_of_sent-1] = new  UnicastData(buffer ,value.IPAddress);
	        			//if (sig.sig_dump_box.getState())  outputArea.append("Forward Data to "+ value.IPAddress+ "\n");
	     			} else {
	     				if (value.distance==1 && !node_map_lookup(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), buffer)) {
	        				node_map_insert(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), buffer); 
	        				if (multicast_done==false) {
	        					number_of_multicast_sent++;
	        					MulticastTreeDataSendArray[number_of_multicast_sent-1] = new  UnicastData(buffer,multicast_data_address);
	        					//if (sig.sig_dump_box.getState())  outputArea.append("Forward Broadcast Data"+ "\n");
	        					multicast_done=true;
	        				}
	        			}
	     			}
	            
	            }
	            else 
	            	 if(value.istree && !node_map_lookup(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), buffer)){
		    			node_map_insert(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), buffer);   
		    			number_of_sent++;
		    			UnicastTreeDataSendArray[number_of_sent-1] = new  UnicastData(buffer,value.IPAddress);
		    		}
	            
	    	}//while
    	}
    	
    	for (int i=0;i<number_of_multicast_sent;i++) {
        	MdataS.q.add(MulticastTreeDataSendArray[i]);
        }
    	for (int i=0;i<number_of_sent;i++) {
        	UnicastTreeDataSender_.q.add(UnicastTreeDataSendArray[i]);
        	}
   }
    
    public void StartChannelTwo(){
    	appR_2 = new AppReceiver(appRCVportChannelTwo, myIP,2);
        appR_2.setDataManager(this);
        AppSender_2 = new AppSender(myIP, appRCVportChannelTwo-1, appTXportChannelTwo);
        
    }
    
    public void StartChannelThree(){
    	appR_3 = new AppReceiver(appRCVportChannelThree, myIP,3);
        appR_3.setDataManager(this);
        AppSender_3 = new AppSender(myIP, appRCVportChannelThree-1, appTXportChannelThree);
        
    }
    
    public void StartChannelFour(){
    	appR_4 = new AppReceiver(appRCVportChannelFour, myIP,4);
        appR_4.setDataManager(this);
        AppSender_4 = new AppSender(myIP, appRCVportChannelFour-1, appTXportChannelFour);
        
    }
    
    public void StartChannelFive(){
    	appR_5 = new AppReceiver(appRCVportChannelFive, myIP,5);
        appR_5.setDataManager(this);
        AppSender_5 = new AppSender(myIP, appRCVportChannelFive-1, appTXportChannelFive);
        
    }
    
	public void run () {
		
		dataR = new UnicastTreeDataReceiver(dataport, myIP, sig);//dataport);
    	dataR.setDataManager(this);
    	appR_1 = new AppReceiver(appRCVportChannelOne, myIP,1);
        appR_1.setDataManager(this);
        MdataR = new MulticastTreeDataReceiver(myIP, multicast_data_port, multicast_data_address);
        MdataR.setDataManager(this);    	
        UnicastTreeDataSender_ = new UnicastTreeDataSender(myIP, dataport-1, dataport, sig);
        AppSender_1 = new AppSender(myIP, appRCVportChannelOne-1, appTXportChannelOne);
        MdataS = new MulticastTreeDataSender(myIP, multicast_data_port);
		            
        while(true){
			
        	qReq.remove();
        	if (!q.isEmpty()) receiveData((DatagramPacket)q.remove());         	
        	if (!qApp.isEmpty()) sendData((DatagramPacket)qApp.remove());
        	
        	
			//if (q.isEmpty() && qApp.isEmpty()) {
		    	
			//}
			
		}	
   		
   }
	
	
}
