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
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;

import prominence.util.Queue;
import Multicast.OverlayNeighbor;

 
public class Signalling implements Runnable{
   
	protected Hashtable neighbors;
	protected Hashtable old_neighbors;
   	protected OverlayNeighbor nb;
   	protected Stato state;
    protected Thread exec;
  	protected TextArea outputArea;
   	protected TextArea outputArea2;
   	protected TextArea outputArea3;
   	protected Label label5,label6; 
   	TextField field1, field2, field3, field4, field5, field6, field7, field8, field9, field10;
   	   
   	protected TREE_CREATETimer ttimer;
   	protected IDR_Timer esiTimer;
   	protected pktTreeCreate forwardingTreeCreate;
   	//protected pktTreeCreate pendingTreeCreate;
   	protected pktTreeCreate lastTreeCreateSent;
   	protected InetAddress nullIP;
    protected long HELLO_INT;
    protected long JOIN_INT;
    protected long fast_HELLO_INT;
    protected long req_interval;
    protected long TCinterval;
        
    protected int TCflag;
    protected MulticastSignallingReceiver MSigRec;
    protected MulticastSignallingSender MSigSend;
    protected UnicastSignallingSender USigSend;
    protected UnicastSignallingReceiver USigRec;
    protected int sig_port;
    protected int broadcast_port;
    protected InetAddress multicast_address;
    protected InetAddress local_address;
    
    protected Queue q;
    private DB_Contest db;
    public boolean fastHelloActive;
    protected String signalling_use;
    protected long Alive_Hello_intervall;
    protected long Allowed_Alive_Hello_loss;
    protected long Allowed_Fast_Hello_loss;
    protected long Allowed_Hello_loss;
    protected long Allowed_Join_loss;
    protected refreshGUI rgui;
    protected Checkbox sig_dump_box;
    protected int TC_counter;
    protected int OUTER_TC_period;
    protected double Delay_Unit;
    protected Checkbox check;
    
    
    protected String cmd;
    protected String Operative_System;
    
    String[] mesh_list_vector;
    InetAddress[] mesh_list_vector_Inet;
    JoinedListEntry [] JoinedList;
    InetAddress[] MEMBER_LIST;
    byte myIPaddressbyte;    
   	
    public Signalling (Checkbox check_) throws Exception {
    	
        db = new DB_Contest("obamp.cfg");
        local_address = InetAddress.getByName(db.get("local_address"));
        sig_port = new Integer( db.get("signalling_port")).intValue();
    	broadcast_port = new Integer( db.get("multicast_signalling_port")).intValue();
    	multicast_address = InetAddress.getByName(db.get("multicast_address"));
    	req_interval = new Long( db.get("Join_Req_intervall")).longValue();
    	TCinterval = new Long( db.get("TC_intervall")).longValue();
    	HELLO_INT = new Long( db.get("Hello_intervall")).longValue();
    	JOIN_INT = new Long( db.get("Join_intervall")).longValue();
    	fast_HELLO_INT = new Long( db.get("Fast_Hello_intervall")).longValue();
    	Operative_System = new String (db.get("Operative_System")).toString();
    	Alive_Hello_intervall = new Long( db.get("Alive_Hello_intervall")).longValue();
        Allowed_Alive_Hello_loss = new Long( db.get("Allowed_Alive_Hello_loss")).longValue();
        Allowed_Fast_Hello_loss = new Long( db.get("Allowed_Fast_Hello_loss")).longValue();
        Allowed_Hello_loss = new Long( db.get("Allowed_Hello_loss")).longValue();
        Allowed_Join_loss = new Long( db.get("Allowed_Join_loss")).longValue();
        OUTER_TC_period = new Integer( db.get("OUTER_TC_period")).intValue();
        Delay_Unit = new Double( db.get("Delay_Unit")).	doubleValue() ;
        state = new Stato(local_address, sig_port);
        state.MAX_TTL = new Integer( db.get("Join_Req_max_TTL")).intValue();
    	state.hello_max_ttl = new Integer( db.get("Hello_max_TTL")).intValue();
        q = new Queue();
        neighbors = new Hashtable();
        old_neighbors = new Hashtable();
        
        check = check_;
        if (Operative_System.equals("Windows")){
        	cmd = "route print ";
        	}
        else if (Operative_System.equals("Linux")){
        	cmd = "route -n ";
        	}
        
        fastHelloActive = true;
        nullIP = InetAddress.getByName("0.0.0.0");
        forwardingTreeCreate = null;
                
        TCflag=0;
        signalling_use = "lock";
        TC_counter = 3;
        
        exec = new Thread (this, "SIGNALLING");
        exec.start ();
     
   }
   
   public void setStato (Stato state_) {
        state = state_;
   }
   
      
    public void setTextArea (TextArea area1, TextArea area2, TextArea area3, Label label5_, Label label6_, Checkbox sig_dump_box_) {
        outputArea = area1;
        outputArea2 = area2;
        outputArea3 = area3;
        label5=label5_;
        label6=label6_;
        sig_dump_box=sig_dump_box_;
    }
    
    boolean CHECK_SEQ_NUM(byte stateSN_, byte pktSN_){
        
        int stateSN = (int)stateSN_;
        if(stateSN<0)
        	stateSN = stateSN + 256;
        int pktSN = (int)pktSN_;
        if(pktSN<0)
        	pktSN = pktSN +256;
        if (Math.abs(pktSN-stateSN)>5){
            return true;
        }else
            if(stateSN < pktSN){
                return true;
            }else
                return false;
    }
   
    public void SetNeighborMinDistance(){
        
        state.NeighborMinDistance = 100;
        Enumeration e = neighbors.keys(); // get all keys stored in Hashtable 
    	while (e.hasMoreElements()) {
            Object key = e.nextElement(); // nextElement returns an Object
            OverlayNeighbor  nb = (OverlayNeighbor)neighbors.get(key); // nextElement returns an Object
            if(nb.distance>0){
                if(state.NeighborMinDistance > nb.distance){
                    state.NeighborMinDistance = nb.distance;
                }
            }
    	}
    }     
    
