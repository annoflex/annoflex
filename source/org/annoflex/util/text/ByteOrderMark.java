/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

/**
 * @author Stefan Czaska
 */
public enum ByteOrderMark {
    
    UTF_32BE_1234(4,"UTF-32BE"),
    UTF_32_2143  (4,"UTF-32"),
    UTF_32LE_4321(4,"UTF-32LE"),
    UTF_32_3412  (4,"UTF-32"),
    UTF_8        (3,"UTF-8"),
    UTF_16BE     (2,"UTF-16BE"),
    UTF_16LE     (2,"UTF-16LE");
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final int offset;
    
    /**
     * 
     */
    private final String charSet;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    private ByteOrderMark(int offset, String charSet) {
        this.offset = offset;
        this.charSet = charSet;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public int getOffset() {
        return offset;
    }
    
    /**
     * 
     */
    public String getCharSet() {
        return charSet;
    }
    
    //================
    // Lookup Methods
    //================
    
    /**
     * 
     */
    public static ByteOrderMark lookup(byte[] bytes) {
        if ((bytes == null) || (bytes.length < 2)) {
            return null;
        }
        
        if (bytes.length >= 3) {
            if (bytes.length >= 4) {
                if (bytes[0] == (byte)0x00) {
                    if (bytes[1] == (byte)0x00) {
                        
                        // UCS-4, big-endian machine (1234 order)
                        if (bytes[2] == (byte)0xFE) {
                            if (bytes[3] == (byte)0xFF) {
                                return UTF_32BE_1234;
                            }
                        }
                        
                        // UCS-4, unusual octet order (2143)
                        // -> simply map it to UTF-32
                        else if ((bytes[2] == (byte)0xFF) &&
                                 (bytes[3] == (byte)0xFE)) {
                            
                            return UTF_32_2143;
                        }
                    }
                    
                    return null;
                }
                
                if (bytes[0] == (byte)0xFF) {
                    
                    // UCS-4, little-endian machine (4321 order)
                    if ((bytes[1] == (byte)0xFE) &&
                        (bytes[2] == (byte)0x00) &&
                        (bytes[3] == (byte)0x00)) {
                        
                        return UTF_32LE_4321;
                    }
                }
                
                else if (bytes[0] == (byte)0xFE) {
                    
                    // UCS-4, unusual octet order (3412)
                    // -> simply map it to UTF-32
                    if ((bytes[1] == (byte)0xFF) &&
                        (bytes[2] == (byte)0x00) &&
                        (bytes[3] == (byte)0x00)) {
                        
                        return UTF_32_3412;
                    }
                }
            }
            
            // UTF-8
            if (bytes[0] == (byte)0xEF) {
                if ((bytes[1] == (byte)0xBB) &&
                    (bytes[2] == (byte)0xBF)) {
                    
                    return UTF_8;
                }
                
                return null;
            }
        }
        
        // UTF-16, big-endian
        if (bytes[0] == (byte)0xFE) {
            if (bytes[1] == (byte)0xFF) {
                return UTF_16BE;
            }
        }
        
        // UTF-16, little-endian
        else if (bytes[0] == (byte)0xFF) {
            if (bytes[1] == (byte)0xFE) {
                return UTF_16LE;
            }
        }
        
        return null;
    }
}
