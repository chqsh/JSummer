/*
 * $Id: XAddFile.java,v 1.13 2006-03-24 10:47:56 io Exp $
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;


class XAddFile extends SelectionAdapter{
    
    private XWindow xw;
    private Config config;
    private Shell xshell;
    private Screen o;
    private final int classDebugLevel = 1;
    
	
    protected XAddFile(XWindow xw){
		super();
		this.xw = xw;
		this.config = this.xw.getConfig();
		this.o = this.config.getScreen();
		this.xshell = this.xw.getShell();
    }
    
    public void widgetSelected(SelectionEvent e){
        this.o.debug(this.toString()+" :: widgetSelected()",this.classDebugLevel);
        boolean clearTable = false;
        if(!this.config.getCheckFile().equals("")){
			this.o.debug("table must be cleared",this.classDebugLevel);
			MessageBox mb = new MessageBox(this.xshell,SWT.OK|SWT.CANCEL|SWT.ICON_QUESTION);
			mb.setText("CLEAR TABLE");
			mb.setMessage("should the table be cleared?");
			if(mb.open()!=SWT.OK){
				this.o.debug("table.clear aborted",this.classDebugLevel);
				return;
			}
			clearTable = true;
		}
		this.config.setCheckMDFile("");
		final FileDialog fd = new FileDialog(this.xshell,SWT.OPEN|SWT.MULTI);
        fd.setText("select file(s) to hash ...");
        fd.open();
        String[] files = fd.getFileNames();
        if(files.length>0 && clearTable){
            final XClearTable xclrtab = new XClearTable(this.xw);
			xclrtab.widgetSelected(e);
        }
        for(int i = 0; i<files.length;i++){
            this.config.addFileDir(fd.getFilterPath()+System.getProperty("file.separator")+files[i]);
        }
	}
}
