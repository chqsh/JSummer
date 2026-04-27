/**
 * $Id: Config.java,v 1.56 2008-03-05 16:34:08 zerwes Exp $
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


import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;



class Config extends CoreConfig{
	
	protected SaveMDFile saveMDFileWriter = null;
	
	private HashSum hs = null;
	
    // in fact this stuff should be hash-alg-dependend set in HashSum
	private String saveFileHashSeparator = " ";
	private char binMarker = '*';
	
	
	Config(Screen o){
		super(o);
	}
	Config(Screen o,String progName){
		super(o,progName);
	}
	
    
    //@Override
	protected boolean configByArgs(String[] args){
        super.configByArgs(args);
        
        // TODO: if checkfile try to determine hash from ext or file
        
        // set default hashsum to MD5
        if (this.hs == null){
            this.o.debug("setting HashSum to default",this.classDebugLevel);
            this.setHashSum(CoreConfig.defaultHashAlg);
        }
        
        return this.configError;
    }
	
	
	//@Override
	protected void setHashSum(String algorithm){
		if(this.o.getDebugLevel() > 5) {
			this.o.debug(this.getClass().getName()+"::setHashSum: '"+algorithm+"'", 5);
		}
		Enumeration hie = this.containerHashSumInfo.elements();
		HashSumInfo hsi = null;
		while(hie.hasMoreElements()) {
			hsi = (HashSumInfo)hie.nextElement();
			if(this.o.getDebugLevel() > 5) {
				this.o.debug("loop hash type:"+hsi.getHashAlgName(), 5);
			}
			if(hsi.matchesHashSumString(algorithm)) {
				break;
			}
			hsi = null;
		}
		if(hsi == null) {
			this.configError = true;
			this.configErrorMsg = "unknown HashSum-Algorithm '"+algorithm+"'";
		} else {
			try{
				this.o.debug("trying to build HashSum hash type:"+hsi.getHashAlgName(), 5);
				this.hs = new HashSum(hsi.getHashAlgName());
				this.actualHashSumInfo = hsi;
			}catch(NoSuchAlgorithmException e){
				this.configError = true;
				this.configErrorMsg = "unknown HashSum-Algorithm '"+algorithm+"'";
			}
		}
	}
	protected String getHashSumName(){
		if(this.hs==null) return "undefined HashSum";
		return this.hs.getName();
	}
	protected boolean isSamehashAlg(String alg){
		if(this.hs==null) return false;
		return this.hs.isSameAlg(alg);
	}
	
	protected HashSum getHashSum(){
		return this.hs;
	}
	
	
    
	//@SuppressWarnings("unchecked")
	//@Override
	protected void addHashFile(String f,String parent){
	    this.o.debug(this.toString()+"::addHashFile file:"+f+" :: parent:"+parent,92);
		this.hashFiles.add(new HashFile(f,this,parent));
		this.o.debug(this.toString()+"::addHashFile file:"+f+" done",92);
    }
    //@SuppressWarnings("unchecked")
	//@Override
	protected void addHashFile(String f){
        this.o.debug(this.toString()+"::addHashFile file:"+f,92);
		this.hashFiles.add(new HashFile(f,this));
		this.o.debug(this.toString()+"::addHashFile file:"+f+" done",92);
    }
    
	protected boolean addHashFileCheck(String f,String hash){
		return this.addHashFileCheck(f,hash,null);
	}
	//@SuppressWarnings("unchecked")
	protected boolean addHashFileCheck(String f,String hash,String parent){
		this.o.debug(this.toString()+"::addHashFileCheck() : file:"+f+"; hash:"+hash,this.classDebugLevel);
		HashFile cf = new HashFile(f,this,parent);
		cf.setCheckhash(hash);
		this.hashFiles.add(cf);
		final boolean ret = cf.isAbsolute(); 
		this.o.debug(this.toString()+"::addHashFileCheck() returning "+ret,this.classDebugLevel);
		return ret;
    }
    
	protected boolean writeToSaveMD5File(String l){
		if(this.saveMDFileWriter==null){
			this.saveMDFileWriter = new SaveMDFile(this);
			if(this.saveMDFileWriter.getErrorCode()>0){
			    System.err.println(this.toString()+"::writeToSaveMD5File : unable to create writer");
				return false;
			}
		}
		//this.saveMDFileWriter.write(l+System.getProperty("line.separator"));
		this.saveMDFileWriter.write(l+'\n');
		if(this.saveMDFileWriter.getErrorCode()>0){
		    this.o.error(this.saveMDFileWriter.getErrorMsg());
			return false;
		}
		return true;
	}
	protected void clearSaveMDFileWriter(){
	    this.o.debug(this.toString()+"::clearSaveMDFileWriter()",this.classDebugLevel);
	    if(this.saveMDFileWriter==null){
	        this.o.debug(this.toString()+"::saveMDFileWriter is NULL",this.classDebugLevel);
	        return;
	    }
	    this.saveMDFileWriter.close();
	    this.setSaveMDFile("");
	    this.saveMDFileWriter = null;
	}
	
	
	
	
	/**
	 * @used by ConsoleFileGutter
	 */
	protected void delscreen(String s){
        for(int i=s.length();i>0;i--){
            this.o.print("\b");
        }
    }
	
	
	protected String getDefaultSaveHashFileName() {
		if(this.hs == null){
			return "undefined";
		}
		return CoreConfig.defaultHashFileName+"."+this.actualHashSumInfo.getHashFileExtension();
	}
	protected String getSaveHashFileExtension() {
		if(this.hs == null){
			return "none";
		}
		return this.actualHashSumInfo.getHashFileExtension();
	}
	
    protected String getSaveFileHashSeparator() {
        return this.saveFileHashSeparator;
    }
    
	protected int getHashLenght() {
		if(this.hs == null){
			this.o.debug(this.toString()+"::getHashLenght() NULL",this.classDebugLevel);
			return 0;
		}
		return this.hs.getHashLenght();
    }
	
    protected char getBinMarker() {
        return this.binMarker;
    }
	
    
	//@Override
	protected boolean setSaveMDFile(String saveMDFile) {
        super.setSaveMDFile(saveMDFile);
        this.saveMDFileWriter = new SaveMDFile(this);
        if(this.saveMDFileWriter.getErrorCode()>0){
            this.configErrorMsg = "unable to open file "+this.getSaveMDFile()
            + " "+this.saveMDFileWriter.getErrorMsg();
            return false;
        }
        return true;
    }
    
	
    //@Override
	protected boolean setCheckMDFile(String checkMDFile) {
		return this.setCheckMDFile(checkMDFile, false);
	}
	protected boolean setCheckMDFile(String checkMDFile, boolean autodetectHashAlg) {
    	this.o.debug("Config :: setCheckMDFile '"+checkMDFile+"'", this.classDebugLevel);
    	checkMDFile = checkMDFile.trim();
        super.setCheckMDFile(checkMDFile);
        if(checkMDFile.length() == 0){
        	return true;
        }
        // try to determine hashtype from file
        if (this.hs == null || autodetectHashAlg){
        	final int dotPos = checkMDFile.lastIndexOf(".");
        	if(dotPos>0){
        		final String ext = checkMDFile.substring(dotPos+1, checkMDFile.length());
        		Enumeration hie = this.containerHashSumInfo.elements();
        		HashSumInfo hsi = null;
        		while(hie.hasMoreElements()) {
        			hsi = (HashSumInfo)hie.nextElement();
        			//if(ext.equalsIgnoreCase(hsi.getHashFileExtension())) {
        			if(hsi.isValidHashFileExtension(ext)) {
        				this.setHashSum(hsi.getHashAlgName());
        				break;
        			}
        		}
        	}
        }
        // set default hashsum to MD5
        if (this.hs == null){
            this.o.debug("setting HashSum to default",this.classDebugLevel);
            this.setHashSum(CoreConfig.defaultHashAlg);
        }
        
        CheckMDFile cf = new CheckMDFile(this);
        if(cf.getErrorCode()>0){
            this.configErrorMsg = "CheckFile-Error :: "+cf.getErrorMsg();
            this.o.debug("Config :: setCheckMDFile '"+checkMDFile
            		+"' returning ERROR: "+cf.getErrorMsg(), this.classDebugLevel);
            return false;
        }
        this.o.debug("Config :: setCheckMDFile '"+checkMDFile+"' returning OK", this.classDebugLevel);
        return true;
    }
}
