/**
 * $Id: HashSum.java,v 1.13 2008-03-04 21:52:09 zerwes Exp $
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
import gnu.crypto.hash.HashFactory;
import gnu.crypto.hash.IMessageDigest;

import java.security.NoSuchAlgorithmException;
import java.util.zip.Checksum;
import java.util.zip.CRC32;
import java.nio.ByteBuffer;


/*
 * TODO: Check if we realy need this as we use HashFactory
 */
class HashSum implements Checksum{
	private int BuffSize = 4012;
	private IMessageDigest md = null;
	private CRC32 crc32 = null;
	
	
	protected HashSum(String mdtype) throws NoSuchAlgorithmException {
		
		try {
			mdtype = mdtype.trim();
			if (mdtype.equalsIgnoreCase("CRC32"))
				this.crc32 = new CRC32();
			else
				this.md = HashFactory.getInstance(mdtype);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			throw new java.security.NoSuchAlgorithmException("No such Algorithm: "+mdtype);
		}
		
		if(this.md == null && this.crc32 == null) {
			throw new java.security.NoSuchAlgorithmException("No such Algorithm: "+mdtype);
		}
	}
	
	protected boolean isSameAlg(String alg){
		if (this.crc32 != null)
			return "CRC32".equalsIgnoreCase(alg);
		
		if (this.md==null) return false;
		return this.md.name().equalsIgnoreCase(alg);
	}
	
	protected String getName(){
		if (this.crc32 != null) return "CRC32";
		if (this.md==null) return "UNDEFINED";
		return this.md.name();
	}
	
	protected int getHashLenght(){
		if (this.crc32 != null)
			return 8;
		if (this.md == null)
			return -1;
		if (this.md.name().equalsIgnoreCase(Registry.WHIRLPOOL_HASH))
			return 128;
		return this.md.hashSize()*2;
	}
	
	protected byte[] getBuffer(){
		return new byte[this.BuffSize];
	}
	
	protected void setBuffSize(int buffsize){
		this.BuffSize = buffsize;
	}
	protected int getBuffSize(){
		return this.BuffSize;
	}
	
	// must be public because we can not reduce visibility of inherited methods 
	public void update(byte[] buff, int off, int len){
		if (this.crc32 != null)
			this.crc32.update(buff,off,len);
		else
			this.md.update(buff,off,len);
    }
	public void update(int i){
		if (this.crc32 != null)
			this.crc32.update(i);
		else
			this.md.update((byte)i);
	}
    
    protected byte[] digest(){
		if (this.crc32 != null) {
			ByteBuffer buffer = ByteBuffer.allocate(4);
			buffer.putInt((int)this.crc32.getValue());
			return buffer.array();
		}
		
		return this.md.digest();
    }
	
	/*
	 * NOT USED but required to implement Checksum
	 */
	public long getValue(){
		if (this.crc32 != null)
			return this.crc32.getValue();
		else
			return 0;
	}
	
	public void reset(){
		if (this.crc32 != null)
			this.crc32.reset();
		else
			this.md.reset();
	}
	
	public String hexit(byte[] array,boolean uppercase) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if(!uppercase){
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toLowerCase().substring(1,3));
            }else{
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).toUpperCase().substring(1,3));
            }
        }
        return sb.toString();
    }
}
