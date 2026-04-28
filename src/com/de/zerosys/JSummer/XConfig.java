/*
 * $Id: XConfig.java,v 1.42 2008-02-08 23:23:00 zerwes Exp $
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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import org.eclipse.swt.graphics.Image;

/**
 * @author zerwes
 */
class XConfig extends Config{

    Display xdisplay = null;
    Shell xshell = null;
    Table table = null;
    Font font = null;
    //@SuppressWarnings("hiding")
	XScreen o = null;
	XWindow xw = null;
    
    protected static int windowH = 450;
	protected static int windowW = 750;
	protected static int columnWidthProgress= 101;
	protected static int columnWidthHashMin = 260; 
	protected static int columnWidthHashMax = 460;
	protected static int columnWidthHashDiff = columnWidthHashMax - columnWidthHashMin;
	// 460 - SHA-256; 300 - sha1; 250 - md5 :: TODO: handle this dynamic ?
	
	// TODO: smart font handling
	private static int fontSize = 8;
	protected static String fontName = "courier new";
	protected static int titleFontSize = 12; 
	
	protected static String iconPath="JSummer.ico";
	private int iconStyle = 1;
	
	protected static Image imgUNKNOWN = null;
	protected static Image imgOK  = null;
	protected static Image imgERR = null;
	
	protected static boolean useColors = true;
	
	//private int numAddFileInRun = 0;

	protected XConfig(XWindow xw){
        super(xw.getScreen());
        this._init(xw);
    }
	protected XConfig(XWindow xw,String progName){
        super(xw.getScreen(),progName);
        this._init(xw);
    }
	
	private void _init(XWindow xwindow){
		this.xw = xwindow;
		this.o = this.xw.getScreen();
		this.xshell = this.xw.getShell();
		this.xdisplay = this.xshell.getDisplay();
        this.setFont();
        this.o.debug(this.toString()+"::XConfig : isWindows ? "+this.isWindows(),this.classDebugLevel);
	}
	
	protected static void setImageResource(int index, Image res) {
		switch (index) {
		case 0:
			if (imgUNKNOWN != null)
				imgUNKNOWN.dispose();
			imgUNKNOWN = res;
			break;
		case 1:
			if (imgOK != null)
				imgOK.dispose();
			imgOK = res;
			break;
		case 2:
			if (imgERR != null)
				imgERR.dispose();
			imgERR = res;
			break;
		}
	}
	protected static Image getImageResource(int index) {
		switch (index) {
		case 1:
			return imgOK;
		case 2:
			return imgERR;
		}
		return imgUNKNOWN;
	}
	
	//@Override
	protected String getVersion(){
		String ret = super.getVersion();
		ret += "\n;"+this.getSWTVersion();
		return ret;
	}
	protected String getSWTVersion(){
		return "SWT-Version: "+SWT.getVersion()+" - "+SWT.getPlatform();
	}
    
    private void setFont(){
        if (this.xshell==null){
            super.getScreen().error(this.toString()+" you must set the X-things before init Font");
            return;
        }
        FontData fontdata = new FontData(XConfig.fontName,XConfig.fontSize,SWT.NORMAL);
		this.font = new Font(this.xdisplay,fontdata);
    }
    protected Font getFont(){
        if(this.font==null){
            super.getScreen().error(this.toString()+" Font not set!");
            this.setFont();
        }
        return this.font;
    }
    protected void disposeFont(){
        if(this.font!=null){
            this.font.dispose();
            this.font = null;
        }
    }
    
    protected void setTable(Table tab){
        this.table = tab;
    }

    protected void clearTable(){
        if(this.hashFiles.size()<1){
            return;
        }
        this.hashFiles.clear();
        this.setRelativePath("");
        this.xdisplay.syncExec(new Runnable(){
            public void run(){
                XConfig.this.table.clearAll();
                XConfig.this.table.setItemCount(0);
                XConfig.this.table.update();
				System.gc();
            }
        });
    }
    
