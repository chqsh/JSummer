/*
 * $Id: XClearTable.java,v 1.12 2008-02-08 23:23:00 zerwes Exp $
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author zerwes
 */
final class XClearTable extends SelectionAdapter{

    private XConfig config;
    private Screen o;
    private XWindow xw = null;
    
    private boolean clearTableDoit = false;
    
    protected static String ClearTableMsg = "Do you realy want stop hashcalculation and cleare the table?";
    protected static String ClearTableTitle = "Cleare table ...";
    
    private final int classDebugLevel = 5;
    
    protected XClearTable(XWindow xw){
        super();
        this.xw = xw;
        this.config = this.xw.getConfig();
        this.o = this.config.getScreen();
    }
    
    public void widgetSelected(SelectionEvent e){
    	this.o.debug(this.toString()+"::widgetSelected() : "+e.toString(),this.classDebugLevel);
    	this.clearTable();
    }
    
    protected void clearTable(){
    	this.clearTableDoit = false;
        this.o.debug("XClearTable selected",this.classDebugLevel);
        if(this.config.getMD5Files().size()==0){
            this.o.debug(this.toString()+"::nothing to clear",this.classDebugLevel);
            this.clearTableDoit = true;
            return;
        }
        if(this.xw!=null){
        	Shell xshell = this.xw.getShell();
        	MessageBox mb = new MessageBox(xshell,SWT.OK|SWT.CANCEL|SWT.ICON_QUESTION);
        	mb.setText(XClearTable.ClearTableTitle);
    		mb.setMessage(XClearTable.ClearTableMsg);
    		if(mb.open()!=SWT.OK){
    			this.o.debug("XClearTable aborted",this.classDebugLevel);
    			return;
    		}
        	this.xw.setStopHash(true);
        	this.xw.setEnableSaveFile(false);
        	
        	this.config.setRelativePath("");
        	this.config.setUseSysIndepPathSeparator(true);
        	this.config.setUseRelPath(true,true);
        	this.config.setCheckMDFile("");
        	this.config.setCheckMDFileEncoding(""); // Unknown Charset name
        	
        	this.xw.setUseRelPath(true);
        	this.xw.updateTitle();
        	
        	this.xw.setNumfilesLabel(0);
        	this.xw.setHashedfilesLabel(0);
        	this.xw.setErrorfilesLabel(0);
        }
        this.clearTableDoit = true;
        this.config.clearTable();
    }

	protected boolean isClearTableDoit() {
		return this.clearTableDoit;
	}
    
}
