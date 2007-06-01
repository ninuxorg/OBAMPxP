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
import java.io.*;
import java.util.*;

public class DB_Contest{
	
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
	
	
	
	public DB_Contest(String url_contest){
		
		File f = new File(url_contest);
		file_contest = f;
		mark_file1 = 0;
		mark_file2 = 0;
	};
	
	//ritorna il valore associato al campo come stringa
	public String get(String campo){
		
		try{
			List lis = new ArrayList();
			Character ch;
			String st1 = null;
			String st2 = null;
			String st = null;
			String st_char = "";
			DataInputStream in = 
				new DataInputStream(
					new BufferedInputStream(
						new FileInputStream(file_contest)));
						
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
						return(st);
					}
				}
			return(st);
				
		}catch(IOException io){System.out.println(io);}					
		
		return("error");
	};
	

	public String write(String campo, String value){return("ciao");};//scrive il valore nel campo rispettivo 
	
	public boolean put(String new_campo){return(true);};//inserisce nuovo campo
	
	//inserisce nuovo campo e il rispettivo valore
	//scrive il valore nel campo rispettivo 
	public boolean put(String campo, String value){
		try{
			
			Character ch;
			String st1 = "";
			String st2 = "";
			String st = "";
			String st_char = "";
			DataInputStream in = 
				new DataInputStream(
					new BufferedInputStream(
						new FileInputStream(file_contest)));
						
				while(in.available() !=0){
					ch = new Character((char)in.readByte());
					aggiorna_mark_file();
					st_char = ch.toString();
					st1 = st1 + st_char;
					while(!(st_char.equals("<"))){
						st_char = ch.toString((char)in.readByte());
						aggiorna_mark_file();
						st1 = st1 + st_char;
					}
					st = read_field(in);
					if(st.equals(campo)){
						st1 = st1 + campo + ">";
						st_char = ch.toString((char)in.readByte());
						while(!(st_char.equals("<"))){
							st_char = ch.toString((char)in.readByte());
							//aggiorna_mark_file2();
							
						}
						st1 = st1 + value + st_char;
						while(in.available() !=0){
							st_char = ch.toString((char)in.readByte());
							st1 = st1 + st_char;
						}
						FileOutputStream f = null;
						f = new FileOutputStream(file_contest);
						DataOutputStream os = new DataOutputStream(f);
						os.writeBytes(st1);
						f.close();
						return(true);
					}else
						st1 = st1 + st +">";
				}
				
			
			
			return(false);
				
		}catch(IOException io){System.out.println(io);}		
		
		
		return(false);
	};
	
	//ritorna il campo passando "in" che punta l' elemento "<" 
	protected String read_field(DataInputStream in){
		try{
			String _field = "";
			String _field_char = null; 
			List lis = new ArrayList();
			Character ch = new Character((char)in.readByte());
			aggiorna_mark_file();
			
			_field_char = ch.toString();
			//if((_field_char).equals("/"))
				//return("/");
			while(!(_field_char).equals(">")){
				_field = _field + _field_char;
				_field_char = ch.toString((char)in.readByte());
				aggiorna_mark_file();
			}
			return(_field);
		}catch(IOException io){System.out.println(io);}		
		return(" ");
	};
	//ritorna il valore passando "in" che punta l' elemento ">"
	protected String read_value(DataInputStream in){
		try{
			String value = "";
			String value_char = null; 
			List lis = new ArrayList();
			Character ch = new Character((char)in.readByte());
			aggiorna_mark_file();
			//if(!((ch.toString()).equals(" ")))
				value_char = ch.toString();
			while(!(value_char).equals("<")){
				if(!(ch.toString()).equals(" ")){
					value = value + value_char;
				}
				//System.out.print(value_char);
				value_char = ch.toString((char)in.readByte());
				aggiorna_mark_file();
			}
			return(value);
		}catch(IOException io){System.out.println(io);}		
		return(" ");
	};
	
	//scrive il valore partendo dall' elemento successivo puntato da "in", fino a "<" 
	
	public static void main(String args[]){
	 
	};
}