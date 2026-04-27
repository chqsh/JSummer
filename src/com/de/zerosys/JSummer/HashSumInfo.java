/*
 * $Id: HashSumInfo.java,v 1.3 2008-03-05 16:34:08 zerwes Exp $
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

final class HashSumInfo {
	private String hashAlgName;
	private String[] alternativeHashNames = null;
	private String[] hashFileExtension = new String[10];
	private int hashFileExtensionIDX = 0;
	
	
	protected HashSumInfo(String hashAlgName) {
		this.hashAlgName = hashAlgName;
		this.addHashFileExtension(this.hashAlgName.toLowerCase());
	}
	protected HashSumInfo(String hashAlgName, String[] alternativeHashNames) {
		this.hashAlgName = hashAlgName;
		this.alternativeHashNames = alternativeHashNames;
		this.addHashFileExtension(this.hashFileExtension[0] = this.hashAlgName.toLowerCase());
	}
	protected HashSumInfo(String hashAlgName, String[] alternativeHashNames, String hashFileExtension) {
		this.hashAlgName = hashAlgName;
		this.alternativeHashNames = alternativeHashNames;
		this.addHashFileExtension(hashFileExtension);
		this.addHashFileExtension(this.hashAlgName.toLowerCase());
	}
	protected HashSumInfo(String hashAlgName, String[] alternativeHashNames, String[] hashFileExtensions) {
		this.hashAlgName = hashAlgName;
		this.alternativeHashNames = alternativeHashNames;
		for(int i=0; i<hashFileExtensions.length; i++) {
			this.addHashFileExtension(hashFileExtensions[i]);
		}
		this.addHashFileExtension(this.hashAlgName.toLowerCase());
	}
	
	protected String getalternativeHashNamesAsString(String delimiter) {
		String ret = "";
		if(this.alternativeHashNames != null) {
			for(int i=0; i<this.alternativeHashNames.length; i++) {
				if(ret.length() > 0) ret += delimiter;
				ret += this.alternativeHashNames[i];
			}
		}
		return ret;
	}
	
	protected boolean matchesHashSumString(String hashsumtype) {
		boolean ret = false;
		if(hashsumtype.equalsIgnoreCase(this.hashAlgName)) ret = true;
		if(ret == false && this.alternativeHashNames != null) {
			for (int i = 0; i < this.alternativeHashNames.length; i++) {
				if(hashsumtype.equalsIgnoreCase(this.alternativeHashNames[i])) return true;
			}
		}
		return ret;
	}
	protected final String getHashFileExtension() {
		return this.hashFileExtension[0];
	}
	
	protected final boolean isValidHashFileExtension(String ext) {
		for(int i=0; i<this.hashFileExtension.length; i++) {
			if(ext.equalsIgnoreCase(this.hashFileExtension[i])) {
				return true;
			}
		}
		return false;
	}

	protected final String getAllHashFileExtensions(String prepend, String delimiter) {
		String ext = prepend+this.hashFileExtension[0];
		for(int i=1; i<this.hashFileExtension.length; i++) {
			if(this.hashFileExtension[i] == null || this.hashFileExtension[i].length() == 0) {
				continue;
			}
			ext += delimiter+prepend+this.hashFileExtension[i];
		}
		return ext;
	}
	protected final String getHashAlgName() {
		return hashAlgName;
	}
	
	protected void addHashFileExtension(String ext) {
		this.hashFileExtension[this.hashFileExtensionIDX] = ext;
		this.hashFileExtensionIDX++;
	}
}
