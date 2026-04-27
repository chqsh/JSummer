/**
 * $Id: ConsoleRun.java,v 1.10 2008-03-05 11:56:16 zerwes Exp $
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



class ConsoleRun {

	private Screen o;
	private Config config;
	
	protected ConsoleRun(String[] args){
		this.o = new Screen(CoreConfig.DEFAULTDEBUG);
		this.config = new Config(this.o);
		this.config.setConsoleVersion(true);
		if (!this.config.configByArgs(args)){
			this.o.debug("recieved " + this.config.getMD5Files().size()+" files as args", 5);
			ConsoleFileGutter cf = new ConsoleFileGutter(this.config);
			cf.doit();
		}else{
			this.o.println(this.config.getHelp());
			this.o.println();
			this.o.fatalError(this.config.getConfigErrorMsg());
		}
		System.exit(this.config.getProgExitCode());
	}
}
