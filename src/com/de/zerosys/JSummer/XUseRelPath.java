/**
 * $Id: XUseRelPath.java,v 1.8 2006-03-24 10:47:56 io Exp $
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
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolItem;


class XUseRelPath extends SelectionAdapter {

	protected XWindow xw;
	protected Screen o;
	protected XConfig config;
	private final int classDebugLevel = 5;
	
	/**
	 * Constructor
	 */
	public XUseRelPath(XWindow xw) {
		super();
		this.xw = xw;
		this.config = this.xw.getConfig();
		this.o = this.config.getScreen();
	}
	
	public void widgetSelected(SelectionEvent e){
		this.o.debug(this+toString()+"::"+e.toString(),this.classDebugLevel);
		String caller = e.widget.getClass().getName();
		this.o.debug("caller:"+caller,this.classDebugLevel);
		if(caller.equals("org.eclipse.swt.widgets.ToolItem")){
			final ToolItem item = (ToolItem) e.widget;
			if(item.getSelection()){
				this.config.setUseRelPath(true);
			}else{
				this.config.setUseRelPath(false);
			}
		}else if (caller.equals("org.eclipse.swt.widgets.MenuItem")) {
			final MenuItem item = (MenuItem) e.widget;
			if(item.getSelection()){
				this.config.setUseRelPath(true);
			}else{
				this.config.setUseRelPath(false);
			}
		}else{
			this.o.error("unknown caller "+caller+" in "+this.toString());
		}
		this.o.debug(this+toString()+"::"+e.toString()+" DONE",this.classDebugLevel);
	}
}
