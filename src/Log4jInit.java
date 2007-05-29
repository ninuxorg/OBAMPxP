/*    
    Copyright (C) 2007  Fabian Bieker
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

import java.io.File;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Wrapper class to deal with log4j init.
 * TODO: there might be a more elegant way to do this - suggestions are 
 * 		 welcome
 * @author Fabian Bieker
 *
 */
public class Log4jInit {

	private static final Logger log =
		Logger.getLogger(Log4jInit.class);
	public static final String LOG4J_CONFIG_FILENAME = "log4j.properties";
	
	private static volatile boolean initDone = false;
	
	/**
	 * Call this method to get log4j initialized.
	 * It is ensured, that log4j is only initialized once.
	 * 
	 * Init is done as follows:
	 * 1. try to read LOG4J_CONFIG_FILENAME from $PWD, if ok -&gt; done
	 * 2. try to read LOG4J_CONFIG_FILENAME from .jar, if ok -&gt; done
	 * 3. init log4j by calling BasicConfigurator.configure();
	 * 
	 */
	public synchronized static void init() {
		if (initDone) return;
		
		// try to get log4j config from $PWD
		File f = new File(LOG4J_CONFIG_FILENAME);
		// TODO: there is a race-cond here, but I do not care atm
		if (f.exists()) { 
			try {
				PropertyConfigurator.configure(LOG4J_CONFIG_FILENAME);
				initDone = true;
				log.debug("log4j init from " + f.getAbsolutePath() +
					" - successfull");
			} catch (Throwable t) {
				t.printStackTrace(System.err);
				System.err.println("failed to init log4j with " +
						"log4j.properties - trying to get" +
				" config from .jar ...");
			}
			
		// try to get log4j config from .jar
		} else {
			try {
				PropertyConfigurator.configure(
						Log4jInit.class.getResource(LOG4J_CONFIG_FILENAME));
				initDone = true;
				log.debug("log4j init from " + LOG4J_CONFIG_FILENAME + 
						" inside .jar - successfull");
			} catch (Throwable t2) {
				t2.printStackTrace(System.err);
				System.err.println("failed to init log4j - falling back " +
					"to defaults...");
				BasicConfigurator.configure();
				// this is not considured a valid init 
				// 	-> initDone not touched

			} 
		}
		
	}

}
