/*
 * $Id: XFileGutterNameThread.java,v 1.8 2006-03-24 10:47:57 io Exp $
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

import java.util.Enumeration;


class XFileGutterNameThread implements Runnable {

	private XConfig config;
    protected Screen o;
    private XFileGutter xfg;
	private XWindow xw;
    
    private final int classDebugLevel = 10;
    
	XFileGutterNameThread(XWindow xw) {
		super();
		this.xw = xw;
		this.o = this.xw.getScreen();
		this.config = this.xw.getConfig();
		this.xfg = this.xw.getXfg();
	}
	
	public void run(){
		this.o.debug(this.toString()+"::run() started",this.classDebugLevel);
		while(this.xw.isUpdateName()){
			this.o.debug(this.toString()+" going round ...",this.classDebugLevel);
			if(this.xfg.isStopMe()||this.xfg.getXdisplay().isDisposed()){
				this.o.debug(this.toString()+" going away ...",this.classDebugLevel);
				return;
			}
			Enumeration fe = this.config.getMD5Files().elements();
			while(fe.hasMoreElements()){
				if(this.xfg.isStopMe()||this.xfg.getXdisplay().isDisposed()){
					this.o.debug(this.toString()+" fileloop : going away ...",this.classDebugLevel);
					return;
				}
				XHashFile f = (XHashFile)fe.nextElement();
				this.xfg.updateFileName(f.getTableItem(),f);
			}
			this.xw.setUpdateName(false);
		}
		this.o.debug(this.toString()+"::run() end!",this.classDebugLevel);
	}
}
