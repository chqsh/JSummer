/*
 * $Id: XMemStatsThread.java,v 1.3 2006-03-24 10:47:56 io Exp $
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

final class XMemStatsThread implements Runnable {
    
    private XWindow xw;
    
    XMemStatsThread(XWindow xw){
        super();
        this.xw = xw;
    }
    
    public void run(){
        while(!this.xw.getShell().isDisposed()){
            this.xw.setMemstats();
            try {
                Thread.sleep(XFileGutter.updateDisplay);
            } catch (InterruptedException e) {
                this.xw.getScreen().error(this.toString()+" sleep : "+e.getMessage());
            }
        }
    }
}
