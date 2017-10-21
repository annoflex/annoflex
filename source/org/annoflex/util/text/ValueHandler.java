/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

import org.annoflex.util.integer.IntHandler;

/**
 * @author Stefan Czaska
 */
final class ValueHandler implements IntHandler {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final int codePoint;
    
    /**
     * 
     */
    private final boolean invert;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    ValueHandler(int codePoint, boolean invert) {
        if ((codePoint < Character.MIN_CODE_POINT) ||
            (codePoint > Character.MAX_CODE_POINT)) {
            
            throw new IllegalArgumentException("invalid code point: "+codePoint);
        }
        
        this.codePoint = codePoint;
        this.invert = invert;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public boolean handleInteger(int codePoint) {
        return (codePoint == this.codePoint) ^ invert;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append(getClass().getSimpleName());
        builder.append("[codePoint=");
        builder.append(codePoint);
        builder.append(",invert=");
        builder.append(invert);
        builder.append("]");
        
        return builder.toString();
    }
}
