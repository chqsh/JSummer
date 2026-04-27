/**
 * Package: com.de.zerosys.JSummer
 * Project: JSummer
 * Created on 17.07.2005
 * Author:	Klaus Zerwes
 * (c) 2005 - 2006 zero-sys.net
 * 
 * $Id: XHashFile.java,v 1.6 2006-03-24 10:47:57 io Exp $
 * 
 * This package is free software.
 * This software is licensed under the terms of the 
 * GNU General Public License (GPL), version 2.0 or later, 
 * as published by the Free Software Foundation. 
 * See http://www.gnu.org/copyleft/gpl.txt for the terms of the 
 * latest version of the GNU General Public License.
 */
package com.de.zerosys.JSummer;

import org.eclipse.swt.widgets.TableItem;


class XHashFile extends HashFile {

	private TableItem tableItem = null;
	
	XHashFile(String f, Config c) {
		super(f, c);
	}

	XHashFile(String f, Config c, String parent) {
		super(f, c, parent);
	}
	
	protected TableItem getTableItem() {
        return this.tableItem;
    }
    protected void setTableItem(TableItem tableItem) {
        this.tableItem = tableItem;
    }
}
