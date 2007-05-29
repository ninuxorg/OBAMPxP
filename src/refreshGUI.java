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
import java.util.Enumeration;
import java.util.Timer;

import Multicast.OverlayNeighbor;


public class refreshGUI {
    
    Timer timer;
    Signalling agent;
        
	
    public refreshGUI(Signalling agent_) {
	   	agent = agent_;
            
    }
    
    public void handle(){
    	
    	agent.label5.setText("Core Id:    "+agent.state.getIPCoreAddress().getHostAddress()+"  ACKING "+agent.state.ACKStatus);
    	agent.label6.setText("Parent Id: "+agent.state.ParentId.getHostAddress()+"  NACKING "+agent.state.NACKStatus);
    	agent.outputArea2.setText("");
    	agent.outputArea2.append("IP address:           Distance:       Tree Link:      LifeTime:       AliveTime:" + "\n");
        Enumeration<InetAddress> e = agent.neighbors.keys(); // get all keys stored in Hashtable 
    	while (e.hasMoreElements()) {
    		Object key = e.nextElement(); // nextElement returns an Object
            OverlayNeighbor  value = agent.neighbors.get(key); // nextElement returns an Object
            int tree = 0;
            if (value.istree) tree = 1;
        
            String add = value.GetIPaddress().getHostAddress();
            long texp = value.Texpire-System.currentTimeMillis();
            String alive="";
            if (value.istree && System.currentTimeMillis()-value.last_pkt_recv_time<1.5*agent.Allowed_Alive_Hello_loss*agent.Alive_Hello_intervall){
            	
            	Long time = new Long((long)(1.5*agent.Allowed_Alive_Hello_loss*agent.Alive_Hello_intervall)-(System.currentTimeMillis()-value.last_pkt_recv_time));
            	alive = time.toString();
            }else{
            	if (value.istree)alive = "expired";
            }
            if (texp>=0){
            	agent.outputArea2.append(add +"            "+ value.distance + "                      "+ tree + "                  "+ texp + "                  "+alive+"\n");
            }else{
            	agent.outputArea2.append(add +"            "+ value.distance + "                      "+ tree + "                   expired"+"                  "+alive+"\n");
            }
            	
    	}
      
    	agent.outputArea3.setText("");
    	for (int i=0; i< agent.JoinedList.length;i++){
        	if(agent.JoinedList[i] != null){
        		if(!agent.mesh_list_vector_Inet[i].equals(agent.state.MyAddress))agent.outputArea3.append( agent.JoinedList[i].IPaddress+ "\n");
        	}
        	}	
    	
	}   
    
} 