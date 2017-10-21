/*
 * AnnoFlex - An annotation-based code generator for lexical scanners
 * 
 * Copyright (c) Stefan Czaska. All rights reserved.
 */
package org.annoflex.regex.dom;

import org.annoflex.regex.LookafterType;

/**
 * @author Stefan Czaska
 */
public class ROMLookafter extends ROMNode {
    
    //========
    // Fields
    //========
    
    /**
     * 
     */
    private final LookafterType lookafterType;
    
    //==============
    // Constructors
    //==============
    
    /**
     * 
     */
    public ROMLookafter(LookafterType lookafterType) {
        super(ROMNodeType.LOOKAFTER);
        
        if (lookafterType == null) {
            throw new IllegalArgumentException("lookafter type may not be null");
        }
        
        this.lookafterType = lookafterType;
    }
    
    //=========
    // Methods
    //=========
    
    /**
     * 
     */
    public final LookafterType getLookafterType() {
        return lookafterType;
    }
    
    //================
    // Object Methods
    //================
    
    /**
     * 
     */
    protected String toStringInternal() {
        return lookafterType.toString();
    }
}
