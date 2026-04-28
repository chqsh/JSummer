/**
 * $Id: CheckMDFile.java,v 1.18 2008-03-05 11:56:16 zerwes Exp $
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.nio.charset.Charset;

class CheckMDFile {

	private Config config;
	private Screen o;
	
	private String parentPath = "";
	
	private File f;
	
	private int errorCode = 0;
	private String errorMsg = "";
	
	private final int maxErrorLines = 5;
	
	private int classDebugLevel = 1;
	
	CheckMDFile(Config c){
		this.config = c;
		this.o = this.config.getScreen();
		
		this.o.debug(this.toString()+" :: "+this.config.getCheckFile(),this.classDebugLevel);
		
		this.f = new File(this.config.getCheckFile());
		
		if(!this.f.exists()){
			this.errorCode = 1;
			this.errorMsg = "unable to find file "+this.f.getAbsolutePath();
			return;
		}
		if(!this.f.canRead()){
			this.errorCode = 2;
			this.errorMsg = "unable to open file "+this.f.getAbsolutePath();
			return;
		}
		this.parentPath = this.f.getAbsoluteFile().getParentFile().getAbsolutePath();
		if(!this.parentPath.endsWith(System.getProperty("file.separator"))){
            this.parentPath += System.getProperty("file.separator");
        }
		this.o.debug("parent path in "+this.toString()+" : "+this.parentPath,this.classDebugLevel);
		this.config.setRelativePath(this.parentPath); //???
		this.parseFile();
	}
	
	
    protected String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2){
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    
    protected class BOM {
        /*
         * nBytes < 0, it is error code.
         * nBytes = 0, the file is non-BOM (non-ByteOrderMarker).
         * nBytes = 1, undefined.
         * nBytes 2 or 3, the file eixsts BOM.
         * nBytes > 3, undefined.
         */
        public int    nBytes;
        public String encoding;
        
        public BOM() {
            this.nBytes = 0; // non-BOM
            this.encoding = System.getProperty("file.encoding");
                /*
                 * This property is used for the default encoding in Java,
                 * all readers and writers would default to use this property.
                 * Code such as System.setProperty("file.encoding", "UTF-8")
                 * can be used to change this property. However, the default
                 * encoding can not be changed dynamically even this property
                 * can be changed.
                 * "java -Dfile.encoding=UTF-8" can be used to set the
                 * default encoding when starting a JVM.
                 */;
        }
        public BOM(int nBytes, String encoding) {
            this.nBytes = nBytes;
            this.encoding = encoding;
        }
        
        public boolean hasError() {
            return (nBytes < 0);
        }
        public int getErrorCode() {
            return (nBytes < 0) ? nBytes : 0;
        }
        public String getErrorMsg() {
            return (nBytes < 0) ? this.encoding : "";
        }
        
        public boolean exists() {
            return (nBytes > 0);
        }
    }
    
    protected BOM detectBOM(File f) {
        try {
            byte[] bom = new byte[3];
            InputStream is = new FileInputStream(f);
            int result = is.read(bom);
            is.close();
            
            if (result < 0) {
                return new BOM(result, String.format("Read file %s is failure! (%d)\n",
                    f.getAbsolutePath(), result));
            }
            if (result < 2) {
                return new BOM();
            }
            
            String content = bytesToHex(bom);
            this.o.debug(String.format("detectBOM(%s): The first %d bytes is '%S'",
                f.getName(), result, content), this.classDebugLevel);
            if ("EFBBBF".equalsIgnoreCase(content)) {
                return new BOM(3, "UTF-8");
            }
            content = content.substring(0, 4);
            if ("FFFE".equalsIgnoreCase(content)) {
                return new BOM(2, "UTF-16LE");
            }
            if ("FEFF".equalsIgnoreCase(content)) {
                return new BOM(2, "UTF-16BE");
            }
            return new BOM();
        } catch (IOException e) {
            String errorMsg = e.getMessage();
            this.o.error(errorMsg);
            return new BOM(-1, errorMsg);
        }
    }
    
    protected String detectEncodingByPythonCoding(File f) {
        String encoding = "";
        
        try {
            FileReader fr = new FileReader(this.f);
            BufferedReader br = new BufferedReader(fr);
            
            int lineNr = 0;
            while(lineNr < 3) {
                lineNr++;
                try{
                    String l = br.readLine();
                    this.o.debug("detectEncodingByPythonCoding(): read line:'"+l+"'",this.classDebugLevel);
                    if(l==null){
                        this.o.debug(this.toString()+": EOF reached!",this.classDebugLevel);
                        break;
                    }
                    l = l.trim();
                    if(l.equals("")){
                        // Empty line is ignored.
                        continue;
                    }
                    char c = l.charAt(0);
                    if (c == '#' || c == ';') {
                        // comment line
                        String coding = l.replaceAll("^.*\\bcoding[:=]\\s*([-\\w.]+)", "$1");
                        if (coding.charAt(0) == c)
                            continue;
                        encoding = coding;
                        break;
                    } else {
                        break;
                    }
                } catch(IOException e) {
                    encoding = "";
                    break;
                }
            } // end of while
            
            try {
                br.close();
                fr.close();
            } catch (IOException e) {
            }
        } catch (FileNotFoundException e) {
        }
        return encoding;
    }
    
    private String convertEncoding(String src, String srcEncoding, String dstEncoding) {
        if (srcEncoding == null || srcEncoding.isEmpty()
         || dstEncoding == null || dstEncoding.isEmpty())
            return null;
        
        try {
            byte[] by = src.getBytes(srcEncoding); // Convert to raw bytes
            return new String(by, dstEncoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
    
    private int indexOfDot(String filenameWithExt) {
        if (filenameWithExt == null || filenameWithExt.isEmpty())
            return -1;
        int idx = filenameWithExt.lastIndexOf('.');
        if (idx < 0)
            return -1;
        return idx;
    }
    
    private String getFilename(String filenameWithExt) {
        int idx = indexOfDot(filenameWithExt);
        if (idx <= 0)
            return new String(filenameWithExt); /* idx = 0, when filenameWithExt is ".someone". */
        return filenameWithExt.substring(0, idx); /* drop '.' */
    }
    
    protected boolean parseFile(){
        this.o.debug(this.toString()+"::parseFile() ...",this.classDebugLevel);
        
        BOM bom = detectBOM(this.f);
        if (bom.hasError()) {
            this.errorCode = 3;
            this.errorMsg = bom.getErrorMsg();
            this.o.error(this.errorMsg);
            return false;
        }
        if (this.f.length() < 8) {
            this.errorCode = 3;
            this.errorMsg = "File " + this.f.getAbsolutePath() + "is too small! ABORTING";
            this.o.error(this.errorMsg);
            return false;
        }
        
        boolean forcedEncoding = true;
        String detectedEncoding = bom.encoding;
        if (!bom.exists()) {
            String coding = this.config.getCheckMDFileEncoding();
            if (!coding.isEmpty()) {
                detectedEncoding = coding;
            } else {
                coding = detectEncodingByPythonCoding(this.f);
                if (!(coding == null || coding.isEmpty()))
                    detectedEncoding = coding;
                else
                    forcedEncoding = false;
            }
        }
        
        FileInputStream fis;
        try {
            fis = new FileInputStream(this.f);
        } catch (FileNotFoundException e) {
            this.errorCode = 1;
            this.errorMsg = e.getMessage();
            return false;
        }
        
        int    l_errorCode = 0;
        String l_errorMsg = "";
        
        int nRetry = 1;
        InputStreamReader fr = null;
        do {
            try {
                // Verify and correct detectedEncoding
                
                this.o.debug("detectedEncoding = " + detectedEncoding, this.classDebugLevel);
                fr = new InputStreamReader(fis, detectedEncoding);
                String coding = this.config.getCheckMDFileEncoding();
                if (!coding.isEmpty() && !detectedEncoding.equalsIgnoreCase(
                        this.config.getCheckMDFileEncoding())) {
                    this.config.setCheckMDFileEncoding(detectedEncoding);
                }
            } catch (UnsupportedEncodingException e) {
                if (nRetry > 0) {
                    l_errorCode = 2;
                    l_errorMsg = "UnsupportedEncodingException '" + e.getMessage() + "'";
                    
                    // rollback
                    if ("UTF-8".equalsIgnoreCase(detectedEncoding))
                        detectedEncoding = System.getProperty("file.encoding");
                    else
                        detectedEncoding = "UTF-8";
                    --nRetry;
                } else {
                    if (l_errorCode == 0) {
                        this.errorCode = 2;
                        this.errorMsg = e.getMessage();
                    } else {
                        this.errorCode = l_errorCode;
                        this.errorMsg = l_errorMsg;
                    }
                    try {
                        fis.close();
                    } catch(IOException ex) {}
                    return false;
                }
            }
        } while (fr == null);
        
        if ("ISO-8859-1".equalsIgnoreCase(detectedEncoding))
            forcedEncoding = true;
        if ("ISO8859_1".equalsIgnoreCase(detectedEncoding))
            forcedEncoding = true;
        
        String readEncoding = (forcedEncoding) ? detectedEncoding : "ISO-8859-1";
        if (!forcedEncoding) {
            // reopen
            try {
                this.o.debug(String.format("Reopen the file '%s' using '%s' instead of '%s'.",
                    this.f, readEncoding, detectedEncoding), this.classDebugLevel);
                fr = new InputStreamReader(fis, readEncoding);
                this.o.debug(String.format("Now, the fr.getEncoding() return '%s'.",
                    fr.getEncoding()), this.classDebugLevel);
            } catch(IOException e) {
                this.errorCode  = 4;
                this.errorMsg = e.getMessage();
                this.o.error("IOException:" + this.errorMsg);
                try {
                    fis.close();
                } catch (IOException ex) {}
                return false;
            }
        }
        
        String f_filename = getFilename(this.f.getName());
        
        l_errorCode = 0;
        l_errorMsg = "";
        
        BufferedReader br = new BufferedReader(fr);
        try{
            
            int lineNr = 0;
            int lineErrors = 0;
            if (bom.exists()) {
                br.skip(1); // skip one char (One UTF-8 char is 3 bytes.)
            }
            while(true){
                lineNr++;
                String l = br.readLine();
                if(l==null){
                    this.o.debug(this.toString()+": EOF reached!",this.classDebugLevel);
                    break;
                }
                this.o.debug("read line:'"+l+"'",this.classDebugLevel);
                
                l = l.trim();
                if(l.isEmpty()){
                    this.o.debug("empty line ignored",this.classDebugLevel);
                    continue;
                }
                char c = l.charAt(0);
                if (c == '#' || c == ';') {
                    this.o.debug("comment line ignored",this.classDebugLevel);
                    continue;
                }
                final String sfvMatchRule = "^(.*) +([0-9A-Fa-f]+)$";
                if (l.matches(sfvMatchRule)) {
					l = l.replaceAll(sfvMatchRule,"$2 *$1");
                }
                if (l.matches("^[0-9a-fA-F]{8,}$")) {
                    StringBuilder s = new StringBuilder(l);
                    s.append(" *");
                    s.append(f_filename);  // Notice: f_filename is UTF-8. l is ISO-8859-1.
                    l = s.toString();
                    forcedEncoding = true; // Urgly correct. It maybe conflict with detectedEncoding
                }
                if(this.isMalformedLine(l)){
                    lineErrors++;
                    this.o.error("file '"+this.f+"' :: malformed line nr "+lineNr);
                    if(!this.errorMsg.equals(""))
                        this.o.error(this.errorMsg);
                    if(lineErrors >= this.maxErrorLines){
                        l_errorCode = 3;
                        l_errorMsg = "maximum number of errors reached! ABORTING";
                        break; // jump out while
                    }
                    continue;
                }
                String file = l.substring(l.indexOf(" ")+1,l.length());
                file = file.trim();
                if(file.startsWith("*")) file = file.substring(1);
                String md = l.substring(0,l.indexOf(" "));
                /* "\\A\\p{ASCII}*\\z" only match when all char <= 127 */
                if ( (!forcedEncoding) && !file.matches("\\A\\p{ASCII}*\\z") ) {
                    String fileUTF8 = convertEncoding(file, readEncoding, "UTF-8");
                    if (fileUTF8 != null) {
                        boolean bExist = this.config.checkHashFileExist(fileUTF8, this.parentPath);
                        if (bExist) {
                            file = fileUTF8;
                        } else if (!detectedEncoding.matches("^(?i)UTF[-_]{0,1}8$")) {
                            String fileNative = convertEncoding(file, readEncoding, detectedEncoding);
                            if (fileNative != null) {
                                file = fileNative;
                            }
                        }
                    }
                }
                this.o.debug(String.format("file:'%s'; hash:'%s'; parentPath:'%s'",
                    file, md, this.parentPath), this.classDebugLevel);
                // this.o.debug(this.toString()+" : config is "+this.config.toString(),this.classDebugLevel);
                /*
                {
                    byte[] by1 = file.getBytes(detectedEncoding); // Convert raw bytes
                    String title1 = "\t" + detectedEncoding;
                    this.o.debug(title1 + ": '"+bytesToHex(by1)+"'",this.classDebugLevel);
                }
                */
                this.config.addHashFileCheck(file,md,this.parentPath);
            } /* end of while */
        } catch(IOException e) {
            l_errorCode = 4;
            l_errorMsg = "IOException '" + e.getMessage() + "'";
        }
        try {
            br.close();
            fr.close();
            fis.close();
        } catch(IOException e) {
            /* Ignore */
            this.o.error(e.getMessage());
        }
        if (l_errorCode != 0) {
            this.errorCode  = l_errorCode;
            this.errorMsg = l_errorMsg;
            this.o.error(l_errorMsg);
        }
        return (l_errorCode == 0) ? true:false;
    }
	
    /**
     * @param line
     * @return true if malformed else false
     */
    private boolean isMalformedLine(String line){
        this.o.debug(this.toString()+"::isMalformedLine('"+line+"') ???",this.classDebugLevel);
        this.o.debug("this.config.getHashLenght():"+this.config.getHashLenght(),this.classDebugLevel);
        int firstSpaceAt = line.indexOf(" ");
        //this.o.debug(this.toString()+" firstSpaceAt="+firstSpaceAt,this.classDebugLevel);
        if( firstSpaceAt != this.config.getHashLenght()){
            /*
            Enumeration hie = this.config.containerHashSumInfo.elements();
            HashSumInfo hsi = null;
            while(hie.hasMoreElements()) {
                hsi = (HashSumInfo)hie.nextElement();
                if(hsi.equals(this.config.actualHashSumInfo)) {
                    continue;
                }
                if(firstSpaceAt == hsi.){
                    
                }
            }
            */
            /*
            if(firstSpaceAt==HashSum.HASHLENGHT_MD5){
                this.errorMsg = "THIS LOOKS LIKE A md5-HASH";
            }else if(firstSpaceAt==HashSum.HASHLENGHT_SHA1){
                this.errorMsg = "THIS LOOKS LIKE A sha-160-HASH";
            }else if(firstSpaceAt==HashSum.HASHLENGHT_SHA2){
                this.errorMsg = "THIS LOOKS LIKE A sha-256-HASH";
            }
            */
            this.o.debug(this.toString()+"::isMalformedLine() : first space located at "
                    +firstSpaceAt+" : expected at "+this.config.getHashLenght(),this.classDebugLevel);
            return true;
        }
        char secondSep = line.charAt(firstSpaceAt+1);
        if( !( secondSep == ' ' || secondSep == this.config.getBinMarker() ) ){
            this.o.debug(this.toString()+"::isMalformedLine() : second separator malformed : '"+secondSep+"'",this.classDebugLevel);
            return true;
        }
        
        /* 
         * gcj is unable to find java::lang::String::matches(java::lang::String*)
         * and java-regex are not supported
         * maybe we should use a extra regex-package like gnu.regex or jackarte-regex?
         */
        /*
        String hash = line.substring(0,this.config.getHashLenght());
        this.o.debug("checking HASH part :: '"+hash+"'",this.classDebugLevel);
        if(!hash.matches("[a-fA-F0-9]{"+this.config.getHashLenght()+"}")){
            this.o.debug(this.toString()+"::isMalformedLine() : malformed hash part !!!",this.classDebugLevel);
            return true;
        }
        */
        // KIND OF UGGLY TEST BUT IT WORKS WITHOUT A REGEX-PACKAGE
        String hash = line.substring(0,this.config.getHashLenght()).toLowerCase();
        this.o.debug("checking HASH part :: '"+hash+"'",this.classDebugLevel);
        for(int i=0;i<hash.length();i++){
            String x = hash.substring(i,i+1);
            this.o.debug("checking char "+x,20);
            try{
                int xi = Integer.parseInt(x,16);
                this.o.debug(x+" represents "+xi,20);
            }catch(NumberFormatException e){
                this.o.debug(e.getMessage(),20);
                return true;
            }
        }
        
        this.o.debug(this.toString()+"::isMalformedLine() line OK",this.classDebugLevel);
        return false;
    }
	
	protected int getErrorCode() {
		return this.errorCode;
	}

	protected String getErrorMsg() {
		return this.errorMsg;
	}

	protected String getParentPath() {
		return this.parentPath;
	}
}
