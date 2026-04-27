/*
 * Package: com.de.zerosys.JSummer
 * Project: JSummer
 * Created on 17.07.2005
 * Author:  Klaus Zerwes
 * (c) 2005 - 2006 zero-sys.net
 * 
 * $Id: XLaunchStarter.java,v 1.10 2008-02-11 15:08:48 zerwes Exp $
 * 
 * This package is free software.
 * This software is licensed under the terms of the 
 * GNU General Public License (GPL), version 2.0 or later, 
 * as published by the Free Software Foundation. 
 * See http://www.gnu.org/copyleft/gpl.txt for the terms of the 
 * latest version of the GNU General Public License.
 */
package com.de.zerosys.JSummer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


class XLaunchStarter {
    
    protected final int classDebugLevel = 5;
    private final Screen o = new Screen(CoreConfig.DEFAULTDEBUG);
    private final XLauncherConfig config = new XLauncherConfig(this.o);
    
    protected final static String portFileString = 
        System.getProperty("user.home")
        +System.getProperty("file.separator")
        +".JSummer"
        +System.getProperty("file.separator")
        +"launcher.dat";
    
    private final static int maxReadTrys = 30;
    private final static int sleepReadTry = 250;
    
    private final File portFile = new File( XLaunchStarter.portFileString);
    
    private BufferedReader fr = null;
    
    private String execCmdServer = "JSummerXS";
    private String execCmdNormal = "JSummerX";
    
    private boolean startX = false;
    private boolean startServer = false;

