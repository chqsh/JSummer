/*
 * Package: com.de.zerosys.JSummer
 * Project: JSummer
 * Created on 17.07.2005
 * Author:	Klaus Zerwes
 * (c) 2005 - 2006 zero-sys.net
 * 
 * $Id: XLaunchXServerShutdown.java,v 1.4 2006-03-24 10:47:56 io Exp $
 * 
 * This package is free software.
 * This software is licensed under the terms of the 
 * GNU General Public License (GPL), version 2.0 or later, 
 * as published by the Free Software Foundation. 
 * See http://www.gnu.org/copyleft/gpl.txt for the terms of the 
 * latest version of the GNU General Public License.
 */
package com.de.zerosys.JSummer;

class XLaunchXServerShutdown extends Thread {

	private Screen o;
	private XLaunchXServer xl;
	
    private final int classDebugLevel = 5;
    
	XLaunchXServerShutdown(Screen o, XLaunchXServer xl) {
		super();
		this.o = o;
		this.xl = xl;
	}

    
    public void run(){
        this.o.debug(this.toString()+" -> XLauncherShutdown::run() @ "+System.currentTimeMillis(),this.classDebugLevel);
        this.xl.cleanup();
    }
}
