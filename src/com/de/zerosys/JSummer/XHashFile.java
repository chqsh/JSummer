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
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;

class XHashFile extends HashFile {
	final static String KEYNAME_STRING = "XHashFile";
	final static int defaultEditableColumn = 1;

	private TableItem tableItem = null;
	private TableEditor tableEditor = null;
	
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
        if (tableItem == null)
			this.tableItem.setData(KEYNAME_STRING, null);
		else
			tableItem.setData(KEYNAME_STRING, this);
		this.tableItem = tableItem;
    }

	/* Notice: getXHashFile() must run at EventDispatchThread,
		because tableItem.getData("KeyName") need EDT context.
	 */
	protected static XHashFile getXHashFile(TableItem tableItem) {
		return (XHashFile)tableItem.getData(KEYNAME_STRING);
	}

	protected boolean createEditor(int column) {
		if (this.tableItem == null) return false;
		
		final Table table = this.tableItem.getParent();
		if (this.tableEditor == null)
			this.tableEditor = new TableEditor(table);
		else {
			// Clean up any previous editor control
			Control oldEditor = this.tableEditor.getEditor();
			if (oldEditor != null) oldEditor.dispose();
		}

		final ProgressBar progressBar = new ProgressBar(table, SWT.SMOOTH);
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		progressBar.setSelection(0);
		progressBar.setSize(100, 10);

		this.tableEditor.grabHorizontal = true;
		this.tableEditor.grabVertical = true;
		// this.tableEditor.minimumWidth = progressBar.getSize().x;
		// this.tableEditor.minimumHeight= progressBar.getSize().y;
		this.tableEditor.horizontalAlignment = SWT.LEFT;
		this.tableEditor.setEditor(progressBar, this.tableItem, column);

		return true;
	}

	protected static boolean createEditor(TableItem tableItem) {
		XHashFile hf = getXHashFile(tableItem);
		if (hf != null)
			return hf.createEditor(defaultEditableColumn);
		else
			return false;
	}
    
	protected TableEditor getEditor() {
		return this.tableEditor;
    }
    
    protected Control getControl() {
		if (this.tableEditor != null)
			return this.tableEditor.getEditor();
		else
			return null;
    }

	protected static Control getControl(TableItem tableItem) {
		XHashFile hf = getXHashFile(tableItem);
		if (hf != null) {
			return hf.getControl();
		}
		return null;
	}

    protected int getColumn() {
		if (this.tableEditor != null)
			return this.tableEditor.getColumn();
		else
			return -1;
    }
    
    protected void disposeEditor(boolean detach) {
		if (this.tableEditor != null) {
			Control ctrl = this.tableEditor.getEditor();
			if (ctrl != null) ctrl.dispose();
			this.tableEditor.dispose();
			this.tableEditor = null;
		}
		if (detach && this.tableItem != null) {
			// Release reference
			this.tableItem.setData(KEYNAME_STRING, null);
			this.tableItem = null;
		}
    }

	protected static void disposeEditor(TableItem tableItem, boolean detach) {
		XHashFile hf = getXHashFile(tableItem);
		if (hf != null) {
			hf.disposeEditor(detach);
		}
	}
}
