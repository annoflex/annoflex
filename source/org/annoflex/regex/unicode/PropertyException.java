/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.unicode;

/**
 * @author Stefan Czaska
 */
public class PropertyException extends RuntimeException {
    
    //===========
    // Constants
    //===========
    
    public static final int INTERNAL_ERROR        = 1;
    public static final int SYNTAX_ERROR          = 2;
    
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
    public PropertyException(int type, String message) {
        super(message);
        
        this.type = type;
    }
    
    /**
     * 
     */
    public PropertyException(String message, Throwable cause) {
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
