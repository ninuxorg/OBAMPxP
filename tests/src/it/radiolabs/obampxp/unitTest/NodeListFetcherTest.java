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



//import NodeListFetcher; // FIXME: can not import stuff from the default package
package it.radiolabs.obampxp.unitTest;

import it.radiolabs.obampxp.util.DB_Contest;
import it.radiolabs.obampxp.util.Log4jInit;
import it.radiolabs.obampxp.util.NodeListFetcher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

/**
 * JUnit test for NodeListFetcher
 * @author fb
 *
 */
public class NodeListFetcherTest extends TestCase {

	private static final String OBAMP_CFG = "obamp.cfg";
	
	public NodeListFetcherTest() {
		super();
		Log4jInit.initOnce();
	}

	/**
	 * Test method for {@link NodeListFetcher#getUrl(DB_Contest)}.
	 * @throws IOException 
	 */
	public void testGetUrlDB_Contest() throws IOException {
		mkObampCfg();
		NodeListFetcher.getUrl(new DB_Contest(OBAMP_CFG));
		checkNodeList();
	}

	/**
	 * Test method for {@link NodeListFetcher#getUrl(java.lang.String)}.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public void testGetUrlString() throws MalformedURLException, IOException {
		NodeListFetcher.getUrl(
			"http://page.mi.fu-berlin.de/bieker/obamp_nodes_test.txt");
		
		checkNodeList();
	}

	/** Compare node-list-file to this:
	 * # THIS FILE SHOULD ONLY BE USED FOR UNIT-TESTING
	 * 42.42.42.23
	 */
	private static void checkNodeList() throws FileNotFoundException, IOException {
		
		BufferedReader bin = null;
		File f = null;
		try {
			f = new File(NodeListFetcher.OBAMP_NODES_FILE);
			assertTrue(f.canRead());
			bin = new BufferedReader(new FileReader(f));
			assertEquals("# THIS FILE SHOULD ONLY BE USED FOR UNIT-TESTING", 
					bin.readLine().trim());
			assertEquals("42.42.42.23", bin.readLine().trim());
		} finally {
			if (bin!=null) bin.close();
		}
	}	
	
	/**
	 * create a obamp.cfg that only contains stuff needed for this test
	 * @throws IOException
	 */
	private static void mkObampCfg() throws IOException {
		File f = null;
		FileWriter out = null;
		try {
			f = new File(OBAMP_CFG);
			out = new FileWriter(f);
			out.write("<Obamp_Nodes_Url>http://page.mi.fu-berlin.de" +
					"/bieker/obamp_nodes_test.txt</Obamp_Nodes_Url>\n");
		} finally {
			if (out!=null) out.close();
		}
	}

	
}
