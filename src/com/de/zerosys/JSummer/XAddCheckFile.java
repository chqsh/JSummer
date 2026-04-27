/*
 * $Id: XAddCheckFile.java,v 1.14 2008-03-05 17:50:19 zerwes Exp $
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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
//import org.eclipse.swt.widgets.MessageBox;


final class XAddCheckFile extends SelectionAdapter{
	
	private XWindow xw;
	private Config config;
	protected Screen o;
	protected Shell xshell;
	private final int classDebugLevel = 2;
	
	protected XAddCheckFile(XWindow xw){
		super();
		this.xw = xw;
		this.config = this.xw.getConfig();
		this.o = this.config.getScreen();
		this.xshell = this.xw.getShell();
	}
	
	public void widgetSelected(SelectionEvent e){
        this.o.debug(this.toString()+"::widgetSelected() : "+e.toString(),this.classDebugLevel);
        final FileDialog fd = new FileDialog(this.xshell,SWT.OPEN|SWT.SINGLE);
        fd.setText("select hash-file to open ...");
        String[] fext = new String[this.config.containerHashSumInfo.size()+3];
        String[] fextText = new String[fext.length];
        fext[0] = "*."+this.config.getSaveHashFileExtension();
        fextText[0] = "Current selected hash algorithm: "+this.config.getHashSumName();
        Enumeration hie = this.config.containerHashSumInfo.elements();
		HashSumInfo hsi = null;
		int i = 1;
		String allSupportedTypesExt = "";
		while(hie.hasMoreElements()) {
			hsi = (HashSumInfo)hie.nextElement();
			fext[i] = hsi.getAllHashFileExtensions("*.", ";");
			if(allSupportedTypesExt.length() > 0) allSupportedTypesExt += ";";
			allSupportedTypesExt += fext[i];
			fextText[i] = hsi.getHashAlgName()+" ("+fext[i]+")";
			if(hsi.getHashAlgName().equals(this.config.getHashSumName())) {
				fext[0] = fext[i];
				fextText[i] += " ("+fext[i]+")";
			}
			if(!this.config.isWindows()) {
				fext[i] += ";"+fext[i].toUpperCase();
			}
			i++;
		}
		fext[i] = allSupportedTypesExt;
		fextText[i] = "all supported files types";
		i++;
		fext[i] = "*.*";
		fextText[i] = "all files (*.*)";
		fd.setFilterExtensions(fext);
		fd.setFilterNames(fextText);
        String file = fd.open();
		if(file!=null){
			this.o.debug(this.toString()+" selected file: "+file,this.classDebugLevel);
		    /*if(clearTable){
				final XClearTable xclrtab = new XClearTable(this.xw);
				xclrtab.widgetSelected(e);
		    }*/
			// now the msgbox is raised by XClearTable
			final XClearTable xclrtab = new XClearTable(this.xw);
			xclrtab.widgetSelected(e);
			if(!xclrtab.isClearTableDoit()){
				this.o.debug(this.toString()+" cleartable::aborted",this.classDebugLevel);
				return;
			}
			
			if(!this.config.setCheckMDFile(file, true)){
				//this.o.error("Error opening Checkfile: '"+file+"'\n"+this.config.configErrorMsg);
			}
		}
		this.xw.updateTitle();
	}
}
