/*
 * $Id: XHashFileInfo.java,v 1.8 2006-03-24 10:47:57 io Exp $
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


class XHashFileInfo extends Dialog {
	
	Object result;
	HashFile hf;
	XScreen o;
	XConfig config;
	int style;
	
	public XHashFileInfo (XConfig config, int style, HashFile hf) {
		super (config.xshell, style);
		this.style = style;
		this.hf = hf;
		this.config = config;
		this.o = config.o;
		this.o.debug(this.toString(),111);
	}
	public XHashFileInfo (XConfig config, HashFile hf) {
		this (config, SWT.NO_TRIM|SWT.APPLICATION_MODAL, hf);
	}
	
	public Object open () {
		this.o.debug(this.toString()+"::open()",111);
		final Shell parent = getParent();
		final Shell shell = new Shell(parent, this.style);
		shell.setText(parent.getText()+" :: file details");
		shell.setImage(parent.getImage());
		
		GridLayout gl = new GridLayout();
		gl.marginWidth = 5;
		gl.numColumns = 3;
		shell.setLayout(gl);
		
		shell.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
		
		
		final FontData fontdata = new FontData(XConfig.fontName,XConfig.titleFontSize,SWT.BOLD);
		final Font titleFont = new Font(shell.getDisplay(),fontdata);
		final Label title = new Label(shell,SWT.CENTER);
		final GridData titleGD = new GridData(
				GridData.HORIZONTAL_ALIGN_CENTER|
				GridData.VERTICAL_ALIGN_END);
		titleGD.horizontalSpan = 2;
		title.setLayoutData(titleGD);
		title.setText("file details");
		title.setToolTipText(title.getText());
		title.setFont(titleFont);
		title.setBackground(shell.getBackground());
		
		
		final Button exitme = new Button(shell,SWT.PUSH);
		final GridData buttonGD = new GridData(
				GridData.HORIZONTAL_ALIGN_END|
				GridData.END|
				GridData.FILL_HORIZONTAL);
		exitme.setLayoutData(buttonGD);
		exitme.setToolTipText("close");
		exitme.setText(" x ");
		exitme.setBackground(shell.getBackground());
		exitme.addSelectionListener(new SelectionAdapter(){
        	public void widgetSelected(SelectionEvent event){
        		shell.dispose();
        	}
        });
        
        
        final Label sep = new Label(shell,SWT.SEPARATOR|SWT.HORIZONTAL);
        final GridData sepGD = new GridData(GridData.FILL_HORIZONTAL);
		sepGD.horizontalSpan = 3;
		sep.setLayoutData(sepGD);
		
		final Label filelabel = new Label(shell,SWT.LEFT);
		filelabel.setText("file:");
		filelabel.setBackground(shell.getBackground());
		final Text filename = new Text(shell,SWT.LEFT|SWT.READ_ONLY|SWT.SINGLE);
		filename.setText(this.hf.getAbsName());
		filename.setBackground(shell.getBackground());
		filename.setDoubleClickEnabled(true);
		filename.setLayoutData(this.getGridData());
		
		final Label hashlabel = new Label(shell,SWT.LEFT);
		hashlabel.setText("hash:");
		hashlabel.setBackground(shell.getBackground());
		final Text hashtext = new Text(shell,SWT.LEFT|SWT.READ_ONLY|SWT.SINGLE);
		if(this.hf.getCalcStatus()<2){
			hashtext.setText("NOT JET HASHED!");
			hashtext.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
		}else{
			if(this.hf.getCheckhash().length()>0){
				hashtext.setText(this.hf.getHash());
			}else{
				hashtext.setText(this.hf.getHashResult());
			}
		}
		hashtext.setBackground(shell.getBackground());
		hashtext.setDoubleClickEnabled(true);
		hashtext.setLayoutData(this.getGridData());
		if(this.hf.getErrorCode()>0){
			hashtext.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
		}
		
		if(this.hf.getCheckhash().length()>0){
			final Label checkhaslabel = new Label(shell,SWT.LEFT);
			checkhaslabel.setText("check:");
			checkhaslabel.setBackground(shell.getBackground());
			final Text checkhashtext = new Text(shell,SWT.LEFT|SWT.READ_ONLY|SWT.SINGLE);
			checkhashtext.setText(this.hf.getCheckhash());
			checkhashtext.setBackground(shell.getBackground());
			checkhashtext.setDoubleClickEnabled(true);
			checkhashtext.setLayoutData(this.getGridData());
			
			final Label checkstatuslabel = new Label(shell,SWT.LEFT);
			checkstatuslabel.setText("status:");
			checkstatuslabel.setBackground(shell.getBackground());
			final Text checkstatustext = new Text(shell,SWT.LEFT|SWT.READ_ONLY|SWT.SINGLE);
			checkstatustext.setText(this.hf.getCheckResult());
			checkstatustext.setBackground(shell.getBackground());
			checkstatustext.setDoubleClickEnabled(true);
			checkstatustext.setLayoutData(this.getGridData());
			if(this.hf.getCheckResult().equalsIgnoreCase("ERROR")){
				checkstatustext.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_RED));
			}
		}
		
		final Label hashtypelabel = new Label(shell,SWT.LEFT);
		hashtypelabel.setText("type:");
		hashtypelabel.setBackground(shell.getBackground());
		final Text hashtype = new Text(shell,SWT.LEFT|SWT.READ_ONLY|SWT.SINGLE);
		hashtype.setText(this.config.getHashSum().getName());
		hashtype.setBackground(shell.getBackground());
		hashtype.setDoubleClickEnabled(true);
		hashtype.setLayoutData(this.getGridData());
		
		final Label hashtimelabel = new Label(shell,SWT.LEFT);
		hashtimelabel.setText("time:");
		hashtimelabel.setBackground(shell.getBackground());
		final Text hashtime = new Text(shell,SWT.LEFT|SWT.READ_ONLY|SWT.SINGLE);
		hashtime.setText(this.hf.getProcTime()+" ms hashtime");
		hashtime.setBackground(shell.getBackground());
		hashtime.setDoubleClickEnabled(true);
		hashtime.setLayoutData(this.getGridData());
		
		final Label sizelabel = new Label(shell,SWT.LEFT);
		sizelabel.setText("size:");
		sizelabel.setBackground(shell.getBackground());
		final Text sizetext = new Text(shell,SWT.LEFT|SWT.READ_ONLY|SWT.SINGLE);
		sizetext.setText(this.hf.getFileSize()+" bytes");
		sizetext.setBackground(shell.getBackground());
		sizetext.setDoubleClickEnabled(true);
		sizetext.setLayoutData(this.getGridData());
		
		final Label sepe = new Label(shell,SWT.SEPARATOR|SWT.HORIZONTAL);
        final GridData sepeGD = new GridData(GridData.FILL_HORIZONTAL);
		sepeGD.horizontalSpan = 3;
		sepe.setLayoutData(sepGD);
		
		shell.pack();
		

		// smart positioning
        Rectangle mr = shell.getDisplay().getBounds();
        Rectangle myr = shell.getBounds();
        Rectangle pr = parent.getBounds();
        myr.x = pr.x+((pr.height-myr.height)/2);
        myr.y = pr.y+((pr.width-myr.width)/2);
        if(myr.x < 0){
            myr.x = 0;
        }
        if(myr.x+myr.width > mr.width){
            myr.x = mr.width - myr.width;
        }
        if(myr.y < 0){
            myr.y = 0;
        }
        if(myr.y+myr.height > mr.height){
            myr.y = mr.height - myr.height;
        }
        shell.setBounds(myr);
        
		shell.open();
		Display display = parent.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) display.sleep();
		}
		titleFont.dispose();
		return this.result;
	}
	
	final GridData getGridData(){
		final GridData GD = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING|
				GridData.BEGINNING|
				GridData.FILL_HORIZONTAL);
		GD.horizontalSpan = 2;
		return GD;
	}
 }
