/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.unicode;

/**
 * @author Stefan Czaska
 */
public class PropertyToolkit {
    
    //==================
    // Public Constants
    //==================
    
    public static final String BINARY_Y = "y";
    public static final String BINARY_YES = "yes";
    public static final String BINARY_T = "t";
    public static final String BINARY_TRUE = "true";
    public static final String BINARY_N = "n";
    public static final String BINARY_NO = "no";
    public static final String BINARY_F = "f";
    public static final String BINARY_FALSE = "false";
    
    //====================
    // Internal Constants
    //====================
    
    /**
     * 
     */
    private static final String U1180_PREFIX = "hanguljungseongo";
    
    //=========================
    // Binary Property Methods
    //=========================
    
    /**
     * 
     */
    public static boolean isBinaryFalse(String normalizedValue) {
        if (normalizedValue != null) {
            switch(normalizedValue) {
            case BINARY_N:
            case BINARY_NO:
            case BINARY_F:
            case BINARY_FALSE:
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 
     */
    public static boolean isBinaryTrue(String normalizedValue) {
        if (normalizedValue != null) {
            switch(normalizedValue) {
            case BINARY_Y:
            case BINARY_YES:
            case BINARY_T:
            case BINARY_TRUE:
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 
     */
    public static String resolveBinaryAlias(String normalizedValue) {
        if (normalizedValue != null) {
            switch(normalizedValue) {
            case BINARY_Y:
            case BINARY_YES:
            case BINARY_T:
            case BINARY_TRUE:
                return BINARY_YES;
            
            case BINARY_N:
            case BINARY_NO:
            case BINARY_F:
            case BINARY_FALSE:
                return BINARY_FALSE;
            }
        }
        
        return normalizedValue;
    }
    
    //======================================
    // Symbolic Value Normalization Methods
    //======================================
    
    /**
     * 
     */
    public static String normalizeSymbolicValue(String symbolicValue) {
        if (symbolicValue != null) {
            int length = symbolicValue.length();
            StringBuilder builder = null;
            
            for (int i=0;i<length;i++) {
                char curChar = symbolicValue.charAt(i);
                
                boolean isIgnorable = (curChar == '_') || (curChar == '-') ||
                        Character.isWhitespace(curChar);
                
                if (isIgnorable || changesOnLowercase(curChar)) {
                    if (builder == null) {
                        builder = new StringBuilder();
                        
                        for (int j=0;j<i;j++) {
                            builder.append(Character.toLowerCase(symbolicValue.charAt(j)));
                        }
                    }
                }
                
                if (!isIgnorable && (builder != null)) {
                    builder.append(Character.toLowerCase(curChar));
                }
            }
            
            return builder != null ? builder.toString() : symbolicValue;
        }
        
        return null;
    }
    
    //======================================
    // Character Name Normalization Methods
    //======================================
    
    /**
     * 
     */
    public static String normalizeCharName(String charName) {
        if (charName != null) {
            StringBuilder builder = null;
            int length = charName.length();
            
            for (int i=0;i<length;i++) {
                char curChar = charName.charAt(i);
                
                boolean isIgnorable = (curChar == '_') ||
                        Character.isWhitespace(curChar) ||
                        (isMedialHyphen(curChar,charName,i) &&
                         !isU1180Exclusion(charName,i,builder));
                
                if (isIgnorable || changesOnLowercase(curChar)) {
                    if (builder == null) {
                        builder = new StringBuilder();
                        
                        for (int j=0;j<i;j++) {
                            builder.append(Character.toLowerCase(charName.charAt(j)));
                        }
                    }
                }
                
                if (!isIgnorable && (builder != null)) {
                    builder.append(Character.toLowerCase(curChar));
                }
            }
            
            return builder != null ? builder.toString() : charName;
        }
        
        return null;
    }
    
    /**
     * 
     */
    private static boolean isMedialHyphen(char character, String string, int index) {
        return (character == '-') && (index > 0) &&
               (index < (string.length() - 1)) &&
               Character.isLetter(string.charAt(index-1)) &&
               Character.isLetter(string.charAt(index+1));
    }
    
    /**
     * 
     */
    private static boolean changesOnLowercase(char character) {
        return Character.toLowerCase(character) != character;
    }
    
    /**
     * 
     */
    private static boolean isU1180Exclusion(String name, int nameIndex,
            StringBuilder tempName) {
        
        char prevChar = name.charAt(nameIndex-1);
        char nextChar = name.charAt(nameIndex+1);
        
        if (((prevChar == 'o') || (prevChar == 'O')) &&
            ((nextChar == 'e') || (nextChar == 'E'))) {
            
            if (tempName != null) {
                return U1180_PREFIX.contentEquals(tempName) &&
                       hasIgnorableCharNameSuffix(name,nameIndex+2);
            }
            
            return name.startsWith(U1180_PREFIX) &&
                   hasIgnorableCharNameSuffix(name,nameIndex+2);
        }
        
        return false;
    }
    
    /**
     * 
     */
    private static boolean hasIgnorableCharNameSuffix(String name, int index) {
        int length = name.length();
        
        for (int i=index;i<length;i++) {
            char curChar = name.charAt(i);
            
            // Note: Medial hyphens must not be tested here as they imply at
            // least one non-content character after it. Thus a medial hyphen
            // behaves like a normal hyphen at this place.
            if ((curChar != '_') && !Character.isWhitespace(curChar)) {
                return false;
            }
        }
        
        return true;
    }
}
