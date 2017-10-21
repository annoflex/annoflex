/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex;

/**
 * @author Stefan Czaska
 */
public class QuantifierException extends IllegalArgumentException {
    
    /**
     * 
     */
    public QuantifierException(String message) {
        super(message);
    }
}
