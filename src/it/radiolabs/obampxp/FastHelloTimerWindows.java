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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;


public class FastHelloTimerWindows {
    
    Timer timer;
    Signalling agent;
    
	
    public FastHelloTimerWindows(Signalling agent_) {
		agent = agent_;
        timer = new Timer();
        timer.schedule(new FastHelloWindowsTask(), 
        		0,
                agent.fast_HELLO_INT);
	    
    }
    
    public void handle(){
    	synchronized(agent.signalling_use){
    	    try {
    	    	
    	    	//agent.route_print("cmd /c route print 192.168.11*>route_print.txt");
    	    	//FileReader route_print1 = new FileReader ("route_print.txt");
    	    	BufferedReader route_printb1 = new BufferedReader (new InputStreamReader(Runtime.getRuntime().exec(agent.cmd).getInputStream()));
    			String str_route_print1;
    			agent.fastHelloActive = false;
    			while ((str_route_print1 = route_printb1.readLine()) !=null){
    				if (str_route_print1.lastIndexOf("Metric") >=0 )
    					break;
    			}
    			
    			route_printb1.mark(1);
    			
    			
			
    				while ((str_route_print1 = route_printb1.readLine()) !=null){
    				if(str_route_print1.lastIndexOf("=")>=0){
    					break;
    				}
    				else{
    					String addressIP = str_route_print1.trim();
    					addressIP = addressIP.substring(0, addressIP.indexOf(' '));
    					
    					for(int h=0; h<agent.JoinedList.length; h++){
    						
    						
    						if(agent.JoinedList[h]!=null && agent.JoinedList[h].IPaddress.equals(addressIP)&& !(addressIP.equals(agent.state.getAddressString()))){
    							
    							String metrica = str_route_print1.substring(70, str_route_print1.length());
    							agent.JoinedList[h].metrica = metrica;
    							if (metrica.equals("1") && agent.SenderISValid(InetAddress.getByName(agent.JoinedList[h].IPaddress))){
    								agent.fastHelloActive = true;
    								agent.receiveVirtualFastHello(addressIP);
    								break;
    							}
    							else{
    								break;
    							}
    							
    						}
    						else{
    							continue;
    						}
    					
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

    class FastHelloWindowsTask extends TimerTask {
	
        @Override
		public void run() {
        	           
            handle();
            
        }
    } 
} 
