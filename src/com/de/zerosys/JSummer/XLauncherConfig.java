/*
* Package: com.de.zerosys.JSummer
* Project: JSummer
* Created on 17.07.2005
* Author:	Klaus Zerwes
* (c) 2005 - 2006 zero-sys.net
* 
* $Id: XLauncherConfig.java,v 1.10 2008-02-08 23:23:00 zerwes Exp $
* 
* This package is free software.
* This software is licensed under the terms of the 
* GNU General Public License (GPL), version 2.0 or later, 
* as published by the Free Software Foundation. 
* See http://www.gnu.org/copyleft/gpl.txt for the terms of the 
* latest version of the GNU General Public License.
*/
package com.de.zerosys.JSummer;



class XLauncherConfig extends CoreConfig {

	// use this for jdk1.5
	//protected Vector<String> hashFiles = new Vector<String>();
	
    /**
     * @param o
     */
    public XLauncherConfig(Screen o) {
        super(o);
    }

    /**
     * @param o
     * @param progName
     */
    public XLauncherConfig(Screen o, String progName) {
        super(o, progName);
    }
    
    
    //@Override
	protected void addFileDir(String f) {
        this.addHashFile(f);
    }
    
    
    //@SuppressWarnings("unchecked")
	//@Override
	protected void addHashFile(String f, String parent) {
    	CoreHashFile hf = new CoreHashFile(f,this,parent);
    	this.hashFiles.add(hf);
    }
    //@SuppressWarnings("unchecked")
	//@Override
	protected void addHashFile(String f) {
        //this.hashFiles.add(f);
        CoreHashFile hf = new CoreHashFile(f,this);
    	this.hashFiles.add(hf);
    }
}
