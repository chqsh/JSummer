/**
 * $Id: CheckMDFile.java,v 1.18 2008-03-05 11:56:16 zerwes Exp $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


class CheckMDFile {

	private Config config;
	private Screen o;
	
	private String parentPath = "";
	
	private File f;
	
	private int errorCode = 0;
	private String errorMsg = "";
	
	private final int maxErrorLines = 5;
	
	private int classDebugLevel = 1;
	
	CheckMDFile(Config c){
		this.config = c;
		this.o = this.config.getScreen();
		
		this.o.debug(this.toString()+" :: "+this.config.getCheckFile(),this.classDebugLevel);
		
		this.f = new File(this.config.getCheckFile());
		
		if(!this.f.exists()){
			this.errorCode = 1;
			this.errorMsg = "unable to find file "+this.f.getAbsolutePath();
			return;
		}
		if(!this.f.canRead()){
			this.errorCode = 2;
			this.errorMsg = "unable to open file "+this.f.getAbsolutePath();
			return;
		}
		this.parentPath = this.f.getAbsoluteFile().getParentFile().getAbsolutePath();
		if(!this.parentPath.endsWith(System.getProperty("file.separator"))){
            this.parentPath += System.getProperty("file.separator");
        }
		this.o.debug("parent path in "+this.toString()+" : "+this.parentPath,this.classDebugLevel);
		this.config.setRelativePath(this.parentPath); //???
		this.parseFile();
	}
	
	
	protected boolean parseFile(){
        this.o.debug(this.toString()+"::parseFile() ...",this.classDebugLevel);
        try{
            FileReader fr = new FileReader(this.f);
            BufferedReader br = new BufferedReader(fr);
            int lineNr = 0;
			int lineErrors = 0;
            while(true){
                lineNr++;
                try{
                    String l = br.readLine();
                    this.o.debug("read line:'"+l+"'",this.classDebugLevel);
                    if(l==null){
                        this.o.debug(this.toString()+": EOF reached!",this.classDebugLevel);
                        break;
                    }
                    if(l.trim().equals("")){
                        this.o.debug("empty line ignored",this.classDebugLevel);
                        continue;
                    }
                    if(this.isMalformedLine(l)){
						lineErrors++;
                        this.o.error("file '"+this.f+"' :: malformed line nr "+lineNr);
						if(!this.errorMsg.equals("")) this.o.error(this.errorMsg);
						if(lineErrors >= this.maxErrorLines){
							this.errorCode = 3;
							this.errorMsg = "maximum number of errors reached! ABORTING";
							//this.o.error(this.errorMsg); // duplicated errormsg !!!
							return false;
						}
                        continue;
                    }
                    String file = l.substring(l.indexOf(" ")+1,l.length());
					file = file.trim();
                    if(file.startsWith("*")) file = file.substring(1);
                    String md = l.substring(0,l.indexOf(" "));
                    this.o.debug("file:'"+file+"'; hash:'"+md+"'",this.classDebugLevel);
                    this.o.debug(this.toString()+" : config is "+this.config.toString(),this.classDebugLevel);
					/*if(this.config.addHashFileCheck(file,md,this.parentPath)){
						this.o.debug(this.toString()+"::parseFile calling this.config.setUseRelPath",this.classDebugLevel);
						this.config.setUseRelPath(false);
					}*/
                    this.config.addHashFileCheck(file,md,this.parentPath);
                }catch(IOException e){
                    //o.fatalError(f.getName()+"::"+e.getMessage());
					this.errorCode = 3;
					this.errorMsg = e.getMessage();
                    return false;
                }
            }
			try{
				br.close();
				fr.close();
			}catch(IOException e){
				this.errorCode = 4;
				this.errorMsg = e.getMessage();
				this.o.error(e.getMessage());
			}
        }catch(FileNotFoundException e){
			this.errorCode = 1;
			this.errorMsg = e.getMessage();
            return false;
        }
        return true;
    }
	
	/**
	 * @param line
	 * @return true if malformed else false
	 */
	private boolean isMalformedLine(String line){
        this.o.debug(this.toString()+"::isMalformedLine('"+line+"') ???",this.classDebugLevel);
		this.o.debug("this.config.getHashLenght():"+this.config.getHashLenght(),this.classDebugLevel);
	    int firstSpaceAt = line.indexOf(" ");
		//this.o.debug(this.toString()+" firstSpaceAt="+firstSpaceAt,this.classDebugLevel);
	    if( firstSpaceAt != this.config.getHashLenght()){
	    	/*Enumeration hie = this.config.containerHashSumInfo.elements();
			HashSumInfo hsi = null;
			while(hie.hasMoreElements()) {
				hsi = (HashSumInfo)hie.nextElement();
				if(hsi.equals(this.config.actualHashSumInfo)) {
					continue;
				}
				if(firstSpaceAt == hsi.){
					
				}
			}*/
			/*if(firstSpaceAt==HashSum.HASHLENGHT_MD5){
				this.errorMsg = "THIS LOOKS LIKE A md5-HASH";
			}else if(firstSpaceAt==HashSum.HASHLENGHT_SHA1){
				this.errorMsg = "THIS LOOKS LIKE A sha-160-HASH";
			}else if(firstSpaceAt==HashSum.HASHLENGHT_SHA2){
				this.errorMsg = "THIS LOOKS LIKE A sha-256-HASH";
			}*/
	        this.o.debug(this.toString()+"::isMalformedLine() : first space located at "
	                +firstSpaceAt+" : expected at "+this.config.getHashLenght(),this.classDebugLevel);
	        return true;
	    }
	    char secondSep = line.charAt(firstSpaceAt+1);
	    if( !( secondSep == ' ' || secondSep == this.config.getBinMarker() ) ){
	        this.o.debug(this.toString()+"::isMalformedLine() : second separator malformed : '"+secondSep+"'",this.classDebugLevel);
	        return true;
	    }
		
	    /* 
	     * gcj is unable to find java::lang::String::matches(java::lang::String*)
	     * and java-regex are not supported
	     * maybe we should use a extra regex-package like gnu.regex or jackarte-regex?
	    String hash = line.substring(0,this.config.getHashLenght());
	    this.o.debug("checking HASH part :: '"+hash+"'",this.classDebugLevel);
	    if(!hash.matches("[a-fA-F0-9]{"+this.config.getHashLenght()+"}")){
	        this.o.debug(this.toString()+"::isMalformedLine() : malformed hash part !!!",this.classDebugLevel);
	        return true;
	    }
	    */
		// KIND OF UGGLY TEST BUT IT WORKS WITHOUT A REGEX-PACKAGE
		String hash = line.substring(0,this.config.getHashLenght()).toLowerCase();
		this.o.debug("checking HASH part :: '"+hash+"'",this.classDebugLevel);
		for(int i=0;i<hash.length();i++){
			String x = hash.substring(i,i+1);
			this.o.debug("checking char "+x,20);
			try{
				int xi = Integer.parseInt(x,16);
				this.o.debug(x+" represents "+xi,20);
			}catch(NumberFormatException e){
				this.o.debug(e.getMessage(),20);
				return true;
			}
		}
		
	    this.o.debug(this.toString()+"::isMalformedLine() line OK",this.classDebugLevel);
	    return false;
	}
	
	protected int getErrorCode() {
		return this.errorCode;
	}

	protected String getErrorMsg() {
		return this.errorMsg;
	}

	protected String getParentPath() {
		return this.parentPath;
	}
}
