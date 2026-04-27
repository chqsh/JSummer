/*
 * $Id: XHashFileInfoListener.java,v 1.4 2006-03-24 10:47:56 io Exp $
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


import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;


/*
 * see http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet3.java?rev=HEAD&content-type=text/vnd.viewcvs-markup
 */
class XHashFileInfoListener implements Listener{
	
	private Screen o;
	private Table table;
	private XConfig config;
	
	XHashFileInfoListener(XConfig config){
		this.config = config;
		this.o = config.getScreen();
		this.table = config.table;
		this.o.debug(this.toString()+"::ready ...",111);
	}
	
	public void handleEvent(Event e){
		this.o.debug(this.toString()+"::event "+e.toString(),111);
		Point pt = new Point(e.x, e.y);
		TableItem item = this.table.getItem(pt);
		if (item == null) return;
		
		int index = this.table.indexOf(item);
		XHashFile hf = this.getHashFileByIndex(index);
		this.o.debug(hf.getAbsName(),111);
		new XHashFileInfo(this.config,hf).open();
	}
	
	private XHashFile getHashFileByIndex(int index){
		return (XHashFile)this.config.hashFiles.elementAt(index);
	}
}
