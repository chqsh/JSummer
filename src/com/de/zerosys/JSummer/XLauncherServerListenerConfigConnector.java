/*
* Package: 	com.de.zerosys.JSummer
* Project: 	JSummer
* Created: 	17.07.2005
* Author: 	Klaus Zerwes
* (c) 2005 - 2006 	zero-sys.net
* 
* $Id: XLauncherServerListenerConfigConnector.java,v 1.6 2006-03-24 10:47:57 io Exp $
* 
* This package is free software.
* This software is licensed under the terms of the 
* GNU General Public License (GPL), version 2.0 or later, 
* as published by the Free Software Foundation. 
* See http://www.gnu.org/copyleft/gpl.txt for the terms of the 
* latest version of the GNU General Public License.
*/
package com.de.zerosys.JSummer;

/*
 * simple connector to XConfig
 * implemented as separateThread, so the XLauncherServerConnector 
 * will not be locked up
 */
class XLauncherServerListenerConfigConnector implements Runnable {
	private XLauncherServer xls;
	private Screen o;
	private String file;
    
    private final int classDebugLevel = 5;
    
	protected XLauncherServerListenerConfigConnector(Screen o, XLauncherServer xls, String file) {
		this.o = o;
		this.xls = xls;
		this.file = file;
	}
	
	public void run(){
		this.o.debug(this.toString()+"::run() for file '"+this.file+"'",this.classDebugLevel);
		this.xls.getConfig().addFileDirFocus(this.file);
		this.o.debug(this.toString()+"::run() done",this.classDebugLevel);
	}
}
