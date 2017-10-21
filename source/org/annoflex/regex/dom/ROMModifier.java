/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

import org.annoflex.regex.Modifier;

/**
 * @author Stefan Czaska
 */
public class ROMModifier extends ROMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final Modifier modifier;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMModifier(Modifier modifier) {
        super(ROMNodeType.MODIFIER);
        
        if (modifier == null) {
            throw new IllegalArgumentException("modifier may not be null");
        }
        
        this.modifier = modifier;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final Modifier getModifier() {
        return modifier;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return modifier.toString();
    }
}