	//@Override
	protected void addHashFile(String f,String parent){
        this.XaddHashFile(f,null,parent);
    }
    //@Override
	protected void addHashFile(String f){
        this.XaddHashFile(f,null,null);
    }
	//@Override
	protected boolean addHashFileCheck(String f,String hash,String parent){
		this.o.debug(this.toString()+"::addHashFileCheck(() file:"+f+"; hash:"+hash+"; parent:"+parent,this.classDebugLevel);
		return this.XaddHashFile(f,hash,parent);
	}
	//@Override
	protected boolean addHashFileCheck(String f,String hash){
		return this.addHashFileCheck(f,hash,null);
    }
	
	//@SuppressWarnings("unchecked")
	private boolean XaddHashFile(final String f,final String checkhash,final String parent){
        this.o.debug(this.toString()+"::XaddHashFile() :: '"+f+"'",this.classDebugLevel);
        final XHashFile hf = new XHashFile(f,this,parent);
        if(checkhash!=null){
            if(!checkhash.equals("")){
            	//this.o.debug(this.toString()+".XaddHashFile() :: checkhash:'"+checkhash+"'",this.classDebugLevel);
                hf.setCheckhash(checkhash);
            }
        }
        if(this.isStopMe()){
			this.o.debug(this.toString()+"::XaddHashFile() aborted",this.classDebugLevel);
			return true;
        }
		//this.o.debug(this.toString()+"::XaddHashFile() run() start :: '"+f+"'",this.classDebugLevel);
		this.xdisplay.syncExec(new Runnable(){
            public void run(){
				//o.debug(this.toString()+"::XaddHashFile() run() for file "+f,classDebugLevel);
            	final TableItem tr = new TableItem(XConfig.this.table,SWT.NONE);
            	hf.setTableItem(tr);
            	tr.setText(0,hf.getName());
				if (XConfig.useColors) {
					tr.setForeground(XConfig.this.getColorTableFGNew());
					tr.setBackground(XConfig.this.getColorTableBG());
				}
				tr.addDisposeListener(new org.eclipse.swt.events.DisposeListener() {
					public void widgetDisposed(org.eclipse.swt.events.DisposeEvent e) {
						hf.disposeEditor(true);
					}
				});

            	//o.debug(this.toString()+"::XaddHashFile() run() END for file "+f,classDebugLevel);
            }
        });
		//this.o.debug(this.toString()+"::XaddHashFile() run() end :: '"+f+"'",this.classDebugLevel);
		this.hashFiles.add(hf);
		
        boolean isAbsoluteFile = hf.isAbsolute();
        if(checkhash!=null && isAbsoluteFile){
        	this.o.debug(this.toString()+"::XaddHashFile() calling this.xw.setUseRelPath",this.classDebugLevel); 
        	//this.xw.setUseRelPath(false);
        	//this.xw.setUnableToUseSysindepPath(true);
        	this.setUseRelPath(false, true);
        	if(this.isWindows()){
        		this.setUseSysIndepPathSeparator(false, true);
        	}
        }
        this.xw.setUpdateHash(true);
        this.setTotalFilesNum();
        this.o.debug(this.toString()+"::XaddHashFile() returning :'"+isAbsoluteFile+"'",this.classDebugLevel);
        return isAbsoluteFile;
    }
    
    
    //@Override
	protected void showHelp(){
        this.o.popUp(super.getHelp(),"SYNTAX and INVOCATION");
        this.o.println(this.getHelp());
    }
    protected void showVersion(boolean exit){
		this.o.println(super.getVersion());
		this.o.popUp(super.getVersion(),"VERSION");
		if(exit) System.exit(0);
    }
    //@Override
	protected void showVersion(){
        this.showVersion(false);
    }
    
    protected int getIconStyle() {
        return this.iconStyle;
    }
    protected void setIconStyle(int iconStyle) {
        this.iconStyle = iconStyle;
    }
    
