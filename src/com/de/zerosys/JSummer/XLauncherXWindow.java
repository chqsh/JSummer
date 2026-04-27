/**
 * $Id: XLauncherXWindow.java,v 1.7 2006-03-24 10:47:57 io Exp $
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


class XLauncherXWindow extends XWindow {

    private XLauncherServer xls = null;
    
    /**
     * @param args
     * @param xls
     * @param o
     */
    protected XLauncherXWindow(String[] args, XLauncherServer xls, XScreen o) {
        this.o = o;
	    this.xls = xls;
	    this.o.debug(this.toString()+" started ...",this.classDebugLevel);
	    this.args = args;
	    
	    this.o.debug(this.toString()+" calling _init()",this.classDebugLevel);
	    String progName = "XS";
	    if(this.xls != null) progName += "+";
	    this._init(progName);
	    this.o.debug(this.toString()+" done _init()",this.classDebugLevel);
	    
	    if(this.xls != null) this.xls.setConfig(this.config);
	    
	    this._startx();
	    
	    this.o.debug(this.toString()+" XServer going down DOWN !!!",this.classDebugLevel);
	    this.xls.setStopMe(true);
    }


    protected void libError(UnsatisfiedLinkError e){
        this.o.error(e.getMessage());
	    this.o.debug("try to stop xls server ...",this.classDebugLevel);
	    this.xls.stopServer();
	    this.o.fatalError("unable to find swt-libs");
    }
    
    protected void setScreen(){
        this.o.debug(this.toString()+"::setScreen() - setting xshell ...",this.classDebugLevel);
        this.o.setXShell(this.xshell);
    }
}
