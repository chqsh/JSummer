/*
 * Package: com.de.zerosys.JSummer
 * Project: JSummer
 * Created on 20.06.2005
 * Author:	Klaus Zerwes
 * (c) 2005 - 2006 zero-sys.net
 * 
 * $Id: XAddDir.java,v 1.14 2008-03-05 17:50:18 zerwes Exp $
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
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;



class XAddDir extends SelectionAdapter{
    
    private XWindow xw;
    private XConfig config;
    private Shell xshell;
    private Screen o;
    private final int classDebugLevel = 1;
    
    protected XAddDir(XWindow xw){
		super();
		this.xw = xw;
		this.config = this.xw.getConfig();
		this.o = this.config.getScreen();
		this.xshell = this.xw.getShell();
    }
    
    public void widgetSelected(SelectionEvent e){
        this.o.debug(this.toString()+"::"+e.toString(),this.classDebugLevel);
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
        final DirectoryDialog dd = new DirectoryDialog(this.xshell,SWT.OPEN|SWT.MULTI);
        dd.setText("select directory to hash recursively ...");
        dd.setMessage("select directory to hash recursively");
        if(this.config.isWindows()) dd.setFilterPath(System.getProperty("user.dir"));
        final String dir = dd.open();
        if(dir!=null){
            if(clearTable){
    			final XClearTable xclrtab = new XClearTable(this.xw);
    			xclrtab.widgetSelected(e);
	        }
            this.o.debug("selected directory: '"+dir+"'",this.classDebugLevel);
			//this.addDir(dir);
            this.config.addFileDir(dir);
        }
		this.o.debug(this.toString()+"::"+e.toString()+" end",this.classDebugLevel);
    }
    /*
     * PROBLEM:
     * if we start a new thread, the method XConfig::XaddHashFile) angs on syncExec on exit.
	private void addDir(final String dir){
		final String callerName = this.toString();
		Thread t = new Thread(new Runnable(){
			public void run(){
				config.numAddFileInRunAdd();
				config.getScreen().debug(callerName+":: thread run() started "+this.toString(),classDebugLevel);
				config.addFileDir(dir);
				config.getScreen().debug(callerName+":: thread run() end "+this.toString(),classDebugLevel);
				config.numAddFileInRunSub();
			}
		});
		t.setPriority(Thread.MAX_PRIORITY);
		t.start();
	}*/
}
