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
public class RegExScanException extends RuntimeException {
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public RegExScanException(String message) {
        super(message);
    }
}