    protected String getIcon(String _icon){
        if(_icon.equals("AddFile")
                ||_icon.equals("AddDir")
                ||_icon.equals("Floppy")
                ||_icon.equals("FloppyX")
                ||_icon.equals("Cancel")
                ||_icon.equals("Clear")
                ||_icon.equals("About")
                ||_icon.equals("MD5Sum")
                ||_icon.equals("Check")
                ||_icon.equals("Cross")
                ||_icon.equals("Unknown")){
            
			return this.iconStyle+"/"+_icon+".png";
        }
        this.o.error("request for undefined icon:'"+_icon+"'");
        return "";
    }
	
    
    //@Override
	protected void setRelativePath(String path){
    	this.o.debug(this.toString()+"::setRelativePath() XConfig : "+path, 99);
        String oldrelpath = this.getRelativePath();
        super.setRelativePath(path);
        if(!oldrelpath.equals(path)){
        	this.o.debug(this.toString()+"::setRelativePath() call of setUpdateName !", 99);
            this.xw.setUpdateName(true);
        }
    }
    
    //@Override
	protected void setUseSysIndepPathSeparator (boolean use){
        this.setUseSysIndepPathSeparator(use,true);
    }
    protected void setUseSysIndepPathSeparator (boolean use,boolean changeX){
        super.setUseSysIndepPathSeparator(use);
        if(changeX){
			this.xw.setUseSysindepPath(use);
		}
    }
	//@Override
	protected void setUseRelPath(boolean useRelPath){
		this.setUseRelPath(useRelPath,true);
	}
	protected void setUseRelPath(boolean useRelPath,boolean changeX){
		this.o.debug(this.toString()+"::setUseRelPath()"+useRelPath,this.classDebugLevel);
		super.setUseRelPath(useRelPath);
		if(changeX){
			this.xw.setUseRelPath(useRelPath);
		}
	}
	
	//@Override
	protected void unableToUseSysIndependentPathSep(){
		this.o.debug(this.toString()+"::unableToUseSysIndependentPathSep()",this.classDebugLevel);
	    super.unableToUseSysIndependentPathSep();
	    this.xw.setUnableToUseSysindepPath(false);
	}
    
    protected void setTotalFilesNum(){
        this.xw.setNumfilesLabel(this.hashFiles.size());
    }
    
    protected Color getColorTableBG(){
    	return this.xdisplay.getSystemColor(SWT.COLOR_WHITE);
    }
    protected Color getColorTableBGError(){
    	return this.xdisplay.getSystemColor(SWT.COLOR_RED);
    }
    protected Color getColorTableFGError(){
    	return this.xdisplay.getSystemColor(SWT.COLOR_WHITE);
    }
    protected Color getColorTableFGNew(){
    	return this.xdisplay.getSystemColor(SWT.COLOR_DARK_BLUE);
    }
    protected Color getColorTableFGHashing(){
    	return this.xdisplay.getSystemColor(SWT.COLOR_DARK_RED);
    }
    protected Color getColorTableFGDone(){
    	return this.xdisplay.getSystemColor(SWT.COLOR_DARK_GREEN);
    }
	
	/*
	 * see comments in XAddDir
	protected synchronized void numAddFileInRunAdd(){
		this.numAddFileInRun++;
		this.o.debug("numAddFileInRunAdd numAddFileInRun:"+numAddFileInRun,this.classDebugLevel);
	}
	protected synchronized void numAddFileInRunSub(){
		this.numAddFileInRun--;
		this.o.debug("numAddFileInRunSub numAddFileInRun:"+numAddFileInRun,this.classDebugLevel);
	}
	protected synchronized int getNumAddFileInRun(){
		this.o.debug("getNumAddFileInRun numAddFileInRun:"+numAddFileInRun,this.classDebugLevel);
		return this.numAddFileInRun;
	}
	*/
    
    protected void addFileDirFocus(String f){
    	this.addFileDir(f);
    	this.xw.setXWindowActive();
    }
	
    //@Override
	protected void finalize() throws Throwable {
        super.finalize();
        this.disposeFont();
    }

	protected Shell getXshell() {
		return this.xshell;
	}
	protected XWindow getXwindow() {
		return this.xw;
	}
}
