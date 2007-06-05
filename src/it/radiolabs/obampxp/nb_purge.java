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

import it.radiolabs.obampxp.multicast.*;

import java.net.InetAddress;
import java.util.*;

public class nb_purge {
    
    Timer timer;
    Signalling agent;
		
	
    public nb_purge(Signalling agent_) {
		
		agent = agent_;
        timer = new Timer();
        timer.schedule(new nb_purgeTask(),
	               (long)(0.75*agent.fast_HELLO_INT),    //initial delay
	               (long)(0.75*agent.fast_HELLO_INT));  //subsequent rate
	               
	    
    }
    
    public void handle () {
        
        synchronized (agent.signalling_use) {
    	long now = System.currentTimeMillis();
        if (agent.neighbors!=null){
            Enumeration<InetAddress> e = agent.neighbors.keys(); // get all keys stored in Hashtable 
            while (e.hasMoreElements()) {
            	Object key = e.nextElement(); // nextElement returns an Object
            	OverlayNeighbor  nb = agent.neighbors.get(key); // nextElement returns an Object
    			if((nb.istree && (System.currentTimeMillis()-nb.last_pkt_recv_time)>1.5*agent.Allowed_Alive_Hello_loss*agent.Alive_Hello_intervall)){
		    	
    			/****FAILURE PROCEDURE****/
    			
	    			if(agent.state.ACKStatus){
	    				if (nb.IPAddress.equals(agent.state.LastTreeCreateIn)) {
    						//faliure on the acking node
		    				agent.state.ACKStatus = false;
		    				InetAddress oldLastTreeCreateIn = agent.state.LastTreeCreateIn;
		    				agent.state.LastTreeCreateIn=agent.nullIP;
		    				agent.forwardingTreeCreate=null;
		    				nb.Reset();
		    				nb.last_failure_time=now;
		    				agent.old_neighbors.put(nb.IPAddress, nb);
		    				agent.neighbors.remove(key);
		    				
		    				for (int i=0; i< agent.mesh_list_vector_Inet.length;i++){
		    			    	if(agent.JoinedList[i] != null){
		    			    		if(agent.mesh_list_vector_Inet[i].equals(nb.IPAddress)){
		    			    			
		    			    			agent.JoinedList[i] = null;
		    			    			//if (agent.sig_dump_box.getState())  agent.outputArea.append("Purged (1) from Joined List " + agent.mesh_list_vector[i] + "\n");
		    			    		}
		    			    		
		    			    	  }
		    			    	}
		    				if (oldLastTreeCreateIn.equals(agent.state.CoreAddress) && agent.state.ParentId.equals(agent.nullIP)) {
		    					// failure when I am acking the core and have not parentID
		    					// then I begin core
		    					agent.state.CoreAddress=agent.state.MyAddress;
	    						agent.state.is_core=true;
	    							    						
	    						agent.sendOuterTreeCreate();
	    						
		    				} else {
		    					// otherwise I keep the old parentID
		    					//if (agent.state.pendingPacket) agent.recvTREE_CREATE(agent.pendingTreeCreate);
		    				}
	    				}else if(agent.state.ParentId.equals(nb.IPAddress)){
	    					// ParentID fails during acking
	    					nb.Reset();
		    				nb.last_failure_time=now;
		    				agent.old_neighbors.put(nb.IPAddress, nb);
		    				agent.neighbors.remove(key);
		    				
		    				for (int i=0; i< agent.mesh_list_vector_Inet.length;i++){
		    			    	if(agent.JoinedList[i] != null){
		    			    		if(agent.mesh_list_vector_Inet[i].equals(nb.IPAddress)){
		    			    			
		    			    			agent.JoinedList[i]= null;
		    			    			//if (agent.sig_dump_box.getState())  agent.outputArea.append("Purged (2) from Joined List " + agent.mesh_list_vector[i] + "\n");
		    			    		}
		    			    		
		    			    	  }
		    			    	}
		    				agent.state.ParentId=agent.nullIP;
	    					
	    				}else{
	    					// failure on a descendant  node during ACKING
	    					nb.Reset();
		    				nb.last_failure_time=now;
		    				agent.old_neighbors.put(nb.IPAddress, nb);
		    				agent.neighbors.remove(key);
		    				
		    				for (int i=0; i< agent.mesh_list_vector_Inet.length;i++){
		    			    	if(agent.JoinedList[i] != null){
		    			    		if(agent.mesh_list_vector_Inet[i].equals(nb.IPAddress)){
		    			    			
		    			    			agent.JoinedList[i]= null;
		    			    			//if (agent.sig_dump_box.getState())  agent.outputArea.append("Purged (3) from Joined List " + agent.mesh_list_vector[i] + "\n");
		    			    		}
		    			    		
		    			    	  }
		    			    	}
	    					
	    				}
	    				
	    			} else if(agent.state.NACKStatus){
	    				if(nb.IPAddress.equals(agent.state.ParentId)) {
	    					//failure on the nacking node
		    				agent.state.NACKStatus = false;
		    				nb.Reset();
		        			nb.last_failure_time=now;
		    				agent.old_neighbors.put(nb.IPAddress, nb);
		    				agent.neighbors.remove(key);
		    				
		    				for (int i=0; i< agent.mesh_list_vector_Inet.length;i++){
		    			    	if(agent.JoinedList[i] != null){
		    			    		if(agent.mesh_list_vector_Inet[i].equals(nb.IPAddress)){
		    			    			
		    			    			agent.JoinedList[i]= null;
		    			    			//if (agent.sig_dump_box.getState())  agent.outputArea.append("Purged (4) from Joined List " + agent.mesh_list_vector[i] + "\n");
		    			    		}
		    			    		
		    			    	  }
		    			    	}
		    				agent.state.ParentId=agent.state.LastTreeCreateIn;
		    				if (agent.forwardingTreeCreate!=null) agent.forwardTREE_CREATE(agent.forwardingTreeCreate);
		    				/*
		    				if (agent.state.pendingPacket) {
		    					agent.recvTREE_CREATE(agent.pendingTreeCreate);
		    				} else {
		    					if (agent.forwardingTreeCreate!=null) agent.forwardTREE_CREATE(agent.forwardingTreeCreate);
		    				}
		    				*/
	    				}else{
	    					//failure on a descendant node during NACKING
	    					nb.Reset();
		        			nb.last_failure_time=now;
		    				agent.old_neighbors.put(nb.IPAddress, nb);
		    				agent.neighbors.remove(key);
		    				
		    				for (int i=0; i< agent.mesh_list_vector_Inet.length;i++){
		    			    	if(agent.JoinedList[i] != null){
		    			    		if(agent.mesh_list_vector_Inet[i].equals(nb.IPAddress)){
		    			    			
		    			    			agent.JoinedList[i]= null;
		    			    			//if (agent.sig_dump_box.getState())  agent.outputArea.append("Purged (5) from Joined List " + agent.mesh_list_vector[i] + "\n");
		    			    		}
		    			    		
		    			    	  }
		    			    	}
	    				}
	    				
	    				
	    			} else {
	    				//failure in absence of ACKING or NACKING
	    				if(nb.IPAddress.equals(agent.state.ParentId)){
	    					if(nb.IPAddress.equals(agent.state.CoreAddress)){
		    					// failure on the parent node which is the core 
		    					agent.state.CoreAddress=agent.state.MyAddress;
		    					agent.state.is_core=true;
		    					nb.Reset();
		    	    			nb.last_failure_time=now;
		    					agent.old_neighbors.put(nb.IPAddress, nb);
		    					agent.neighbors.remove(key);
		    					
		    					for (int i=0; i< agent.mesh_list_vector_Inet.length;i++){
			    			    	if(agent.JoinedList[i] != null){
			    			    		
			    			    		if(agent.mesh_list_vector_Inet[i].equals(nb.IPAddress)){
			    			    			
			    			    			
			    			    			agent.JoinedList[i]= null;
			    			    			//if (agent.sig_dump_box.getState())  agent.outputArea.append("Purged (6) from Joined List " + agent.mesh_list_vector[i] + "\n");
			    			    		}
			    			    		
			    			    	  }
			    			    	}
		    					agent.state.ParentId=agent.nullIP;
		    					agent.forwardingTreeCreate=null;
		    					new TimerWaitforCoreFailure(agent);
		    					//agent.sendOuterTreeCreate();
	    					}else{
		    					// failure on the parent node which is not the core
		    					//then I try to connect with the core
		    					nb.Reset();
		    	    			nb.last_failure_time=now;
		    					agent.old_neighbors.put(nb.IPAddress, nb);
		    					agent.neighbors.remove(key);
		    					
		    					for (int i=0; i< agent.mesh_list_vector_Inet.length;i++){
			    			    	if(agent.JoinedList[i] != null){
			    			    		if(agent.mesh_list_vector_Inet[i].equals(nb.IPAddress)){
			    			    			
			    			    			agent.JoinedList[i]= null;
			    			    			//if (agent.sig_dump_box.getState())  agent.outputArea.append("Purged (7) from Joined List " + agent.mesh_list_vector[i] + "\n");
			    			    		}
			    			    		
			    			    	  }
			    			    	}
		    					agent.state.ParentId=agent.nullIP;
		    					agent.forwardingTreeCreate=null;
		    					agent.state.LastTreeCreateIn=agent.state.CoreAddress;
		    					agent.nb_insert(agent.state.CoreAddress,agent.state.CoreAddress);
		    					agent.sendTreeCreateAck(agent.state.CoreAddress);
		    				}
	    				} else {
	    					//failure on a descendant node
		    				nb.Reset();
	    	    			nb.last_failure_time=now;
	    					agent.old_neighbors.put(nb.IPAddress, nb);
	    					agent.neighbors.remove(key);
	    					
	    					for (int i=0; i< agent.mesh_list_vector_Inet.length;i++){
		    			    	if(agent.JoinedList[i] != null){
		    			    		if(agent.mesh_list_vector_Inet[i].equals(nb.IPAddress)){
		    			    			
		    			    			agent.JoinedList[i]= null;
		    			    			//if (agent.sig_dump_box.getState())  agent.outputArea.append("Purged (8) from Joined List " + agent.mesh_list_vector[i] + "\n");
		    			    		}
		    			    		
		    			    	  }
		    			    	}
		    			}
	    			}
	    				
	    					
	    				
	    			
    			} else if (nb.istree && (now-nb.last_pkt_sent_time)>agent.Alive_Hello_intervall){
	    			pktHelloAlive pkt = new pktHelloAlive(agent.state.MyAddress, agent);
	    			UnicastData usp = new  UnicastData(pkt.GetpktHelloAlive(),nb.IPAddress);
	    	    	agent.USigSend.q.add(usp);
	    	    	
	    	    }
            
            }
        }
        
        if (agent.state.is_core && !agent.state.ParentId.equals(agent.nullIP)){
        	agent.state.ParentId = agent.nullIP;
        }
    
        if (agent.neighbors!=null){
            Enumeration<InetAddress> e = agent.neighbors.keys(); // get all keys stored in Hashtable 
            while (e.hasMoreElements()) {
    		Object key = e.nextElement(); // nextElement returns an Object
    		OverlayNeighbor  nb = agent.neighbors.get(key); // nextElement returns an Object
     		if (!agent.state.ParentId.equals(agent.nullIP)) {
			    	boolean acking_flag = true;
			    	if (agent.forwardingTreeCreate!=null) acking_flag=agent.forwardingTreeCreate.SourceIPAddress.equals(nb.IPAddress);
     				if(nb.Texpire < now  && !nb.istree && !agent.state.ParentId.equals(nb.IPAddress) && !acking_flag) {
						   agent.old_neighbors.put(nb.IPAddress, nb);
						   agent.neighbors.remove(key);
						   
						   for (int i=0; i< agent.mesh_list_vector_Inet.length;i++){
		    			    	if(agent.JoinedList[i] != null){
		    			    		if(agent.mesh_list_vector_Inet[i].equals(nb.IPAddress)){
		    			    			
		    			    			agent.JoinedList[i]= null;
		    			    			//if (agent.sig_dump_box.getState())  agent.outputArea.append("Purged (9) from Joined List " + agent.mesh_list_vector[i] + "\n");
		    			    		}
		    			    		
		    			    	  }
		    			    	}
			        } else if (nb.Texpire<now) {
				        	nb.distance=agent.state.hello_max_ttl+1;
			        }
			    } else {
			    	if(nb.Texpire < now &&  !nb.istree) {
						   agent.old_neighbors.put(nb.IPAddress, nb);
						   agent.neighbors.remove(key);
						   
						   for (int i=0; i< agent.mesh_list_vector_Inet.length;i++){
		    			    	if(agent.JoinedList[i] != null){
		    			    		if(agent.mesh_list_vector_Inet[i].equals(nb.IPAddress)){
		    			    			
		    			    			agent.JoinedList[i]= null;
		    			    			//if (agent.sig_dump_box.getState())  agent.outputArea.append("Purged (10) from Joined List " + agent.mesh_list_vector[i] + "\n");
		    			    		}
		    			    		
		    			    	  }
		    			    	}
					} else if (nb.Texpire<now) {
				        	nb.distance=agent.state.hello_max_ttl+1;
				    }
	    		
			    }		
            }
        }
        agent.rgui.handle();
        
        
    	for (int i=0; i< agent.JoinedList.length;i++){
    	if(agent.JoinedList[i] != null){
    		if(agent.JoinedList[i].Texpire < now){
    			
    			agent.JoinedList[i] = null;
    			//if (agent.sig_dump_box.getState())  agent.outputArea.append("Purged from Joined List " + agent.mesh_list_vector[i] + "\n");
    		}
    		
    	}
    	}
        
    }
        }
    		
  

    class nb_purgeTask extends TimerTask {
	
        public void run() {
	    		handle();
        }
    } 
} 