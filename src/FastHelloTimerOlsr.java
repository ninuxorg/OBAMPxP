/*    
 Copyright (C) 2007 Fabian Bieker
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
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
//import java.util.Arrays; // not needed atm, see below
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

/**
 * Fast Hello Timer for OLSR txtinfo plugin.
 * Note: plugin is expceted to be reachable on localhost:2006
 * @author fb
 *
 */
public class FastHelloTimerOlsr {

	private static final Logger log = 
		Logger.getLogger(FastHelloTimerOlsr.class);
	
	private final Timer timer;
	private final Signalling agent;

	public FastHelloTimerOlsr(Signalling agent_) {
		agent = agent_;
		timer = new Timer();
		timer.schedule(new FastHelloOlsrTask(), 0, agent.fast_HELLO_INT);

	}

	public void handle() {
		log.debug("handle() was called");
		
		Socket sock = null;
		synchronized (agent.signalling_use) {
			try {

				//agent.route_print("cmd /c route print 192.168.11*>route_print.txt");
				//FileReader route_print1 = new FileReader ("route_print.txt");
				
				// testing code
				/*BufferedReader route_printb1 = new BufferedReader(
						new InputStreamReader(Runtime.getRuntime().exec(
								agent.cmd).getInputStream()));*/
			    
				sock = new Socket();
				BufferedReader route_printb1 = connectToOlsrdInfo(sock);
			    
				String str_route_print1;
				agent.fastHelloActive = false;
				while ((str_route_print1 = route_printb1.readLine()) != null) {
					if (str_route_print1.lastIndexOf("Metric") >= 0) // FIXME: odd
						break;
				}

				route_printb1.mark(1);

				while ((str_route_print1 = route_printb1.readLine()) != null) {

					// eat non-intressting lines
					if (str_route_print1.trim().length() == 0)
						continue;

					//log.debug("handle() read: '" + str_route_print1 
					//		+ "'");

					int endIndex = str_route_print1.indexOf('\t');
					if (endIndex < 0)
						endIndex = str_route_print1.indexOf(' ');
					String addressIP = str_route_print1.substring(0, endIndex);

					for (int h = 0; h < agent.JoinedList.length; h++) {
						if (agent.JoinedList[h] != null
								&& agent.JoinedList[h].IPaddress
										.equals(addressIP)) {

							// use ETX as metric
							String[] tokens = str_route_print1.split("[\\t ]");
							if (tokens.length < 3) {
								//FIXME: not java 1.4.2
								//log.debug("--continue tokens="+Arrays.toString(tokens));
								continue; // FIXME: is it ok to continue here?
							}
							String metricString = tokens[2].trim();
							int metric = 1;
							try {
								metric = Integer.parseInt(metricString);
							} catch (NumberFormatException e) {
								log.warn("Failed to parse metric " +
										"string '" + metricString + 
										"', defaulting to 1", e);
							}
							
							log.info("got olsr metric="+metricString+" for "
									+addressIP);
							agent.JoinedList[h].metrica = metricString;
							if (metric <= 1 // TODO: test
								&& agent.SenderISValid(
										InetAddress.getByName(
												agent.JoinedList[h].IPaddress))) {
													
								agent.fastHelloActive = true;
								agent.receiveVirtualFastHello(addressIP);
								break;
							} else {
								break;
							}

						} else {
							continue;
						}

					}

				}

			} catch (IOException e) {
				log.error("I/O Error while trying to read olsr txtinfo", 
						e);
			} finally {
				if (sock!=null && !sock.isClosed())
					try {
						sock.close();
					} catch (IOException e) {
						log.error("failed to close sock ", e);
					}
			}
		}
	}
	
	/**
	 * Connect to olsrd txtinfo plugin
	 * @param sock Socket to use to create connection 
	 * @return a BufferedReader from the connected input stream of sock
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static BufferedReader connectToOlsrdInfo(Socket sock) 
	throws UnknownHostException, IOException {
		// FIXME: not java 1.4.2
		//assert !sock.isConnected();
		try {
			sock.connect(new InetSocketAddress("localhost", 2006)); // FIXME: use config val
		} catch (ConnectException e) {
			log.fatal("failed to connect to olsr plugin: "+e);
			System.exit(42); // FIXME: sux!
		}
		BufferedReader route_printb1 = new BufferedReader
		  (new InputStreamReader(sock.getInputStream()));
		return route_printb1;
	}

	class FastHelloOlsrTask extends TimerTask {

		public void run() {

			handle();

		}
	}
}
