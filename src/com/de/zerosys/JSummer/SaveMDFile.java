/**
 * $Id: SaveMDFile.java,v 1.10 2008-03-05 11:56:16 zerwes Exp $
 * 
 * (c) 2005 - 2006 Klaus Zerwes zero-sys.net
 * 
 * This package is free software.
 * This software is licensed under the terms of the 
 * GNU General Public License (GPL), version 2.0 or later, 
 * as published by the Free Software Foundation. 
 * See http://www.gnu.org/copyleft/gpl.txt for the terms of the 
 * latest version of the GNU General Public License.
 */
package com.de.zerosys.JSummer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



class SaveMDFile{
	private Screen o;
	private Config config;
	
	private int classDebugLevel = 11;
	
	private int errorCode = 0;
	private String errorMsg = "";
	
	private FileWriter fw = null;
	
	SaveMDFile(Config c){
		this.config = c;
		this.o = this.config.getScreen();
		this.o.debug(this.toString(),this.classDebugLevel);
		if(this.config.getSaveMDFile().equals("")){
			this.errorCode = 1;
			this.errorMsg = "saveMDFile NOT SET!";
			return;
		}
		try{
			this.config.fileSaveMDFile = new File(this.config.getSaveMDFile()); 
			this.fw = new FileWriter(this.config.fileSaveMDFile, false);
		}catch(IOException e){
			this.o.error(e.getMessage());
			this.errorCode = 2;
			this.errorMsg = e.getMessage();
		}
	}

	protected int getErrorCode() {
		return this.errorCode;
	}

	protected String getErrorMsg() {
		return this.errorMsg;
	}
	
	protected void write(String s){
	    this.o.debug(this.toString()+"::write() '"+s+"'",this.classDebugLevel);
	    if(this.fw==null){
	        this.errorCode = 10;
			this.errorMsg = this.toString()+"::write() : writer is NULL!!!";
	        this.o.error(this.errorMsg);
	        return;
	    }
		try{
			this.fw.write(s);
			if(s.endsWith("\n")){
				this.fw.flush();
			}
		}catch(IOException e){
			this.o.error(e.getMessage());
			this.errorCode = 3;
			this.errorMsg = this.toString()+"::write() : "+e.getMessage();
		}
		this.o.debug(this.toString()+"::write() exitcode "+this.errorCode, this.classDebugLevel);
	}
	
	protected void close(){
	    if(this.fw==null){
	        return;
	    }
	    this.o.debug(this.toString()+"::close()",this.classDebugLevel);
	    
		try{
		    this.fw.flush();
			this.fw.close();
			this.fw = null;
		}catch(IOException e){
			this.o.error(this.toString()+" : "+e.getMessage());
			this.errorCode = 4;
			this.errorMsg = e.getMessage();
		}
	}
	
	
	protected void finalize() throws Throwable {
		this.close();
	}
}
