/*
 * $Id: XClose.java,v 1.7 2006-03-24 10:47:56 io Exp $
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

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Shell;

/**
 * @author zerwes
 */
class XClose extends SelectionAdapter {
    private Shell xshell;
    private XConfig config;
    
    protected static int classDebugLevel = 1;
    
    protected XClose(Shell s,XConfig conf){
        this.config = conf;
        this.xshell = s;
    }
    public void widgetSelected(SelectionEvent e){
    	this.config.getScreen().debug(this.toString()+" :: widgetSelected : "+e.toString(),XClose.classDebugLevel);
		this.xshell.close();
    }
}
