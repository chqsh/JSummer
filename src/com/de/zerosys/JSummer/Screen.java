/**
 * $Id: Screen.java,v 1.11 2006-03-24 10:47:56 io Exp $
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

/**
 * TODO: 
 *  - add capability to append to file
 *  - multiple instances loging to one file?
 *  - keep stderr and stdout in sysnc
 */

package com.de.zerosys.JSummer;


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class Screen {

    private PrintWriter _out;
    private PrintWriter _err;

    private boolean writeToFile = false;
    private FileWriter outFile;
    private String outFileString;
    
    private byte debugLevel = -1;

    private final String sysEOL = System.getProperty("line.separator");
	
	// TODO: make this configurable !
	private final boolean printTimestamps = false;

    /*
     * log writing to file
     */
    boolean logFileWrites = false;


    Screen(){
        _init();
    }

    Screen( int _level ){
        setDebugLevel(_level);
        _init();
        this.debug("init " + this.toString() + " w/ debugLevel " + this.debugLevel, 2);
    }

    private void _init(){
    	this._out = new PrintWriter( System.out, true );
    	this._err = new PrintWriter( System.err, true );
    }

    public void setDebugLevel( int _level ){
        if ( _level > 127 ) _level = 127;
        this.debugLevel = (byte) _level;
    }

    public int getDebugLevel(){
        return this.debugLevel;
    }

    public void incrementDebugLevel(){
        this.setDebugLevel(this.debugLevel+1);
    }

    public boolean enableFile( String _file ){
        try {
        	this.outFile = new FileWriter( _file );
        } catch ( IOException e ){
            this.fatalError( "unable to open file '" + _file + "'" );
            return false;
        }
        this.debug("logging to file '"+_file+"'",1);
        this.outFileString = _file;
        this.writeToFile = true;
        return true;
    }

    protected String getOutFile(){
        if ( this.writeToFile ){
            return this.outFileString;
        }
        return "";
    }

    private void toFile( String _s ){
        if (!this.writeToFile){
            return;
        }
        if ( _s.equals(".") ){
            this.logFileWrites = false;
        }
        if (this.logFileWrites){
            this.debug("log to file ... '" + _s + "'", 15);
        }
        try {
        	this.outFile.write( _s );
        	this.outFile.flush();
            if (this.logFileWrites){
                this.debug("writing done", 15);
            }
        } catch ( IOException e ){
            this.fatalError( "error writing to file !!!");
        }
    }

    public void debug( String _msg, int _level ){
        if ( _level > this.debugLevel ){
            return;
        }
        String zerofill = "";
        if ( _level < 100 ) zerofill += "0";
        if ( _level < 10 ) zerofill += "0";
		if(this.printTimestamps){
			errln( "[ " + zerofill + _level + " :: " + System.currentTimeMillis() + " | " + _msg + " ]" );
		}else{
			errln( "[ " + zerofill + _level + " | " + _msg + " ]" );
		}
    }
    public void debug( String _msg ){
        this.debug( _msg, 0 );
    }


    public void error( String _msg ){
        String dl = dashLine(_msg);
        String msg = " "+dl+this.sysEOL+" | "+_msg+" |"+this.sysEOL+" "+dl+this.sysEOL;
        err(msg);
    }

    public void fatalError( String _msg ){
        this.error( _msg );
        System.exit(1);
    }

    private String dashLine( String _s, int plus ){
        String _dashLine = "";
        for( int i = 0; i < _s.length() + plus; i++ ) {
            _dashLine += "-";
        }
        return _dashLine;
    }
    private String dashLine( String _s ){
        return dashLine(_s, 4);
    }

    /**
     * stdout
     * @param _s
     */
    public void print( String _s ){
        out(_s);
    }
    public void print( String _s, boolean _flush ){
        out(_s);
        if ( _flush ) this._out.flush();
    }
    public void println( String _s ){
    	this.outln(_s);
        this._out.flush();
    }
    public void println(){
    	this.outln("");
        this._out.flush();
    }

    public void flush(){
    	this._out.flush();
    	this._err.flush();
    }

    /**
     * internal wrapper to stdout and stderr
     * @param _s
     */
    private void err( String _s ){
    	this._err.print( _s );
        this.toFile(_s);
        this._err.flush();
    }
    private void errln( String _s ){
    	this._err.println( _s );
        this.toFile(_s+this.sysEOL);
    }

    private void out( String _s ){
    	this._out.print(_s);
        this.toFile(_s);
    }
    private void outln( String _s ){
    	this._out.println(_s);
        this.toFile(_s + this.sysEOL);
    }


    public void close(){
        this.debug("closing " + this.toString(), 9);

        if ( this.writeToFile ) {
            this.debug("closing logfile ...", 9);
	        try{
	        	this.outFile.flush();
	        	this.outFile.close();
	            this.debug("done logfile", 9);
	        } catch ( IOException e ){
	            this.error("ERROR CLOSING FILE !!!");
	        }
        }
    }


    protected void finalize() throws Throwable {
        this.close();
    }
}
