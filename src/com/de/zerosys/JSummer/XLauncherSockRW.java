/*
* Package: com.de.zerosys.JSummer
* Project: JSummer
* Created on 17.07.2005
* Author:	Klaus Zerwes
* (c) 2005 - 2006 zero-sys.net
* 
* $Id: XLauncherSockRW.java,v 1.7 2006-03-24 10:47:56 io Exp $
* 
* This package is free software.
* This software is licensed under the terms of the 
* GNU General Public License (GPL), version 2.0 or later, 
* as published by the Free Software Foundation. 
* See http://www.gnu.org/copyleft/gpl.txt for the terms of the 
* latest version of the GNU General Public License.
*/
package com.de.zerosys.JSummer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*
 * this is just a helper class to free the serverListener of all that red/write stuff
 */
class XLauncherSockRW {
	protected Socket s = null;
	protected InputStream si;
	protected OutputStream so;
	protected Screen o;
	
    private int classDebugLevel = 5;
	
	protected XLauncherSockRW(Screen o,Socket s){
		this.o = o;
		this.s = s;
		try{
            this.si = s.getInputStream();
            this.so = s.getOutputStream();
        }catch(IOException e){
            o.error(this.toString()+" :: "+e.getMessage());
            this.s = null;
        }
	}

	private boolean isConn(){
		//this.o.debug(this.toString()+"::isConn()", this.classDebugLevel);
		boolean ret = false;
        if(this.s != null){
        	if(this.s.isConnected()){
        		ret = true;
        	}else{
        		this.o.debug(this.toString()+" not connected! "
        				+this.s.isConnected(), this.classDebugLevel);
        	}
        }else{
        	this.o.debug(this.toString()+" socket NULL!", this.classDebugLevel);
        }
        //this.o.debug(this.toString()+"::isConn() ret "+ret, this.classDebugLevel);
        return ret;
    }
    
    protected boolean sendLine(String l){
    	this.o.debug(this.toString()+"::sendLine() '"+l+"'",this.classDebugLevel);
    	boolean ret = true;
        if(!this.isConn()){
        	ret = false;
        }else{
	        l += "\n";
	        try{
	            this.so.write(l.getBytes());
	        }catch(IOException e){
	            this.o.error(this.toString()+" :: "+e.getMessage());
	            this.close();
	            ret = false;
	        }
        }
        this.o.debug(this.toString()+"::sendLine() '"+l+"' ret "+ret,this.classDebugLevel);
        return ret;
    }
    
    protected String readLine(){
    	this.o.debug(this.toString()+"::readLine()",this.classDebugLevel);
    	String l = "";
    	if(this.isConn()){
	        int c=0;
	        while(this.isConn()){
	            //o.debug(this.toString()+":: read char"+c+"("+(char)c+")",XLauncher.classDebugLevel);
	            try{
	                c = this.si.read();
	            }catch(IOException e){
	                this.o.error(this.toString()+" :: "+e.getMessage());
	                this.close();
	            }
	            if(c==10){
	                //o.debug(this.toString()+"::read "+c,XLauncher.classDebugLevel);
	                break;
	            }
	            if(c==-1) break;
	            l += (char)c;
	        }
    	}
        this.o.debug(this.toString()+"::readLine() :'"+l+"'",this.classDebugLevel);
        return l;
    }
    
    protected void close(){
        if(isConn()){
            try{
                this.s.close();
                this.s = null;
            }catch(IOException e){
                this.o.error(this.toString()+" :: "+e.getMessage());
            }
        }
    }
    
    protected void finalize() throws Throwable {
        this.close();
    }
}
