/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.jdt.dom;

/**
 * @author Stefan Czaska
 */
public class JOMModifier extends JOMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private Modifier modifier;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public JOMModifier() {
        super(JOMNodeType.MODIFIER);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public void setModifier(Modifier modifier) {
        this.modifier = modifier;
    }
    
    /**
     * 
     */
    public Modifier getModifier() {
        return modifier;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return modifier != null ? modifier.name().toLowerCase() : null;
    }
}
