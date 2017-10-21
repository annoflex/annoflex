/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

/**
 * @author Stefan Czaska
 */
public class ROMCharacterClass extends ROMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private boolean invert;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMCharacterClass(boolean invert) {
        super(ROMNodeType.CHARACTER_CLASS);
        
        setInvert(invert);
    }
    
    /**
     * 
     */
    public ROMCharacterClass(ROMNode sequence) {
        super(ROMNodeType.CHARACTER_CLASS);
        
        appendChild(sequence);
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public void setInvert(boolean invert) {
        this.invert = invert;
    }
    
    /**
     * 
     */
    public boolean getInvert() {
        return invert;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return "invert=" + invert;
    }
}
