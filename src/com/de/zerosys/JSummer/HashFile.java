/**
 * $Id: HashFile.java,v 1.39 2008-03-05 11:56:16 zerwes Exp $
 * 
 * (c) 2005 - 2008 Klaus Zerwes zero-sys.net
 * 
 * This package is free software.
 * This software is licensed under the terms of the 
 * GNU General Public License (GPL), version 2.0 or later, 
 * as published by the Free Software Foundation. 
 * See http://www.gnu.org/copyleft/gpl.txt for the terms of the 
 * latest version of the GNU General Public License.
 */
package com.de.zerosys.JSummer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


class HashFile extends CoreHashFile implements Runnable{
	
	//@SuppressWarnings("hiding")
	protected Config config;
	
	
	/**
     * signals the status of the HASH-proc
     * Values >= 10 signal a error
     * 0 - not jet started
     * 1 - running
     * 2 - done
     */
    private int calcStatus = 0;
	
	private long procTime = 0;
	private long callerProcTime = 0;
	private long startTime = 0;
	private long eProcTime = 250;
	private float donePercent = 0;
	
	private String hash = "";
	private String checkhash = "";
	private String checkResult = "";
	private boolean checkResultBool = false;
	
	
	private boolean stopMe = true;
	private boolean isReset = false;
	
	
	HashFile (String f, Config c){
		super(f,c);
        this.config = c;
	}
	
	HashFile (String f,Config c,String parent){
		super(f,c,parent);
        this.config = c;
	}
	
