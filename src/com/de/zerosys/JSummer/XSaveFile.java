/*
 * $Id: XSaveFile.java,v 1.13 2008-03-05 11:56:16 zerwes Exp $
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

import java.io.File;
import java.util.Enumeration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author zerwes
 */
final class XSaveFile extends SelectionAdapter{
    private FileDialog fd;
    private Config config;
    private Shell xshell;
    private Screen o;
    private final int classDebugLevel = 5;
    
    protected XSaveFile(Config config,Shell xshell) {
        super();
        this.config = config;
        this.o = this.config.getScreen();
        this.xshell = xshell;
    }
    
    public void widgetSelected(SelectionEvent e){
        this.o.debug(e.toString(),this.classDebugLevel);
        this.fd = new FileDialog(this.xshell,SWT.SAVE|SWT.SINGLE);
		if(this.config.getRelativePath()!=null||!this.config.getRelativePath().equals("")){
			this.fd.setFilterPath(this.config.getRelativePath());
		}
		this.fd.setFileName(this.config.getDefaultSaveHashFileName());
		
		/*if(this.config.isWindows()){
			String[] fext = { "*."+this.config.getSaveHashFileExtension(),"*.*"};
			this.fd.setFilterExtensions(fext);
		}*/
		String[] fext = { "*."+this.config.getSaveHashFileExtension(),"*.*"};
		this.fd.setFilterExtensions(fext);
		
		this.fd.setText("select file to save ...");
        String file = this.fd.open();
        if(file!=null){
            if(!file.equals("")){
	            File savefile = new File(file);
	            if(savefile.exists()){
	                MessageBox mb = new MessageBox(this.xshell,SWT.OK|SWT.CANCEL|SWT.ICON_WARNING);
	                mb.setMessage("file "+savefile.getAbsolutePath()+" exists!\n\nOVERWRITE?");
	                mb.setText("WARNING");
	                if(mb.open()==SWT.CANCEL){
	                    return;
	                }
	                savefile.delete();
	            }
	            Enumeration fe = this.config.getMD5Files().elements();
	            this.config.setSaveMDFile(savefile.getAbsolutePath());
	            while(fe.hasMoreElements()){
	                HashFile f = (HashFile)fe.nextElement();
	                this.o.debug(this.toString()+" file: "+f.getName(),this.classDebugLevel);
	    			
	    			if(f.file.equals(this.config.fileSaveMDFile)) {
	    				this.o.debug("skip checkfile '"+f.getName()+"'", this.classDebugLevel);
	    				continue;
	    			}
	    			
	    			if(f.getErrorCode()>0){
	    				this.o.error(f.getAbsName()+"::"+f.getErrorMsg());
	    				continue;
	    			}
	    			
	    			boolean writestatus = this.config.writeToSaveMD5File(f.getHashLine());
	    			
	    			this.o.debug(this.toString()+" : wrote line : "+writestatus,this.classDebugLevel);
	    			if(!writestatus){
	    			    this.o.error("ERROR saving file");
	    			    break;
	    			}
	            }
	            this.config.clearSaveMDFileWriter();
            }
        }
	}
}
