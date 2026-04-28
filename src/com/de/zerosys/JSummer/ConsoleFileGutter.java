/**
 * $Id: ConsoleFileGutter.java,v 1.13 2008-03-05 11:56:16 zerwes Exp $
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

import java.util.Enumeration;
import java.util.Vector;

/**
 * @author klaus zerwes zero-sys.net
 */
final class ConsoleFileGutter {
	private int classDebugLevel = 20;
	
	private Config config;
	private Screen o;
	
	private static int updateDisplay = 500; // ms
	
	ConsoleFileGutter(Config c){
		this.config = c;
		this.o = this.config.getScreen();
		this.o.debug(this.toString(),this.classDebugLevel);
	}
	
	protected void doit(){
		Vector fv = this.config.getMD5Files();
        Enumeration fe = fv.elements();
		long totalSize = 0;
		long startTime = System.currentTimeMillis();
		
		if(!this.config.isGnuCompat()){
			if(this.config.beVerbose()){
				this.o.println("we have to hash "+fv.size()+" files.");
			}
		}
		
		int totalnum = 0;
		int errornum = 0;
		while(fe.hasMoreElements()){
			HashFile f = (HashFile)fe.nextElement();
			this.o.debug("file "+f.getAbsName(),this.classDebugLevel);
			if(f.getErrorCode()>0){
				this.o.error(f.getErrorMsg());
				continue;
			}
			totalnum++;
			if(this.config.beVerbose() && !this.config.isGnuCompat()){
				totalSize += f.getFileSize();
			}
            this.o.debug("starting file "+f.getFileName(),20);
			Thread ft = new Thread(f);
			ft.start();
			
			float oldpercent = f.getDonePercent();
			if(this.config.isConsoleVersion()){
				if(!this.config.isGnuCompat()){
                    this.o.print(f.getName());
    				this.o.print(" ");
    				this.o.print(oldpercent+"%");
    				this.o.flush();
				}
			}
			while(f.getCalcStatus()<2){
				try{
					long sleep = ConsoleFileGutter.updateDisplay;
					if(sleep > f.getEstimatedDoneTime()){
						sleep = f.getEstimatedDoneTime();
					}
					Thread.sleep(sleep);
					if(f.getCalcStatus()==2) break;
					
					if(this.config.isConsoleVersion()){
						if(!this.config.isGnuCompat()){
						    float percent = f.getDonePercent();
				            this.config.delscreen(oldpercent+"%");
				            this.o.print(percent+"%");
				            this.o.flush();
				            oldpercent=percent;
						}
					}
				}catch(InterruptedException e){
					this.o.error(this.toString()+" :: "+e.getMessage());
				}
			}
            this.o.debug("done file "+f.getFileName(),20);
			
			if(!this.config.isGnuCompat()){
				this.config.delscreen(oldpercent+"%");
				if(this.config.beVerbose()){
					//this.o.print(": read "+f.getFileSize()+" bytes in "+f.getProcTime()+" ms");
					this.o.print(String.format(": read %d bytes in %d ms (%s)",
						f.getFileSize(), f.getProcTime(), f.getProcSpeed()));
					if(this.config.getCheckFile().equals("")){
						this.o.println();
					}else{
						this.o.print(" : ");
					}
				}
			}
			
			if(this.config.getCheckFile().equals("")){
				if(!this.config.isGnuCompat()){
					if(!this.config.beVerbose()){
						this.config.delscreen(" ");
						this.config.delscreen(f.getName());
					}
				}
				this.o.debug("CalcStatus:"+f.getCalcStatus(),1);
				//if(f.getCalcStatus()==2){
				if(f.getErrorCode()==0){
				    //this.o.println(f.getHash()+" "+f.getName());
				    this.o.println(f.getHashLine());
				    if(!this.config.getSaveMDFile().equals("")){
				        /*
				        this.config.writeToSaveMD5File(f.getHash()
				                +this.config.getSaveFileHashSeparator()
				                +f.getName()+System.getProperty("line.separator"));
				        */
				        this.config.writeToSaveMD5File(f.getHashLine());
				    }
				}else{
				    this.o.println(f.getHashResult()+"  "+f.getAbsName());
				}
			}else{
				if(f.getCheckResult().equals("ERROR")){
					this.config.setProgExitCode(1);
					errornum++;
				}
				if(!this.config.isGnuCompat()){
					this.o.println(f.getCheckResult()+"    ");
				}else{
				    if(this.config.beVerbose() || f.getCheckResult().equals("ERROR")){
						this.o.println(f.getName()+" "+f.getCheckResult());
					}
				}
			}
		}
		
		if(!this.config.isGnuCompat()&&this.config.beVerbose()){
			long procTime = System.currentTimeMillis()-startTime;
			double procTime_Sec = procTime / 1000.0;
			if (totalSize < 1048576L)
				this.o.println(String.format("hashed %d files with total %d bytes in %d ms",
					fv.size(), totalSize, procTime));
			else
				this.o.println(String.format("hashed %d files with total %d bytes (%s) in %d ms (%sB/s)\n",
					fv.size(), totalSize,
					CoreHashFile.getHumanReadableFileSize(totalSize),
					procTime,
					CoreHashFile.getHumanReadableNumber(totalSize / procTime_Sec)));
		}
		if(errornum>0){
			this.o.println(this.config.getHashSumName()+": WARNING: "+errornum+" of "+totalnum+" computed checksums did NOT match");
		}
	}
}
