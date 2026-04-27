/**
 * $Id: JSummerMRun.java,v 1.5 2008-03-05 11:56:16 zerwes Exp $
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


import java.io.InputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


class JSummerMRun {

	protected XScreen o = null;
	protected Config config;
	protected Shell xshell;
	protected Display xdisplay;
	
	protected Text fileText;
	protected Text hashText;
	protected Text checkText;
	
	JSummerMRun(String[] args){
		String h = " ";
		for(int i=0;i<=CoreConfig.maxHashStrLen;i++){
			h += " ";
		}
		
		try{
	        this.xdisplay = new Display();
	        this.xshell = new Shell(this.xdisplay, SWT.TITLE|SWT.CLOSE|SWT.MIN|SWT.BORDER);
	    }catch(UnsatisfiedLinkError e){
	        this.libError(e);
	    }
	    
	    this.o = new XScreen(CoreConfig.DEFAULTDEBUG,this.xshell);
	    this.config = new Config(this.o,CoreConfig.ProgName+" mini");
	    
	    if(this.config.configByArgs(args)){
		    this.o.println(this.config.getHelp());
			this.o.println();
		    this.o.error(this.config.getConfigErrorMsg());
		}
	    
	    // TODO: check for max one file and NO checkfile in config
	    
	    Display.setAppName(this.config.getProgName());
		this.xshell.setText(this.config.getProgName());
		
		final GridLayout shellLayout = new GridLayout();
		shellLayout.numColumns = 1;
		this.xshell.setLayout(shellLayout);
		
		InputStream iconsstream = XWindow.class.getResourceAsStream(XConfig.iconPath);
		if(iconsstream==null){
			//TODO: this.o.error("unable to load icon "+XConfig.iconPath);
		}else{
			final Image img = new Image(this.xdisplay,iconsstream);
			this.xshell.setImage(img);
			this.xshell.addDisposeListener(new DisposeListener(){
                public void widgetDisposed(DisposeEvent e){
                    img.dispose();
                }
            });
		}
		
		
		
		final Menu menu = new Menu(this.xshell, SWT.BAR);
		this.xshell.setMenuBar(menu);
		
		// TODO: menubar
		
		final Composite comp = new Composite(this.xshell, SWT.NONE);
		final GridData cGD = new GridData();
		cGD.grabExcessHorizontalSpace = true;
		comp.setLayoutData(cGD);
		final GridLayout compLayout = new GridLayout();
		compLayout.numColumns = 3;
		compLayout.makeColumnsEqualWidth = false;
		compLayout.marginLeft = 5;
		comp.setLayout(compLayout);
		
		
		final Label filelabel = new Label(comp,SWT.SHADOW_NONE|SWT.LEFT);
		filelabel.setText("file:");
		this.fileText = new Text(comp,SWT.LEFT|SWT.READ_ONLY|SWT.SINGLE);
		final GridData fGD = new GridData();
		fGD.grabExcessHorizontalSpace = true;
		fGD.grabExcessVerticalSpace = false;
		fGD.horizontalAlignment = SWT.LEFT;
		this.fileText.setLayoutData(fGD);
		this.fileText.setText(h);
		final Button filechose = new Button(comp,SWT.PUSH|SWT.CENTER);
		String iconname = "1/AddFile.png";
		iconsstream = this.getClass().getResourceAsStream(iconname);
		if(iconsstream==null){
			this.o.error("unable to load icon "+iconname);
			filechose.setText("open");
		}else{
			final Image icon = new Image(this.xdisplay,iconsstream);
			filechose.setImage(icon);
			filechose.addDisposeListener(new DisposeListener(){
                public void widgetDisposed(DisposeEvent e){
                    icon.dispose();
                }
            });
		}
		filechose.setToolTipText("select file");
		filechose.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				JSummerMRun.this.o.debug(e.toString(),1);
				FileDialog fd = new FileDialog(JSummerMRun.this.xshell,SWT.OPEN);
				String f = fd.open();
				if(f != null && f.length()>0){
					JSummerMRun.this.o.debug("open: "+f,1);
				}
			}
		});
		
		final Label hashlabel = new Label(comp,SWT.SHADOW_NONE|SWT.LEFT);
		hashlabel.setText("hash:");
		this.hashText = new Text(comp,SWT.LEFT|SWT.READ_ONLY|SWT.SINGLE);
		final GridData htGD = new GridData();
		htGD.grabExcessHorizontalSpace = true;
		htGD.grabExcessVerticalSpace = false;
		htGD.horizontalAlignment = SWT.LEFT;
		htGD.horizontalSpan = 2;
		this.hashText.setLayoutData(htGD);
		this.hashText.setText(h);
		
		
		final Label checklabel = new Label(comp,SWT.SHADOW_NONE|SWT.LEFT);
		checklabel.setText("checksum:");
		this.checkText = new Text(comp,SWT.LEFT|SWT.READ_ONLY|SWT.SINGLE);
		final GridData ctGD = new GridData();
		ctGD.grabExcessHorizontalSpace = true;
		ctGD.grabExcessVerticalSpace = false;
		ctGD.horizontalAlignment = SWT.LEFT;
		ctGD.horizontalSpan = 2;
		this.checkText.setLayoutData(ctGD);
		this.checkText.setText(h);
		
		final Composite compbut = new Composite(comp,SWT.NONE);
		final GridData btGD = new GridData();
		btGD.grabExcessHorizontalSpace = true;
		btGD.grabExcessVerticalSpace = false;
		btGD.horizontalAlignment = SWT.CENTER;
		btGD.horizontalSpan = 3;
		compbut.setLayoutData(btGD);
		final GridLayout compbutlayout = new GridLayout();
		compbutlayout.makeColumnsEqualWidth = true;
		compbutlayout.numColumns = 2;
		compbut.setLayout(compbutlayout);
		
		final Composite comphashalg = new Composite(compbut,SWT.NONE);
		final GridLayout chlayout = new GridLayout();
		chlayout.numColumns = 2;
		comphashalg.setLayout(chlayout);
		final Label hashalglabel = new Label(comphashalg,SWT.LEFT);
		hashalglabel.setText("algorithm:");
		final GridData hlGD = new GridData();
		hlGD.verticalAlignment = SWT.LEFT;
		hlGD.horizontalAlignment = SWT.CENTER;
		hashalglabel.setLayoutData(hlGD);
		final Combo hashalgselect = new Combo(comphashalg,SWT.DROP_DOWN|SWT.READ_ONLY);
		hashalgselect.add("md5");
		hashalgselect.add("sha-160");
		hashalgselect.add("sha-256");
		hashalgselect.select(0);
		hashalgselect.setToolTipText("choose algorithm");
		
		final Button doit = new Button(compbut,SWT.PUSH|SWT.CENTER);
		doit.setText("compute hash");
		
		this.xshell.pack();
		
		this.hashText.setText("");
		this.fileText.setText("");
		this.checkText.setText("");
		
		this.xshell.open();
		
		while(!this.xshell.isDisposed()){
			if(!this.xdisplay.readAndDispatch()){
				this.xdisplay.sleep();
			}
		}
	}
	
	// TODO: XWIndow::libError and this one should be merged into XScreen
	protected void libError(UnsatisfiedLinkError e){
	    e.printStackTrace();
		//TODO: more eloquent errormessage
	    System.exit(1);
	}
}
