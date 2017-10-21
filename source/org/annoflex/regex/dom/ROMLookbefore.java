/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

import org.annoflex.regex.LookbeforeType;

/**
 * @author Stefan Czaska
 */
public class ROMLookbefore extends ROMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final LookbeforeType lookbeforeType;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMLookbefore(LookbeforeType lookbeforeType) {
        super(ROMNodeType.LOOKBEFORE);
        
        if (lookbeforeType == null) {
            throw new IllegalArgumentException("lookbefore type may not be null");
        }
        
        this.lookbeforeType = lookbeforeType;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final LookbeforeType getLookbeforeType() {
        return lookbeforeType;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return lookbeforeType.toString();
    }
}
