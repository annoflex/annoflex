/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

import org.annoflex.util.text.StringToolkit;

/**
 * @author Stefan Czaska
 */
public class ROMName extends ROMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final String name;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMName(String name) {
        super(ROMNodeType.NAME);
        
        if (name == null) {
            throw new IllegalArgumentException("name may not be null");
        }
        
        if (!StringToolkit.isASCIIIdentifier(name)) {
            throw new IllegalArgumentException("invalid name \""+name+"\"");
        }
        
        this.name = name;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final String getName() {
        return name;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return name;
    }
}
