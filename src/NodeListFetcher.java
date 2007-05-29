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
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.Logger;

/**
 * This is just a Wrapper class to hold two static methods.
 * getURL(...) can be used to fetch the obamp_nodes.txt from a url.
 * 
 * @author Fabian Bieker
 *
 */
public class NodeListFetcher {
	
	/**
	 * Fieldname of the config param that holds a url to a obamp node
	 * list.
	 * @see NodeListFetcher#getUrl(DB_Contest db)
	 */
	public static final String OBAMP_NODES_URL = "Obamp_Nodes_Url";

	/**
	 * Filename of the obamp node list
	 */
	public static final String OBAMP_NODES_FILE = "obamp_nodes.txt";
	
	private static final Logger log = 
		Logger.getLogger(NodeListFetcher.class);
	
	/**
	 * Fetch obamp_nodes.txt from url set in config file
	 * and write it to $PWD/obamp_nodes.txt.
	 * 
	 * @param db Parsed config file
	 * @throws IOException if I/O error occured while writing file
	 * @throws MalformedURLException if url is malformed
	 * @see java.net.URL
	 */
	public static void getUrl(DB_Contest db) 
	throws MalformedURLException, IOException {
		String url = db.get(OBAMP_NODES_URL).trim();
		// Note: db.get(...) returns "ciao" if no value is set
		if (url != null && url.length() > 0 && !url.equals("ciao"))
			getUrl(url);
		else
			log.info("empty node list URL, not fetching it...");
	}
	
	/**
	 * Fetch obamp_nodes.txt from url and write it to 
	 * $PWD/obamp_nodes.txt
	 * @param url Sting rep. of the url to fetch
	 * @throws IOException if I/O error occured while writing file
	 * @throws MalformedURLException if url is malformed
	 * @see java.net.URL
	 * TODO: there must be a more elegant way to do this...
	 */
	public static void getUrl(String url) 
	throws IOException, MalformedURLException {
		InputStream in = null;
		BufferedReader bin = null;
		File outFile;
		FileWriter out = null;
		
		try {
			in = (new URL(url)).openConnection().getInputStream();
			bin = new BufferedReader(new InputStreamReader(in));
			outFile = new File(OBAMP_NODES_FILE);
			out = new FileWriter(outFile);
			String line;
			while ((line = bin.readLine()) != null)
				out.write(line + "\n");
			log.info("got nodes from " + url + ", wrote it to " + 
					OBAMP_NODES_FILE);
		} finally {
			if (out!=null) out.close();
			
			if (bin!=null) bin.close();
			else if (in!=null) in.close();
		}
	}
	
	/**
	 * leazy unit testing ...
	 */
	public static void main(String args[]) throws 
	MalformedURLException, IOException {
		NodeListFetcher.getUrl(
				"http://page.mi.fu-berlin.de/bieker/obamp_nodes.txt");
	}

}
