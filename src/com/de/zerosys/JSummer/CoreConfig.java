/**
 * $Id: CoreConfig.java,v 1.23 2008-03-06 11:01:15 zerwes Exp $
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

import gnu.crypto.Registry;

import java.io.File;
import java.util.Enumeration;
import java.util.Vector;


class CoreConfig {
    
    protected static int DEFAULTDEBUG = 0;
    
    public final static String VERSION = "0.1.0.0";
    protected final static String COPYLEFT = "(c) 2004 - 2008 Klaus Zerwes zero-sys.net";
    protected final static String GNUCRYPTOCOPYLEFT = "This software makes use of parts from the gnu.crypto project;\nsee: http://www.gnu.org/software/gnu-crypto for more details.";
    protected final static String SWTCOPYLEFT = "The GUI of this software uses SWT from http://www.eclipse.org.";
    protected final static String GPL = "This software is licensed under the terms of the GNU General Public License (GPL),\nversion 2.0 or later, as published by the Free Software Foundation.\nSee http://www.gnu.org/copyleft/gpl.txt\nfor the terms of the latest version of the GNU General Public License.";
    protected static String ProgName = "JSummer";
    

    protected final static int maxHashStrLen = 128;
    protected final static String defaultHashFileName = "checkfile"; 
    
    protected Screen o;
    private boolean isWindows = false;
    
    
    protected boolean binModus = false;
    protected boolean useRelPath = true;
    protected boolean useSysIndepPathSeparator = true;
    protected boolean upperCaseHash = false;
    protected boolean warnEmptyDirs = false;
    
    protected boolean beVerbose = false;
    protected boolean gnuCompat = false;
    protected boolean isConsoleVersion = false;
    
    protected boolean configError = false;
    protected String configErrorMsg = "";
    protected int progExitCode = 0;
    
    protected Vector hashFiles = new Vector();
    
    private String saveMDFile = "";
    protected File fileSaveMDFile = null;
    
    private String checkMDFile = "";
    
    private String relativePath = "";
    
    private boolean stopMe = false; // required for recursedir called as thread from XAddDir
    
    protected int classDebugLevel = 1;
    
    //  used for server-stuff
    protected final static int serverPortMin = 49152;
    protected final static int serverPortMax = 65535;
    protected final static String serverInitString = CoreConfig.VERSION+"-com.de.zerosys.JSummer.XLaunchXServer";
    
    protected Vector containerHashSumInfo = new Vector();
    protected HashSumInfo actualHashSumInfo;
    
    protected final static String defaultHashAlg = "md5"; 
    
    CoreConfig(Screen o){
        this._init(o);
    }
    CoreConfig(Screen o,String progName){
        this._init(o);
        CoreConfig.ProgName += progName;
    }
    
    protected void _init(Screen s){
        this.o = s;
        this.o.debug("config init done",this.classDebugLevel);
        this.debugSpecs();
        this.debugMem();
        this.checkIfIsWindows();
        if(this.isWindows) this.binModus = true;
        this.containerHashSumInfo.add(new HashSumInfo(Registry.MD5_HASH));
        this.containerHashSumInfo.add(new HashSumInfo(Registry.MD4_HASH));
        this.containerHashSumInfo.add(new HashSumInfo(Registry.MD2_HASH));
        this.containerHashSumInfo.add(new HashSumInfo(Registry.SHA160_HASH, new String[]{"SHA","SHA1","SHA-1","SHA160"}, new String[]{"sha1", "sha160"}));
        this.containerHashSumInfo.add(new HashSumInfo(Registry.SHA256_HASH, new String[]{"SHA256","SHA2-1"}, "sha256"));
        this.containerHashSumInfo.add(new HashSumInfo(Registry.SHA384_HASH, new String[]{"SHA384","SHA2-2"}, "sha384"));
        this.containerHashSumInfo.add(new HashSumInfo(Registry.SHA512_HASH, new String[]{"SHA512","SHA2-3"}, "sha512"));
        this.containerHashSumInfo.add(new HashSumInfo(Registry.RIPEMD128_HASH, new String[]{Registry.RIPEMD_128_HASH}));
        this.containerHashSumInfo.add(new HashSumInfo(Registry.RIPEMD160_HASH, new String[]{Registry.RIPEMD_160_HASH}));
        this.containerHashSumInfo.add(new HashSumInfo(Registry.WHIRLPOOL_HASH));
        this.containerHashSumInfo.add(new HashSumInfo(Registry.TIGER_HASH));
        this.containerHashSumInfo.add(new HashSumInfo(Registry.HAVAL_HASH));
    }
    
    protected void debugSpecs(){
        if(this.o.getDebugLevel()==0){
            return;
        }
        this.o.debug("VERSION:"+CoreConfig.VERSION,1);
        this.o.debug("APP is running in dir:"+System.getProperty("user.dir"),1);
        this.o.debug("java.version: "+System.getProperty("java.version"),1);
        this.o.debug("java.vendor: "+System.getProperty("java.vendor"),1);
        this.o.debug("java.vm.version: "+System.getProperty("java.vm.version"),1);
        this.o.debug("java.vm.vendor: "+System.getProperty("java.vm.vendor"),1);
        this.o.debug("java.vm.name: "+System.getProperty("java.vm.name"),1);
        this.o.debug("os.name: "+System.getProperty("os.name"),1);
        this.o.debug("os.arch: "+System.getProperty("os.arch"),1);
        this.o.debug("os.version: "+System.getProperty("os.version"),1);
    }
    
    protected void debugMem(String mark){
        this.o.debug("debugMem: "+mark,1);
        this.debugMem();
    }
    
    protected void debugMem(){
        this.o.debug("free mem:  "+Runtime.getRuntime().freeMemory(),1);
        this.o.debug("total mem: "+Runtime.getRuntime().totalMemory(),1);
        this.o.debug("max mem:   "+Runtime.getRuntime().maxMemory(),1);
    }
    
    protected Screen getScreen(){
        return this.o;
    }
    
    
    protected void checkIfIsWindows(){
        if(System.getProperty("os.name").toLowerCase().startsWith("windows")){
            this.setIsWindows(true);
        }else{
            this.isWindows = false;
        }
        this.o.debug(this.toString()+"::checkIfIsWindows : "+this.isWindows(),this.classDebugLevel);
    }
    protected boolean isWindows(){
        return this.isWindows;
    }
    protected void setIsWindows(boolean w){
        this.isWindows = w;
    }
    
    
    protected boolean configByArgs(String[] args){
        argsloop: for ( int i=0; i<args.length; i++ ){
            this.o.debug("parsing arg '" + args[i] + "' ...",this.classDebugLevel);
            if(args[i].length()==0){
                this.o.debug("empty arg!!",this.classDebugLevel);
                continue;
            }
            if ( args[i].charAt(0) == '-') {
                this.o.debug("found dash-arg", this.classDebugLevel);
                if (args[i].length() != 2) {
                    this.configError = true;
                    this.configErrorMsg = "invalid argumen " + args[i];
                    break argsloop;
                }
                switch (args[i].charAt(1)) {
                case 'v':
                    this.beVerbose = true;
                    break;
                case 'g':
                    this.gnuCompat = true;
                    this.setRelativePath("");
                    this.setUseRelPath(false);
                    break;
                case 'b':
                    this.binModus = true;
                    break;
                case 'm':
                    if ( i == args.length-1 ) {
                        this.configErrorMsg = args[i] + " needs argument";
                        this.configError = true;
                        break argsloop;
                    }
                    i++;
                    this.setHashSum(args[i]);
                    break;
                case 'e':
                    this.warnEmptyDirs = true;
                    break;
                case 'u':
                    this.upperCaseHash = true;
                    break;
                case 's':
                    this.setUseSysIndepPathSeparator(false);
                    break;
                case 'r':
                    this.setUseRelPath(false);
                    this.setRelativePath("");
                    break;
                case 'V':
                    this.showVersion();
                    break;
                case 'h':
                case '?':
                    this.showHelp();
                    break;
                case 'f':
                    if ( i == args.length-1 ) {
                        this.configErrorMsg = args[i] + " needs argument";
                        this.configError = true;
                        break argsloop;
                    }
                    i++;
                    if(!this.setSaveMDFile(args[i])){
                        this.configError = true;
                        break argsloop;
                    }
                    break;
                case 'c':
                    if ( i == args.length-1 ) {
                        this.configErrorMsg = args[i] + " needs argument";
                        this.configError = true;
                        break argsloop;
                    }
                    i++;
                    if(this.hashFiles.size()>0){
                        this.configErrorMsg = "Syntax Error!";
                        this.configError = true;
                        break;
                    }
                    if(!this.setCheckMDFile(args[i])){
                        this.configError = true;
                        this.setCheckMDFile("");
                    }
                    break;
                case 'd':
                    if ( i == args.length-1 ) {
                        this.configErrorMsg = args[i] + " needs argument";
                        this.configError = true;
                        break argsloop;
                    }
                    this.o.debug("evaluate next arg '" + args[i+1] + "' as int ...", 6);
                    try {
                        Integer intArg = new Integer(args[i+1]);
                        this.o.debug("OK :: '" + args[i+1] + "' evaluates to " + intArg.intValue(), 6);
                        this.o.setDebugLevel(intArg.intValue());
                        this.o.debug("debugLevel set to: "+this.o.getDebugLevel());
                        this.debugSpecs();
                    } catch ( NumberFormatException e ) {
                        this.configErrorMsg = args[i+1]+" is not valid; integer expected!";
                        this.configError = true;
                        break argsloop;
                    }
                    i++;
                    break;
                default:
                    this.configErrorMsg = "undefined option '"+args[i]+"'";
                    this.configError = true;
                } // end switch
            }else{
                if (this.getCheckMDFile().equals("")){
                    this.o.debug("arg '"+args[i]+"' : assuming file", 5);
                    this.addFileDir(args[i]);
                }else{
                    this.configErrorMsg = "undefined parameter '"+args[i]+"'";
                    this.configError = true;
                }
            }
        } // end argsloop
    
        this.o.debug("this.isConsoleVersion::"+this.isConsoleVersion(),this.classDebugLevel);
        this.o.debug("this.hashFiles.size()::"+this.hashFiles.size(),this.classDebugLevel);
        this.o.debug("this.checkMDFile.equals(\"\")::"+this.checkMDFile.equals(""),this.classDebugLevel);
    
        if(this.isConsoleVersion() && this.hashFiles.size()==0 && this.checkMDFile.equals("")){
            this.configErrorMsg = "NO FILES!";
            this.configError = true;
        }
        
        if((!this.checkMDFile.equals("")) && (!this.saveMDFile.equals(""))){
            this.configErrorMsg = "options -c and -f are exclusive!";
            this.configError = true;
        }
        
        this.o.debug("done configByArgs :: returning "+this.configError,this.classDebugLevel);
        
        return this.configError;
    }
    
    protected boolean isConfigError(){
        return this.configError;
    }
    protected String getConfigErrorMsg(){
        return this.configErrorMsg;
    }
    
    protected String getSaveMDFile() {
        return this.saveMDFile;
    }
    
    protected boolean setSaveMDFile(String saveMDFile) {
        this.saveMDFile = saveMDFile;
        return true;
    }
    
    protected String getCheckFile(){
        return this.getCheckMDFile();
    }
    
    protected void showHelp(){
        this.o.println(this.getHelp());
        System.exit(0);
    }
    
    /*
     * TODO: implement -w for warnings about malformed checkfilelines
     * 
     */
    protected String getHelp(){
        String h = CoreConfig.ProgName+" "+CoreConfig.VERSION+"  "
            +CoreConfig.COPYLEFT+"\n"
            + "usage:\n"
            +" "+CoreConfig.ProgName+" -h | -V | [options] -c file | [-f tofile] file(s) | dir(s)\n"
            +"general options:\n"
            +"  -h | -? \tdisplay this help\n"
            +"  -V \t\tshow version and exit\n"
            +"  -m alg\tuse 'alg' as algorithm where \n"
            +"  \t\t'alg' may be caseinsensitive one of:\n";
        
        Enumeration hie = this.containerHashSumInfo.elements();
		HashSumInfo hsi = null;
		while(hie.hasMoreElements()) {
			hsi = (HashSumInfo)hie.nextElement();
			h += "  \t\t"+hsi.getHashAlgName().toUpperCase();
			String alt = hsi.getalternativeHashNamesAsString(" ").toUpperCase();
			if(alt.length() > 0) {
				h += " (alternatives: "+alt+")";
			}
			h += "\n";
		}
        
        h += "  -c f \t\tcheck hashsums listed in checkfile f\n"
            +"  \t\tif the checkfile ends with .md5 .sha1 .sha160 .sha256,\n"
            +"  \t\tthe coresponding hash-alg will be used.\n"
            +"  -b \t\tthread as binary (only cosmetic effect at the moment)\n"
            +"  -g \t\tbe GNU-md5sum|sha1sum-compilant\n"
            +"  -f f \t\tsave checksum to file f\n"
            +"  -r \t\tdo not use relative path\n"
            +"  -u \t\tuppercase hash\n"
            +"  -e \t\tprint a warning for empty dirs\n"
            +"  -s \t\tdont use systemindependent pathseparator\n"
            +"  -v \t\tbe more verbose\n"
            +"\t\t\tin GNU-comilant mode print filenames while checking\n"
            +"\t\t\tin normal mode print some additional infos\n"
            +"  -d n \t\tset debuglevel (0 < n < 125)\n"
            +"calculate md5sum:\n"
            +" "+CoreConfig.ProgName+" [-g] [-v] [-b] [-f tofile] file(s)|dir(s)\n"
            +"  file(s)|dir(s): the files and/or directories to act on\n"
            +"check md5sum:\n"
            +" "+CoreConfig.ProgName+" [-g] [-v] -c file\n"
            +"  -c file: check md5sums from file";
        
        return h;
    }
    protected void showVersion(){
        this.o.println(this.getVersion());
        System.exit(0);
    }
    protected String getVersion(){
        String ret = CoreConfig.ProgName
                    +" "+CoreConfig.VERSION
                    +"\n"+this.getJVMSpecs()
                    +"\n"+CoreConfig.COPYLEFT
                    +"\n"
                    +"\n"+CoreConfig.GNUCRYPTOCOPYLEFT;
        if(!this.isConsoleVersion){
            ret += "\n"+CoreConfig.SWTCOPYLEFT;
        }
        return ret;
    }
    protected String getShort(){
        return CoreConfig.ProgName+" "+CoreConfig.VERSION;
    }
    
    protected String getRelativePath(){
        this.o.debug(this.toString()+"::getRelativePath(): '"+this.relativePath+"'", 99);
        return this.relativePath;
    }
    protected void setRelativePath(String path){
        this.o.debug(this.toString()+"::setRelativePath() : "+path, 99);
        this.relativePath = path;
    }
    
    protected int getProgExitCode() {
        return this.progExitCode;
    }
    protected void setProgExitCode(int progExitCode) {
        this.progExitCode = progExitCode;
    }
    
    protected String getJVMSpecs(){
        return System.getProperty("java.vm.name")+" - "+System.getProperty("java.version");
    }
    protected String getProgName() {
        return CoreConfig.ProgName;
    }
    protected String getCheckMDFile() {
        return this.checkMDFile;
    }
    
    protected boolean setCheckMDFile(String checkMDFile) {
    	this.o.debug("CoreConfig :: setCheckMDFile '"+checkMDFile+"'", this.classDebugLevel);
        this.checkMDFile = checkMDFile;
        return true;
    }
    
    protected boolean isBinModus(){
        return this.binModus;
    }
    
    protected boolean useUpperCaseHash(){
        return this.upperCaseHash;
    }
    protected boolean beVerbose(){
        return this.beVerbose;
    }
    protected boolean isConsoleVersion() {
        return this.isConsoleVersion;
    }
    protected void setConsoleVersion(boolean isConsoleVersion) {
        this.isConsoleVersion = isConsoleVersion;
    }
    protected boolean isGnuCompat() {
        return this.gnuCompat;
    }
    
    protected boolean isUseRelPath() {
    	this.o.debug(this.toString()+"::isUseRelPath "+this.useRelPath,this.classDebugLevel);
        return this.useRelPath;
    }
    protected void setUseRelPath(boolean useRelPath) {
    	this.o.debug(this.toString()+"::setUseRelPath "+useRelPath,this.classDebugLevel);
        this.useRelPath = useRelPath;
    }
    protected boolean isUseSysIndepPathSeparator() {
        return this.useSysIndepPathSeparator;
    }
    protected void setUseSysIndepPathSeparator(boolean useSysIndepPathSeparator) {
        this.useSysIndepPathSeparator = useSysIndepPathSeparator;
    }
    
    protected void setHashSum(String algorithm){
        this.configError = true;
        this.configErrorMsg = "ERROR: you must overwrite setHashSum() alg:"+algorithm;
    }
    
    protected void addFileDir(String f){
    	this.o.debug(this.toString()+"::addFileDir '"+f+"'",this.classDebugLevel);
        if(this.isStopMe()){
            this.o.debug(this.toString()+"::addFileDir() for file "+f+" :: ABORTED stopMe",1);
            return;
        }
        this.o.debug("start addFileDir w/ param '"+f+"'",91);
        File file = new File(f);
        this.o.debug("file absolute: '"+file.getAbsolutePath()+"'",91);
        if( !( file.exists() && file.canRead() ) ){
            this.o.debug("file '"+file.getName()+"' exists:"+file.exists(),91);
            this.o.debug("file '"+file.getName()+"' canRead:"+file.canRead(),91);
            String emsg = "file '"+file.getName()+"' not found or can not read";
            this.o.debug(emsg,1);
            this.o.error(emsg);
            return;
        }
        if(file.isDirectory()){
            this.o.debug("is Directory !!",91);
            if(this.gnuCompat){
                this.o.error(file.getAbsolutePath()+" is a directory");
                return;
            }
            if((!this.gnuCompat) && (this.isUseRelPath()||!this.isConsoleVersion())){
                this.o.debug("calling setRelativePath with param "+file.getAbsolutePath(),91);
                this.findRelativePath(file.getAbsolutePath());
            }
            this.o.debug("call recurse ....",91);
            this.recurseDir(file);
        }else{
            this.o.debug("NOT Directory !!",91);
            if(this.fileSaveMDFile != null && file.equals(this.fileSaveMDFile)) {
            	this.o.debug("skipping checkfile!", 1);
            } else {
	            if((!this.gnuCompat) && (this.isUseRelPath() || !this.isConsoleVersion)){
	                if(f.startsWith("."+System.getProperty("file.separator"))){
	                    this.findRelativePath(System.getProperty("user.dir"));
	                }else{
	                    this.findRelativePath(file.getAbsoluteFile().getParent());
	                }
	            }
	            this.o.debug("call addHashFile for file '"+f+"'",91);
	            this.addHashFile(f);
            }
        }
    }
    
    protected void recurseDir(File d){
        if(this.isStopMe()){
            this.o.debug(this.toString()+"::recurseDir() for dir "+d+" :: ABORTED stopMe",1);
            return;
        }
        this.o.debug("recurseDir called for "+d.getName(),92);
        if(!d.isDirectory()) return;
        if(!d.canRead()){
            this.o.error("can not read directory "+d.getName());
            return;
        }
        String[] dlist = d.list();
        if(dlist.length == 0){
            if(this.warnEmptyDirs){
                this.o.error("empty dir "+d.getName());
            }
            return;
        }
        for(int i=0; i<dlist.length; i++){
            this.o.debug(this.toString()+"::recurseDir() LOOP for dir "+d,92);
            if(this.isStopMe()){
                this.o.debug(this.toString()+"::recurseDir() LOOP for dir "+d+" :: ABORTED stopMe",1);
                return;
            }
            this.addFileDir(d.getAbsolutePath()
                    +System.getProperty("file.separator")
                    +dlist[i]);
        }
    }
    
    protected Vector getMD5Files(){
        return this.hashFiles;
    }
    
    //@SuppressWarnings("unchecked")
	protected void addHashFile(String f,String parent){
        this.o.debug(this.toString()+"::addHashFile CoreHashFile file:"+f+" :: parent:"+parent,92);
        this.hashFiles.add(new CoreHashFile(f,this,parent));
        this.o.debug(this.toString()+"::addHashFile CoreHashFile file:"+f+" done",92);
    }
    //@SuppressWarnings("unchecked")
	protected void addHashFile(String f){
        this.o.debug(this.toString()+"::addHashFile CoreHashFile file:"+f,92);
        this.hashFiles.add(new CoreHashFile(f,this));
        this.o.debug(this.toString()+"::addHashFile CoreHashFile file:"+f+" done",92);
    }
    
    protected synchronized boolean isStopMe() {
        return this.stopMe;
    }
    protected synchronized void setStopMe(boolean stopMe){
        this.o.debug(this.toString()+"::setStopMe() "+stopMe,this.classDebugLevel);
        this.stopMe = stopMe;
    }
    
    /**
     * TODO make it better - MUCH better !!!
     * @param newPath
     */
    protected void findRelativePath(String newPath){
        if(!newPath.endsWith(System.getProperty("file.separator"))){
            newPath += System.getProperty("file.separator");
        }
        this.o.debug("start findRelativePath :: '"+this.getRelativePath()+"' :: '"+newPath+"'",99);
        if(!this.isUseRelPath()){
            if(this.isConsoleVersion()){
                this.setRelativePath("");
                this.o.debug("findRelativePath ignored!",99);
                return;
            }
        }
        if(this.getRelativePath().equals("")){
            this.setRelativePath(new File(newPath).getAbsolutePath());
            this.o.debug("relPath NEW!",99);
        }else if(this.getRelativePath().equals(newPath)){
            this.o.debug("relPath equals!",99);
        }else{
            String matchPath = "";
            int startMatchTest = 1;
            int endMatchTest = 0;
            boolean testMatch = true;
            int loopc = 0;
            while(testMatch){
                loopc++;
                this.o.debug("loop:"+loopc,99);
                this.o.debug("relPath:"+this.getRelativePath(),99);
                endMatchTest = this.getRelativePath().indexOf(System.getProperty("file.separator"),startMatchTest);
                this.o.debug("endMatchTest::"+endMatchTest,99);
                this.o.debug("startMatchTest::"+startMatchTest,99);
                if(endMatchTest >= newPath.length()){
                    this.o.debug("endMatchTest ("+endMatchTest+") greater newPath.length ("+newPath.length()+")",99);
                    endMatchTest = -1;
                }
                if(endMatchTest<0){
                    this.setRelativePath(matchPath);
                    this.o.debug("BREAK :: endMatchTest:"+endMatchTest+"; relativePath:'"+this.getRelativePath()+"'",99);
                    break;
                }
                String p = this.getRelativePath().substring(0,endMatchTest+1);
                this.o.debug("string relpath:'"+p+"'",99);
                File relpath = new File(p);
                p = newPath.substring(0,endMatchTest+1);
                this.o.debug("string testpath:'"+p+"'",99);
                File testpath = new File(p);
                this.o.debug("relpath::"+relpath.getName(),99);
                this.o.debug("testpath::"+testpath.getName(),99);
                this.o.debug("matchPath:'"+matchPath+"'",99);
                if(relpath.compareTo(testpath) == 0){
                    startMatchTest = endMatchTest+1;
                    matchPath = relpath.getAbsolutePath();
                    if(!matchPath.endsWith(System.getProperty("file.separator"))){
                        matchPath += System.getProperty("file.separator");
                    }
                    this.o.debug("matching! :: matchPath: '"+matchPath+"'",99);
                }else{
                    testMatch = false;
                    this.setRelativePath(matchPath);
                    if(!this.getRelativePath().equals("") && !this.getRelativePath().endsWith(System.getProperty("file.separator"))){
                        this.setRelativePath(this.getRelativePath() + System.getProperty("file.separator"));
                    }
                    this.o.debug("no match! :: set relPath to '"+this.getRelativePath()+"'",99);
                }
            }
        }
        if((!this.getRelativePath().equals(""))&&(!this.getRelativePath().endsWith(System.getProperty("file.separator")))){
            this.o.debug("append filesep!",99);
            this.setRelativePath(this.getRelativePath() + System.getProperty("file.separator"));
        }
        this.o.debug("end setRelativePath :: '"+this.getRelativePath()+"'",99);
        if(this.isWindows() && this.getRelativePath().equals("") && this.isUseSysIndepPathSeparator()){
            this.unableToUseSysIndependentPathSep();
        }
        this.o.debug("end findRelativePath :: '"+this.getRelativePath()+"'",99);
    }
    
    protected void unableToUseSysIndependentPathSep(){
    	this.o.debug(this.toString()+"::unableToUseSysIndependentPathSep()",this.classDebugLevel);
        if(this.isConsoleVersion()){
            this.o.error("unable to use systemindependent pathseparator!");
        }
        this.setUseSysIndepPathSeparator(false);
    }
}
