/*
* Package: com.de.zerosys.JSummer
* Project: JSummer
* Created on 17.07.2005
* Author:	Klaus Zerwes
* (c) 2005 - 2006 zero-sys.net
* 
* $Id: XLauncherServer.java,v 1.13 2006-03-24 10:47:57 io Exp $
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
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

class XLauncherServer implements Runnable {
    
	private final static int classDebugLevel = 1;
	private Screen o;
	
	private int serverPort = 0;
	private ServerSocket sock = null;
	private InetAddress loopback;
	
	private boolean stopMe = false;
	private boolean stopRunning = false;
	
	private XConfig config = null;
	
	protected XLauncherServer(Screen o) {
		this.o = o;
		try{
			this.loopback = InetAddress.getByName("127.0.0.1");
		}catch(UnknownHostException e){
			this.o.fatalError(this.toString()+" :: "+e.getMessage());
		}
		if(this.startServer()){
			this.o.debug("server up and running",XLauncherServer.classDebugLevel);
		}
	}
	
	private boolean startServer(){
		for(int i=CoreConfig.serverPortMin;i<=CoreConfig.serverPortMax;i++){
			this.o.debug("trying port "+i,XLauncherServer.classDebugLevel);
			try{
				this.sock = new ServerSocket(i,10,this.loopback);
				this.o.debug("port "+i+" OK!",XLauncherServer.classDebugLevel);
				this.serverPort = i;
				return true;
			}catch(IOException e){
				this.o.debug("port "+i+" failed!",XLauncherServer.classDebugLevel);
			}
		}
		return false;
	}
	
	protected int getServerPort() {
		return this.serverPort;
	}
	
	protected boolean stopServer(){
	    if(this.stopRunning){
	        this.o.debug(this.toString()+"::stopServer() - nothing to do :: stop allready running !",XLauncherServer.classDebugLevel);
	        return true;
	    }
	    this.stopRunning = true;
		this.o.debug(this.toString()+"::stopServer() @ "+System.currentTimeMillis(),XLauncherServer.classDebugLevel);
		if(this.sock==null || this.sock.isClosed()){
		    this.o.debug(this.toString()+"::stopServer() - nothing to do :: is closed",XLauncherServer.classDebugLevel);
		    this.sock = null;
		    return true;
		}
		this.o.debug(this.toString()+"::stopServer() WE MUST STOP IT @ "+System.currentTimeMillis(),XLauncherServer.classDebugLevel);
		try{
			this.sock.close();
			this.o.debug(this.toString()+"::stopServer socket:isClosed() :: "+this.sock.isClosed(),XLauncherServer.classDebugLevel);
			this.sock = null;
			this.o.debug(this.toString()+"::stopServer socket closed @ "+System.currentTimeMillis(),XLauncherServer.classDebugLevel);
		}catch(IOException e){
		    this.o.debug(this.toString()+"::stopServer() close() exception caught! "+e.toString(),XLauncherServer.classDebugLevel);
			this.o.debug(this.toString()+"::stopServer() : "+e.getMessage(),XLauncherServer.classDebugLevel);
			return false;
		}
		return true;
	}
	
	protected boolean isStopMe() {
		return this.stopMe;
	}

	protected void setStopMe(boolean stopMe) {
		this.stopMe = stopMe;
		if(this.stopMe){
			this.stopServer();
		}
	}
	
	protected synchronized XConfig getConfig() {
        return this.config;
    }
    protected synchronized void setConfig(XConfig config) {
        this.o.debug(this.toString()+"::setConfig()",XLauncherServer.classDebugLevel);
        this.config = config;
    }
    
    protected Screen getScreen() {
        return this.o;
    }
	
	public void run(){
	    this.o.debug(this.toString()+"::run() waiting for XConfig",XLauncherServer.classDebugLevel);
	    while(this.getConfig() == null){
	        if(this.sock.isClosed()) break;
	        try{
	            Thread.sleep(250);
	        }catch(InterruptedException e){
	            this.o.error(this.toString()+" :: "+e.getMessage());
	        }
	    }
	    this.o.debug(this.toString()+"::run() started",XLauncherServer.classDebugLevel);
		while(!this.sock.isClosed()){
			this.o.debug(this.toString()+"::run() waiting for connections ...",XLauncherServer.classDebugLevel);
			try{
				Socket s = this.sock.accept();
				InetAddress remote = s.getInetAddress();
				this.o.debug(this.toString()+" connect from "+remote.toString(),XLauncherServer.classDebugLevel);
				if(remote.equals(this.loopback)){
					this.o.debug("connection accepted",XLauncherServer.classDebugLevel);
					// detach new listener thread
					XLauncherServerListener l = new XLauncherServerListener(this.o,this,s);
					Thread lt = new Thread(l);
					lt.start();
				}else{
					this.o.debug("connection rejected",XLauncherServer.classDebugLevel);
					s.close();
				}
			}catch(IOException e){
			    if(this.stopRunning){
			        this.o.debug(this.toString()+"::"+e.getMessage()+" :: seems server is going down",XLauncherServer.classDebugLevel);
			    }else{
			        this.o.error(this.toString()+"::"+e.getMessage());
			        this.o.debug(this.toString()+"::run() - try stopServer on exit ...",XLauncherServer.classDebugLevel);
			        this.stopServer();
			    }
				break;
			}
		}
		this.o.debug(this.toString()+"::run() end",XLauncherServer.classDebugLevel);
	}
	
	protected void finalize() throws Throwable {
		this.stopServer();
		super.finalize();
	}
}
