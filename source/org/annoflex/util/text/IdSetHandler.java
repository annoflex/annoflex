/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.util.text;

import org.annoflex.util.integer.IdSet;
import org.annoflex.util.integer.IntHandler;

/**
 * @author Stefan Czaska
 */
final class IdSetHandler implements IntHandler {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final IdSet idSet;
    
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
    IdSetHandler(char[] characterSet, boolean invert) {
        if (characterSet == null) {
            throw new IllegalArgumentException("character set may not be null");
        }
        
        if (characterSet.length == 0) {
            throw new IllegalArgumentException("character set may not be empty");
        }
        
        idSet = new IdSet();
        
        for (int i=0;i<characterSet.length;i++) {
            idSet.add(characterSet[i]);
        }
        
        this.invert = invert;
    }
    
    /**
     * 
     */
    IdSetHandler(int[] codePointSet, boolean invert) {
        if (codePointSet == null) {
            throw new IllegalArgumentException("code point set may not be null");
        }
        
        if (codePointSet.length == 0) {
            throw new IllegalArgumentException("code point set may not be empty");
        }
        
        idSet = new IdSet();
        
        for (int i=0;i<codePointSet.length;i++) {
            int curCodePoint = codePointSet[i];
            
            if ((curCodePoint < Character.MIN_CODE_POINT) ||
                (curCodePoint > Character.MAX_CODE_POINT)) {
                
                throw new IllegalArgumentException("invalid code point: "+curCodePoint);
            }
            
            idSet.add(curCodePoint);
        }
        
        this.invert = invert;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public boolean handleInteger(int codePoint) {
        return idSet.contains(codePoint) ^ invert;
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
        builder.append("[idSet=");
        builder.append(idSet);
        builder.append(",invert=");
        builder.append(invert);
        builder.append("]");
        
        return builder.toString();
    }
}
