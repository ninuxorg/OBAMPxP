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

import java.util.Timer;
import java.util.TimerTask;


public class TREE_CREATETimer {
    
    Timer timer;
    Signalling agent;
	Integer prova;
	Stato state;
	int count;
	
    public TREE_CREATETimer(Signalling agent_) {
		count = 0;
		agent = agent_;
        timer = new Timer();
        timer.schedule(new TREE_CREATETask(),
	               0,        //initial delay
	               agent_.TCinterval);  //subsequent rate
	    
    }
    
    public void handle(){
    		
    		if (agent.state.is_core){
               	synchronized(agent.signalling_use){
               		agent.sendTreeCreate();
               	}
    		}
    }

    class TREE_CREATETask extends TimerTask {
	
        public void run() {
	    		handle();
        }
    } 
} 