    protected XLaunchStarter(String[] args) {
        this.o.debug(this.toString()+" started "+System.currentTimeMillis(),this.classDebugLevel);
        this.o.debug(this.toString()+" : args-count:"+args.length,this.classDebugLevel);
        if (this.config.configByArgs(args)){
            this.o.println(this.config.getHelp());
            this.o.println();
            this.o.error(this.config.getConfigErrorMsg());
        }
        
        if(this.config.isWindows()){
        	this.execCmdNormal = this.execCmdNormal+".exe";
        	this.execCmdServer = this.execCmdServer+".exe";
        	/*if(System.getProperty("java.vm.name").equals("GNU libgcj")){
	        	this.execCmdNormal = this.execCmdNormal+".exe";
	        	this.execCmdServer = this.execCmdServer+".exe";
        	}else{
	        	this.execCmdNormal = this.execCmdNormal+".bat";
	        	this.execCmdServer = this.execCmdServer+".bat";
        	}*/
        }
        
        // if checkfile: simply pass the stuff to JSummerX
        if(!this.config.getCheckFile().equals("")){
            try{
                String[] cmdarr = new String[args.length+1];
                cmdarr[0] = this.execCmdNormal;
                for(int i = 0; i<args.length; i++){
                    cmdarr[i+1] = args[i];
                }
                Runtime.getRuntime().exec(cmdarr);
                this.o.debug(this.toString()+" : launched "+cmdarr[0]+" for checkfile argscount:"+cmdarr.length,this.classDebugLevel);
                System.exit(0);
            }catch(Exception e){
                this.o.error(e.getMessage());
                this.o.error(e.toString());
                e.printStackTrace();
                System.exit(1);
            }
        }
        
        // else :: try to start server
        this.o.debug(this.toString()+": test server-role :"+System.currentTimeMillis(),this.classDebugLevel);
        if(!this.createDir()){
            this.startX = true;
            this.startServer = false;
        }else{
            if(this.portFileExists() && this.portFile.canRead()){
                // read port and try to connect
                this.o.debug("try to read port from portFile ...",this.classDebugLevel);
                int port = 0;
                try{
                    this.fr = new BufferedReader(new FileReader(this.portFile));
                    for (int readtry=1;readtry<XLaunchStarter.maxReadTrys;readtry++){
                        this.o.debug("read try nr: "+readtry,this.classDebugLevel);
                        if(readtry>1){
                            this.o.debug("not the first readtry :: reseting reader",this.classDebugLevel);
                            this.resetReadstream();
                        }
                        port = this.getPortfromFile();
                        if(port < 0){
                            this.startX = true;
                            break;
                        }
                        if(port > 0){
                            break;
                        }
                        if(port == 0){
                            try{
                            	Thread.yield();
                                Thread.sleep(XLaunchStarter.sleepReadTry);
                            }catch(InterruptedException e){
                                this.o.error(this.toString()+" : "+e.getMessage());
                            }
                        }
                    }
                    this.o.debug("done reading : got port "+port,this.classDebugLevel);
                    this.closeReader();
                    if(port < CoreConfig.serverPortMin
                    		|| port > CoreConfig.serverPortMax){
                        this.o.debug("wrong port:"+port+" !!!!!!!",this.classDebugLevel);
                        // takeover server role !
                        //this.startX = true;
                        //this.takeOverServer();
                        this.startX = true;
                        this.startServer = true;
                    }else{
                        // try to connect
                        try{
                            InetAddress loopback = InetAddress.getByName("127.0.0.1");
                            try{
                                Socket s = new Socket(loopback,port);
                                XLauncherServConn sconn = new XLauncherServConn(this.o,s);
                                if(!sconn.sendFiles(this.config.hashFiles)){
//                                  // TODO: takeover server role? !?!?!?!?!?!?!?!
                                    this.startX = true;
                                    this.startServer = false;
                                }
                                sconn.closeConn();
                            }catch(IOException e){
                                this.o.error(this.toString()+" : "+e.getMessage());
                                this.startX = true;
                            }
                        }catch(UnknownHostException e){
                            this.o.error(this.toString()+" : "+e.getMessage());
                            this.startX = true;
                        }
                    }
                }catch(FileNotFoundException e){
                    this.o.error(this.toString()+"::"+e.getMessage());
                    this.startX = true;
                }
            }else{
                // try to create file and start server
                this.o.debug("try to create file and start server ...",this.classDebugLevel);
                //this.tryStartServer();
                this.startX = true;
                this.startServer = true;
            }
        }
        
        if(this.startX){
        	this.o.debug("starting X ...",this.classDebugLevel);
        	String[] cmdarr = new String[args.length+1];
        	cmdarr[0] = this.execCmdNormal;
            if(this.startServer){
            	if(this.createFile()){
            		cmdarr[0] = this.execCmdServer;
                }
            }
            try{
                for(int i = 0; i<args.length; i++){
                    cmdarr[i+1] = args[i];
                }
                this.o.debug(this.toString()+" : trying to launch "+cmdarr[0]+" : argscount:"+cmdarr.length,this.classDebugLevel);
                Runtime.getRuntime().exec(cmdarr);
                this.o.debug(this.toString()+" : launched "+cmdarr[0]+" : argscount:"+cmdarr.length,this.classDebugLevel);
                System.exit(0);
            }catch(Exception e){
                this.o.error(e.getMessage());
                this.o.error(e.toString());
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
    
    private boolean portFileExists(){
        return this.portFile.exists();
    }
    
    private boolean createDir(){
        File pDir = this.portFile.getParentFile();
        if(pDir.exists()){
            return true;
        }
        final boolean ret = pDir.mkdirs();
        this.o.debug(pDir.getAbsolutePath()+" created:"+ret,this.classDebugLevel);
        return ret;
    }
    
    private boolean resetReadstream(){
        try{
            this.o.debug("reset readerstream ...",this.classDebugLevel);
            this.fr.reset();
        }catch(IOException e){
            this.o.error(this.toString()+" :: "+e.getMessage());
            return false;
        }
        return true;
    }
    
    /**
     * sets port from file
     * @return int
     * -1 on error
     * 0 if not set
     * port if all OK
     */
    private int getPortfromFile(){
        if(this.fr == null) return -1;
        this.o.debug("getPortfromFile() ...",this.classDebugLevel);
        if(this.fr.markSupported()){
            try{
                this.fr.mark(500);
            }catch(IOException e){
                this.o.error(this.toString()+" :: "+e.getMessage());
            }
        }
        int c = 0;
        String line = "";
        this.o.debug("start reading ...",this.classDebugLevel);
        while (c != -1){
            try{
                c = this.fr.read();
            }catch(IOException e){
                this.o.error(this.toString()+" :: "+e.getMessage());
                this.closeReader();
                return -1;
            }
            if(c == 10 || c == 13){
                //this.o.error("NEWLINE in "+this.portFile.getAbsolutePath());
                break;
            }
            line += (char)c;
        }
        
        if(line.length() > 0){
            this.o.debug("parsing line '"+line+"'",this.classDebugLevel);
            final String initString = CoreConfig.serverInitString+":";
            if(line.startsWith(initString)){
                if(line.length()+1 > initString.length()){
                    String port = line.substring(initString.length()+1);
                    this.o.debug("port as string:"+port,this.classDebugLevel);
                    if(port.length()>0){
                        try{
                            Integer p = new Integer(port);
                            return p.intValue();
                        }catch(NumberFormatException e){
                            this.o.error(this.toString()+":: port error");
                            return -1;
                        }
                    }
                }
            }
        }
        return 0;
    }
    
    private void closeReader(){
        if(this.fr != null){
            this.o.debug(this.toString()+"::closeReader()",this.classDebugLevel);
            try{
                this.fr.close();
                this.fr = null;
            }catch(IOException e){
                this.o.error(this.toString()+"::"+e.getMessage());
            }
        }
    }
    
    private boolean createFile(){
        boolean ret = false;
        try{
            ret = this.portFile.createNewFile();
            /*if(ret){
                this.fw = new FileWriter(this.portFile);
            }*/
        }catch(IOException e){
            this.o.error(this.toString()+"::"+e.getMessage());
            ret = false;
        }
        this.o.debug(this.toString()+"::createFile() returning "+ret,this.classDebugLevel);
        return ret;
    }
}
