package it.radiolabs.obampxp.util;
/*    
	Copyright (C) 2007  Claudio Loreti
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
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

/**
 * This class deals with reading / parsing the obamp config file.
 * 
 * @author Claudio Loreti
 * @author Fabian Bieker (minor improvments)
 *
 */
// TODO: use log4j - do not println(exception)
public class DB_Contest{
	
	private static final Logger log = 
		Logger.getLogger(DB_Contest.class);
	
	private int mark_file1;
	private int mark_file2;
	
	private File file_contest;
	
	protected void reset_mark_file(){
		mark_file1 = 0;
		mark_file2 = 0;
	};
	
	protected void aggiorna_mark_file(){
		mark_file1++;
		mark_file2++;
		
	};
	
	protected void aggiorna_mark1(){
		mark_file1++;
		
	};
	
	protected void aggiorna_mark2(){
		mark_file2++;	
	};
	
	
	
	public DB_Contest(String url_contest) throws FileNotFoundException{
		
		File f = new File(url_contest);
		if (!f.exists()) {
		    throw new FileNotFoundException("File " + url_contest 
		            + " not found");
		}
		
		file_contest = f;
		mark_file1 = 0;
		mark_file2 = 0;
	};
	
	//ritorna il valore associato al campo come stringa
	public String get(String campo){
		
		DataInputStream in = null;
		
		try{
			Character ch;
			String st = null;
			String st_char = "";
			in = new DataInputStream(
					new BufferedInputStream(
						new FileInputStream(file_contest)));
				
				// TODO: deal with EOF cases without triggering an exception
				while(in.available() !=0){
					ch = new Character((char)in.readByte());
					aggiorna_mark_file();
					st_char = ch.toString();
	
					while(!(st_char.equals("<"))){
						ch = new Character((char)in.readByte());
						aggiorna_mark_file();
						st_char = ch.toString();
					}
					st = read_field(in);
					if(st.equals(campo)){
						st = read_value(in);
						return st;
					}
				}
			return st;
				
		} catch(IOException e) {
			log.error("I/O error while reading config", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.info("failed to close input stream", e);
				}
			}
		}
		
		return("error"); // TODO: throw exception
	};
	

	public String write(String campo, String value){
		return("ciao");//scrive il valore nel campo rispettivo 
	}
	
	public boolean put(String new_campo){
		return(true);//inserisce nuovo campo
	};
	
	//inserisce nuovo campo e il rispettivo valore
	//scrive il valore nel campo rispettivo 
	public boolean put(String campo, String value){
		
		DataInputStream in = null;
		
		try{
			
			Character ch;
			String st1 = "";
			String st = "";
			String st_char = "";
			in = new DataInputStream(
					new BufferedInputStream(
							new FileInputStream(file_contest)));
						
				while(in.available() !=0){
					ch = new Character((char)in.readByte());
					aggiorna_mark_file();
					st_char = ch.toString();
					st1 = st1 + st_char;
					while(!(st_char.equals("<"))){
						st_char = Character.toString((char)in.readByte()); // TODO: breaks encoding
						aggiorna_mark_file();
						st1 = st1 + st_char;
					}
					st = read_field(in);
					if(st.equals(campo)){
						st1 = st1 + campo + ">";
						st_char = Character.toString((char)in.readByte()); // TODO: breaks encoding
						while(!(st_char.equals("<"))){
							st_char = Character.toString((char)in.readByte()); // TODO: breaks encoding
							//aggiorna_mark_file2();
							
						}
						st1 = st1 + value + st_char;
						while(in.available() !=0){
							st_char = Character.toString((char)in.readByte()); // TODO: breaks encoding
							st1 = st1 + st_char;
						}
						FileOutputStream f = null;
						f = new FileOutputStream(file_contest);
						DataOutputStream os = new DataOutputStream(f);
						os.writeBytes(st1);
						f.close(); // FIXME: might not be reached?
						return true ;
					}else
						st1 = st1 + st +">";
				}
				
			return false;
				
		}catch(IOException e){
			log.error("I/O error while updating config", e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					log.info("failed to close input stream", e);
				}
			}
		}
		
		return false;
	};
	
	//ritorna il campo passando "in" che punta l' elemento "<" 
	protected String read_field(DataInputStream in){
		try{
			String _field = "";
			String _field_char = null; 
			Character ch = new Character((char)in.readByte());
			aggiorna_mark_file();
			
			_field_char = ch.toString();
			//if((_field_char).equals("/"))
				//return("/");
			while(!(_field_char).equals(">")){
				_field = _field + _field_char;
				_field_char = Character.toString((char)in.readByte()); //TODO: breaks encoding
				aggiorna_mark_file();
			}
			return(_field);
		}catch(IOException e){
			log.error("I/O error while reading input stream", e);
		}		
		return(" "); // TODO: throw expception or return null?
	};
	//ritorna il valore passando "in" che punta l' elemento ">"
	protected String read_value(DataInputStream in){
		try{
			String value = "";
			String value_char = null; 
			Character ch = new Character((char)in.readByte());
			aggiorna_mark_file();
			//if(!((ch.toString()).equals(" ")))
				value_char = ch.toString();
			while(!(value_char).equals("<")){
				if(!(ch.toString()).equals(" ")){
					value = value + value_char;
				}
				//System.out.print(value_char);
				value_char = Character.toString((char)in.readByte()); //TODO: breaks encoding
				aggiorna_mark_file();
			}
			return(value);
		}catch(IOException e){
			log.error("I/O error while reading input stream", e);
		}		
		return(" "); // TODO: throw expception or return null?
	};
	
	//scrive il valore partendo dall' elemento successivo puntato da "in", fino a "<" 
	
	// TODO: remove?
	/*public static void main(String args[]){
	 
	};*/
}