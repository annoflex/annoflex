/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.compiler;

/**
 * @author Stefan Czaska
 */
@SuppressWarnings("serial")
public class RegExCompileException extends RuntimeException {
    
    //===========
    // Constants
    //===========
    
    public static final int INTERNAL_ERROR         = 1;
    public static final int SYNTAX_ERROR           = 2;
    public static final int CONDITIONS_NOT_ALLOWED = 3;
    public static final int UNSUPPORTED_FEATURE    = 4;
    public static final int UNKNOWN_MACRO          = 5;
    public static final int INVALID_MACRO          = 6;
    public static final int INVALID_MACRO_USAGE    = 7;
    public static final int INVALID_CHAR_RANGE     = 8;
    public static final int EMPTY_CHAR_CLASS       = 9;
    public static final int UNKNOWN_CHAR_NAME      = 10;
    public static final int INVALID_CHAR_NAME      = 11;
    public static final int UNKNOWN_CHAR_PROPERTY  = 12;
    public static final int INVALID_CHAR_PROPERTY  = 13;
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final int type;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public RegExCompileException(int type, String message) {
        super(message);
        
        this.type = type;
    }
    
    /**
     * 
     */
    public RegExCompileException(String message, Throwable cause) {
        super(message+": "+cause.getMessage(),cause);
        
        this.type = INTERNAL_ERROR;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final int getType() {
        return type;
    }
}
