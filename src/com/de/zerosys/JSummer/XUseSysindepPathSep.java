/*
 * Package: com.de.zerosys.JSummer
 * Project: JSummer
 * Created on 27.06.2005
 * Author:	Klaus Zerwes
 * (c) 2005 - 2006 zero-sys.net
 * 
 * $Id: XUseSysindepPathSep.java,v 1.4 2006-03-24 10:47:56 io Exp $
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
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;



class XUseSysindepPathSep extends SelectionAdapter {

	protected XWindow xw;
	protected Screen o;
	protected XConfig config;
	private final int classDebugLevel = 5;
	
	/**
	 * Constructor
	 */
	public XUseSysindepPathSep(XWindow xw) {
		super();
		this.xw = xw;
		this.config = this.xw.getConfig();
		this.o = this.config.getScreen();
	}
	
	public void widgetSelected(SelectionEvent e){
		this.o.debug(e.toString(),this.classDebugLevel);
		String caller = e.widget.getClass().getName();
		this.o.debug("caller:"+caller,this.classDebugLevel);
		/*if(caller.equals("org.eclipse.swt.widgets.ToolItem")){
			final ToolItem item = (ToolItem) e.widget;
			if(item.getSelection()){
				this.config.setUseSysIndepPathSeparator(true,false);
				this.xw.setMenuUseSysindepPath(true);
			}else{
				this.config.setUseSysIndepPathSeparator(false,false);
				this.xw.setMenuUseSysindepPath(false);
			}
		}else if (caller.equals("org.eclipse.swt.widgets.MenuItem")) {
			final MenuItem item = (MenuItem) e.widget;
			if(item.getSelection()){
				this.config.setUseSysIndepPathSeparator(true,false);
				this.xw.setToolbarUseSysindepPath(true);
			}else{
				this.config.setUseSysIndepPathSeparator(false,false);
				this.xw.setToolbarUseSysindepPath(false);
			}
		}else{
			this.o.error("unknown caller "+caller+" in "+this.toString());
		}*/
		if(caller.equals("org.eclipse.swt.widgets.ToolItem")){
			final ToolItem item = (ToolItem) e.widget;
			if(item.getSelection()){
				this.config.setUseSysIndepPathSeparator(true);
			}else{
				this.config.setUseSysIndepPathSeparator(false);
			}
		}else if (caller.equals("org.eclipse.swt.widgets.MenuItem")) {
			final MenuItem item = (MenuItem) e.widget;
			if(item.getSelection()){
				this.config.setUseSysIndepPathSeparator(true);
			}else{
				this.config.setUseSysIndepPathSeparator(false);
			}
		}else{
			this.o.error("unknown caller "+caller+" in "+this.toString());
		}
	}

}
