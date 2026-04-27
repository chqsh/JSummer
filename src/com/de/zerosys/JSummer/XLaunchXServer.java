/*
 * Package: com.de.zerosys.JSummer
 * Project: JSummer
 * Created on 17.07.2005
 * Author:	Klaus Zerwes
 * (c) 2005 - 2006 zero-sys.net
 * 
 * $Id: XLaunchXServer.java,v 1.7 2006-03-24 10:47:56 io Exp $
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

class XLaunchXServer {

    protected final int classDebugLevel = 5;
	
    private final XScreen o = new XScreen(CoreConfig.DEFAULTDEBUG);
    private final XLauncherConfig config = new XLauncherConfig(this.o);
    
    private final File portFile = new File( XLaunchStarter.portFileString);
    
    private FileWriter fw = null;
	private XLauncherServer server = null;
	
	private long lastMod = 0;
	
	XLaunchXServer(String[] args) {
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		this.o.debug(this.toString()+" started "+System.currentTimeMillis(),this.classDebugLevel);
    	this.o.debug(this.toString()+" : args-count:"+args.length,this.classDebugLevel);
        if (this.config.configByArgs(args)){
		    this.o.println(this.config.getHelp());
			this.o.println();
			this.o.error(this.config.getConfigErrorMsg());
		}
        
        if(!this.portFileExists()){
        	//this.o.error("portfile is NOT THERE! : '"+this.portFile.getAbsolutePath()+"'");
        	if(!this.createFile()){
        		this.o.error("unable to create portFile '"+this.portFile.getAbsolutePath()+"'");
        	}
        }
        
        if(this.portFileExists()){
        	try{
        		this.fw = new FileWriter(this.portFile);
        		this.writeToFile(CoreConfig.serverInitString+":");
    			this.server = new XLauncherServer(this.o);
    			if(this.server.getServerPort()>0){
    				Thread serverThread = new Thread(this.server);
    				serverThread.start();
    				//serverThread.setPriority(Thread.MAX_PRIORITY);
    				this.writeToFile(":"+this.server.getServerPort()+System.getProperty("line.separator"));
    				this.closeWriter();
    				this.lastMod = this.portFile.lastModified();
    				this.o.debug(this.toString()+" lastMod set to "+this.lastMod+" @ "+System.currentTimeMillis(),this.classDebugLevel);
    				XLaunchXServerShutdown xlShutdown = new XLaunchXServerShutdown(this.o,this);
    				Runtime.getRuntime().addShutdownHook(xlShutdown);
    			}else{
    				this.server.stopServer();
    				this.server = null;
    			}
        	}catch (IOException e) {
        		this.o.error(this.toString()+"::"+e.getMessage());
			}
        }
        
        
        try{
        	this.o.debug(this.toString()+" starting XWindow for Server @ "
					+System.currentTimeMillis(),this.classDebugLevel);
            new XLauncherXWindow(args,this.server,this.o);
        }catch(NoClassDefFoundError e){
            this.o.error(e.getMessage());
            this.o.error(e.toString());
            e.printStackTrace();
        }
	}
	
	private boolean portFileExists(){
        return this.portFile.exists();
    }
	
	private boolean createFile(){
		boolean ret;
		try{
			ret = this.portFile.createNewFile();
		}catch(IOException e){
			this.o.error(this.toString()+"::"+e.getMessage());
			ret = false;
		}
		this.o.debug(this.toString()+"::createFile() returning "+ret,this.classDebugLevel);
        return ret;
    }

	
	private void closeWriter(){
	    if(this.fw != null){
	        this.o.debug(this.toString()+"::closeWriter()",this.classDebugLevel);
			try{
				this.fw.close();
				this.fw = null;
			}catch(IOException e){
				this.o.error(this.toString()+"::"+e.getMessage());
			}
		}
	}
	
	private boolean delFile(){
	    this.o.debug(this.toString()+"::delFile() @ "+System.currentTimeMillis(),this.classDebugLevel);
	    boolean ret = true;
		if(this.portFileExists()){
		    this.o.debug(this.toString()+"::delFile() - file exists",this.classDebugLevel);
		    if(this.lastMod == 0){
		        this.o.debug("THIS IS NOT MY FILE :: DO NOT DELETE IT",this.classDebugLevel);
		    }else{
		        long actualLastMod = this.portFile.lastModified();
		        if(this.lastMod != actualLastMod){
		            this.o.debug("SOMEONE HAS TAKEN THE FILE OVER :: DO NOT DELETE IT",this.classDebugLevel);
		            this.o.debug("lastMod:"+this.lastMod+" :: actualLastMod:"+actualLastMod,this.classDebugLevel);
		        }else{
		            this.o.debug(this.toString()+"::delFile() - delete() ...",this.classDebugLevel);
			        this.portFile.deleteOnExit();
			        ret = this.portFile.delete();
		        }
		    }
		}
		this.o.debug(this.toString()+"::delFile() @ "+ret,this.classDebugLevel);
		return ret;
	}
	
	private void writeToFile(String s){
		if(this.fw == null){
			this.o.fatalError(this.toString()+" :: can not write if writer is NULL!");
		}
		try{
			this.fw.write(s);
			this.fw.flush();
		}catch(IOException e){
			this.o.fatalError(this.toString()+"::"+e.getMessage());
		}
	}
	
	protected void cleanup(){
	    if(this.server != null){
	        this.o.debug(this.toString()+"::cleanup() :: we must stop server ...",this.classDebugLevel);
			this.server.setStopMe(true);
			this.server = null;
		}
		this.closeWriter();
		this.delFile();
	}
	
	protected void finalize() throws Throwable {
	    this.cleanup();
		super.finalize();
	}
}
