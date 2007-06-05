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

public class TimerWaitforCoreFailure {
    
        Timer timer;
        Signalling agent;
	
	
   
    
    public TimerWaitforCoreFailure(Signalling agent_) {
		
		agent = agent_;
		timer = new Timer();
		timer.schedule(new TimerWaitforCoreFailureTask(), (long) (1.5*agent.Allowed_Alive_Hello_loss*agent.Alive_Hello_intervall));
		
    }
    
   
    
    public void handle(){
    	
    	agent.sendOuterTreeCreate();
           		
    	
    }

    class TimerWaitforCoreFailureTask extends TimerTask {
	
        public void run() {
        	
	    		handle();
	    		timer.cancel();
	    		
        }
    } 
} 