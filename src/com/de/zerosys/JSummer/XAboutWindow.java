/*
 * $Id: XAboutWindow.java,v 1.7 2006-03-24 10:47:57 io Exp $
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
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;


class XAboutWindow extends Dialog{
	Object result;
	XConfig config;
	int style;
	
	protected XAboutWindow(XConfig config, int style) {
		super(config.xshell, style);
		this.style = style;
		this.config = config;
		this.config.getScreen().debug(this.toString(),111);
	}
	protected XAboutWindow(XConfig config) {
		this (config, SWT.NO_TRIM|SWT.APPLICATION_MODAL);
	}
	
	public Object open () {
		this.config.getScreen().debug(this.toString()+"::open()",111);
		
		final String titlestring = this.config.getProgName()+" :: about";
		
		final Shell parent = getParent();
		final Shell shell = new Shell(parent, this.style);
		
		shell.setText(titlestring);
		shell.setImage(parent.getImage());
		
		GridLayout gl = new GridLayout();
		gl.marginWidth = 0;
		gl.numColumns = 2;
		shell.setLayout(gl);
		
		shell.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		shell.setForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		
		
		final FontData fontdata = new FontData(XConfig.fontName,XConfig.titleFontSize,SWT.BOLD);
		final Font titleFont = new Font(shell.getDisplay(),fontdata);
		final Label title = new Label(shell,SWT.CENTER);
		final GridData titleGD = new GridData(
				GridData.HORIZONTAL_ALIGN_CENTER|
				GridData.VERTICAL_ALIGN_CENTER|
				GridData.FILL_BOTH);
		title.setLayoutData(titleGD);
		title.setText(titlestring);
		title.setToolTipText(title.getText());
		title.setFont(titleFont);
		title.setBackground(shell.getBackground());
		title.setForeground(shell.getForeground());
		
		
		final Button exitme = new Button(shell,SWT.PUSH);
		final GridData buttonGD = new GridData(GridData.HORIZONTAL_ALIGN_END);
		exitme.setLayoutData(buttonGD);
		exitme.setToolTipText("close");
		exitme.setText(" x ");
		exitme.setBackground(shell.getBackground());
		exitme.setForeground(shell.getForeground());
		exitme.addSelectionListener(new SelectionAdapter(){
        	public void widgetSelected(SelectionEvent event){
        		shell.dispose();
        	}
        });
		
		final Label sep = new Label(shell,SWT.SEPARATOR|SWT.HORIZONTAL);
        final GridData sepGD = new GridData(GridData.FILL_HORIZONTAL);
		sepGD.horizontalSpan = 2;
		sep.setLayoutData(sepGD);
		sep.setBackground(shell.getBackground());
		
		
		final Composite comp = new Composite(shell,SWT.NONE);
		comp.setBackground(shell.getBackground());
		comp.setForeground(shell.getForeground());
		final GridData compGD = new GridData(
				GridData.HORIZONTAL_ALIGN_CENTER|
				GridData.CENTER|
				GridData.VERTICAL_ALIGN_CENTER|
				GridData.FILL_BOTH);
		compGD.horizontalSpan = 2;
		comp.setLayoutData(compGD);
		final GridLayout complayout = new GridLayout();
		complayout.marginWidth = 15;
		complayout.numColumns = 2;
		complayout.horizontalSpacing = 5;
		comp.setLayout(complayout);
		
		final Label imglabel = new Label(comp,SWT.LEFT);
		imglabel.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING|
				GridData.VERTICAL_ALIGN_BEGINNING));
		imglabel.setImage(parent.getImage());
		imglabel.setBackground(shell.getBackground());
		imglabel.setForeground(shell.getForeground());
		
		final Label prog = new Label(comp,SWT.WRAP);
		prog.setFont(titleFont);
		prog.setLayoutData(new GridData(
				GridData.HORIZONTAL_ALIGN_CENTER|
				GridData.VERTICAL_ALIGN_CENTER|
				GridData.FILL_BOTH));
		prog.setText(this.config.getShort());
		prog.setBackground(shell.getBackground());
		prog.setForeground(shell.getForeground());
		
		
		final Label copyleft = new Label(comp,SWT.WRAP);
		copyleft.setText(CoreConfig.COPYLEFT);
		copyleft.setLayoutData(this.getGridData());
		copyleft.setBackground(shell.getBackground());
		copyleft.setForeground(shell.getForeground());
		
		final Label license = new Label(comp,SWT.WRAP|SWT.HORIZONTAL);
		license.setText(CoreConfig.GPL);
		license.setLayoutData(this.getGridData());
		license.setBackground(shell.getBackground());
		license.setForeground(shell.getForeground());
		
		final Label gnucrypto = new Label(comp,SWT.WRAP);
		gnucrypto.setText(CoreConfig.GNUCRYPTOCOPYLEFT);
		gnucrypto.setLayoutData(this.getGridData());
		gnucrypto.setBackground(shell.getBackground());
		gnucrypto.setForeground(shell.getForeground());
		
		final Label swt = new Label(comp,SWT.WRAP);
		swt.setText(CoreConfig.SWTCOPYLEFT);
		swt.setLayoutData(this.getGridData());
		swt.setBackground(shell.getBackground());
		swt.setForeground(shell.getForeground());
		
		final Text arch = new Text(comp,SWT.WRAP|SWT.MULTI|SWT.READ_ONLY|SWT.LEFT);
		arch.setText(this.config.getShort()
				+Text.DELIMITER
				+this.config.getJVMSpecs()
				+Text.DELIMITER
				+this.config.getSWTVersion());
		arch.setLayoutData(this.getGridData());
		arch.setBackground(shell.getBackground());
		arch.setForeground(shell.getForeground());
		
		final Label sep2 = new Label(shell,SWT.SEPARATOR|SWT.HORIZONTAL);
        final GridData sepGD2 = new GridData(GridData.FILL_HORIZONTAL);
		sepGD2.horizontalSpan = 2;
		sep2.setLayoutData(sepGD2);
		sep2.setBackground(shell.getBackground());
		
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
	
	private GridData getGridData(){
		final GridData GD = new GridData(
				GridData.HORIZONTAL_ALIGN_BEGINNING|
				GridData.VERTICAL_ALIGN_BEGINNING|
				GridData.FILL_BOTH);
		GD.horizontalSpan = 2;
		return GD;
	}
}
