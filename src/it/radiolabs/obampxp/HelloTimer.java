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

import it.radiolabs.obampxp.multicast.OverlayNeighbor;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;



public class HelloTimer {
    
    Timer timer;
    Signalling agent;
        
	
    public HelloTimer(Signalling agent_) {
	   	agent = agent_;
        timer = new Timer();
        timer.schedule(new HelloTask(), 
                0,
                agent.HELLO_INT);
	    
    }
    
    public void handle(){
                
    	synchronized(agent.signalling_use){
    	    try {
    	    	if (!agent.fastHelloActive){
    	    		

    	        	int l=0;
    	            for (int i=2; i<100; i++){
    	            	if(l==0){
    	            		
    	            		for (int p=0; p<agent.JoinedList.length;p++){
    	            			
    	            		 if(agent.JoinedList[p]!=null){
    	            			 
    	            			Integer metrica = new Integer (agent.JoinedList[p].metrica);
    	            			int TTL = metrica.intValue();
    	            			if (TTL<=i && TTL!=0){
    	            				
    	            				
    	            				InetAddress addressIP = InetAddress.getByName(agent.JoinedList[p].IPaddress);
    	            				if (agent.SenderISValid(addressIP)){
    	            				if(!(addressIP.equals(agent.state.MyAddress))){
    	            				OverlayNeighbor nb = agent.neighbors.get(addressIP);
    	            				if (nb==null) {
        	                        	agent.nb_insert(addressIP, agent.state.CoreAddress);
        	                        	nb = agent.neighbors.get(addressIP);
        	                        } 
    	            				nb.CoreAddress = agent.state.CoreAddress;
        	                        nb.distance=TTL;
        	                        nb.Texpire=System.currentTimeMillis()+((long)(1.5*agent.Allowed_Hello_loss*agent.HELLO_INT));
        	                        
        	                        int HSN = (int)agent.state.HelloConfSequenceNumber;
        	                        if(HSN<0)
        	                           HSN = HSN + 256;
        	                        HSN = HSN + 1;
        	                        if(HSN>255)
        	                            HSN = 1;
        	                        agent.state.HelloConfSequenceNumber = (byte)HSN;
        	                        byte TTLp = metrica.byteValue();
        	                        agent.sendHelloConf(agent.state.HelloConfSequenceNumber, addressIP, TTLp);
        	                        l++;
        	                		continue;
    	            		}
    	            			}
    	            				else continue;
    	            			}
    	            			else{
    	            				continue;
    	            			}
    	            			
    	            			}
    	            			else{
    	            				continue;
    	            			}
    	   	 
    	        	} 
    	            }
    	            	else{
    	            		break;
    	            	}
    	    	}
    	    }
    	    	
    	    }
    	        	catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} 
    	    }
    	    }
    
    
    class HelloTask extends TimerTask {
	
        public void run() {
                       
            handle();
            
        }
    } 
} 