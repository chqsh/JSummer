/**
 * $Id: exportSWTPlatform.java,v 1.3 2008-03-05 11:56:16 zerwes Exp $
 * 
 * (c) 2006 - 2008 Klaus Zerwes zero-sys.net
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

public class exportSWTPlatform {
    public static void main(String[] args) {
        System.out.println(SWT.getPlatform());
    }

}
