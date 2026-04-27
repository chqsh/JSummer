/*
 * $Id: XAbout.java,v 1.4 2006-03-24 10:47:56 io Exp $
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

/**
 * @author zerwes
 */
class XAbout extends SelectionAdapter{
	
	private XConfig config;
	
	protected XAbout(XWindow xw){
		super();
		this.config = xw.getConfig();
	}
	
	public void widgetSelected(SelectionEvent e){
		this.config.getScreen().debug(e.toString(),111);
	    new XAboutWindow(this.config).open();
	}

}
