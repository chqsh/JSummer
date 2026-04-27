/*
* Package: com.de.zerosys.JSummer
* Project: JSummer
* Created on 17.07.2005
* Author:	Klaus Zerwes
* (c) 2005 - 2006 zero-sys.net
* 
* $Id: XLauncherServConn.java,v 1.9 2006-03-24 10:47:56 io Exp $
* 
* This package is free software.
* This software is licensed under the terms of the 
* GNU General Public License (GPL), version 2.0 or later, 
* as published by the Free Software Foundation. 
* See http://www.gnu.org/copyleft/gpl.txt for the terms of the 
* latest version of the GNU General Public License.
*/
package com.de.zerosys.JSummer;

import java.net.Socket;
import java.util.Enumeration;
import java.util.Vector;

class XLauncherServConn extends XLauncherSockRW{

	private boolean connStatus = false;
    
    private int classDebugLevel = 5;
    
	protected XLauncherServConn(Screen o, Socket s) {
		super(o, s);
		this.connStatus = this.connect();
	}
	
	private boolean connect(){
		this.o.debug(this.toString()+"::connect()",this.classDebugLevel);
		boolean ret = true;
		final String initSend = "+"+CoreConfig.serverInitString;
		String response = this.readLine();
		if(!response.equals(initSend)){
			ret = false;
		}else{
			this.sendLine(initSend);
			response = this.readLine();
			if(!response.equals("+OK")){
				ret = false;
			}
		}
		this.o.debug(this.toString()+"::connect() ret:"+ret,this.classDebugLevel);
		return ret;
	}
	
	protected boolean getConnStatus(){
		return this.connStatus;
	}
	
	protected boolean sendFiles(Vector v){
	    Enumeration e = v.elements();
	    while(e.hasMoreElements()){
	        //HashFile hf = (HashFile)e.nextElement();
            CoreHashFile hf = (CoreHashFile)e.nextElement();
	    	String f = hf.getAbsName();
	        this.o.debug("sending filestring "+f,this.classDebugLevel);
	    	if(!this.sendFile(f)){
	            return false;
	        }
	    }
	    return true;
	}
	private boolean sendFile(String f){
	    this.o.debug(this.toString()+"sendFile('"+f+"')",this.classDebugLevel);
	    boolean ret = false;
	    if(!this.connStatus){
	        this.o.error("NOT CONNECTED");
			return false;
		}
	    this.sendLine("+"+f);
	    if(this.readLine().equals("+OK")){
	        ret = true;
	    }
	    this.o.debug("file send status:"+ret,this.classDebugLevel);
	    return ret;
	}
	
	protected void closeConn(){
	    this.sendLine("-BYBY");
	    String resp = this.readLine();
	    if(!resp.equals("-OK")){
	        this.o.error("error closing conn: response:'"+resp+"'");
	    }
	    this.close();
	}
}
