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

import java.util.Timer;
import java.util.TimerTask;
import java.io.DataInputStream;
import java.net.InetAddress;

import Multicast.OverlayNeighbor;



public class IDR_Timer {
    
        Timer timer;
        Signalling agent;
	Integer prova;
	Stato state;
	int id;
	DataInputStream is;
	InetAddress dest;
	pktTreeCreate pktTC;
	byte[] pkt_to_send;
	byte SN;
	
   
    
    public IDR_Timer(Signalling agent_, InetAddress dest_, int id_, double delay_) {
		
		agent = agent_;
		dest = dest_;
		id = id_;
		timer = new Timer();
        timer.schedule(new IDR_TimerTask(), (int)delay_*1000);
	    
    }
    
    public IDR_Timer(Signalling agent_, DataInputStream is_, int id_, double delay_) {
		
		agent = agent_;
		is = is_;
		id = id_;
		
        timer = new Timer();
        timer.schedule(new IDR_TimerTask(), (int)delay_*1000);  //subsequent rate
	    
    }
    
    public IDR_Timer(Signalling agent_, pktTreeCreate pkt_, int id_, double delay_) {
		
		agent = agent_;
		pktTC = pkt_;
		id = id_;
		
        timer = new Timer();
        timer.schedule(new IDR_TimerTask(), (int)delay_*1000);  //subsequent rate
	    
    }
    
    public IDR_Timer(Signalling agent_, byte[] pkt_to_send_, InetAddress dest_, int id_, double delay_) {
		
		agent = agent_;
		pkt_to_send = pkt_to_send_;
		id = id_;
		dest = dest_;
				
        timer = new Timer();
        timer.schedule(new IDR_TimerTask(), (int)delay_*1000);  //subsequent rate
	    
    }
    
    public IDR_Timer(Signalling agent_, int id_, double delay_) {
		
		agent = agent_;
		id = id_;
		
        timer = new Timer();
        timer.schedule(new IDR_TimerTask(), (int)delay_*1000);  //subsequent rate
	    
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
    
    public void handle(){
    	
    	
    	switch (id) {
    		
    		
           	case 2://TreeCreate packet delay
           		synchronized(agent.signalling_use){
           			agent.recvTREE_CREATE(pktTC);
           		}
           		
           		break;
           	case 3://TreeCreateAck Retransmission Timer
           		OverlayNeighbor nb = (OverlayNeighbor)agent.neighbors.get(dest);
           		if (nb == null) break; 
           		if (CHECK_SEQ_NUM(nb.last_conf_sn, pkt_to_send[2])){
           			synchronized(agent.signalling_use){
           				agent.retransmitTreeCreateAck(dest, pkt_to_send);
           			}	
    			}
    			break;
           	case 4://TreeCreateNack Retransmission Timer
           		OverlayNeighbor nb1 = (OverlayNeighbor)agent.neighbors.get(dest);
           		if (nb1 == null) break; 
           		if (CHECK_SEQ_NUM(nb1.last_nack_conf_sn, pkt_to_send[2])){
           			synchronized(agent.signalling_use){
           				agent.retransmitTREE_CREATE_NACK(dest, pkt_to_send);
           			}	
    			} else {
    				int i=1;
    			}
    			break;
           	case 5://expanding ring Hello Timer 
                        
                        break;
           			
        }    		
    	
    }

    class IDR_TimerTask extends TimerTask {
	
        public void run() {
	    		handle();
	    		timer.cancel();
        }
    } 
} 