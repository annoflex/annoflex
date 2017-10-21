/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;


/**
 * @author Stefan Czaska
 */
public class ROMCharRef extends ROMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final CharRef charRef;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMCharRef(CharRef charRef) {
        super(ROMNodeType.CHAR_REF);
        
        if (charRef == null) {
            throw new IllegalArgumentException("char reference may not be null");
        }
        
        this.charRef = charRef;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final CharRef getCharRef() {
        return charRef;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return charRef.toString();
    }
}