    public void node_map_insert(String addr, byte[] pkt_) {
        
    	int id = 0;
		for (int t=0; t<mesh_list_vector.length; t++){
					
			if(mesh_list_vector[t].equals(addr)){
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
        pkt_[ref_byte+6]=(byte)(pkt_[ref_byte+6]|(insert_char));
        return;
   	}

    public void node_map_insert_Join(String addr, byte[] pkt_) {
      int id = 0;
		for (int t=0; t<mesh_list_vector.length; t++){
					
			if(mesh_list_vector[t].equals(addr)){
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
        pkt_[ref_byte]=(byte)(pkt_[ref_byte]|(insert_char));
        return;
   	}
    
    
public void node_map_insert_Join(int  id, byte[] pkt_) {
        int ref_byte,ref_bit;
        byte insert_char=0x01;
        ref_byte=(int)id/8;
        ref_bit=id % 8;
        insert_char=(byte)(insert_char<<ref_bit);
        pkt_[ref_byte]=(byte)(pkt_[ref_byte]|(insert_char));
        return;
   	}
    

    
    
public void node_map_insert(int id_, byte[] pkt_) {
        int id = id_;
		int ref_byte,ref_bit;
        byte insert_char=0x01;
        ref_byte=(int)id/8;
        ref_bit=id % 8;
        insert_char=(byte)(insert_char<<ref_bit);
        pkt_[ref_byte+6]=(byte)(pkt_[ref_byte+6]|(insert_char));
        return;
   	}
   	
   	public boolean node_map_lookup(String addr, byte[] pkt_) {
   		
   		int id = 0;
		for (int t=0; t<mesh_list_vector.length; t++){
					
			if(mesh_list_vector[t].equals(addr)){
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
        if(((pkt_[ref_byte+6]) & (insert_char))==0x00) {
                return(false);
        } else {
                return(true);
        }
   	}
   	
public boolean node_map_lookup_Join(String addr, byte[] pkt_) {
   		int id = 0;
		for (int t=0; t<mesh_list_vector.length; t++){
					
			if(mesh_list_vector[t].equals(addr)){
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
        if(((pkt_[ref_byte]) & (insert_char))==0x00) {
                return(false);
        } else {
                return(true);
        }
   	}
   	
public boolean node_map_lookup_Join(int id, byte[] pkt_) {
		
    int ref_byte,ref_bit;
    byte insert_char=0x01;
    ref_byte=(int)id/8;
    ref_bit=(id) % (8);
    insert_char=(byte)(insert_char<<ref_bit);
    if(((pkt_[ref_byte]) & (insert_char))==0x00) {
            return(false);
    } else {
            return(true);
    }
	}

public boolean node_map_lookup(int id_, byte[] pkt_) {
   		
   		int id = id_;
		int ref_byte,ref_bit;
        byte insert_char=0x01;
        ref_byte=(int)id/8;
        ref_bit=(id) % (8);
        insert_char=(byte)(insert_char<<ref_bit);
        if(((pkt_[ref_byte+6]) & (insert_char))==0x00) {
                return(false);
        } else {
                return(true);
        }
   	}
   	
   	public void update_last_recv_time(InetAddress ip) {
   		
   		OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(ip);
   		if (nb==null) return;
		nb.last_pkt_recv_time = System.currentTimeMillis();
        
   	}

   	public void update_last_send_time(InetAddress ip) {
   		OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(ip);
   		if (nb==null) return;
		nb.last_pkt_sent_time = System.currentTimeMillis();

	}
 
   
 
        
public void sendHelloConf (byte SequenceNumber, InetAddress dest_addr, byte TTL) {
        
        pktHelloConf pktnew = new pktHelloConf(SequenceNumber, state.MyAddress, dest_addr, state.CoreAddress, TTL, this);
        byte[] data = pktnew.GetpktHelloConf();
        UnicastData usp = new  UnicastData(data,dest_addr);
        USigSend.q.add(usp);
        if (sig_dump_box.getState())  outputArea.append("send Hello-Conf to "+ dest_addr + "\n");
    }
   
  
    public double Get_Tree_Create_Delay(OverlayNeighbor nb){
        
    	double uniform = new Random().nextDouble(); 
     	double delay;
            
            if (this.Is_Nearest(nb.IPAddress)||nb.distance==1){
                delay = 0;
            }else{
                int dist = this.Get_Dist(nb.IPAddress);
                delay = (dist-1)*Delay_Unit+uniform*0.25;
    	}    
    	return delay; 
    }  
   	
    boolean Is_Nearest(InetAddress ip){
            
        OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(ip);
        double dist = 10000;
        Enumeration e = neighbors.keys(); // get all keys stored in Hashtable 
        while (e.hasMoreElements()) {
            Object key = e.nextElement(); // nextElement returns an Object
            OverlayNeighbor  value = (OverlayNeighbor)neighbors.get(key);
            if (1.0*value.distance+1e-3*value.IP<dist){
            	dist=1.0*value.distance+1e-3*value.IP;
            }
        }
        if (1.0*nb.distance+1e-3*nb.IP==dist){
            return true;
        }else{
            return false;
        }
   }
        
    int Get_Dist(InetAddress ip){
    	        
        OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(ip);
        if(nb.distance!=0){
            return nb.distance;
        }
        return 1000;
    }
 
    public void sendTreeCreate () {
    	
    	byte[] pktNodeMap = new byte[14];
    	node_map_insert(state.getAddressString(), pktNodeMap);
    	Enumeration e = neighbors.keys(); // get all keys stored in Hashtable 
        while (e.hasMoreElements()) {
        	Object key = e.nextElement(); // nextElement returns an Object    		
            OverlayNeighbor  value = (OverlayNeighbor)neighbors.get(key); // nextElement returns an Object
            if ((Is_Nearest(value.IPAddress) || value.distance==1) && value.CoreAddress.equals(state.CoreAddress)) node_map_insert(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), pktNodeMap); 
        }
 
    	boolean multicast_done = false;
    	int TCSN = (int)state.TreeCreateSequenceNumber;
    	if(TCSN<0)
            TCSN = TCSN + 256;
        TCSN = TCSN + 1;
        if(TCSN>255)
            TCSN = 1;
        state.TreeCreateSequenceNumber = (byte)TCSN;
    	
    	Enumeration en = neighbors.keys();
        while (en.hasMoreElements()) {
        	Object key = en.nextElement(); // nextElement returns an Object    		
            OverlayNeighbor  value = (OverlayNeighbor)neighbors.get(key); // nextElement returns an Object
            byte delay;
            if(check.getState()){  
            if (value.distance>1){
            	if(this.Is_Nearest(value.IPAddress)){
            		delay = 0;
            	}else{
            		delay = 1;
            	}
            	pktTreeCreate packet = new pktTreeCreate(state.TreeCreateSequenceNumber, state.MyAddress, state.CoreAddress, delay, pktNodeMap, this);
        		UnicastData uData = new UnicastData(packet.getData(), value.IPAddress);
        		USigSend.q.add(uData);
        		if (sig_dump_box.getState())  outputArea.append(state.TreeCreateSequenceNumber +" send Tree-Create to " + value.GetIPaddress() + "\n");
            }else{
            	if(value.distance==1){
            		delay = 0;
            		if (multicast_done == false){
            			pktTreeCreate packet = new pktTreeCreate(state.TreeCreateSequenceNumber, state.MyAddress, state.CoreAddress, delay, pktNodeMap, this);
            			MulticastData mData = new  MulticastData(packet.getData(), 1);
            			MSigSend.q.add(mData);
            			multicast_done = true;
            			if (sig_dump_box.getState())  outputArea.append(state.TreeCreateSequenceNumber +" send broadcast Tree-Create" + "\n");
            		}       
				}          	
            }
        }
            else {
            	if (value.distance>1){
                	if(this.Is_Nearest(value.IPAddress)){
                		delay = 0;
                	}else{
                		delay = 1;
                	}
                	pktTreeCreate packet = new pktTreeCreate(state.TreeCreateSequenceNumber, state.MyAddress, state.CoreAddress, delay, pktNodeMap, this);
            		UnicastData uData = new UnicastData(packet.getData(), value.IPAddress);
            		USigSend.q.add(uData);
            		if (sig_dump_box.getState())  outputArea.append(state.TreeCreateSequenceNumber +" send Tree-Create to " + value.GetIPaddress() + "\n");
                }
            	else{
            		if(value.distance==1){
                		delay = 0;
                		pktTreeCreate packet = new pktTreeCreate(state.TreeCreateSequenceNumber, state.MyAddress, state.CoreAddress, delay, pktNodeMap, this);
                		UnicastData uData = new UnicastData(packet.getData(), value.IPAddress);
                		USigSend.q.add(uData);
                		if (sig_dump_box.getState())  outputArea.append(state.TreeCreateSequenceNumber +" send Tree-Create to " + value.GetIPaddress() + "\n");  
    				}  
            	}
            	
            }
        }
        
        if ((TC_counter%OUTER_TC_period)==0) sendOuterTreeCreate();
        TC_counter ++;
    }  
    
    
    void sendOuterTreeCreate() {
    	
    	try {
    		
  		   	BufferedReader route_printb1 = new BufferedReader (new InputStreamReader(Runtime.getRuntime().exec(this.cmd).getInputStream()));
			String str_route_print1;
			
			while ((str_route_print1 = route_printb1.readLine()) !=null){
				if (str_route_print1.lastIndexOf("Metric") >=0 )
					break;
			}
			
			route_printb1.mark(1);
			
			
			  if (Operative_System.equals("Windows")){
				  while ((str_route_print1 = route_printb1.readLine()) !=null){
						if(str_route_print1.lastIndexOf("=")>=0){
							break;
						      }
						else{
							String addressIP = str_route_print1.trim();
							addressIP = addressIP.substring(0, addressIP.indexOf(' '));
							for(int h=0; h<mesh_list_vector.length; h++){
								if(mesh_list_vector[h].equals(addressIP)){
									
									//String metrica = str_route_print1.substring(70, str_route_print1.length());
									MEMBER_LIST[h]= InetAddress.getByName(addressIP);
									}
								else{
									continue;
								}
							}
						}
						
						
						}
		        }
		        else if (Operative_System.equals("Linux")){
		        	while ((str_route_print1 = route_printb1.readLine()) !=null){
	    				
    					String addressIP = str_route_print1.substring(0, str_route_print1.indexOf(' '));
    					for(int h=0; h<mesh_list_vector.length; h++){
							if(mesh_list_vector[h].equals(addressIP)){
								
								//String metrica = str_route_print1.substring(70, str_route_print1.length());
								MEMBER_LIST[h]= InetAddress.getByName(addressIP);
								}
							else{
								continue;
							}
						}
    				}
		        }
				
    	int IPcore = (int) state.getAddressInt(state.CoreAddress);
		
		for(int r=0 ;r<MEMBER_LIST.length; r++){
			if (MEMBER_LIST[r]!=null){
				
				int IP = (int) state.getAddressInt(MEMBER_LIST[r]);
				
				if(IP<IPcore && this.SenderISValid(MEMBER_LIST[r])&& !(MEMBER_LIST[r].equals(state.MyAddress))){
					this.sendOuterTreeCreate(MEMBER_LIST[r]);
				}
			}
			else{
				continue;
			}
		} 
		
			}
     
    	catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
    
    }

    
    
    void sendOuterTreeCreate(InetAddress destaddr){
    	
    	int TCSN = (int)state.TreeCreateSequenceNumber;
    	if(TCSN<0)
            TCSN = TCSN + 256;
        TCSN = TCSN + 1;
        if(TCSN>255)
            TCSN = 1;
        state.TreeCreateSequenceNumber = (byte)TCSN;
        byte[] pktNodeMap = new byte[14];
        byte[] pktJoinedList = new byte[8];
        
        //node_map_insert_Join(state.getAddressString(), pktJoinedList);
        
        for (int i=0; i<this.JoinedList.length ;i++){
        	if(this.JoinedList[i]!= null){
        		node_map_insert_Join(i, pktJoinedList);
        	}
        	else{
        		continue;
        	}
        
        }
        pktOuterTreeCreate pkt = new pktOuterTreeCreate(state.TreeCreateSequenceNumber, state.MyAddress, state.CoreAddress, (byte)0,pktNodeMap, pktJoinedList, this);
        byte pkt_[] = pkt.getData();
        pkt_[0]=14;
        pkt_[1]=127;
    	UnicastData uData = new UnicastData(pkt_, destaddr);
		USigSend.q.add(uData);
		if (sig_dump_box.getState()) outputArea.append(state.TreeCreateSequenceNumber +" send OUTER Tree-Create a " + destaddr + "\n");
    	
    }  

    
    void sendTREE_CREATE_NACK(InetAddress dest){
    	
    	int TCNSN = (int)state.TreeCreateNackSequenceNumber;
		if(TCNSN<0)
			TCNSN = TCNSN + 256;
		TCNSN = TCNSN + 1;
	    if(TCNSN>255)
	    	TCNSN = 1;
	    state.TreeCreateNackSequenceNumber = (byte)TCNSN;
    		
	    pktTreeCreateNack pkt = new pktTreeCreateNack(state.TreeCreateNackSequenceNumber, state.getIPAddress(), state.CoreAddress, this);
	    state.NACKStatus = true;
	    byte[] data = pkt.GetpktTreeCreateNack();
	    new IDR_Timer(this, data, dest, 4, 1);
	    UnicastData usp = new  UnicastData(data,dest);
	    USigSend.q.add(usp);
	    if (sig_dump_box.getState())  outputArea.append("send Tree-Create-Nack to "+ dest +"\n");
   }
    
    void retransmitTREE_CREATE_NACK(InetAddress dest, byte[] pkt_){
	   	
	    state.NACKStatus = true;
	    new IDR_Timer(this, pkt_, dest, 4, 1);
	    UnicastData usp = new  UnicastData(pkt_, dest);
	    USigSend.q.add(usp);
	    if (sig_dump_box.getState())  outputArea.append("send Tree-Create-Nack to "+ dest + "\n");
   }
   
   	void sendTreeCreateAck(InetAddress dest){
   		
   		int TCASN = (int)state.TreeCreateAckSequenceNumber;
		if(TCASN<0)
			TCASN = TCASN + 256;
		TCASN = TCASN + 1;
	    if(TCASN>255)
	    	TCASN = 1;
	    state.TreeCreateAckSequenceNumber = (byte)TCASN;
   		pktTreeCreateAck pkt = new pktTreeCreateAck(state.TreeCreateAckSequenceNumber, state.getIPAddress(), state.getIPCoreAddress(), this);
   		state.ACKStatus = true;
   		SET_TREE_LINK(dest);
   		byte[] data = pkt.GetpktTreeCreateAck();
   		new IDR_Timer(this, data, dest, 3, 1);
	    UnicastData usp = new  UnicastData(data,dest);
	    USigSend.q.add(usp);
	    if (sig_dump_box.getState())  outputArea.append((state.TreeCreateAckSequenceNumber) +" send Tree-Create-Ack to " + dest +"\n");
   	}
   	
   	void retransmitTreeCreateAck(InetAddress dest, byte[] pkt_){
   		
   		state.ACKStatus = true;
   		new IDR_Timer(this, pkt_, dest, 3, 1);
   		UnicastData usp = new  UnicastData(pkt_, dest);
	    USigSend.q.add(usp);
	    if (sig_dump_box.getState())  outputArea.append("send Tree-Create-Ack to " + dest +"\n");
   	}
   	
   	void sendTreeCreateConf(InetAddress dest, byte SN){
   	
   		pktTreeCreateConf pkt = new pktTreeCreateConf(SN, state.getIPAddress(), state.CoreAddress, this);
   		byte[] data = pkt.GetpktTreeCreateConf();
   		UnicastData usp = new  UnicastData(data,dest);
        USigSend.q.add(usp);
        if (sig_dump_box.getState())  outputArea.append(SN + " send Tree-Create-Conf to " + dest +"\n");
   	}
   	
   	void sendTreeCreateNackConf(InetAddress dest, byte SN){
   	
   		pktTreeCreateNackConf pkt = new pktTreeCreateNackConf(SN, state.getIPAddress(), state.CoreAddress, this);
   	    byte[] data = pkt.GetpktTreeCreateNackConf();
	    UnicastData usp = new  UnicastData(data,dest);
	    USigSend.q.add(usp);
       	if (sig_dump_box.getState())  outputArea.append(SN + " send Tree-Create-Nack-Conf to " + dest +"\n");
  	}
   
   
   	public void SET_TREE_LINK(InetAddress key){
            OverlayNeighbor value =  ( OverlayNeighbor )neighbors.get(key);
            value.istree = true;
            value.last_pkt_recv_time=System.currentTimeMillis();
   		 	
   		return;
   	} 
   
   	public void CLEAR_TREE_LINK(InetAddress key){
   		          
        if (!key.equals(state.MyAddress)){
            OverlayNeighbor value =  ( OverlayNeighbor )neighbors.get(key);
            value.istree = false;
            return;
        }
   	} 
   	
   	public void nb_insert(InetAddress id, InetAddress CoreAddress_) {
   		
   		byte ind [] = id.getAddress();
        byte SourceIP = ind [3];
   		if (neighbors.containsKey(id)) {
   			OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(id);
   			if (CoreAddress_.equals(state.CoreAddress)) {
   				nb.Texpire = System.currentTimeMillis()+(long)(1.5*Allowed_Hello_loss*HELLO_INT);
   			} else {
   				nb.Texpire = System.currentTimeMillis()+(long)(3.5*TCinterval);
   			}
   			nb.CoreAddress=CoreAddress_;
   			return;
   		}
   		if (old_neighbors.containsKey(id)){
   			OverlayNeighbor onb = (OverlayNeighbor)old_neighbors.get(id);
   			onb.distance = 4;
   			onb.Texpire = System.currentTimeMillis()+(long)(1.5*Allowed_Hello_loss*HELLO_INT);
   			onb.CoreAddress=CoreAddress_;
   			neighbors.put(id, onb);
   			old_neighbors.remove(id);
   			return;
   		}
   		OverlayNeighbor new_nb = new OverlayNeighbor(id);
   		new_nb.Texpire = System.currentTimeMillis()+(long)(1.5*Allowed_Hello_loss*HELLO_INT);
   		new_nb.CoreAddress=CoreAddress_;
   		new_nb.IP = SourceIP;
   		neighbors.put(id, new_nb);
 
   	}
   	
   	public boolean SenderISValid(InetAddress address){
   		if (old_neighbors!=null) {
			OverlayNeighbor nb_old = (OverlayNeighbor)old_neighbors.get(address);
			if(nb_old!=null)
				if ((System.currentTimeMillis()-nb_old.last_failure_time)<1.5*Allowed_Alive_Hello_loss*Alive_Hello_intervall) return false;
        }
   		return true;
   	}
  
    
   	public void receive (DatagramPacket p) {
   		            
        //try {
            byte[] packet = p.getData();     				
       		ByteArrayInputStream biStream = new ByteArrayInputStream( packet/*p.getData()*/, 0, p.getLength() ) ;
    		DataInputStream diStream = new DataInputStream( biStream ) ;
       		int MID = packet[0];//(int)diStream.readByte();
         	if(MID<0)
                    MID = MID+256;
         	switch (MID) {
                    case 5:  receiveTreeCreate(packet); break;
                    case 6:  receiveTreeCreateNak(diStream); break;
                    case 8:  receiveTreeCreateAck(diStream); break;
                    case 9:  receiveTreeCreateConf(diStream); break;
                    case 10: receiveTreeCreateNackConf(diStream); break;
                 // case 11: receiveHello(diStream); break;
                    case 12: receiveHelloConf(diStream);break;
                    case 14: receiveOuterTreeCreate(packet); break;
                    case 15: receiveJoin(packet); break;
                    
         	}
     	
 	}
   
  
  	      	 
	void recvTREE_CREATE(pktTreeCreate pkt){
		
		if (!pkt.IPCoreAddress.equals(state.CoreAddress) && !pkt.SourceIPAddress.equals(state.ParentId)){
			if (sig_dump_box.getState())  outputArea.append("discarded Tree-Create from " + pkt.IPCoreAddress + " different Core or Parent ID\n");
			return; 
		}
		if(!SenderISValid(pkt.SourceIPAddress)) return;
				
		OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress);
		
		if (nb==null){
			nb_insert(pkt.SourceIPAddress, pkt.IPCoreAddress);
			nb = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress);
			//nb.IP=pkt.SourceIP;
			if (pkt.IPCoreAddress.equals(state.CoreAddress)) nb.Texpire=System.currentTimeMillis()+100;
			
		}  
		nb.CoreAddress=pkt.IPCoreAddress;
		node_map_insert(state.getAddressString(), pkt.data);
		if(CORE_RESOLUTION(pkt)){
			if(CHECK_SEQ_NUM(pkt)){
				
		    	if(state.getTreeCreateSequenceNumber() == pkt.SequenceNumber){
		      		nb.Number_of_TCS = nb.Number_of_TCS + 1;
		      		return;
		      	}else{
                       
                    if (state.ACKStatus || state.NACKStatus){
                        //StorePacket(pkt);
		      			
		            }else{
		            	state.LastTreeCreateIn = pkt.SourceIPAddress;         	
		            	forwardingTreeCreate = pkt;
		            	nb.Number_of_TCS = nb.Number_of_TCS + 1;
			      		state.TreeCreateSequenceNumber = pkt.SequenceNumber;
		      			
                        if (state.ParentId.equals(pkt.SourceIPAddress)){
                        	
                            forwardTREE_CREATE(pkt);
      			
                        }else{
      				
                            sendTreeCreateAck(pkt.SourceIPAddress);	            	
            	
                        }
                    }
                }
			}else{
				if (sig_dump_box.getState())  outputArea.append("discarded Tree-Create from " + pkt.SourceIPAddress + " Sequence Number NOT VALID \n");
			}
		}else{
			if (sig_dump_box.getState())  outputArea.append("discarded Tree-Create from " + pkt.SourceIPAddress + " CORE_RESOLUTION \n");
		}
	}
		            
		          
	/*
	public void StorePacket(pktTreeCreate pkt){
		
		state.pendingPacket = true;
		pendingTreeCreate = pkt;
	
	}
	*/


    public void forwardTREE_CREATE(pktTreeCreate pkt){
    	
    	
    	
    	byte[] pktNodeMap = new byte[14];
    	byte[] pktdata = pkt.getData();
    	for (int element = 6; element < pktdata.length; element++){
    		pktNodeMap[element] = pktdata[element];
		}
    	node_map_insert(state.getAddressString(), pktNodeMap);
    	Enumeration e = neighbors.keys(); // get all keys stored in Hashtable 
        while (e.hasMoreElements()) {
        	Object key = e.nextElement(); // nextElement returns an Object    		
            OverlayNeighbor  value = (OverlayNeighbor)neighbors.get(key); // nextElement returns an Object
            if ((Is_Nearest(value.IPAddress) || value.distance == 1) && value.CoreAddress.equals(state.CoreAddress)) node_map_insert(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), pktNodeMap); 
        }
 
    	boolean multicast_done = false;
    	Enumeration en = neighbors.keys(); 
        while (en.hasMoreElements()) {		
        	Object key = en.nextElement(); // nextElement returns an Object
        	OverlayNeighbor  value = (OverlayNeighbor)neighbors.get(key); // nextElement returns an Object
        	if ((!node_map_lookup(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), pkt.getData()))) {
        		byte delay;
        		if(check.getState()){ 
                if (value.distance>1){
                	if(this.Is_Nearest(value.IPAddress)){
                		delay = 0;
                	}else{
                		delay = 1;
                	}
                	pktTreeCreate packet = new pktTreeCreate(pkt.SequenceNumber, state.MyAddress, pkt.IPCoreAddress, delay, pktNodeMap, this);
            		UnicastData uData = new UnicastData(packet.getData(), value.IPAddress);
            		USigSend.q.add(uData);
                	if (sig_dump_box.getState())  outputArea.append(pkt.SequenceNumber +" forward Tree-Create to " + value.GetIPaddress() + "\n");
                }else{
                	if(value.distance==1){
                		delay = 0;
                		if (multicast_done == false){
                			pktTreeCreate packet = new pktTreeCreate(pkt.SequenceNumber, state.MyAddress, pkt.IPCoreAddress, delay, pktNodeMap, this);
                			MulticastData mData = new  MulticastData(packet.getData(), 1);
                			MSigSend.q.add(mData);
                			multicast_done = true;
                   		 	if (sig_dump_box.getState())  outputArea.append(state.TreeCreateSequenceNumber +" forward Tree-Create" + "\n");
                		}       
    				}          	
                }
        	}
        		else {
        			if (value.distance>1){
                    	if(this.Is_Nearest(value.IPAddress)){
                    		delay = 0;
                    	}else{
                    		delay = 1;
                    	}
                    	pktTreeCreate packet = new pktTreeCreate(pkt.SequenceNumber, state.MyAddress, pkt.IPCoreAddress, delay, pktNodeMap, this);
                		UnicastData uData = new UnicastData(packet.getData(), value.IPAddress);
                		USigSend.q.add(uData);
                    	if (sig_dump_box.getState())  outputArea.append(pkt.SequenceNumber +" forward Tree-Create to " + value.GetIPaddress() + "\n");
                    }else{
                    	if(value.distance==1){
                    		delay = 0;
                    		pktTreeCreate packet = new pktTreeCreate(pkt.SequenceNumber, state.MyAddress, pkt.IPCoreAddress, delay, pktNodeMap, this);
                    		UnicastData uData = new UnicastData(packet.getData(), value.IPAddress);
                    		USigSend.q.add(uData);
                        	if (sig_dump_box.getState())  outputArea.append(pkt.SequenceNumber +" forward Tree-Create to " + value.GetIPaddress() + "\n");
                        }  	
                    }
        			
        		}
        	}	
        }
	}
 
        
    public void receiveTreeCreate (byte[] pkt_) {
   		
    	pktTreeCreate pkt = new pktTreeCreate(state.MyAddress);
    	pkt.SetpktTreeCreate(pkt_, this);
        
        OverlayNeighbor  value = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress); // nextElement returns an Object
        if (value==null){
			nb_insert(pkt.SourceIPAddress, pkt.IPCoreAddress);
			value = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress); // nextElement returns an Object
			//value.IP=pkt.SourceIP;
			if (pkt.IPCoreAddress.equals(state.CoreAddress))value.Texpire=System.currentTimeMillis()+100;
			//this.printHashTable();
		}
 
        double delayB;
        
        if (!this.Is_Nearest(value.IPAddress) && pkt.delay==1){
            delayB = this.Get_Tree_Create_Delay(value);
        } else {
            delayB = 0;
        }
        
       
        delayB = java.lang.Math.max(0.0, delayB);
        if (sig_dump_box.getState())  outputArea.append( pkt.SequenceNumber + " received Tree-Create from " + value.IPAddress + " delay = " + delayB + "\n");
        if (delayB>0) {
        	new IDR_Timer(this, pkt, 2, delayB);
        } else recvTREE_CREATE(pkt);
        
    }
    
    

	public void receiveOuterTreeCreate (byte[] pkt_) {
    	
    	if(!state.is_core){
    		pktOuterTreeCreate pkt = new pktOuterTreeCreate(state.MyAddress);
            pkt.SetpktOuterTreeCreate(pkt_, this);
    		if(!SenderISValid(pkt.SourceIPAddress)) return;
    		byte[] pktJoinedList = new byte[8];
    	   	byte[] pktdata = pkt.getData();
    	    for (int element = 0; element < pktJoinedList.length; element++){
    			pktJoinedList[element] = pktdata[element+14];
    			}
    		for (int t=0; t<mesh_list_vector.length; t++){
    			if((this.node_map_lookup_Join(t, pktJoinedList) || pkt.SourceIPAddress.equals(mesh_list_vector_Inet[t])) && SenderISValid(mesh_list_vector_Inet[t])){
    				this.JoinedList[t]= new JoinedListEntry("0",mesh_list_vector[t],(System.currentTimeMillis()+(long)(1.5*Allowed_Join_loss*JOIN_INT)), (byte)0);
    				//if (sig_dump_box.getState())  outputArea.append("Insert In Joined List " + mesh_list_vector[t] + "\n");
    			}
    			else{
    				
    				continue;
    			}
    		}
       
    		return;
    	}else{
    		
    		pktOuterTreeCreate pkt = new pktOuterTreeCreate(state.MyAddress);
            pkt.SetpktOuterTreeCreate(pkt_, this);
            if(!SenderISValid(pkt.SourceIPAddress)) return;
            byte[] pktJoinedList = new byte[8];
    	   	byte[] pktdata = pkt.getData();
    	   	
    		for (int element = 0; element < pktJoinedList.length; element++){
    			pktJoinedList[element] = pktdata[element+14];
    			}
            if (pkt.SourceIPAddress.equals(state.MyAddress)) return;
            if (sig_dump_box.getState())  outputArea.append("received Outer Tree-Create from " + pkt.SourceIPAddress + "\n");
            
           
            for (int t=0; t<mesh_list_vector.length; t++){
    			if((this.node_map_lookup_Join(t, pktJoinedList) || pkt.SourceIPAddress.equals(mesh_list_vector_Inet[t])) && SenderISValid(mesh_list_vector_Inet[t])){
    				this.JoinedList[t]= new JoinedListEntry("0",mesh_list_vector[t],(System.currentTimeMillis()+(long)(1.5*Allowed_Join_loss*JOIN_INT)), (byte)0);
    				//if (sig_dump_box.getState())  outputArea.append("Insert In Joined List " + mesh_list_vector[t] + "\n");
    			}
    			else{
    				
    				continue;
    			}
    		}
            
    		
    		byte[] pktTreeArray= new byte[14];
    		
    	    for(int r=0; r<14; r++){
            	pktTreeArray[r]= pktdata[r];
             }
            pktTreeCreate pktTree = new pktTreeCreate(state.MyAddress);
            pktTree.SetpktTreeCreate(pktTreeArray, this);
            
            
            if(CORE_RESOLUTION(pktTree)){
            	recvTREE_CREATE(pktTree);
            }else{
            	sendOuterTreeCreate(pkt.SourceIPAddress);
            	//TC_counter=1;
            }
            
    	}
    	
    }
   
    public void receiveTreeCreateNak (DataInputStream is) {
        
    	pktTreeCreateNack pkt = new pktTreeCreateNack(state.MyAddress);
        pkt.SetpktTreeCreateNack(is, this);
        if(!SenderISValid(pkt.SourceIPAddress)) return;
        OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress);
        if (nb==null) {
        	sendTreeCreateNackConf(pkt.SourceIPAddress, pkt.SequenceNumber);
        	
    		return;
    	}
        
        if(this.CHECK_SEQ_NUM(nb.last_nack_sn, pkt.SequenceNumber)|| nb.last_nack_sn==pkt.SequenceNumber){  
        	CLEAR_TREE_LINK(pkt.SourceIPAddress);
			nb.last_nack_sn=pkt.SequenceNumber;
			nb.CoreAddress=pkt.IPCoreAddress;
			sendTreeCreateNackConf(pkt.SourceIPAddress, pkt.SequenceNumber);
			
		}
       
         
                  
         if (sig_dump_box.getState())  outputArea.append("received Tree-Create-Nack from " + pkt.SourceIPAddress +"\n"); 
        
    }
	
	
    public void receiveTreeCreateAck (DataInputStream is) {
            
        pktTreeCreateAck pkt = new pktTreeCreateAck(state.MyAddress);
        pkt.SetpktTreeCreateAck(is, this);
        if(!SenderISValid(pkt.SourceIPAddress)) return;
        OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress);
        if (nb==null){
			nb_insert(pkt.SourceIPAddress, pkt.IPCoreAddress);
			nb = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress);
			//nb.IP=pkt.SourceIP;
			if (pkt.IPCoreAddress.equals(state.CoreAddress)) nb.Texpire=System.currentTimeMillis()+100;
		}
        
              
        if (CHECK_SEQ_NUM(nb.last_ack_sn, pkt.SequenceNumber) || nb.last_ack_sn==pkt.SequenceNumber){
        	SET_TREE_LINK(pkt.SourceIPAddress);
        	nb.last_ack_sn=pkt.SequenceNumber;
        	nb.CoreAddress=pkt.IPCoreAddress;
            sendTreeCreateConf(pkt.SourceIPAddress, pkt.SequenceNumber);
        }
        
        if (sig_dump_box.getState())  outputArea.append("received Tree-Create-Ack from " + pkt.SourceIPAddress +"\n"); 
   	
    }

        
	public void receiveTreeCreateConf (DataInputStream is) {
        
		pktTreeCreateConf pkt = new pktTreeCreateConf(state.MyAddress);
        pkt.SetpktTreeCreateConf(is, this);
        if(!SenderISValid(pkt.SourceIPAddress)) return;
        OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress);
        if (nb==null){
			nb_insert(pkt.SourceIPAddress, pkt.IPCoreAddress);
			nb = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress);
			//nb.IP=pkt.SourceIP;
			if (pkt.IPCoreAddress.equals(state.CoreAddress)) nb.Texpire=System.currentTimeMillis()+100;
			
		}  
        if (state.ACKStatus==false || !CHECK_SEQ_NUM(nb.last_conf_sn, pkt.SequenceNumber)) {
        	return;	
        }
        state.ACKStatus = false;
        //SET_TREE_LINK(pkt.SourceIPAddress);  
        nb.last_conf_sn = pkt.SequenceNumber;
        nb.CoreAddress=pkt.IPCoreAddress;
        if (sig_dump_box.getState())  outputArea.append("received Tree-Create-Conf from " + pkt.SourceIPAddress +"\n"); 
        
        if(state.ParentId.equals(pkt.SourceIPAddress)){
        	if (forwardingTreeCreate!=null) forwardTREE_CREATE(forwardingTreeCreate);
        	/*
            if (state.pendingPacket) {
            	if (forwardingTreeCreate!=null) forwardTREE_CREATE(forwardingTreeCreate);
            	recvTREE_CREATE(this.pendingTreeCreate);
            } else {
            	if (forwardingTreeCreate!=null) forwardTREE_CREATE(forwardingTreeCreate);
            }
            */
            return;
        }
		if(!state.ParentId.equals(nullIP)){
            sendTREE_CREATE_NACK(state.ParentId);
            return;
		} else {
            state.ParentId = pkt.SourceIPAddress;	
            if (forwardingTreeCreate!=null) forwardTREE_CREATE(forwardingTreeCreate);
            /*
            if (state.pendingPacket) {
            	recvTREE_CREATE(pendingTreeCreate);
            } else {
                if (forwardingTreeCreate!=null) forwardTREE_CREATE(forwardingTreeCreate);
            }
            */
            return;
		}
    }
	
    public void receiveTreeCreateNackConf (DataInputStream is) {
        
    	pktTreeCreateNackConf pkt = new pktTreeCreateNackConf(state.MyAddress);
        pkt.SetpktTreeCreateNackConf(is, this);
        if(!SenderISValid(pkt.SourceIPAddress)) return;
        OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress);
        if (nb==null){
			nb_insert(pkt.SourceIPAddress, pkt.IPCoreAddress);
			nb = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress);
			//nb.IP=pkt.SourceIP;
			if (pkt.IPCoreAddress.equals(state.CoreAddress)) nb.Texpire=System.currentTimeMillis()+100;
		}
        if (state.NACKStatus == false || !CHECK_SEQ_NUM(nb.last_nack_conf_sn, pkt.SequenceNumber)) {
        	return;	
        }
		state.NACKStatus = false;
		CLEAR_TREE_LINK(pkt.SourceIPAddress);
		nb.last_nack_conf_sn = pkt.SequenceNumber;
		nb.CoreAddress=pkt.IPCoreAddress;
		state.ParentId = state.LastTreeCreateIn;
		if (forwardingTreeCreate!=null) forwardTREE_CREATE(forwardingTreeCreate);
		/*
		if (state.pendingPacket) {
	            recvTREE_CREATE(pendingTreeCreate);
		} else {
			if (forwardingTreeCreate!=null) forwardTREE_CREATE(forwardingTreeCreate);
		}
		*/                      
        if (sig_dump_box.getState())  outputArea.append("received Tree-Create-Nack-Conf from " + pkt.SourceIPAddress +"\n"); 
   	
    }
	
	
    
        
    public void receiveHelloConf (DataInputStream is) {
        
        pktHelloConf pkt = new pktHelloConf(state.MyAddress);
        pkt.SetpktHelloConf(is, this);
        if(!SenderISValid(pkt.SourceIPAddress)) return;
        OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress);
        if (nb==null) {
        	nb_insert(pkt.SourceIPAddress, pkt.IPCoreAddress);
        	nb = (OverlayNeighbor)neighbors.get(pkt.SourceIPAddress);
        	//nb.IP=pkt.SourceIP;
        }
        
    	if(this.CHECK_SEQ_NUM(nb.hello_seq_num, pkt.SequenceNumber)){
            nb.hello_seq_num=pkt.SequenceNumber;
            nb.CoreAddress=pkt.IPCoreAddress;
            nb.distance=pkt.TTL;
            if (pkt.TTL==1){
            	if (pkt.IPCoreAddress.equals(state.CoreAddress)) nb.Texpire=System.currentTimeMillis()+((long)(1.5*Allowed_Fast_Hello_loss*fast_HELLO_INT));
            	fastHelloActive=true;
            }else{
            	if (pkt.IPCoreAddress.equals(state.CoreAddress)) nb.Texpire=System.currentTimeMillis()+((long)(1.5*Allowed_Hello_loss*HELLO_INT));
            }
        }
        state.hello_found_neighbour=true;
        if (sig_dump_box.getState())  outputArea.append("received Hello-Conf from " + pkt.SourceIPAddress +"\n"); 
   	
    }
    
    boolean CORE_RESOLUTION(pktTreeCreate pkt){
           
        if(state.getIPCoreAddress() == null){
           	state.setCoreAddress(pkt.IPCoreAddress);    
        	return true;
        }
		//converting in unsigned integer
		
        if (pkt.SourceIPAddress.equals(state.ParentId) && !state.getIPCoreAddress().equals(pkt.IPCoreAddress)) {
        	state.setCoreAddress(pkt.IPCoreAddress);
            state.is_core = false;    
            state.TreeCreateSequenceNumber = 0;
            return true;
        }
        
        int ip1 = state.getAddressInt(state.CoreAddress);
        int ip2 = state.getAddressInt(pkt.IPCoreAddress);
		
		if(ip1<ip2){// the minor core wins
	            return false;
		}
		if(ip1==ip2){// the minor core wins
	            return true;
		} else {
            // the minor core wins
            	    //to be inserted setup function
            state.setCoreAddress(pkt.IPCoreAddress);
            state.is_core = false;    
            state.TreeCreateSequenceNumber = 0;
            return true;
        }
    }

    boolean CHECK_SEQ_NUM(pktTreeCreate pkt){
        
        int stateTCSN = (int)state.TreeCreateSequenceNumber;
        if(stateTCSN<0)
            stateTCSN = stateTCSN + 256;
        int pktTCSN = (int)pkt.SequenceNumber;
        if(pktTCSN<0)
            pktTCSN = pktTCSN +256;
        //if (pktTCSN<3 && stateTCSN>253){
        if (Math.abs(pktTCSN-stateTCSN)>5.0) {
        	return true;
        }else
            if(stateTCSN <= pktTCSN){
                return true;
            }else
                return false;
    }
    
    

    void CLEAR_NUMBER_TREE_CREATE(){
        
        Enumeration e = neighbors.keys(); // get all keys stored in Hashtable 
    	while (e.hasMoreElements()) {
            Object key = e.nextElement(); // nextElement returns an Object    		
            OverlayNeighbor  value = (OverlayNeighbor)neighbors.get(key); // nextElement returns an Object
            value.ResetNumberTCS();
    	}
        return;
    }  

    
    
    public void receiveVirtualFastHello (String address_) throws UnknownHostException {
		
	    InetAddress address = InetAddress.getByName(address_);
	    if(!SenderISValid(address)) return;
	    if (sig_dump_box.getState())  outputArea.append("received Virtual-Fast-Hello from " + address + "\n"); 
	    if (!neighbors.containsKey(address) && !address.equals(state.MyAddress)){
	        nb_insert(address, state.CoreAddress);
	        OverlayNeighbor overlay_neighbor = (OverlayNeighbor)neighbors.get(address);
	        //overlay_neighbor.IP = pkt.SourceIP;
	        overlay_neighbor.Texpire = (System.currentTimeMillis()+(long)(1.5*Allowed_Fast_Hello_loss*fast_HELLO_INT));
	        overlay_neighbor.distance = 1;
	        //fastHelloActive=true;           
	        return;
	    }else if(neighbors.containsKey(address)){
	    	OverlayNeighbor nb = (OverlayNeighbor)neighbors.get(address);
	    	    nb.CoreAddress=state.CoreAddress;
	            nb.distance=1;
	            //fastHelloActive=true;
	            nb.Texpire = System.currentTimeMillis()+((long)(1.5*Allowed_Fast_Hello_loss*fast_HELLO_INT));
	             return;
	        }
	        
	    }
		
	
   public void receiveJoin (byte[] pkt_){
	
	pktJoin pkt = new pktJoin(state.MyAddress);
    pkt.SetpktJoin(pkt_, this);
    if(!SenderISValid(pkt.SourceIPAddress)) return;
    
    for (int t=0; t<mesh_list_vector.length; t++){
    		
		if(mesh_list_vector[t].equals(pkt.SourceIPAddress.toString().substring(1, pkt.SourceIPAddress.toString().length()))){
			
		if(JoinedList[t]!=null){
			if(CHECK_SEQ_NUM(JoinedList[t].JoinSequenceNumber , pkt.SequenceNumber)){
			
			this.JoinedList[t] = new JoinedListEntry("0",mesh_list_vector[t],(System.currentTimeMillis()+(long)(1.5*Allowed_Join_loss*JOIN_INT)),pkt.SequenceNumber);
			
			 
 			this.forwardJOIN(pkt_);
		    //if (sig_dump_box.getState())  outputArea.append("received Join from " + pkt.SourceIPAddress + "\n");
		    //if (sig_dump_box.getState())  outputArea.append("Insert in Joined List " + pkt.SourceIPAddress + "\n");
			}
			else{
				//if (sig_dump_box.getState())  outputArea.append("discarded Join from " + pkt.SourceIPAddress + " Sequence Number NOT VALID \n");
			}
		}
		else{
			this.JoinedList[t] = new JoinedListEntry("0",mesh_list_vector[t],(System.currentTimeMillis()+(long)(1.5*Allowed_Join_loss*JOIN_INT)),pkt.SequenceNumber);
			//if (sig_dump_box.getState())  outputArea.append("Insert (però) " + mesh_list_vector[t] + " in Joined List con Seq.Num " + pkt.SequenceNumber + "\n");
		}
		}	
		else{
			
			continue;
		}
		
   }
  }
    
   public void forwardJOIN(byte[] pkt_){
	   
	boolean multicast_done = false;
   	byte[] pktNodeMapJoinPacket = new byte[8];
   	byte[] pktNodeMapJoin = new byte[8];
   	int number_of_sent=0;
   	InetAddress[] AddressArray = new InetAddress[64];
   	
   	for (int element = 0; element < pktNodeMapJoinPacket.length; element++){
   		pktNodeMapJoinPacket[element] = pkt_[element+3];
   		pktNodeMapJoin[element] = pkt_[element+3];
		}
   	node_map_insert_Join(state.getAddressString(), pktNodeMapJoin);
   	node_map_insert_Join(state.getAddressString(), pktNodeMapJoinPacket);
   	
	
   	Enumeration e = neighbors.keys(); // get all keys stored in Hashtable 
       while (e.hasMoreElements()) {
       	Object key = e.nextElement(); // nextElement returns an Object    		
           OverlayNeighbor  value = (OverlayNeighbor)neighbors.get(key); // nextElement returns an Object
        if(check.getState()){   
        if((value.istree || value.distance==1) && SenderISValid(value.IPAddress)){
        	if (!node_map_lookup_Join(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), pktNodeMapJoinPacket)){
        	   
        		if(value.distance>1){
        		   node_map_insert_Join(value.getAddressString(), pktNodeMapJoin);
           		   number_of_sent++;
           		   AddressArray[number_of_sent-1]= value.IPAddress;
        		    }
        	   else{
        		   if(value.distance==1){
        			   node_map_insert_Join(value.getAddressString(), pktNodeMapJoin);
        			   if(multicast_done == false){
	            		multicast_done = true;
               			
	            		}
        			   
        		   }
        	   }
        	 
           }
         }
       }
        else{
        	if (value.istree && !node_map_lookup_Join(value.IPAddress.toString().substring(1, value.IPAddress.toString().length()), pktNodeMapJoinPacket)){
        		   node_map_insert_Join(value.getAddressString(), pktNodeMapJoin);
        		   number_of_sent++;
        		   AddressArray[number_of_sent-1]= value.IPAddress;
        	}
        	
        }
       }
       
       pktJoin packt = new pktJoin(state.MyAddress);
       packt.SetpktJoin(pkt_, this);
   	   pktJoin pkt = new pktJoin(packt.SequenceNumber, packt.SourceIPAddress, pktNodeMapJoin, this);
   	   
   	 if(multicast_done == true){
	    	MulticastData mData = new  MulticastData(pkt.getData(), 1);
			MSigSend.q.add(mData);
			if (sig_dump_box.getState())  outputArea.append("forward broadcast Join" + "\n");
	    }
	    
     for (int i=0;i<number_of_sent;i++) {
     	 UnicastData usp = new  UnicastData(pkt.getData(),AddressArray[i]);
			 USigSend.q.add(usp);
			 if (sig_dump_box.getState())  outputArea.append("forward Join to " + AddressArray[i] + "\n");
     }
   }

   
   	public void sendJoin(){
   		
   		boolean multicast_done = false;
   		
   		int number_of_sent=0;
    	
    	InetAddress[] AddressArray = new InetAddress[64];
    	
    	byte[] node_map_Join = new byte[8];
		node_map_insert_Join(state.getAddressString(), node_map_Join);
		
		        
		int JSN = (int)state.JoinSequenceNumber;
		if(JSN<0)
			JSN = JSN + 256;
		JSN = JSN + 1;
		if(JSN>255)
			JSN = 1;
		state.JoinSequenceNumber = (byte)JSN;
		
        Enumeration enu = neighbors.keys(); // get all keys stored in Hashtable 
	    while (enu.hasMoreElements()) {
	    	
	            Object keyu = enu.nextElement(); // nextElement returns an Object
	            OverlayNeighbor  valueu = (OverlayNeighbor)neighbors.get(keyu);
	            if(check.getState()){
	            if((valueu.istree || valueu.distance==1) && SenderISValid(valueu.IPAddress)){
	            	
	            	if(valueu.distance==1){
        				node_map_insert_Join(valueu.getAddressString(), node_map_Join);
	            		if(multicast_done == false){
	            		multicast_done = true;
                		}
	            	}
	            	else{	
	            	if(valueu.distance>1){
	            		node_map_insert_Join(valueu.getAddressString(), node_map_Join);
	            		number_of_sent++;
	            		AddressArray[number_of_sent-1]= valueu.IPAddress;
	            		
	            	}
	            	}
	            }
	            }
	            	else{
	            		if(valueu.istree){
		            		node_map_insert_Join(valueu.getAddressString(), node_map_Join);
		            		number_of_sent++;
		            		AddressArray[number_of_sent-1]= valueu.IPAddress;
		            		
		            	}
	            	}
	            	
	            }

	    
	    
	    pktJoin pkt = new pktJoin (state.JoinSequenceNumber, state.MyAddress ,node_map_Join, this);
	    
	    if(multicast_done == true){
	    	MulticastData mData = new  MulticastData(pkt.getData(), 1);
			MSigSend.q.add(mData);
			if (sig_dump_box.getState())  outputArea.append(state.JoinSequenceNumber + " send broadcast Join" + "\n");
	    }
	    
        for (int i=0;i<number_of_sent;i++) {
        	 UnicastData usp = new  UnicastData(pkt.getData(),AddressArray[i]);
			   USigSend.q.add(usp);
			   if (sig_dump_box.getState())  outputArea.append(state.JoinSequenceNumber + " send Join to " + AddressArray[i] + "\n");
        }
   	}
   
               	
   		
    
    
    
    
    
    public void Mesh_list_creator() throws IOException{
  	  
   	 
   	 int i=0;
   		FileReader obamp_nodes = new FileReader ("obamp_nodes.txt");
   		BufferedReader obamp_nodesb = new BufferedReader (obamp_nodes);
   		String str_obamp_nodes;
   		while ((str_obamp_nodes = obamp_nodesb.readLine()) !=null){
   			
   			i++;
   		}
   		
        obamp_nodes = new FileReader ("obamp_nodes.txt");
   		obamp_nodesb = new BufferedReader (obamp_nodes);
   		mesh_list_vector = new String[i];
   		JoinedList = new JoinedListEntry[i];
   		MEMBER_LIST = new InetAddress[i];
   		mesh_list_vector_Inet = new InetAddress[i];
   		   		
   		for (int t=0; t<i; t++){
             str_obamp_nodes = obamp_nodesb.readLine();
			 mesh_list_vector[t]= str_obamp_nodes;
   			}
         for (int t=0; t<i; t++){
           mesh_list_vector_Inet[t] = InetAddress.getByName(mesh_list_vector[t]);
   		}
   } 
    
    public InetAddress getAddresspkt(byte Address){
      	 
      	int id = (int) Address;
      	if(id<0){
      		id=id+256;
      	}
      	return mesh_list_vector_Inet[id];
      	
          }
   
    
    
    public byte getIAddresspkt(InetAddress Address){
   	 
   	 int id = 0;
   		for (int t=0; t<mesh_list_vector.length; t++){
   					
   			if(mesh_list_vector_Inet[t].equals(Address)){
   				break;
   			}
   			else{
   				id++;
   				continue;
   			}
   		}
       	return (byte)id;
       }
    
    
    public void route_print(String command) throws IOException{
    	
        Runtime.getRuntime().exec(command);
            
                    }
    
    
    
    public void run () {
    	
    	try {
    		
	    	USigRec = new UnicastSignallingReceiver(sig_port, state.MyAddress, this);
	    	USigSend = new UnicastSignallingSender(state.MyAddress, sig_port-1, sig_port, this);
			MSigRec = new MulticastSignallingReceiver(state.MyAddress, broadcast_port, multicast_address,this);
	    	MSigSend = new MulticastSignallingSender(state.MyAddress, broadcast_port, multicast_address);
	    	
	        this.Mesh_list_creator();
	        new JoinedTimer(this);
	        if (Operative_System.equals("Windows")){
	        	new FastHelloTimerWindows(this);
	        }
	        else if (Operative_System.equals("Linux")){
	        	new FastHelloTimerLinux(this);
	        }
	    	new TREE_CREATETimer(this);
	    	new HelloTimer(this);
	    	new nb_purge(this);
	    	
	        
	        rgui = new refreshGUI(this);
	    		    	
	    	DatagramPacket packet;
	    	while(true){
	    		SignallingElement spkt=(SignallingElement)q.remove();
	    		packet = spkt.dpacket;
	    		synchronized(signalling_use){
	    			if (old_neighbors!=null) {
	    				OverlayNeighbor nb_old = (OverlayNeighbor)old_neighbors.get(spkt.src);
	    				if(nb_old!=null) {
	    					if((System.currentTimeMillis()-nb_old.last_failure_time)>1.5*Allowed_Alive_Hello_loss*Alive_Hello_intervall) receive(packet); 
	    				} else receive(packet);	
	    			} else receive(packet);	
	    		}  		
	    	}
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

 }