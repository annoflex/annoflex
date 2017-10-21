/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

import org.annoflex.regex.Quantifier;

/**
 * @author Stefan Czaska
 */
public class ROMQuantifier extends ROMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final Quantifier quantifier;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMQuantifier(Quantifier quantifier) {
        super(ROMNodeType.QUANTIFIER);
        
        if (quantifier == null) {
            throw new IllegalArgumentException("quantifier may not be null");
        }
        
        this.quantifier = quantifier;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final Quantifier getQuantifier() {
        return quantifier;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return quantifier.toString();
    }
}
