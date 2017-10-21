/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.parser;

/**
 * @author Stefan Czaska
 */
@SuppressWarnings("serial")
public class RegExParseException extends RuntimeException {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public RegExParseException(String message) {
        super(message);
    }
}
