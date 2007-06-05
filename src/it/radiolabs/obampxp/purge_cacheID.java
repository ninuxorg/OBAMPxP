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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;


public class purge_cacheID {
    
    Timer timer;
    DataManager agent;
		
	
    public purge_cacheID(DataManager agent_) {
		
		agent = agent_;
        timer = new Timer();
        timer.schedule(new purge_cacheIDTask(),
	               1000,        //initial delay
	               1000);  //subsequent rate
	               
	    
    }
    
    public void handle () {
   		
   		Enumeration<Byte> e = agent.dataSource.keys(); // get all keys stored in Hashtable 
    	while (e.hasMoreElements()) {
    		Byte key = e.nextElement();
    		Hashtable<Integer, Long> cache = agent.dataSource.get(key);
    		
    		Enumeration<Integer> en = cache.keys(); // get all keys stored in Hashtable 
    		while (en.hasMoreElements()) {
    			Object id = en.nextElement(); // nextElement returns an Object
    			Long value = cache.get(id); // nextElement returns an Object
    			long timearrival = value.longValue();
    			
    			//long timearrival = (Long)cache.get(key).longValue(); 
    			if ((System.currentTimeMillis()-timearrival)>10000){
    				cache.remove(id);		
    			}
    		    	
    		}
    		
    	}
         	
    }

    class purge_cacheIDTask extends TimerTask {
	
        public void run() {
	    		handle();
        }
    } 
} 