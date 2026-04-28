/*
 * $Id: XFileGutter.java,v 1.26 2006-03-24 10:47:56 io Exp $
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


import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ProgressBar;


class XFileGutter implements Runnable {
    
    XConfig config;
    protected Screen o;
    protected Table table;
    private Shell xshell;
    protected Display xdisplay;
    
    //private Thread nameThread;
    private Thread hashThread;
    //private boolean updateName = false;
    private boolean updateHash = false;
    
    protected static int updateDisplay = 500;
	
    private boolean stopMe = false;
    private boolean stopHash = true;
    private boolean hashRunning = false;
    private XWindow xwindow;
    
    private int classDebugLevel = 2;
    
    protected XFileGutter(XWindow xwin){
        this.xwindow = xwin;
        this.config = this.xwindow.getConfig();
        this.o = this.config.getScreen();
        this.o.debug(this.toString()+" started",this.classDebugLevel);
        this.xshell = this.xwindow.getShell();
        this.xdisplay = this.xshell.getDisplay();
    }

    public void run(){
    	this.o.debug(this.toString()+"::run() start ...",this.classDebugLevel);
    	this.setStopMe(false);
	    this.setStopHash(false);
	    this.o.debug(this.toString()+".run() started",this.classDebugLevel);
	    XFileGutterHashThread xfghashthread = new XFileGutterHashThread(this);
	    this.hashThread = new Thread(xfghashthread);
	    this.hashThread.start();
	    if(this.config.getMD5Files().size()>0){
			this.setUpdateHash(true);
		}
    	this.o.debug(this.toString()+"::run() end!",this.classDebugLevel);
    }
	
	protected void updateFileName(final TableItem tr,final HashFile f){
		this.o.debug(this.toString()+"::updateFileName() "+f.getName(),this.classDebugLevel);
	    this.xdisplay.asyncExec(new Runnable(){
            public void run(){
                if(tr==null){
                    return;
                }
                if(tr.isDisposed()){
                    return;
                }
                if(!tr.getText(0).equals(f.getName())){
                	tr.setText(0,f.getName());
        		}
            }
        });
	}
    
    protected void setProgress(final TableItem tr, final Control ctrl, float donePercent){
		if (donePercent < 0)
			return;
		int percent = Math.round(donePercent);
		percent = (percent > 100) ? 100:percent;
		setText(tr, donePercent+"%", 2, ctrl, percent);
    }
    
	protected void setResult(final TableItem tr,String txt){
		if (txt.matches("^[0-9A-Fa-f]+$"))
			txt = "OK"; // Defer actal work to following setText( HashValue ).
		this.setText(tr,txt,1,null,0);
    }
	protected void setText(final TableItem tr,final String txt){
		this.setText(tr,txt,2,null,0);
    }
	protected void setText(final TableItem tr,final String txt,final int col,
		final Control ctrl, int value){
        //this.o.debug("setText():"+col+": "+txt+" (old txt:"+tr.getText(col)+")",this.classDebugLevel);
		this.o.debug("setText():"+col+": "+txt,this.classDebugLevel);
        this.xdisplay.asyncExec(new Runnable(){
            public void run(){
                if(!tr.isDisposed()){
                    tr.setText(col,txt);
                    if (ctrl != null) {
						((ProgressBar)ctrl).setSelection(value);
					}
                }
            }
        });
    }
	protected void markError(final TableItem tr){
		this.o.debug(this.toString()+"::markError()",this.classDebugLevel);
        this.xdisplay.asyncExec(new Runnable(){
            public void run(){
                if(!tr.isDisposed()){
	                tr.setBackground(XFileGutter.this.config.getColorTableBGError());
	                tr.setForeground(XFileGutter.this.config.getColorTableFGError());
	                tr.setImage(XFileGutter.this.config.imgERR);
                }
            }
        });
    }
	
	protected void markTableRowDone(final TableItem tr){
		this.o.debug(this.toString()+"::markTableRowDone()",this.classDebugLevel);
		if(XConfig.useColors){
			this.xdisplay.asyncExec(new Runnable(){
	            public void run(){
	                if(!tr.isDisposed()){
	                    XFileGutter.this.o.debug("markTableRowDone()"+tr.getText(),7);
	                    tr.setForeground(XFileGutter.this.config.getColorTableFGDone());
	                    tr.setImage(XFileGutter.this.config.imgOK);
	                }
	            }
	        });
		}
	}
	
	protected void marcTableRowInitial(final TableItem tr){
		if(XConfig.useColors){
			this.xdisplay.asyncExec(new Runnable(){
	            public void run(){
	                if(!tr.isDisposed()){
	                    tr.setForeground(XFileGutter.this.config.getColorTableFGNew());
	                    tr.setBackground(XFileGutter.this.config.getColorTableBG());
	                    tr.setImage(XFileGutter.this.config.imgUNKNOWN);
	                }
	            }
	        });
		}
	}
	
	protected void markTableRowHashing(final TableItem tr){
		this.o.debug(this.toString()+"::markTableRowHashing()",this.classDebugLevel);
		if(XConfig.useColors){
			this.xdisplay.asyncExec(new Runnable(){
	            public void run(){
	                if(!tr.isDisposed()){
						tr.setForeground(XFileGutter.this.config.getColorTableFGHashing());
						tr.getParent().showItem(tr);
	                }
	            }
	        });
		}
	}
	

    protected synchronized void setStopMe(boolean stopMe) {
    	this.o.debug(this.toString()+"::setStopMe() "+stopMe,this.classDebugLevel);
        this.stopMe = stopMe;
		this.config.setStopMe(stopMe);
    	//this.setUpdateName(stopMe);
		this.xwindow.setUpdateName(stopMe);
    	notifyAll();
    }
    protected synchronized boolean isStopMe(){
		this.o.debug(this.toString()+"::isStopMe():"+this.stopMe,this.classDebugLevel);
        return this.stopMe;
    }
    protected synchronized boolean isStopHash() {
		//this.o.debug(this.toString()+"::isStopHash():"+this.stopHash,this.classDebugLevel);
        boolean ret = this.stopHash;
        if(ret){
            this.o.debug(this.toString()+"::setStopHash "+this.stopHash+" (inverted)",1);
            this.stopHash = false;
        }
        return ret;
    }
    protected synchronized void setStopHash(boolean stopHash) {
    	this.o.debug(this.toString()+"::setStopHash() ...",this.classDebugLevel);
    	if(stopHash && !this.isHashRunning()){
            this.o.debug(this.toString()+"::setStopHash true call, but no hash running :: ignored",this.classDebugLevel);
            return;
        }
        this.o.debug(this.toString()+"::setStopHash "+stopHash+" (was "+this.stopHash+")",this.classDebugLevel);
        this.stopHash = stopHash;
    }
    protected synchronized boolean isHashRunning(){
		this.o.debug(this.toString()+"::isHashRunning():"+this.hashRunning,this.classDebugLevel);
        return this.hashRunning;
    }
    protected synchronized void setHashRunning(boolean hashRunning){
		this.o.debug(this.toString()+"::setHashRunning():"+hashRunning,this.classDebugLevel);
        this.hashRunning = hashRunning;
    }

    
	protected XConfig getConfig() {
		return this.config;
	}

	protected Screen getScreen() {
		return this.o;
	}

	protected Table getTable() {
		return this.table;
	}

	protected Display getXdisplay() {
		return this.xdisplay;
	}

	protected Shell getXshell() {
		return this.xshell;
	}

	protected XWindow getXwindow() {
		return this.xwindow;
	}


	protected synchronized boolean isUpdateHash() {
		this.o.debug(this.toString()+"::isUpdateHash():"+this.updateHash,this.classDebugLevel);
		if(!this.updateHash){
			try{
                wait();
            }catch(InterruptedException e){
                this.o.error(this.toString()+"::isUpdateHash() wait()...:"+e.getMessage());
            }
            this.setUpdateHash(false);
		}
		return true;
	}

	protected synchronized void setUpdateHash(boolean updateHash) {
		this.o.debug(this.toString()+"::setUpdateHash():"+updateHash+" (was:"+this.updateHash+")",this.classDebugLevel);
		if(this.updateHash == updateHash){
			this.o.debug(this.toString()+"::setUpdateHash() repeated call ignored!",this.classDebugLevel);
			return;
		}
		this.updateHash = updateHash;
		notify();
	}
}
