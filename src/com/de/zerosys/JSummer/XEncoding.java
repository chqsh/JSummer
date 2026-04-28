/*
 * $Id: XAbout.java,v 1.4 2006-03-24 10:47:56 io Exp $
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

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author zerwes
 */
class XEncoding extends SelectionAdapter{
	
	private XConfig config;
	private Menu parentMenu;
	private String prevSelected;

	static private List<String> charsetName = new ArrayList<>(Arrays.asList(
		"Auto", "", "UTF-8", "UTF-16BE", "UTF-16LE",
		"GB18030", "GBK", "Big5", "Big5-HKSCS",
		"EUC-JP", "Shift_JIS", "EUC-KR", "JOHAB", "..."
	));
	
	protected XEncoding(XWindow xw, Menu parentMenu){
		super();
		this.config = xw.getConfig();
		this.parentMenu = parentMenu;
		this.prevSelected = "";

		String file_encding = System.getProperty("file.encoding");
		if (file_encding == null || file_encding.isEmpty())
			file_encding = Charset.defaultCharset().name();
		if (file_encding != null && !file_encding.isEmpty()) {
			charsetName.remove(file_encding); // remove duplcate item
			charsetName.set(1, file_encding);
		}
	}

	protected List<String> getCharsetName() {
		return charsetName;
	}
	
	protected void setSelection(String encodingName) {
		boolean found = false;
		
		String csName = (encodingName == null || encodingName.isEmpty())
			? "Auto" : encodingName;
		prevSelected = csName;
		
		MenuItem[] items = parentMenu.getItems();
		for (MenuItem menuItem : items) {
			boolean expectedSelected = false;
			if (menuItem.getText().equalsIgnoreCase(csName)) {
				found = true;
				expectedSelected = true;
			}
			if (menuItem.getSelection() != expectedSelected) {
				menuItem.setSelection(expectedSelected);
			}
		}
		if (found) return;

		final MenuItem item = new MenuItem(parentMenu, SWT.RADIO);
		item.setText(csName);
		item.addSelectionListener(this);
		item.setSelection(true);
	}

	protected String getSelection() {
		String result = ""; 
		if (parentMenu == null)
			return result;
		
		MenuItem[] items = parentMenu.getItems();
		for (MenuItem menuItem : items) {
			if (menuItem.getSelection()) {
				result = menuItem.getText();
				if (result.equals("Auto") ||
					result.equals("..."))
					result = "";
				break;
			}
		}
		return result;
	}

	public void widgetSelected(SelectionEvent e){
		this.config.getScreen().debug(e.toString(),111);
		if (e.getSource() instanceof MenuItem) {
			MenuItem item = (MenuItem)e.getSource();
			if (item.getSelection()) {
				String savedPrevSelected = prevSelected;
				if (prevSelected.isEmpty())
					prevSelected = "Auto"; 
				String encoding = item.getText();
				if (encoding.equals("...")) {
					// open new input dialog
					InputDialog dialog = new InputDialog(this.config.getXshell(),
						"Input", "Check-file encoding:",
						charsetName.get(1));
					String csName = dialog.open();
					if (csName != null && !csName.isEmpty()
						&& !csName.equalsIgnoreCase(prevSelected)) {
						if (this.config.setCheckMDFileEncoding(csName)) {
							item.setSelection(false);
							XEncoding.this.setSelection(csName);
						}
					}
				} else if (encoding.equalsIgnoreCase(prevSelected)) {
					// System.out.printf("Current file encoding: \"%s\"%n",
					// 	this.config.getCheckMDFileEncoding());
					return;
				} else {
					if (encoding.equals("Auto"))
						encoding = "";
					this.config.setCheckMDFileEncoding(encoding);
					prevSelected = encoding;
				}
				// System.out.printf("Current file encoding \"%s\" -> \"%s\"%n",
				// 	savedPrevSelected, this.config.getCheckMDFileEncoding());

				String checkMDFile = this.config.getCheckMDFile();
				if (checkMDFile != null && !checkMDFile.isEmpty()) {
					XWindow xw = this.config.getXwindow();
					new XClearTable(xw).clearTable();
					this.config.setCheckMDFile(checkMDFile); // re-parseFile
				}
			}
		}
	}

}
