/*
 * $Id: XFileGutterHashThread.java,v 1.14 2008-03-06 11:01:15 zerwes Exp $
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

import org.eclipse.swt.widgets.TableItem;


class XFileGutterHashThread implements Runnable {

	private XConfig config;
    protected Screen o;
    private XFileGutter xfg;
    private XWindow xw;
    
    private final int classDebugLevel = 4;
    
    XFileGutterHashThread(XFileGutter xfg) {
		super();
		this.xfg = xfg;
		this.xw = this.xfg.getXwindow();
		this.o = this.xfg.getScreen();
		this.config = this.xfg.getConfig();
	}
    
	public void run(){
		this.debug("run() started ...");
		hashloop: while(this.xfg.isUpdateHash()){
			//this.config.debugMem(this.toString()+"::hashloop");
			if(this.decideStopThread()){
				this.debug("going away ... decideStopThread 1");
				break hashloop;
			}
			this.debug("going round ...");
			
			Enumeration<CoreHashFile> fe = this.config.getMD5Files().elements();
            int filecount = 0;
            int errorcount = 0;
            this.xw.setHashedfilesLabel(filecount);
            this.xw.setErrorfilesLabel(errorcount);
            fileloop: while(fe.hasMoreElements()){
                if(this.decideStopThread()){
					this.debug(this.toString()+"going away ... decideStopThread 2");
					break hashloop;
				}
				
                XHashFile f = (XHashFile)fe.nextElement();
				TableItem tr = f.getTableItem();
				
				filecount++;
				
				this.debug("fileloop for file "+f.toString()+" ("+f.getFileName()+") calcStatus:"+f.getCalcStatus());
				
				if(tr==null){
					this.o.debug("--------------------------------------------------------------",0);
					this.o.debug("hashfile w/ no tableItem!!! '"+f.getAbsName()+"' :: "+f.toString(),0);
					this.o.debug("--------------------------------------------------------------",0);
					// TODO: HM !!! WHATS BEST HERE? maybe sleep a little bit?
					// FIXME: so the situation will never happen !!!
					try{
						Thread.sleep(100);
					}catch(InterruptedException e){
						this.o.error(this.toString()+" :: "+e.getMessage());
					}
					this.xfg.setUpdateHash(true);
					break;
				}
				if(f.resetDone()){
					this.debug(this.toString()+" file "+f.getFileName()+" unset reset-mark");
				}
				
				if (f.getCalcStatus()!=0) {
                    if ( (f.isCheck() && ! f.istCheckresultOK()) 
					  || (f.getErrorCode()>0)) {
                        errorcount++;
						this.xw.setErrorfilesLabel(errorcount);
					}
				}
				
				if(f.getCalcStatus()==0){
				    this.debug(this.toString()+" - start thread "+f.getAbsName());
				    this.xw.setEnableSaveFile(false);
				    this.xfg.markTableRowInitial(tr);
				    Thread ft = new Thread(f);
				    this.setHashIsRunning(true);
					ft.start();
					
					this.xfg.markTableRowHashing(tr);
					
					while(f.getCalcStatus()<2){
						if(f.resetDone()){
							this.debug(this.toString()+"::calcloop broken :: file reset:"+f.getFileName());
							this.setHashIsRunning(false);
							continue hashloop;
						}
						if(this.decideStopThread()){
							this.debug(this.toString()+" going away ... decideStopThread 3 file:"+f.getFileName());
							f.setStopMe(true);
							this.setHashIsRunning(false);
							break hashloop;
						}
						if(this.decideStopHash()){
							f.setStopMe(true);
							this.debug(this.toString()+" breaking fileloop at file:"+f.getFileName());
							this.setHashIsRunning(false);
							break fileloop;
						}
						long sleep = XFileGutter.updateDisplay;
						long edtime = f.getEstimatedDoneTime();
						if(sleep > edtime){
							sleep = edtime;
						}else{
							//this.xfg.setText(f.getTableItem(),f.getDonePercent()+"%");
							this.xfg.setProgress(f.getTableItem(),f.getDonePercent());
						}
						try{
							Thread.sleep(sleep);
						}catch(InterruptedException e){
							this.o.error(this.toString()+" sleep : "+e.getMessage());
						}
					}
					this.debug(this.toString()+" finished "+f.getAbsName());
					this.xfg.setProgress(f.getTableItem(),f.getDonePercent());
					this.xfg.setResult(f.getTableItem(),f.getHashResult());
					this.xfg.setText(f.getTableItem(),
						(f.getErrorCode()>0) ? "" : f.getHash());
                    if(f.isCheck() && ! f.istCheckresultOK()){
                        errorcount++;
                        this.xw.setErrorfilesLabel(errorcount);
						this.xfg.markError(f.getTableItem());
					}else{
                        if(f.getErrorCode()>0){
                        	// maybe errormsg?
                        	this.o.debug(this.toString()
                        			+" errorcode:"+f.getErrorCode()
                        			+"; errormsg:"+f.getErrorMsg());
                            errorcount++;
                            this.xw.setErrorfilesLabel(errorcount);
                            this.xfg.markError(f.getTableItem());
                        }else{
                        	this.xfg.markTableRowDone(f.getTableItem());
                        }
					}
					this.setHashIsRunning(false);
					/* [20260405] Can not recover progressBar once it is disposed.
					 * But, only when the progressBar is disposed, the text of the table item can be displayed.
					 * (Move to markTableRowDone(), markError() or setResult())
					 */
				}
				if(this.decideStopThread()){
					this.debug(this.toString()+" going away ...");
					break hashloop;
				}
                
				this.xw.setHashedfilesLabel(filecount);
                
                Thread.yield();
			}
			this.debug(this.toString()+"::fileloop end!");
			
			this.xfg.setUpdateHash(false);
			if(filecount > 0) {
				this.xw.setEnableSaveFile(true);
			}
		}
		this.debug(this.toString()+"::run() end!",this.classDebugLevel);
	}

	private boolean decideStopHash(){
		boolean ret = this.xfg.isStopMe()||this.xfg.getXdisplay().isDisposed()||this.xfg.isStopHash();
		this.debug(this.toString()+"::decideStopHash() : "+ret);
		return ret;
	}
	
	private boolean decideStopThread(){
		boolean ret = this.xfg.isStopMe()||this.xfg.getXdisplay().isDisposed();
		this.debug(this.toString()+"::decideStopThread() : "+ret);
		return ret;
	}
	
	protected synchronized boolean isHashIsRunning(){
		//this.o.debug(this.toString()+"::isHashIsRunning()",this.classDebugLevel);
		//return this.hashIsRunning;
		return this.xfg.isHashRunning();
	}

	protected synchronized void setHashIsRunning(boolean hashIsRunning) {
		//this.o.debug(this.toString()+"::setHashIsRunning() : "+hashIsRunning,this.classDebugLevel);
		//this.hashIsRunning = hashIsRunning;
		this.xfg.setHashRunning(hashIsRunning);
		
	}

	protected XConfig getConfig(){
		return this.config;
	}

	protected XFileGutter getXfg() {
		return this.xfg;
	}
	
	private void debug(String _m){
		this.debug(_m,this.classDebugLevel);
	}
	private void debug(String _m, int _l){
		if (_l > this.o.getDebugLevel()){
			return;
		}
		this.o.debug("XFileGutterHashThread::"+_m,_l);
	}
}
