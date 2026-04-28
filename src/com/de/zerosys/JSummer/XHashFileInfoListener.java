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

import org.eclipse.swt.graphics.Rectangle;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TextTransfer;

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
		
		int selectedColumn = -1;
        for (int col = 0; col < this.table.getColumnCount(); col++) {
            Rectangle rect = item.getBounds(col);
            if (rect.contains(pt)) {
                selectedColumn = col;
                break;
            }
        }
		
		int index = this.table.indexOf(item);
		XHashFile hf = this.getHashFileByIndex(index);
		this.o.debug(hf.getAbsName(),111);
		
		String oriText = null;
		if (selectedColumn == 2 && hf.getCalcStatus() >=2 && hf.getErrorCode() == 0) {
            oriText = item.getText(1);
            item.setText(1, "Copy to Clipboard");
            Clipboard clipboard = new Clipboard(Display.getDefault());
            String hashText = hf.getHashLine();
            clipboard.setContents(new Object[] { hashText }, new Transfer[] { TextTransfer.getInstance() });
            clipboard.dispose();
        }
		
		if (e.button == 1 || oriText != null) {
            // e.button == 1 is left   button
            // e.button == 2 is center button
            // e.button == 3 is right  button
            new XHashFileInfo(this.config,hf).open();
            if (oriText != null) {
                item.setText(1, oriText);
            }
        }
	}
	
	private XHashFile getHashFileByIndex(int index){
		return (XHashFile)this.config.hashFiles.elementAt(index);
	}
}
