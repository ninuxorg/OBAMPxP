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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import Multicast.OverlayNeighbor;


public class JoinedTimer {
    
    Timer timer;
    Signalling agent;
    
	
    public JoinedTimer(Signalling agent_) {
    	
		agent = agent_;
        timer = new Timer();
        timer.schedule(new JoinTask(), 
        		0,
                agent.JOIN_INT);
	    
    }
    
    public void handle(){
    	synchronized(agent.signalling_use){
    	    agent.sendJoin();
			 }
    	    }

    class JoinTask extends TimerTask {
	
        public void run() {
        	           
            handle();
            
        }
    } 
} 