	protected boolean resetDone(){
		if(this.isReset){
			this.isReset = false;
			return true;
		}
		return false;
	}
	protected void reset(){
		this.debug("reset()");
		this.setStopMe(true);
		while(this.calcStatus==1){
			this.debug(" reset() waiting for stop");
			try{
				Thread.sleep(10);
			}catch(InterruptedException e){
				this.o.fatalError(this.toString()+"::reset() : "+e.getMessage());
			}
			this.debug("reset() waiting for stop ... RETRY");
		}
		this.calcStatus = 0;
		this.procTime = 0;
		this.callerProcTime = 0;
		this.startTime = 0;
		this.eProcTime = 250;
		this.donePercent = 0;
		this.hash = "";
		this.checkhash = "";
		this.checkResult = "";
		this.setStopMe(false);
		this.debug("reset() reset all for file");
		this.isReset = true;
	}
	
    
	protected long getEstimatedDoneTime(){
		long ret = this.eProcTime/4;
		this.debug("getEstimatedDoneTime : "+ret);
		if(this.eProcTime>1000){
			ret = 1000;
		}else{
			if(this.eProcTime>500){
				ret = 400;
			}
		}
		if(ret<1) ret = 2;
		this.debug("getEstimatedDoneTime returning "+ret);
		return ret;
	}
	
	
	protected boolean doHashSum(){
	    this.debug("starting doHashSum() ...");
		if(this.getErrorCode()>0){
			this.checkResult = "ERROR: Code "+this.getErrorCode()+" '"+this.getErrorMsg()+"'";
			this.hash = this.checkResult;
			this.calcStatus = 2;
			return false;
		}
	    this.setStopMe(false);
		this.calcStatus = 1;
		HashSum hs = this.config.getHashSum();
		if (hs == null) return false;
		hs.reset(); // Clear State
		this.startTime = System.currentTimeMillis();
		long thisTime = this.startTime;
		try{
			FileInputStream fis = new FileInputStream(this.file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			byte[] buffer = hs.getBuffer();
			int len = 0;
			long donesize = 0;
			try{
				while ((len = bis.read(buffer)) > -1) {
				    if(this.isStopMe()){
				        this.debug("stop forced !!!");
				        break;
				    }
				    thisTime = System.currentTimeMillis();
                    hs.update(buffer,0,len);
                    donesize = donesize + len;
                    // FILESIZE MAY BE ZERO !!!!!
                    if (this.fileSize > 0){
                    	int p = 10; // precision - numbers after decimal-sep
                    	// the actual value is (donesize*100)/this.fileSize)
                    	// casting this to float and multiply with p
                    	// round and divide by p
                    	// cast it to float
                        this.donePercent = ((float)Math.round(((float)(donesize*100)/this.fileSize)*p)/p); // simple formater
                    }else{
                        this.donePercent = donesize;
                    }
                    if(this.donePercent>100){
                        // !!!! this means the file has grown!
                        this.debug("!!!!!!! percent tooooo big !!!!"+this.fileSize+":"+donesize,1);
                        this.debug("ERROR : file:"+this.getAbsName()+" : it seems another proc has this file open!!");
                        this.eProcTime = 0;
                        this.errorCode = 101;
                        this.errorMsg = "Filesize has changed during hashcalculation!";
                        break;
                    }
                    if(donesize>0){
                        this.eProcTime = ((thisTime-this.startTime)*(this.fileSize-donesize))/donesize;
                    }
					
					this.debug(donesize + " bytes read : "+this.donePercent+" done",100);
					this.debug("remaining time: "+this.eProcTime+" :: elapsed time: "+(thisTime-this.startTime),100);
				}
				
				if(this.getErrorCode() == 0 && donesize != this.fileSize){
					this.o.debug(this.toString()+":"+this.getAbsName()
							+" size changed! "
							+donesize+" :: "+this.fileSize);
                    this.errorCode = 102;
                    this.errorMsg = "Filesize has changed during hashcalculation!";
				}
				
				if(this.getErrorCode()>0){
					this.calcStatus = 2;
					bis.close();
					fis.close();
					return false;
				}
				
				this.procTime = System.currentTimeMillis() - this.startTime;
				
				this.hash = hs.hexit(hs.digest(),this.config.useUpperCaseHash());
				
				if(!this.checkhash.equals("")){
                    if(this.checkhash.equalsIgnoreCase(this.hash)){
                        this.checkResult = "OK";
                        this.checkResultBool = true;
                    }else{
						this.checkResult = "ERROR";
                    }
                }

				bis.close();
				try{
					fis.close();
				}catch(IOException e){
					boolean setError = true;
					// mingw native build sets this error !!! GNU libgcj - 3.4.2
					if(this.config.isWindows()){
						this.o.debug(this.toString()+"::"+e.getMessage(),1);
						this.o.debug("isWindows!",1);
						if(System.getProperty("java.vm.name").equals("GNU libgcj")){
							this.o.debug("isGNUlibgcj!",1);
							if(System.getProperty("java.version").equals("3.4.2")){
								this.o.debug("isBuggyVersion!",1);
								this.o.debug("this.getFileSize():"+this.getFileSize()+" :: donesize:"+donesize,1);
								if(this.getFileSize() == donesize){
									this.o.debug("mingw bugfix applies!",1);
									setError = false;
								}
							}
						}
					}
					if(setError){
						this.errorCode = 25;
						this.errorMsg = e.getMessage();
						this.debug(this.errorCode+":"+this.errorMsg);
					}
				}
				
				this.debug("finished and closed!",100);
			}catch(IOException e){
				this.errorCode = 20;
				this.errorMsg = e.getMessage();
				this.debug(this.errorCode+":"+this.errorMsg);
			}
		}catch(FileNotFoundException e){
			this.errorCode = 10;
			this.errorMsg = e.getMessage();
		}
		
		this.calcStatus = 2;
		
		this.debug("end doHashSum() :: "+this.getHash());
		return true;
	}
	
	
	protected String getName(){
        this.debug(this.toString()+"::getName()",100);
		this.debug(this.toString()+"::getName() this.config.relativePath:"
				+this.config.getRelativePath(),100);
        this.debug(this.toString()+"::getName() isUseRelPath():"
				+this.config.isUseRelPath(),100);
		String fn;
        if(!this.config.isUseRelPath()){
        	if(this.config.isConsoleVersion()) {
        		fn = this.file.getPath(); // PATH AS TYPED !!!
        		this.debug("config.useRelPath is false: returning getPath():"+fn,100);
        	}else{
        		fn = this.file.getAbsolutePath();
        		this.debug("config.useRelPath is false: returning getAbsolutePath():"+fn,100);
        	}
			return fn;
        }
		if (this.config.getRelativePath().equals("")){
			fn = this.getSysindependentPath(this.file.getAbsolutePath());
			this.debug("config.relativePath is not set: returning getAbsolutePath():"+fn,100);
            return fn;
        }
        String filepath = this.file.getAbsolutePath();
        String relpath = this.config.getRelativePath();
        if(this.config.isWindows()){
            this.debug("stupid OS: converting path to lovercase",100);
            filepath = filepath.toLowerCase();
            relpath = relpath.toLowerCase();
        }
        this.debug("filepath:'"+filepath+"'; relpath:'"+relpath+"'",100);
        if(!filepath.startsWith(relpath)){
        	this.debug("not matching relativePath: clear relPath and returning getAbsolutePath",100);
        	this.config.setRelativePath("");
        	fn = this.getSysindependentPath(this.file.getAbsolutePath());
        	this.debug("getName() returning "+fn,100);
	        return fn;
	    }
        fn = this.getSysindependentPath(this.file.getAbsolutePath().substring(this.config.getRelativePath().length()));
        this.debug("matching relativePath: returning stripped getAbsolutePath:"+fn,100);
        return fn;
    }
    
	/**
	 * @return hashline
	 * used by console and savehash
	 */
	protected String getHashLine(){
	    char binMarker = ' ';
	    if(this.config.isBinModus()){
	        binMarker = this.config.getBinMarker();
	    }
	    return this.hash
	    			+this.config.getSaveFileHashSeparator()
	    			+binMarker
	    			+this.getName();
	}
	
	protected String getSysindependentPath(String p){
		if(this.config.isUseSysIndepPathSeparator()&& this.config.isWindows()){
			p = p.replace('\\','/');
		}
		return p;
	}

	protected String getCheckResult() {
		return this.checkResult;
	}
	
	protected String getFileName() {
		return this.fileName;
	}

	protected long getFileSize() {
		return this.fileSize;
	}

	protected String getCheckhash() {
		return this.checkhash;
	}

	protected void setCheckhash(String checkhash) {
		this.debug("setCheckhash() : "+checkhash);
		this.checkhash = checkhash;
	}

	protected String getHash() {
		return this.hash;
	}
	
	protected String getHashResult() {
		String ret;
		if(this.calcStatus<2){
			this.o.error("call for hash befor terminating!");
		}
		this.debug("getHashResult() is checkhash ?? :"+this.getCheckhash());
		if(this.getCheckhash().equals("")){
			if(this.getErrorCode()>0){
				ret = "ERROR: "+this.getErrorMsg()+" :: ERRORCODE:"+this.getErrorCode();
			}else{
				ret = this.getHash();
			}
		}else{
			ret = this.getCheckResult();
		}
		this.debug("getHashResult() : "+ret);
		return ret;
	}

	protected boolean isCheck(){
		if(this.getCheckhash().equals("")){
			return false;
		}
		return true;
	}
	
	protected boolean istCheckresultOK(){
		if(this.isCheck()){
			return this.checkResultBool;
		}
		this.o.error("requested getCheckresult() for non Check!");
		return true;
	}
	
	protected int getCalcStatus() {
		return this.calcStatus;
	}

	protected long getProcTime() {
		return this.procTime;
	}

	protected String getProcSpeed() {
		long fileSize0 = this.fileSize;
		long procTime0 = this.procTime;
		if (fileSize0 <= 0 || procTime0 <= 0)
			return "";
		double procTime_Sec = procTime0 / 1000.0;
		return getHumanReadableNumber(fileSize0 / procTime_Sec) + "B/s";
	}

	protected long getCallerProcTime() {
		return this.callerProcTime;
	}

	protected int getErrorCode() {
		return this.errorCode;
	}

	protected String getErrorMsg() {
		return this.errorMsg;
	}

	protected float getDonePercent() {
		//this.debug("getDonePercent() : "+this.donePercent,90);
		return this.donePercent;
	}	
	
	protected boolean isModified(long l){
		if(l > this.fileLastModified) return true;
		return false;
	}
	
	protected long getStartTime() {
		return this.startTime;
	}

	protected void setStartTime(long startTime) {
		this.startTime = startTime;
	}
    
	protected boolean isStopMe(){
        boolean ret = this.stopMe;
        if(ret){
            this.debug("setStopMe "+this.stopMe+" (inverted)",1);
            this.stopMe = false;
        }
        return ret;
    }
    protected void setStopMe(boolean stopMe) {
        this.debug("setStopMe set to "+stopMe+" (was "+this.stopMe+")",1);
        this.stopMe = stopMe;
    }
    
    protected boolean isAbsolute(){
    	return this.file.isAbsolute();
    }
    
    
    
    public void run(){
		this.doHashSum();
	}
}
