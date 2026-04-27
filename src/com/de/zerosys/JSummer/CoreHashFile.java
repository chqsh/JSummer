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
}
