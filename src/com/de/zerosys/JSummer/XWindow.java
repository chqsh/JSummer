/**
 * $Id: XWindow.java,v 1.59 2008-03-05 11:56:16 zerwes Exp $
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


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.regex.Pattern;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import org.eclipse.swt.dnd.*;

class XWindow {
    
    protected XScreen o = null;
	protected XConfig config;
	protected Shell xshell;
	protected Display xdisplay;
	protected Table table;
	
	protected ToolItem toolSaveFile;
	protected MenuItem menuSaveFile;
	protected ToolItem toolUseRelPath;
	protected MenuItem menuUseRelPath;
	protected ToolItem toolUseSysindepPath;
	protected MenuItem menuUseSysindepPath;
	protected XFileGutter xfg;
	private boolean statusSaveFile = false;
	
	// hash-alg => object 
	protected HashMap Hash2MenuMenuItem = new HashMap();
	protected HashMap Hash2ToolMenuItem = new HashMap();
	protected HashMap Hash2XChangeHashAlg = new HashMap();
	
	
    Label numfileslabel;
    Label hashedfileslabel;
    Label errorfileslabel;
    Label memlabel;
    Label totalsizelabel;
    Label totalhashtime;
    
	protected String[] args;
	
	private Thread nameThread;
	private boolean updateName = false;
	
	protected int classDebugLevel = 1;
	
	
	protected XWindow (){
	    // dummy constructor for subclasses
	}
	
	protected XWindow (String[] args){
	    this.args = args;
	    this._init("X");
	    this._startx();
	}
	
	protected void _init(String progName){
	    try{
	        this.xdisplay = new Display();
	        this.xshell = new Shell(this.xdisplay);
	    }catch(UnsatisfiedLinkError e){
	        this.libError(e);
	    }
	    this.setScreen();
		this.config = new XConfig(this,progName);
		this.config.setConsoleVersion(false);
		this.o.debug(this.toString()+" started ...",this.classDebugLevel);
	}
	
	protected void setScreen(){
	    this.o = new XScreen(CoreConfig.DEFAULTDEBUG,this.xshell);
	    this.o.debug(this.toString()+"::setScreen() - created XScreen ... done",this.classDebugLevel);
	}
	
	protected void libError(UnsatisfiedLinkError e){
	    e.printStackTrace();
		//TODO: more eloquent errormessage
	    System.exit(1);
	}
	
	protected void updateTitle(){
		this.xdisplay.asyncExec(new Runnable(){
            public void run(){
            	String title = XWindow.this.config.getProgName()+" "+XWindow.this.config.getHashSumName();
            	if(!XWindow.this.config.getCheckFile().equals("")){
            		title += " checkfile:'"+XWindow.this.config.getCheckFile()+"'";
            	}
				Display.setAppName(title);
				XWindow.this.xshell.setText(title);
            }
        });
	}
	
	protected void _startx(){
		//this.o.debug(this.toString()+"::_startx()",1);
		this.xshell.setSize(XConfig.windowW,XConfig.windowH);
		
		GridLayout gl = new GridLayout();
		gl.marginWidth = 5;
		gl.numColumns = 1;
		this.xshell.setLayout(gl);
		
		Display.setAppName(this.config.getProgName());
		this.xshell.setText(this.config.getProgName());
		
		final GridLayout shellLayout = new GridLayout();
		shellLayout.numColumns = 1;
		this.xshell.setLayout(shellLayout);
		
		InputStream iconsstream = XWindow.class.getResourceAsStream(XConfig.iconPath);
		if(iconsstream==null){
			this.o.error("unable to load icon "+XConfig.iconPath);
		}else{
			final Image img = new Image(this.xdisplay,iconsstream);
			this.xshell.setImage(img);
			this.xshell.addDisposeListener(new DisposeListener(){
                public void widgetDisposed(DisposeEvent e){
                    img.dispose();
                }
            });
		}
		
		//MENUE && Toolbar TODO: organize this bunch of uggly code !!!!!!!!1
		final Menu menu = new Menu(this.xshell, SWT.BAR);
		this.xshell.setMenuBar(menu);
		/*
		 * ToolBar :: use style SWT.RIGHT
		 * otherwise on some systems mixing toolitems 
		 * with icons and other only with text will end up in 
		 * BIG buttons, because the text is drawn BELOW a imaginary icon
		 */
		final ToolBar toolBar = new ToolBar(this.xshell,SWT.HORIZONTAL|SWT.FILL|SWT.RIGHT);
		toolBar.setLayout(new FillLayout(SWT.FILL));
		toolBar.setSize(XConfig.windowW,16);
		toolBar.setLocation(0,0);
		
		// menu entries :: fieMenu, hashMenu, configMenu, helpMenu
		//file
        final MenuItem filemenue = new MenuItem (menu, SWT.CASCADE);
        filemenue.setText("&File");
        filemenue.setAccelerator(SWT.ALT|'f');
        final Menu fileMenu = new Menu(filemenue);
        filemenue.setMenu(fileMenu);
        // hash
		final MenuItem hashmenue = new MenuItem (menu, SWT.CASCADE);
		hashmenue.setText("&Hash-Algorithm");
		hashmenue.setAccelerator(SWT.ALT|'h');
        final Menu hashMenu = new Menu(hashmenue);
        hashmenue.setMenu(hashMenu);
        //config
		final MenuItem configmenue = new MenuItem (menu, SWT.CASCADE);
        configmenue.setText("&Configuration");
		configmenue.setAccelerator(SWT.ALT|'c');
        final Menu configMenu = new Menu(configmenue);
        configmenue.setMenu(configMenu);
        //help
        final MenuItem helpmenue = new MenuItem (menu, SWT.CASCADE);
        helpmenue.setText("&?");
        helpmenue.setAccelerator(SWT.ALT|'?');
        final Menu helpMenu = new Menu(helpmenue);
        helpmenue.setMenu(helpMenu);
        // done menue entries
        
		//addFile
        final MenuItem newFile = new MenuItem(fileMenu, SWT.PUSH);
        newFile.setText("add file(s)");
        final XAddFile xaddfile = new XAddFile(this);
        newFile.addSelectionListener(xaddfile);
        final ToolItem maddfile = new ToolItem(toolBar,SWT.PUSH);
		String iconname = this.config.getIcon("AddFile");
		iconsstream = XWindow.class.getResourceAsStream(iconname);
		if(iconsstream==null){
			this.o.error("unable to load icon "+iconname);
			maddfile.setText("add file(s)");
		}else{
			final Image icon = new Image(this.xdisplay,iconsstream);
			maddfile.setImage(icon);
            maddfile.addDisposeListener(new DisposeListener(){
                public void widgetDisposed(DisposeEvent e){
                    icon.dispose();
                }
            });
		}
        maddfile.setToolTipText("add file(s) to hash ...");
        maddfile.addSelectionListener(xaddfile);
        //addDir
        final MenuItem newDir = new MenuItem(fileMenu, SWT.PUSH);
        newDir.setText("add directory");
        final XAddDir xadddir = new XAddDir(this);
        newDir.addSelectionListener(xadddir);
        final ToolItem madddir = new ToolItem(toolBar,SWT.PUSH);
        try{
            final String is = this.config.getIcon("AddDir");
            final URL iconURL = this.getClass().getResource( is );
            if(iconURL!=null){
				final InputStream s = iconURL.openStream();
	            final Image icon = new Image(this.xdisplay,s);
	            madddir.setImage(icon);
	            madddir.addDisposeListener(new DisposeListener(){
	                public void widgetDisposed(DisposeEvent e){
	                    icon.dispose();
	                }
	            });
	        }else{
				this.o.error("unable to load resource:'"+is+"'");
				madddir.setText("add dir");
	        }
        }catch(IOException e){
            this.o.error(e.getMessage());
        }
        madddir.setToolTipText("add directory to hash recursive ...");
        madddir.addSelectionListener(xadddir);
        
		// sep0
        new ToolItem(toolBar,SWT.SEPARATOR).setWidth(10);
        new MenuItem(fileMenu,SWT.SEPARATOR);
		
		// add checkfile
		final MenuItem addCheckFile = new MenuItem(fileMenu, SWT.PUSH);
		addCheckFile.setText("open check-file");
		final ToolItem maddCheckFile = new ToolItem(toolBar,SWT.PUSH);
		maddCheckFile.setToolTipText("open check-file");
		try{
            final String is = this.config.getIcon("MD5Sum");
            final URL iconURL = this.getClass().getResource( is );
            if(iconURL!=null){
				final InputStream s = iconURL.openStream();
	            final Image icon = new Image(this.xdisplay,s);
	            maddCheckFile.setImage(icon);
				maddCheckFile.addDisposeListener(new DisposeListener(){
					public void widgetDisposed(DisposeEvent e){
						icon.dispose();
					}
				});
	        }else{
				this.o.error("unable to load resource:'"+is+"'");
				maddCheckFile.setText("open check-file");
	        }
        }catch(IOException e){
            this.o.error(e.getMessage());
        }
		final XAddCheckFile xaddcf = new XAddCheckFile(this);
		addCheckFile.addSelectionListener(xaddcf);
		maddCheckFile.addSelectionListener(xaddcf);
		
        // sep1
        new ToolItem(toolBar,SWT.SEPARATOR).setWidth(10);
        new MenuItem(fileMenu,SWT.SEPARATOR);
        
        //saveFile
        this.menuSaveFile = new MenuItem(fileMenu, SWT.PUSH);
        this.menuSaveFile.setText("save Hash");
        this.toolSaveFile = new ToolItem(toolBar,SWT.PUSH);
        try{
            final String is = this.config.getIcon("Floppy");
            final URL iconURL = this.getClass().getResource( is );
            if(iconURL!=null){
				final InputStream s = iconURL.openStream();
	            final Image icon = new Image(this.xdisplay,s);
	            this.toolSaveFile.setImage(icon);
	            final String isdis = this.config.getIcon("FloppyX");
	            final URL iconURLdis = this.getClass().getResource( isdis );
	            if(iconURLdis!=null){
					final InputStream sdis = iconURLdis.openStream();
	                final Image icondis = new Image(this.xdisplay,sdis);
	                this.toolSaveFile.setDisabledImage(icondis);
	                this.toolSaveFile.addDisposeListener(new DisposeListener(){
		                public void widgetDisposed(DisposeEvent e){
		                    icon.dispose();
		                    icondis.dispose();
		                }
		            });
	            }else{
					this.o.error("unable to load resource:'"+isdis+"'");
	                this.toolSaveFile.addDisposeListener(new DisposeListener(){
		                public void widgetDisposed(DisposeEvent e){
		                    icon.dispose();
		                }
		            });
	            }
	        }else{
				this.o.error("unable to load resource:'"+is+"'");
				this.toolSaveFile.setText("save hash");
	        }
        }catch(IOException e){
            this.o.error(e.getMessage());
        }
        this.toolSaveFile.setToolTipText("save check-file ...");
        this.menuSaveFile.setEnabled(false);
        this.toolSaveFile.setEnabled(false);
        final XSaveFile xsavefile = new XSaveFile(this.config,this.xshell);
        this.toolSaveFile.addSelectionListener(xsavefile);
        this.menuSaveFile.addSelectionListener(xsavefile);
        
        // sep2
        new ToolItem(toolBar,SWT.SEPARATOR).setWidth(10);
        new MenuItem(fileMenu,SWT.SEPARATOR);
        
        // clear table
        final ToolItem mclear = new ToolItem(toolBar,SWT.PUSH);
        final MenuItem clear = new MenuItem(fileMenu,SWT.PUSH);
        clear.setText("Clear table ...");
        mclear.setToolTipText("Stop hashing and clear table ...");
        try{
            final String is = this.config.getIcon("Clear");
            final URL iconURL = this.getClass().getResource( is );
	        if(iconURL!=null){
	            final InputStream s = iconURL.openStream();
	            final Image icon = new Image(this.xdisplay,s);
	            mclear.setImage(icon);
	            mclear.addDisposeListener(new DisposeListener(){
	                public void widgetDisposed(DisposeEvent e){
	                    icon.dispose();
	                }
	            });
	        }else{
				this.o.error("unable to load resource:'"+is+"'");
				mclear.setText("clear view");
	        }
        }catch(IOException e){
            this.o.error(e.getMessage());
        }
        final XClearTable xclrtab = new XClearTable(this);
        clear.addSelectionListener(xclrtab);
        mclear.addSelectionListener(xclrtab);
        
		// sep2.5
        new ToolItem(toolBar,SWT.SEPARATOR).setWidth(10);
        //new MenuItem(fileMenu,SWT.SEPARATOR);
		
        // select hash-algorithm
        final ToolItem selectHash = new ToolItem(toolBar,SWT.DROP_DOWN);
        selectHash.setToolTipText("Select the hash-algorithm ...");
        selectHash.setText("Algorithm");
        final Menu selectHashMenue = new Menu(this.xshell,SWT.POP_UP);
        
        Enumeration hie = this.config.containerHashSumInfo.elements();
		while(hie.hasMoreElements()) {
			HashSumInfo hsi = (HashSumInfo)hie.nextElement();
			XChangeHashAlg xchgalgHandler = new XChangeHashAlg(this,hsi.getHashAlgName());
			MenuItem mit = new MenuItem(hashMenu,SWT.CHECK);
			mit.setText(hsi.getHashAlgName().toUpperCase());
			mit.addSelectionListener(xchgalgHandler);
			MenuItem  mitTool = new MenuItem(selectHashMenue,SWT.CHECK);
			mitTool.setText(hsi.getHashAlgName().toUpperCase());
			mitTool.addSelectionListener(xchgalgHandler);
			this.Hash2MenuMenuItem.put(hsi.getHashAlgName(), mit);
			this.Hash2ToolMenuItem.put(hsi.getHashAlgName(), mitTool);
			this.Hash2XChangeHashAlg.put(hsi.getHashAlgName(), xchgalgHandler);
		}
        
        
        selectHash.addListener(SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				Rectangle rect = selectHash.getBounds ();
				Point pt = new Point (rect.x, rect.y + rect.height);
				pt = toolBar.toDisplay (pt);
				selectHashMenue.setLocation (pt.x, pt.y);
				selectHashMenue.setVisible (true);
			}
		});
        
        // sep3
        ToolItem sep3 = new ToolItem(toolBar,SWT.SEPARATOR);
        sep3.setWidth(10);
        
        // relative Path / sysindep Pathseparator
		this.menuUseRelPath = new MenuItem(configMenu,SWT.CHECK);
		this.toolUseRelPath = new  ToolItem(toolBar,SWT.CHECK);
		this.menuUseRelPath.setText("use relative path");
		this.toolUseRelPath.setText("relative path");
		this.toolUseRelPath.setToolTipText("do not use relative Path");
		this.menuUseRelPath.setSelection(true);
		this.toolUseRelPath.setSelection(true);
		final XUseRelPath xuserelp = new XUseRelPath(this);
		this.menuUseRelPath.addSelectionListener(xuserelp);
		this.toolUseRelPath.addSelectionListener(xuserelp);
		if(this.config.isWindows()){
		    this.menuUseSysindepPath = new MenuItem(configMenu,SWT.CHECK);
		    this.toolUseSysindepPath = new ToolItem(toolBar,SWT.CHECK);
		    this.menuUseSysindepPath.setText("use sysindependent pathseparator");
		    this.toolUseSysindepPath.setText("use /");
		    //this.menuUseSysindepPath.setSelection(true);
		    //this.toolUseSysindepPath.setSelection(true);
		    final XUseSysindepPathSep xusesysindep = new XUseSysindepPathSep(this);
		    this.menuUseSysindepPath.addSelectionListener(xusesysindep);
		    this.toolUseSysindepPath.addSelectionListener(xusesysindep);
		    this.setUseSysindepPath(true);
		}else{
		    sep3.setWidth(30);
		}
		
		// sep4
        new ToolItem(toolBar,SWT.SEPARATOR).setWidth(10);
        new ToolItem(toolBar,SWT.SEPARATOR).setWidth(10);
        new MenuItem(fileMenu,SWT.SEPARATOR);
		
        // About
        final MenuItem aboutmenu = new MenuItem(helpMenu,SWT.PUSH);
        aboutmenu.setText("about ...");
        final ToolItem abouttool = new ToolItem(toolBar,SWT.PUSH);
        abouttool.setToolTipText("about ...");
        try{
            final String is = this.config.getIcon("About");
            final URL iconURL = this.getClass().getResource( is );
	        if(iconURL!=null){
	            final InputStream s = iconURL.openStream();
	            final Image icon = new Image(this.xdisplay,s);
	            abouttool.setImage(icon);
	            abouttool.addDisposeListener(new DisposeListener(){
	                public void widgetDisposed(DisposeEvent e){
	                    icon.dispose();
	                }
	            });
	        }else{
				this.o.error("unable to load resource:'"+is+"'");
				abouttool.setText("about");
	        }
        }catch(IOException e){
            this.o.error(e.getMessage());
        }
        final XAbout xabout = new XAbout(this);
        aboutmenu.addSelectionListener(xabout);
        abouttool.addSelectionListener(xabout);
        
        // sep5
        new ToolItem(toolBar,SWT.SEPARATOR).setWidth(10);
        new ToolItem(toolBar,SWT.SEPARATOR).setWidth(10);
        new MenuItem(fileMenu,SWT.SEPARATOR); // double delimiter for exit !!!
		
		
        // EXIT
        final MenuItem exitMe = new MenuItem(fileMenu,SWT.PUSH);
        exitMe.setText("EXIT");
        final ToolItem mexitme = new ToolItem(toolBar,SWT.PUSH);
        try{
            final String is = this.config.getIcon("Cancel");
            final URL iconURL = this.getClass().getResource( is );
	        if(iconURL!=null){
	            final InputStream s = iconURL.openStream();
	            final Image icon = new Image(this.xdisplay,s);
	            mexitme.setImage(icon);
	            mexitme.addDisposeListener(new DisposeListener(){
	                public void widgetDisposed(DisposeEvent e){
	                    icon.dispose();
	                }
	            });
	        }else{
				this.o.error("unable to load resource:'"+is+"'");
				mexitme.setText("exit");
	        }
        }catch(IOException e){
            this.o.error(e.getMessage());
        }
        mexitme.setToolTipText("EXIT");
        final XClose xclose = new XClose(this.xshell,this.config);
        mexitme.addSelectionListener(xclose);
        exitMe.addSelectionListener(xclose);
		
        // done MENU
        toolBar.pack();
        
        
        // TABLE
        final Composite comp = new Composite(this.xshell, SWT.NONE);
		this.table = new Table (comp,SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL|SWT.FULL_SELECTION);
		this.config.setTable(this.table);
		this.table.setFont(this.config.getFont());
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);
		this.table.addListener(SWT.MouseDoubleClick,new XHashFileInfoListener(this.config));
		//this.table.setToolTipText("double-click on a item to view details ...");
		
		final GridData tableGD = new GridData();
		tableGD.grabExcessHorizontalSpace = true;
		tableGD.grabExcessVerticalSpace = true;
		tableGD.verticalAlignment = GridData.FILL;
		tableGD.horizontalAlignment = GridData.FILL;
		comp.setLayoutData(tableGD);
		
		final TableColumn column0 = new TableColumn(this.table, SWT.NONE);
        column0.setText("File");
        column0.setWidth(XConfig.windowW-XConfig.columnWidthHashMin);
        final TableColumn column1 = new TableColumn(this.table, SWT.NONE);
        column1.setText("Hash");
        column1.setWidth(XConfig.columnWidthHashMax);
        
        // resize columns if window resizes // inspired by eclipse/SWT examples
        comp.addControlListener(new ControlAdapter() {
    		public void controlResized(ControlEvent e) {
    			Rectangle area = comp.getClientArea();
    			Point preferredSize = XWindow.this.table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
    			int width = area.width - 2*XWindow.this.table.getBorderWidth();
    			if (preferredSize.y > area.height + XWindow.this.table.getHeaderHeight()) {
    				// Subtract the scrollbar width from the total column width
    				// if a vertical scrollbar will be required
    				Point vBarSize = XWindow.this.table.getVerticalBar().getSize();
    				width -= vBarSize.x;
    			}
    			Point oldSize = XWindow.this.table.getSize();
    			int c1width = XConfig.columnWidthHashMax;
    			int c0width = width-XConfig.columnWidthHashMin;
    			if (area.width>XConfig.windowW){
    				//XWindow.this.o.debug("area bigger",0);
    				if(area.width-XConfig.windowW<XConfig.columnWidthHashDiff){
        				//XWindow.this.o.debug("bigger diff smaller "+XConfig.columnWidthHashDiff,0);
    					c0width = width-(XConfig.columnWidthHashMin+(area.width-XConfig.windowW));
    				}else{
    					c0width = width-XConfig.columnWidthHashMax;
    				}
    			}
    			if (c0width<50){
    			    c0width = 50;
    			}
    			XWindow.this.o.debug("controlResized :: area.width="+area.width+"; c0width="+c0width+"; c1width="+c1width, XWindow.this.classDebugLevel);
    			if (oldSize.x > area.width) {
    				// table -> smaller
    				column0.setWidth(c0width);
    				column1.setWidth(c1width);
    				XWindow.this.table.setSize(area.width, area.height);
    			} else {
    				// table -> bigger
    				XWindow.this.table.setSize(area.width, area.height);
    				column0.setWidth(c0width);
    				column1.setWidth(c1width);
    			}
    			XWindow.this.table.update();
    			XWindow.this.table.redraw();
    		}
    	});
		
		// DnD (Drag and Drop) - Drop Target
		DropTarget target = new DropTarget(comp,DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK);
		target.setTransfer(new Transfer[]{FileTransfer.getInstance()});
        target.addDropListener(
          new DropTargetAdapter() {
            // DropTargetListener is a abstract interface. So, you MUST override every method.
            // But, DropTargetAdapter is only overrided used method.
            
            /* Now, we can ignore dropAccept() when we do only FileTransfer. */
            /*
            @Override
            public void dropAccept(DropTargetEvent event) {
                // Refer To:
                // https://github.com/blizzy78/blizzys-backup/blob/master/de.blizzy.backup/src/de/blizzy/backup/settings/SettingsDialog.java
                
                System.out.printf("%s, {detail 0x%X, feedback 0x%X, operations 0x%X}\n\t currentDataType is %s\n",
                    event.toString(), event.detail, event.feedback, event.operations,
                    event.currentDataType.toString());
                
                if (!FileTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    event.detail = DND.DROP_NONE; // DO NOT accept drop operation. The "drop()" will NOT be called!
                }
            }
            */
             
            @Override
            public void drop(DropTargetEvent event) {
                // System.out.println(event.toString());
                String[] files = (String[]) event.data;
                if (files == null || files.length <= 0)
                    return;
                
                if (files.length == 1) {
                    String file = files[0];
                    /*
                    final String  matchesRule = "(?i).*\\.(md5|sha1|sha256|sha384|sha512)$";
                    final Pattern pattern = Pattern.compile(matchesRule);
                    if (pattern.matcher(file).matches()) {
                    */
                    final String matchesRule = "(?i).*\\.("
                        + config.getAllSupportedTypesExt("", "|") + ")$";
                    if (file.matches(matchesRule)) {
                        // now the msgbox is raised by XClearTable
                        final XClearTable xclrtab = new XClearTable(XWindow.this);
                        Event e = new Event();
                        e.type = SWT.Selection;
                        e.widget = table;
                        xclrtab.widgetSelected(new SelectionEvent(e));
                        if(!xclrtab.isClearTableDoit()){
                            o.debug(this.toString()+" cleartable::aborted",XWindow.this.classDebugLevel);
                            return;
                        }
                        config.setCheckMDFile(file, true);
                        return;
                    } else {
                        config.addFileDir(file);
                        return;
                    }
                }
                
                for (String file : files) {
                    config.addFileDir(file);
                }
            }
            
            /*
            @Override
            public void dragOver(DropTargetEvent event) {
                // TODO Auto-generated method stub
            }
            @Override
            public void dragOperationChanged(DropTargetEvent event) {
                // TODO Auto-generated method stub
            }
            @Override
            public void dragLeave(DropTargetEvent event) {
                // TODO Auto-generated method stub
            }
            @Override
            public void dragEnter(DropTargetEvent event) {
                // TODO Auto-generated method stub
            }
            */
        });
        
        // status-bar
        final Composite labelcomp = new Composite(this.xshell, SWT.NONE);
        final GridLayout labellayout = new GridLayout();
        labellayout.numColumns = 4;
        labellayout.makeColumnsEqualWidth = true;
        //labelcomp.setSize(XConfig.windowW,10);
        labelcomp.pack();
        labelcomp.setLayout(labellayout);
        final Composite comp1 = new Composite(labelcomp,SWT.NONE);
        comp1.setLayout(this.getGridLayout());
        final Label label1 = new Label(comp1,SWT.LEFT);
        label1.setText("Total Files:");
        label1.setLayoutData(this.getGridData());
        this.numfileslabel = new Label(comp1,SWT.SHADOW_IN);
        this.numfileslabel.setText("0");
        this.numfileslabel.setLayoutData(this.getGridData());
        final Composite comp2 = new Composite(labelcomp,SWT.NONE);
        comp2.setLayout(this.getGridLayout());
        final Label label2 = new Label(comp2,SWT.LEFT);
        label2.setText("Hashed Files:");
        label2.setLayoutData(this.getGridData());
        this.hashedfileslabel = new Label(comp2,SWT.SHADOW_IN);
        this.hashedfileslabel.setText("0");
        this.hashedfileslabel.setLayoutData(this.getGridData());
        final Composite comp3 = new Composite(labelcomp,SWT.NONE);
        comp3.setLayout(this.getGridLayout());
        final Label label3 = new Label(comp3,SWT.LEFT);
        label3.setText("Errors:");
        label3.setLayoutData(this.getGridData());
        this.errorfileslabel = new Label(comp3,SWT.SHADOW_IN);
        this.errorfileslabel.setText("0");
        this.errorfileslabel.setLayoutData(this.getGridData());
        final Composite comp4 = new Composite(labelcomp,SWT.NONE);
        comp4.setLayout(this.getGridLayout());
        comp4.setLayout(this.getGridLayout());
        final Label label4 = new Label(comp4,SWT.LEFT);
        label4.setText("Memory used/avail (KB):");
        label4.setLayoutData(this.getGridData());
        this.memlabel = new Label(comp4,SWT.SHADOW_IN);
        final String s = this.getMemStats();
        this.memlabel.setText(s);
        this.memlabel.setLayoutData(this.getGridData());
        
        
        
        
        
        // close :: confirm close ////////////////////////////////////////
        this.xshell.addShellListener(new ShellAdapter(){
        	public void shellClosed(ShellEvent e){
        		XWindow.this.o.debug("shellClosed:"+e.toString(),XClose.classDebugLevel);
        		MessageBox mb = new MessageBox(XWindow.this.xshell,SWT.OK|SWT.CANCEL|SWT.ICON_QUESTION);
        		mb.setText("CLOSE "+CoreConfig.ProgName);
        		mb.setMessage("Do you realy want to exit ?");
        		int r = mb.open();
        		//o.debug("shellClosed::mb : "+r,XClose.classDebugLevel);
        		e.doit = r==SWT.OK;
        		if(!e.doit){
        			XWindow.this.o.debug("shellClosed aborted",XClose.classDebugLevel);
        		}else{
        			XWindow.this.o.debug("shellClosed setStop ...",XClose.classDebugLevel);
					XWindow.this.config.setStopMe(true);
        			XWindow.this.xfg.setStopMe(true);
        			XWindow.this.o.debug("shellClosed setStop done",XClose.classDebugLevel);
        		}
        	}
        });
        
        this.xshell.open();
        
        //this.o.debug(this.toString()+"::shell opened",1);
        
        final XMemStatsThread memstats = new XMemStatsThread(this);
        new Thread(memstats).start();
        /*this.setErrorfilesLabel(0);
        this.setNumfilesLabel(0);
        this.setHashedfilesLabel(0);*/
        
        if(this.config.configByArgs(this.args)){
			String errorMsg = this.config.getConfigErrorMsg();
			if (errorMsg.isEmpty()) {
				this.o.println(this.config.getHelp());
				this.o.println();
			} else {
				this.o.error(this.config.getConfigErrorMsg());
			}
		}
		
		// adjust hash-selection and title
        Enumeration hie2 = this.config.containerHashSumInfo.elements();
		while(hie2.hasMoreElements()) {
			HashSumInfo hsi = (HashSumInfo)hie2.nextElement();
			if(this.config.getHashSumName().equals(hsi.getHashAlgName())) {
				XChangeHashAlg xchg = (XChangeHashAlg) this.Hash2XChangeHashAlg.get(hsi.getHashAlgName());
				xchg.actOnSelection(true);
			}
		}
		
		this.o.debug(this.toString()+":: start XFileGutter ...",1);
		
		final boolean origUpdateName = this.updateName;
		
		this.xfg = new XFileGutter(this);
		Thread fg = new Thread(this.xfg);
		fg.start();
		
		// now the namethread ist started from xw
		this.o.debug(this.toString()+":: start XFileGutterNameThread ...",1);
		XFileGutterNameThread xfgnamethread = new XFileGutterNameThread(this);
	    this.nameThread = new Thread(xfgnamethread);
	    this.nameThread.setPriority(Thread.MAX_PRIORITY);
	    this.nameThread.start();
	    
	    this.setUpdateName(origUpdateName);
		
		while(!this.xshell.isDisposed()){
			if(!this.xdisplay.readAndDispatch()){
				this.xdisplay.sleep();
			}
		}
		
		this.o.debug(this.toString()+" going down ...",1);
		
		this.xfg.setStopMe(true);
		
		this.config.disposeFont();
		this.xshell.dispose();
		
		this.o.debug(this.config.getProgName()+"::EXIT",1);
	}
    
    protected String getMemStats(){
        long tomb = 1024;
        float used = (Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/tomb;
        float total = Runtime.getRuntime().totalMemory()/tomb;
        final NumberFormat nf = NumberFormat.getNumberInstance();
        return nf.format(used)+" / "+nf.format(total);
    }
    protected void setMemstats(){
        this.xdisplay.asyncExec(new Runnable(){
            public void run(){
                XWindow.this.memlabel.setText(" "+XWindow.this.getMemStats()+" ");
                XWindow.this.memlabel.pack();
                XWindow.this.memlabel.redraw();
            }
        });
    }
    
    protected void setNumfilesLabel(final int n){
        this.setNumfilesLabel(Integer.toString(n));
    }
    protected void setNumfilesLabel(final String n){
        this.xdisplay.asyncExec(new Runnable(){
            public void run(){
                XWindow.this.numfileslabel.setText(n+" ");
                XWindow.this.numfileslabel.pack();
                XWindow.this.numfileslabel.redraw();
            }
        });
    }
    protected void setHashedfilesLabel(final int n){
        this.setHashedfilesLabel(Integer.toString(n));
    }
    protected void setHashedfilesLabel(final String n){
        this.xdisplay.asyncExec(new Runnable(){
            public void run(){
                XWindow.this.hashedfileslabel.setText(n+" ");
                XWindow.this.hashedfileslabel.pack();
                XWindow.this.hashedfileslabel.redraw();
            }
        });
    }
    protected void setErrorfilesLabel(final int n){
    	this.setErrorfilesLabel(Integer.toString(n));
    }
    protected void setErrorfilesLabel(final String n){
        this.xdisplay.asyncExec(new Runnable(){
            public void run(){
                XWindow.this.errorfileslabel.setText(n+" ");
                if(n.equals("0")){
                	XWindow.this.errorfileslabel.setForeground(XWindow.this.xdisplay.getSystemColor(SWT.COLOR_BLACK));
                }else{
                	XWindow.this.errorfileslabel.setForeground(XWindow.this.xdisplay.getSystemColor(SWT.COLOR_RED));
                }
                XWindow.this.errorfileslabel.pack();
                XWindow.this.errorfileslabel.redraw();
            }
        });
    }
    
    private GridData getGridData(){
        GridData GD = new GridData(
                GridData.HORIZONTAL_ALIGN_BEGINNING |
                GridData.VERTICAL_ALIGN_BEGINNING );
        GD.grabExcessHorizontalSpace = true;
        GD.grabExcessVerticalSpace = false;
        return GD;
    }
    
    private GridLayout getGridLayout(){
    	final GridLayout gl = new GridLayout();
    	gl.numColumns = 1;
    	return gl;
    }
    
	protected synchronized  void setEnableSaveFile(final boolean enable){
	    if(this.statusSaveFile==enable){
	        return;
	    }
	    this.statusSaveFile = enable;
	    this.xdisplay.asyncExec(new Runnable(){
            public void run(){
                XWindow.this.toolSaveFile.setEnabled(enable);
        	    XWindow.this.menuSaveFile.setEnabled(enable);
            }
        });
	    
	}
	
	// TODO :: CLEAN UP AND CORRECT THIS SHIT !!!!!!!!!!!!!
	protected synchronized void setUseRelPath(final boolean enable){
		this.o.debug("setUseRelPath: "+enable,this.classDebugLevel);
		this.setMenuUseRelPath(enable);
    	this.setToolbarUseRelPath(enable);
    	if(this.xfg!=null){
    		this.setUpdateName(true);
    	}
	}
	private void setMenuUseRelPath(final boolean useRelPath){
		this.o.debug("setMenuUseRelPath + toolbar-tooltip: "+useRelPath,this.classDebugLevel);
		this.xdisplay.asyncExec(new Runnable(){
            public void run(){
            	XWindow.this.menuUseRelPath.setSelection(useRelPath);
            	XWindow.this.menuUseRelPath.setEnabled(true);
				if(useRelPath){
					XWindow.this.toolUseRelPath.setToolTipText("use relative path");
					if(XWindow.this.toolUseSysindepPath!=null){
						XWindow.this.toolUseSysindepPath.setEnabled(true);
						XWindow.this.menuUseSysindepPath.setEnabled(true);
					}
				}else{
					XWindow.this.toolUseRelPath.setToolTipText("do not use relative path");
					if(XWindow.this.toolUseSysindepPath!=null){
						XWindow.this.toolUseSysindepPath.setEnabled(false);
						XWindow.this.menuUseSysindepPath.setEnabled(false);
					}
				}
            }
        });
	}
	private void setToolbarUseRelPath(final boolean useRelPath){
		this.o.debug("setToolbarUseRelPath + tooltip: "+useRelPath,this.classDebugLevel);
		this.xdisplay.asyncExec(new Runnable(){
            public void run(){
            	XWindow.this.toolUseRelPath.setSelection(useRelPath);
            	XWindow.this.toolUseRelPath.setEnabled(true);
				if(useRelPath){
					XWindow.this.toolUseRelPath.setToolTipText("do not use relative path");
				}else{
					XWindow.this.toolUseRelPath.setToolTipText("use relative path");
				}
            }
        });
	}
	
	protected synchronized void setUnableToUseSysindepPath(final boolean enable){
		this.o.debug("setUnableToUseSysindepPath: "+enable,this.classDebugLevel);
		if(!this.config.isWindows()){
			return;
		}
		this.xdisplay.asyncExec(new Runnable(){
            public void run(){
				if(!enable){
					XWindow.this.toolUseRelPath.setSelection(false);
					XWindow.this.toolUseRelPath.setToolTipText("unable to use relative path!!!");
					XWindow.this.menuUseRelPath.setSelection(false);
					XWindow.this.toolUseRelPath.setEnabled(enable);
					XWindow.this.menuUseRelPath.setEnabled(enable);
				}
                if(XWindow.this.toolUseSysindepPath!=null){
                	XWindow.this.toolUseSysindepPath.setSelection(enable);
                	XWindow.this.toolUseSysindepPath.setEnabled(enable);
                }
        	    if(XWindow.this.menuUseSysindepPath!=null)
        	    	XWindow.this.menuUseSysindepPath.setEnabled(enable);
            }
        });
		this.setUpdateName(true);
	}
	protected void setUseSysindepPath(final boolean useRelPath){
		this.setMenuUseSysindepPath(useRelPath);
		this.setToolbarUseSysindepPath(useRelPath);
		this.setUpdateName(true);
	}
	private void setMenuUseSysindepPath(final boolean useRelPath){
		this.o.debug("setMenuUseSysindepPath + toolbar-tooltip: "+useRelPath,this.classDebugLevel);
		if(this.menuUseSysindepPath == null){
			this.o.debug("setMenuUseSysindepPath :: menuUseSysindepPath NULL",this.classDebugLevel);
			return;
		}
		this.xdisplay.asyncExec(new Runnable(){
            public void run(){
            	XWindow.this.menuUseSysindepPath.setSelection(useRelPath);
				if(useRelPath){
					XWindow.this.toolUseSysindepPath.setToolTipText("use sysindependent path");
				}else{
					XWindow.this.toolUseSysindepPath.setToolTipText("do not use sysindependent path");
				}
            }
        });
	}
	private void setToolbarUseSysindepPath(final boolean useRelPath){
		this.o.debug("setToolbarUseSysindepPath + tooltip: "+useRelPath,this.classDebugLevel);
		if(this.toolUseSysindepPath == null){
			this.o.debug("setToolbarUseSysindepPath :: toolUseSysindepPath NULL",this.classDebugLevel);
			return;
		}
		this.xdisplay.asyncExec(new Runnable(){
            public void run(){
            	XWindow.this.toolUseSysindepPath.setSelection(useRelPath);
				if(useRelPath){
					XWindow.this.toolUseSysindepPath.setToolTipText("use Sysindependent path");
				}else{
					XWindow.this.toolUseSysindepPath.setToolTipText("do not use sysindependent path");
				}
            }
        });
	}
	
	protected XScreen getScreen(){
		return this.o;
	}
    protected XConfig getConfig() {
        return this.config;
    }
    protected Shell getShell() {
        return this.xshell;
    }
    
    // wrapper for XFileGutter
    protected synchronized void setStopHash(boolean stopHash){
    	if(this.xfg==null) return;
        this.xfg.setStopHash(stopHash);
    }
    
    // wrapper for XFileGutter
    protected synchronized void setUpdateHash(boolean updateHash){
    	if(this.xfg==null) return;
        this.xfg.setUpdateHash(updateHash);
    }
	
	protected synchronized boolean isUpdateName() {
		this.o.debug(this.toString()+"::isUpdateName(): "+this.updateName,this.classDebugLevel);
		if(!this.updateName){
			try{
                wait();
            }catch(InterruptedException e){
                this.o.error(this.toString()+"::isUpdateName() wait()...:"+e.getMessage());
            }
            this.setUpdateName(false);
		}
		return true;
	}

	protected synchronized void setUpdateName(boolean updateName) {
		this.o.debug(this.toString()+"::setUpdateName():"+updateName+" (was: "+this.updateName+")",this.classDebugLevel);
		if(this.updateName == updateName){
			this.o.debug(this.toString()+"::setUpdateName() repeated call ignored!",this.classDebugLevel);
			return;
		}
		this.updateName = updateName;
		notify();
	}

	protected XFileGutter getXfg() {
		if(this.xfg==null){
			this.o.fatalError(this.toString()+"::getXfg() XFG is NULL !!!");
		}
		return this.xfg;
	}

	protected Table getTable() {
		return this.table;
	}
	
    protected void setXWindowActive(){
    	this.xdisplay.syncExec(new Runnable(){
            public void run(){
            	XWindow.this.xshell.forceActive();
            }
        });
    }
    
}
