/**
 * $Id: CoreHashFile.java,v 1.3 2008-03-05 11:56:16 zerwes Exp $
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

import java.io.File;

class CoreHashFile {
    
    protected Screen o;
    protected CoreConfig config;
    
    protected File file = null;
    protected String fileName = "";
    protected long fileSize = 0;
    protected long fileLastModified = 40;
    
    protected String errorMsg = "";
    protected int errorCode = 0;
    
    protected int classDebugLevel = 6;
    
    
    CoreHashFile (String f,CoreConfig c){
        this._init(f,c,null);
    }
    
    CoreHashFile (String f,CoreConfig c,String parent){
        this._init(f,c,parent);
    }
    
    protected void _init(String f,CoreConfig c,String parent){
        this.config = c;
        this.o = this.config.getScreen();
        this.debug("file:'"+f+"' parent:'"+parent+"'");
        this.fileName = f;
        if(f.equals("")){
            this.errorCode = 100;
            this.errorMsg = "file '"+f+"' :: empty name";
            return;
        }
        
        this.file = new File(f);
        this.debug("try absolute file:'"+this.file.getAbsolutePath()+"'");
        if(parent!=null && !this.file.isAbsolute()){
            /*
             * here we have a little problem:
             * if the file in the checkfile is absolute,
             * the constructor creates the file RELATIVE to the parent, 
             * altough the javadoc says:
             * "If the child pathname string is absolute then it is converted 
             * into a relative pathname in a system-dependent way. 
             * If parent is the empty abstract pathname then the new File 
             * instance is created by converting child into an abstract pathname 
             * and resolving the result against a system-dependent default directory. 
             * Otherwise each pathname string is converted into an abstract pathname 
             * and the child abstract pathname is resolved against the parent."
             * 
             * so we do 2 steps:
             *  create simple file
             *  if !file.isAbsolute() create with parent
             */
            this.debug("!!!using relative constructor!!! relPath:"+this.config.getRelativePath());
            this.file = new File(this.config.getRelativePath(),f);
        }
        this.debug("absolute file:'"+this.file.getAbsolutePath()+"'");
        
        if(!this.file.exists()){
            this.errorCode = 1;
            this.errorMsg = "file '"+f+"' not found";
            return;
        }
        if(!this.file.canRead()){
            this.errorCode = 2;
            this.errorMsg = "file '"+f+"' can not be opened";
            return;
        }
        if(!this.file.isFile()){
            this.errorCode = 3;
            this.errorMsg = "'"+f+"' is not a regular file";
            return;
        }
        this.fileSize = this.file.length();
        this.fileLastModified = this.file.lastModified();
        this.debug("init DONE");
    }
    
    protected String getAbsName(){
        return this.file.getAbsolutePath();
    }
    
    protected void debug(String _m){
        this.debug(_m,this.classDebugLevel);
    }
    protected void debug(String _m,int _l){
        if (_l > this.o.getDebugLevel()){
            return;
        }
        this.o.debug(this.toString()+": file "+this.fileName+" :: "+_m,_l);
    }
    
	public static final long KILO = 1024L;
	public static final long MEGA = 1048576L;
	public static final long GIGA = 1073741824L;
	
	public static final double KILO_D = 1024.0D;
	public static final double MEGA_D = 1048576.0D;
	public static final double GIGA_D = 1.073741824E9D;
	
	public String getHumanReadableFileSize() {
		return getHumanReadableFileSize(this.fileSize);
	}
	
	public static String getHumanReadableFileSize(long fileSize) {
		String sizeText = "";
		if (fileSize < 0) {
			;
		} else if (fileSize < KILO) {
		  sizeText = String.format("%d", new Object[] { Long.valueOf(fileSize) });
		} else if (fileSize < MEGA) {
		  sizeText = String.format("%s KB", new Object[] {
				numberToString(fileSize / KILO_D, 0, 2) });
		} else if (fileSize < GIGA) {
		  sizeText = String.format("%s MB", new Object[] {
				numberToString(fileSize / MEGA_D, 0, 2) });
		} else {
		  sizeText = String.format("%s GB", new Object[] {
				numberToString(fileSize / GIGA_D, 0, 2) });
		}
		return sizeText;
	}
	
	public static String getHumanReadableNumber(double number) {
		String sizeText = "";
		if (number < 0.0) {
			return "-" + getHumanReadableNumber(-number);
		} else if (number < KILO_D) {
		  sizeText = String.format("%d ", new Object[] {
				numberToString(number, 0, 2)});
		} else if (number < MEGA_D) {
		  sizeText = String.format("%s K", new Object[] {
				numberToString(number / KILO_D, 0, 2) });
		} else if (number < GIGA_D) {
		  sizeText = String.format("%s M", new Object[] {
				numberToString(number / MEGA_D, 0, 2) });
		} else {
		  sizeText = String.format("%s G", new Object[] {
				numberToString(number / GIGA_D, 0, 2) });
		}
		return sizeText;
	}
	
	private static String numberToString(int number, int digit) {
		StringBuilder s = new StringBuilder(String.valueOf(number));
		while (s.length() < digit) {
			s.insert(0, "0");
		}
		return s.toString();
	}
	
	private static String numberToString(double number, int digit, int scale) {
		int intNumber = (int)number;
		double part = number - intNumber;
		StringBuilder s = new StringBuilder(numberToString(intNumber, digit));
		s.append('.');
		
		for (int i = 1; i <= scale; i++) {
		  part *= 10.0D;
		}
		intNumber = (int)Math.round(part);
		s.append(numberToString(intNumber, scale));
		return s.toString();
	}
}
