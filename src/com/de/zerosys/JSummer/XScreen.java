/*
 * $Id: XScreen.java,v 1.6 2006-03-24 10:47:56 io Exp $
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
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * @author zerwes
 */
class XScreen extends Screen{

    private Shell xshell = null;
    
    XScreen() {
        super();
    }
    XScreen(int _level) {
        super(_level);
    }
    XScreen(Shell _shell) {
        super();
        this.xshell = _shell;
    }
    XScreen( int _level,Shell _shell ){
        super(_level);
        this.xshell = _shell;
    }
    
    protected void setXShell(Shell _shell){
        this.xshell = _shell;
    }
    
    public void error( String _msg ){
        super.error(_msg);
        this.popUp(_msg);
    }
    protected void popUp(String _msg, String _title){
        if( this.xshell == null ){
            return;
        }
        if(this.xshell.isDisposed()){
            super.error("popUp request for disposed shell! message:'"+_msg+"'");
            return;
        }
        MessageBox mb = new MessageBox(this.xshell,SWT.OK|SWT.ICON_ERROR);
        mb.setMessage(_msg);
        mb.setText(_title);
        mb.open();
    }
    protected void popUp(String _msg){
        this.popUp(_msg,"ERROR");
    }
    public void fatalError( String _msg ){
        this.error( _msg );
        if( this.xshell != null ){
            this.xshell.dispose();
        }
        System.exit(1);
    }
    
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
