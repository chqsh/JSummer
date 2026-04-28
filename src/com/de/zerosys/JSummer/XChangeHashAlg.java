/*
 * $Id: XChangeHashAlg.java,v 1.13 2008-03-06 11:01:14 zerwes Exp $
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

import java.util.Enumeration;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

class XChangeHashAlg extends SelectionAdapter{

	protected XConfig config;
    private Screen o;
    protected XWindow xw = null;
    protected String hash;
    
    private final int classDebugLevel = 5;

	XChangeHashAlg(XWindow xw,String hash) {
		super();
		this.hash = hash;
		this.xw = xw;
        this.config = this.xw.getConfig();
        this.o = this.config.getScreen();
	}
    
	public void widgetSelected(SelectionEvent e){
		this.o.debug(this.toString()+"::widgetSelected() :: "+e.toString(),this.classDebugLevel);
		this.actOnSelection();
		if(this.config.isConfigError()){
            e.doit = false;
            if (e.getSource() instanceof MenuItem) {
                MenuItem item = (MenuItem)e.getSource();
                item.setSelection(false);
                item.setEnabled(false);
            }
            this.config.clearConfigError();
		}
	}
	
	protected void actOnSelection(){
		this.actOnSelection(false);
	}
	protected void actOnSelection(boolean isfirst){
		this.o.debug(this.toString()+"::actOnSelection() :: "+this.hash,this.classDebugLevel);
		
		if(!isfirst){
			if(this.config.isSamehashAlg(this.hash)){
				this.o.debug("switch actual hashalg ignored",this.classDebugLevel);
				this.setSelectionForHash();
				return;
			}
		}
		this.config.setHashSum(this.hash);
		if(this.config.isConfigError()){
			this.o.error(this.config.getConfigErrorMsg());
			return;
		}
		
		if(!isfirst){
			if(!(this.config.getCheckFile().equals("")) &&  this.config.getMD5Files().size()>0){
				Shell xshell = this.xw.getShell();
				MessageBox mb = new MessageBox(xshell,SWT.OK|SWT.CANCEL|SWT.ICON_QUESTION);
	        	mb.setText("recalculate hash");
	    		mb.setMessage("Do you want to calculate a new hash for the files from the checkfile '"+this.config.getCheckFile()+"'?");
	    		if(mb.open()!=SWT.OK){
	    			new XClearTable(this.xw).clearTable();
	    		}
			}
			if(this.config.getMD5Files().size()>0){
				this.config.setCheckMDFile("");
				this.xw.setStopHash(true);
				Enumeration<CoreHashFile> fe = this.config.getMD5Files().elements();
				while(fe.hasMoreElements()){
					XHashFile f = (XHashFile)fe.nextElement();
					f.reset();
					this.xw.getXfg().setText(f.getTableItem(),"");
					this.xw.getXfg().markTableRowInitial(f.getTableItem());
					this.o.debug(this.toString()+" reset done for "+f.getAbsName(),this.classDebugLevel);
				}
				this.xw.getTable().update();
				this.xw.getTable().redraw();
			}
		}
		
		this.setSelectionForHash();
		this.xw.setStopHash(false);
		this.xw.setUpdateHash(true);
		this.xw.updateTitle();
		
		this.o.debug(this.toString()+"::actOnSelection() done", this.classDebugLevel);
	}
	
	
	private void setSelectionForHash(){
		this.xw.getShell().getDisplay().asyncExec(new Runnable(){
            public void run(){
            	Enumeration<HashSumInfo> hie = XChangeHashAlg.this.config.containerHashSumInfo.elements();
        		while(hie.hasMoreElements()) {
        			HashSumInfo hsi = hie.nextElement();
        			boolean sel = false;
        			if(XChangeHashAlg.this.hash.equals(hsi.getHashAlgName())) {
        				sel = true;
        			}
        			MenuItem mit = (MenuItem) XChangeHashAlg.this.xw.Hash2MenuMenuItem.get(hsi.getHashAlgName());
        			mit.setSelection(sel);
        			MenuItem mitTool = (MenuItem) XChangeHashAlg.this.xw.Hash2ToolMenuItem.get(hsi.getHashAlgName());
        			mitTool.setSelection(sel);
        		}
            }
        });
	}
}
