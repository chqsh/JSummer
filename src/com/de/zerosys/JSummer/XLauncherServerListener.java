/*
* Package: com.de.zerosys.JSummer
* Project: JSummer
* Created on 17.07.2005
* Author:	Klaus Zerwes
* (c) 2005 - 2006 zero-sys.net
* 
* $Id: XLauncherServerListener.java,v 1.9 2006-03-24 10:47:56 io Exp $
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

/*
 * this reads the new files from sock and passes them to XWindow
 * some short summary of the protocol:
 * 	- client connectes 
 * 	- server sends '+CoreConfig.serverInitString'
 * 	- client sends same string
 * 	- server sends '+OK'
 * 	- client sends +filename
 * 	- server sends +OK
 *  - +file +ok may be repeated
 *  - client sends "-BYBY"
 *  - server sends "-OK" and closes conn
 */
class XLauncherServerListener extends XLauncherSockRW implements Runnable {
	
    private XLauncherServer xls;
    
    private final int classDebugLevel = 5;
    
	protected XLauncherServerListener(Screen o,XLauncherServer xls, Socket s) {
		super(o, s);
	    this.xls = xls;
		this.o.debug(this.toString()+" initialized",this.classDebugLevel);
	}

	public void run(){
		this.o.debug(this.toString()+"::run()",this.classDebugLevel);
		final String initSend = "+"+CoreConfig.serverInitString;
		this.sendLine(initSend);
		String response = this.readLine();
		this.o.debug(this.toString()+": test response",this.classDebugLevel);
		this.o.debug(response+" :=: "+initSend,this.classDebugLevel);
		if(response.equals(initSend)){
			this.sendLine("+OK");
			while (true){
				response = this.readLine();
				if(response.startsWith("+") && response.length()>1){
					final String file = response.substring(1);
					while(this.xls.getConfig()==null){
					    this.o.debug(this.toString()+"::run() waiting for XConfig",this.classDebugLevel);
					    try{
				            Thread.sleep(250);
				        }catch(InterruptedException e){
				            this.o.error(this.toString()+" :: "+e.getMessage());
				        }
					}
					/*
					 * this must be done in a separate thread!
					 * Problem: for example we get here a single file wich is a
					 * 	dirctory with a huge number of small files
					 * 	the function WILL NOT RETURN UNTILL the dir is recursed!
					 */
					XLauncherServerListenerConfigConnector cc = new XLauncherServerListenerConfigConnector(this.o,this.xls,file);
					Thread ccthread = new Thread(cc);
					ccthread.setDaemon(true);
					ccthread.start();
					this.o.debug(ccthread.toString()+" isDeamon:"+ccthread.isDaemon(),this.classDebugLevel);
					this.sendLine("+OK");
				}else{
				    if(response.equals("-BYBY")){
				        this.sendLine("-OK");
				    }else{
				        this.o.error(this.toString()+" unknown response '"+response+"'");
				    }
				    break;
				}
			}
		}
		super.close();
	}
}
